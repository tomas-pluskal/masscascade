/*
 * Copyright (C) 2014 EMBL - European Bioinformatics Institute
 *
 * This file is part of MassCascade.
 *
 * Based on: NetCDFReadTask (MzMine)
 *
 * http://sourceforge.net/p/mzmine/code/HEAD/tree/trunk/src/main/java/net/sf/
 * mzmine/modules/rawdatamethods/rawdataimport/fileformats/NetCDFReadTask.java#l440
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
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */

package uk.ac.ebi.masscascade.io;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import uk.ac.ebi.masscascade.core.scan.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class for reading netCDF files. <ul> <li>Parameter <code> DATA FILE </code>- The data file to be read.</li>
 * <li>Parameter <code> SCAN_CONTAINER </code>- The target scan container.</li> </ul>
 *
 * alpha version, under heavy development - do not use!
 */
public class NetCDFReader extends CallableTask {

    private File cdfFile;
    private ScanContainer scanContainer;

    // Scan information
    private int scanNumber = 0;
    private Constants.MSN msn = Constants.MSN.MS1;
    private double retentionTime = 0;
    private double basePeak = 0;
    private double basePeakIntensity = 0;
    private double totalIonCurrent = 0;
    private Constants.ION_MODE ionMode = Constants.ION_MODE.UNKNOWN;

    // Tandem information
    private int parentCharge = 0;
    private int parentScan = -1;
    private int parentScanIndex = -1;
    private double parentMz = 0;

    /**
     * Constructs a netCDF reader task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public NetCDFReader(ParameterMap params) throws MassCascadeException {

        super(NetCDFReader.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the file reader task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        cdfFile = params.get(Parameter.DATA_FILE, File.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);

        if (cdfFile == null || !cdfFile.isFile()) throw new MassCascadeException("File not found.");
    }

    /**
     * Parses a netCDF file.
     *
     * @return the compiled mass spec sample
     */
    public ScanContainer call() {

        String creationTime = (new Date()).toString();
        String name = cdfFile.getName();
        msn = Constants.MSN.MS1;
        ionMode = Constants.ION_MODE.POSITIVE;

        NetcdfFile ncfile = null;
        try {
            ncfile = NetcdfFile.open(cdfFile.getAbsolutePath());

            Variable massValueVariable = ncfile.findVariable("mass_values");
            Attribute massScaleFacAttr = massValueVariable.findAttribute("scale_factor");
            double 	massValueScaleFactor = massScaleFacAttr.getNumericValue().doubleValue();

            Variable intensityValueVariable = ncfile.findVariable("intensity_values");
            Attribute intScaleFacAttr = intensityValueVariable.findAttribute("scale_factor");
            double intensityValueScaleFactor = intScaleFacAttr.getNumericValue().doubleValue();

            Variable scanIndexVariable = ncfile.findVariable("scan_index");
            int totalScans = scanIndexVariable.getShape()[0];

            int[] scanStartPositions = new int[totalScans + 1];
            Array scanIndexArray = scanIndexVariable.read();
            IndexIterator scanIndexIterator = scanIndexArray.getIndexIterator();
            int ind = 0;
            while (scanIndexIterator.hasNext()) {
                scanStartPositions[ind] = ((Integer) scanIndexIterator.next());
                ind++;
            }

            scanStartPositions[totalScans] = (int) massValueVariable.getSize();
            double[] retentionTimes = new double[totalScans];
            Variable scanTimeVariable = ncfile.findVariable("scan_acquisition_time");
            Array scanTimeArray = scanTimeVariable.read();

            IndexIterator scanTimeIterator = scanTimeArray.getIndexIterator();
            ind = 0;
            while (scanTimeIterator.hasNext()) {
                if (scanTimeVariable.getDataType().getPrimitiveClassType() == float.class) {
                    retentionTimes[ind] = ((Double) scanTimeIterator.next());
                }
                if (scanTimeVariable.getDataType().getPrimitiveClassType() == double.class) {
                    retentionTimes[ind] = ((Double) scanTimeIterator.next());
                }
                ind++;
            }

            Hashtable<Integer, Double> scansRetentionTimes = new Hashtable<>();
            Hashtable<Integer, Integer[]> scansIndex = new Hashtable<>();
            for (int i = 0; i < totalScans; i++) {
                Integer scanNum = i;
                Integer[] startAndLength = new Integer[2];
                startAndLength[0] = scanStartPositions[i];
                startAndLength[1] = scanStartPositions[i + 1]
                        - scanStartPositions[i];
                scansRetentionTimes.put(scanNum, retentionTimes[i]);
                scansIndex.put(scanNum, startAndLength);
            }

            List<Integer> indices = new ArrayList<>(scansRetentionTimes.keySet());
            Collections.sort(indices);

            for (int index : indices) {
                int[] scanStartPosition = new int[1];
                int[] scanLength = new int[1];
                Integer[] startAndLength = scansIndex.get(index);

                if (startAndLength == null) {
                    continue;
                }

                scanStartPosition[0] = startAndLength[0];
                scanLength[0] = startAndLength[1];

                retentionTime = scansRetentionTimes.get(index);

                Array massValueArray = massValueVariable.read(scanStartPosition, scanLength);
                Array intensityValueArray = intensityValueVariable.read(scanStartPosition, scanLength);

                ucar.ma2.Index massValuesIndex = massValueArray.getIndex();
                ucar.ma2.Index intensityValuesIndex = intensityValueArray.getIndex();
                int arrayLength = massValueArray.getShape()[0];
                XYList completeDataPoints = new XYList();
                for (int j = 0; j < arrayLength; j++) {
                    ucar.ma2.Index massIndex0 = massValuesIndex.set0(j);
                    ucar.ma2.Index intensityIndex0 = intensityValuesIndex.set0(j);
                    double mz = massValueArray.getDouble(massIndex0)
                            * massValueScaleFactor;
                    double intensity = intensityValueArray.getDouble(intensityIndex0)
                            * intensityValueScaleFactor;
                    completeDataPoints.add(new XYPoint(mz, intensity));

                    if (basePeakIntensity < intensity) {
                        basePeakIntensity = intensity;
                        basePeak = mz;
                    }
                    totalIonCurrent += intensity;
                }

                Constants.ACQUISITION_MODE acquisitionMode = ScanUtils.isCentroided(
                        completeDataPoints) ? Constants.ACQUISITION_MODE.CENTROID : Constants.ACQUISITION_MODE.PROFILE;
                XYList optimizedXYList = ScanUtils.removeZeroAndDuplicateDataPoints(completeDataPoints, acquisitionMode);
                Range mzExtendableRange =
                        new ExtendableRange(optimizedXYList.get(0).x, optimizedXYList.get(optimizedXYList.size() - 1).x);

                if (msn.getLvl() > Constants.MSN.MS1.getLvl()) parentScan = parentScanIndex;
                else parentScanIndex = scanNumber;

                XYList baseXY = new XYList();
                baseXY.add(new XYPoint(basePeak, basePeakIntensity));
                ScanImpl scan =
                        new ScanImpl(scanNumber, msn, ionMode, optimizedXYList, mzExtendableRange, baseXY, retentionTime,
                                totalIonCurrent, parentScan, parentCharge, parentMz);
                scanContainer.addScan(scan);

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

        } catch (IOException | InvalidRangeException ioe) {
            ioe.printStackTrace();
        } finally {
            if (null != ncfile) try {
                ncfile.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        scanContainer.finaliseFile(creationTime);
        return scanContainer;
    }
}
