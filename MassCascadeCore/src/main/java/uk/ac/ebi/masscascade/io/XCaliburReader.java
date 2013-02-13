/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
 *
 * This file is part of MassCascade.
 *
 * MassCascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassCascade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassCascade. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.masscascade.io;

import com.mindprod.ledatastream.LEDataInputStream;
import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.OleDate;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for reading XCalibur RAW files.
 * <ul>
 * <li>Parameter <code> DATA FILE </code>- The data file to be read.</li>
 * <li>Parameter <code> RAW FILE </code>- The target raw container.</li>
 * <li>Parameter <code> WORKING DIRECTORY </code>- The working directory.</li>
 * </ul>
 */
public class XCaliburReader extends ACallableTask {

    private File file;
    private String workingDirectory;
    private RawContainer rawContainer;

    // scan information
    private int scanNumber = 0;
    private Constants.MSN msn = Constants.MSN.MS1;
    private double retentionTime = 0;
    private double basePeak = 0;
    private double basePeakIntensity = 0;
    private double totalIonCurrent = 0;
    private Constants.ION_MODE ionMode;

    // Tandem information
    private int parentCharge = 0;
    private int parentScan = -1;
    private int parentScanIndex = -1;
    private double parentMz = 0;

    /**
     * Constructs a XCalibur reader.
     *
     * @param params the parameter map: Data
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public XCaliburReader(ParameterMap params) throws MassCascadeException {

        super(XCaliburReader.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the XCalibur reader.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    private void setParameters(ParameterMap params) throws MassCascadeException {

        file = params.get(Parameter.DATA_FILE, File.class);
        workingDirectory = params.get(Parameter.WORKING_DIRECTORY, String.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);

        if (file == null || !file.isFile()) throw new MassCascadeException("File not found.");
    }

    /**
     * Parses a XCalibur '.raw' file.
     *
     * @return the compiled mass spec sample
     */
    public RawContainer call() {

        Process dumper = null;
        try {
            String cmdLine[] =
                    {System.getProperty("java.io.tmpdir") + File.separator + "RAWdumpProfile.exe", file.getPath()};

            // Create a separate process and execute RAWdumpCentroid.exe
            dumper = Runtime.getRuntime().exec(cmdLine);

            // Get the stdout of RAWdumpCentroid.exe process as InputStream
            InputStream dumpStream = dumper.getInputStream();
            BufferedInputStream bufStream = new BufferedInputStream(dumpStream);

            // Read the dump data
            readRawDump(bufStream);
            // Finish
            bufStream.close();
        } catch (Exception e) {
            if (dumper != null) dumper.destroy();
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return rawContainer;
    }

    /**
     * Reads the '.raw' file utilizing the external exe.
     *
     * @param bufStream the input stream as send by the external executable
     * @throws IOException unexpected behaviour
     */
    public void readRawDump(BufferedInputStream bufStream) throws IOException {

        String line;
        String creationDate = "";

        TextUtils tx = new TextUtils();

        while ((line = tx.readLineFromStream(bufStream)) != null) {
            if (line.startsWith("ERROR: ")) {
                throw (new IOException(line.substring("ERROR: ".length())));
            } else if (line.startsWith("SAMPLE ID: ")) {
                // do nothing
            } else if (line.startsWith("NUMBER OF SCANS: ")) {
                // do nothing
            } else if (line.startsWith("SCAN NUMBER: ")) {
                scanNumber = Integer.parseInt(line.substring("SCAN NUMBER: ".length()));
            } else if (line.startsWith("CREATION DATE: ")) {
                OleDate oleDate = new OleDate();
                creationDate =
                        oleDate.getDate(Double.parseDouble(line.substring("CREATION DATE: ".length()))).toString();
            } else if (line.startsWith("BASE PEAK MZ: ")) {
                basePeak = Double.parseDouble(line.substring("BASE PEAK MZ: ".length()));
            } else if (line.startsWith("BASE PEAK INT: ")) {
                basePeakIntensity = Double.parseDouble(line.substring("BASE PEAK INT: ".length()));
            } else if (line.startsWith("TOTAL ION CURRENT: ")) {
                totalIonCurrent = Double.parseDouble(line.substring("TOTAL ION CURRENT: ".length()));
            } else if (line.startsWith("SCAN FILTER: ")) {
                if (line.contains(" + ")) {
                    ionMode = Constants.ION_MODE.POSITIVE;
                } else if (line.contains(" - ")) {
                    ionMode = Constants.ION_MODE.NEGATIVE;
                } else {
                    ionMode = Constants.ION_MODE.IN_SILICO;
                }
                if (line.contains("ms ")) {
                    msn = Constants.MSN.MS1;
                } else {
                    /*
                    * Typical filter line looks like this: ITMS - c ESI d Full
                    * ms3 587.03@cid35.00 323.00@cid35.00
                    */
                    Pattern p = Pattern.compile("ms(\\d).* (\\d+\\.\\d+)@");
                    Matcher m = p.matcher(line);
                    if (!m.find()) {
                        throw new IOException("Unexpected CmlSpectrumFactory: " + line);
                    }
                    msn = Constants.MSN.get(m.group(1));

                    // Initially we obtain precursor m/z from this filter line,
                    // even though the precision is not good. Later more precise
                    // precursor m/z may be reported using PRECURSOR: line, but
                    // sometimes it is missing (equal to 0)
                    parentMz = Double.parseDouble(m.group(2));
                }
            } else if (line.startsWith("RETENTION TIME: ")) {
                retentionTime = Double.parseDouble(line.substring("RETENTION TIME: ".length())) * 60;
            } else if (line.startsWith("PRECURSOR: ")) {
                String tokens[] = line.split(" ");
                double token2 = Double.parseDouble(tokens[1]);
                int token3 = Integer.parseInt(tokens[2]);
                if (token2 > 0) {
                    parentMz = token2;
                    parentCharge = token3;
                }
            } else if (line.startsWith("DATA POINTS: ")) {
                int numOfDataPoints = Integer.parseInt(line.substring("DATA POINTS: ".length()));

                XYList xyList = new XYList();
                // Because Intel CPU is using little endian natively, we
                // need to use LEDataInputStream instead of normal Java
                // DataInputStream, which is big-endian.
                LEDataInputStream dis = new LEDataInputStream(bufStream);
                for (int i = 0; i < numOfDataPoints; i++) {
                    xyList.add(new XYPoint(dis.readDouble(), dis.readDouble()));
                }
                Constants.ACQUISITION_MODE acquisitionMode = Constants.ACQUISITION_MODE.PROFILE;
                if (ScanUtils.isCentroided(xyList)) {
                    acquisitionMode = Constants.ACQUISITION_MODE.CENTROID;
                }
                XYList optimizedXYList = ScanUtils.removeZeroAndDuplicateDataPoints(xyList, acquisitionMode);
                ExtendableRange mzExtendableRange = new ExtendableRange(optimizedXYList.get(0).x,
                        optimizedXYList.get(optimizedXYList.size() - 1).x);

                if (msn.getLvl() > 1) {
                    parentScan = parentScanIndex;
                } else {
                    parentScanIndex = scanNumber;
                }

                XYList baseXY = new XYList();
                baseXY.add(new XYPoint(basePeak, basePeakIntensity));
                ScanImpl scan = new ScanImpl(scanNumber, msn, ionMode, optimizedXYList, mzExtendableRange, baseXY,
                        retentionTime, totalIonCurrent, parentScan, parentCharge, parentMz);
                rawContainer.addScan(scan);

                // Clean the variables for next scan
                scanNumber = 0;
                msn = Constants.MSN.MS1;
                retentionTime = 0;
                parentMz = 0;
                parentCharge = 0;
                parentScan = -1;
                basePeak = 0;
                basePeakIntensity = 0;
                totalIonCurrent = 0;
            }
        }

        // wrap up loose ends
        rawContainer.finaliseFile(creationDate);
    }
}
