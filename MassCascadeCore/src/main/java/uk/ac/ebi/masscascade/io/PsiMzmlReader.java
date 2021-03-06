/*
 * Copyright (C) 2013 EMBL - European Bioinformatics Institute
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
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */

package uk.ac.ebi.masscascade.io;

import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.MzML;
import uk.ac.ebi.jmzml.model.mzml.ParamGroup;
import uk.ac.ebi.jmzml.model.mzml.Precursor;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class for reading PSI mzML files. <ul> <li>Parameter <code> DATA FILE </code>- The data file to be read.</li>
 * <li>Parameter <code> SCAN_CONTAINER </code>- The target scan container.</li> </ul>
 */
public class PsiMzmlReader extends CallableTask {

    private File mzmlFile;
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
     * Constructs a mzML reader task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public PsiMzmlReader(ParameterMap params) throws MassCascadeException {

        super(PsiMzmlReader.class);
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

        mzmlFile = params.get(Parameter.DATA_FILE, File.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);

        if (mzmlFile == null || !mzmlFile.isFile()) throw new MassCascadeException("File not found.");
    }

    /**
     * Parses a Psi '.mzML' file.
     *
     * @return the compiled mass spec sample
     */
    public ScanContainer call() {

        MzMLUnmarshaller mzMLUnmarshaller = new MzMLUnmarshaller(mzmlFile);
        MzML mzml = mzMLUnmarshaller.unmarshall();

        // Meta information
        Calendar creationDate = mzml.getRun().getStartTimeStamp();
        String creationTime = (new Date()).toString();
        if (creationDate != null && creationDate.getTime() != null) {
            creationTime = creationDate.getTime().toString();
        }

        // FeatureSetImpl information
        for (Spectrum spectrum : mzml.getRun().getSpectrumList().getSpectrum()) {

            boolean isSpectrum = false;
            List<BinaryDataArray> bdaList = spectrum.getBinaryDataArrayList().getBinaryDataArray();
            for (BinaryDataArray bda : spectrum.getBinaryDataArrayList().getBinaryDataArray()) {
                for (CVParam cvparam : bda.getCvParam()) {
                    if (cvparam.getName().equals("m/z array")) {
                        isSpectrum = true;
                    }
                }
            }
            if (!isSpectrum) continue;

            scanNumber = spectrum.getIndex();
            for (CVParam cvparam : spectrum.getCvParam()) {
                String name = cvparam.getName();
                if (name.equals("ms level")) {
                    msn = Constants.MSN.get(cvparam.getValue());
                } else if (name.equals("base peak m/z")) {
                    basePeak = Double.parseDouble(cvparam.getValue());
                } else if (name.equals("base peak intensity")) {
                    basePeakIntensity = Double.parseDouble(cvparam.getValue());
                } else if (name.equals("total ion current")) {
                    totalIonCurrent = Double.parseDouble(cvparam.getValue());
                } else if (name.equals("positive scan")) {
                    ionMode = Constants.ION_MODE.POSITIVE;
                } else if (name.equals("negative scan")) {
                    ionMode = Constants.ION_MODE.NEGATIVE;
                }
            }

            for (uk.ac.ebi.jmzml.model.mzml.Scan s : spectrum.getScanList().getScan()) {
                for (CVParam cvparam : s.getCvParam()) {
                    if (cvparam.getName().equals("scan start time")) {
                        retentionTime = Double.parseDouble(cvparam.getValue());
                        if (cvparam.getUnitName().equals("minute")) retentionTime *= 60;
                    }
                }
            }

            if (spectrum.getPrecursorList() != null && spectrum.getPrecursorList().getPrecursor() != null) {
                for (Precursor precursor : spectrum.getPrecursorList().getPrecursor()) {
                    for (ParamGroup group : precursor.getSelectedIonList().getSelectedIon()) {
                        for (CVParam cvParam : group.getCvParam()) {
                            if (cvParam.getName().equals("selected ion m/z"))
                                parentMz = Double.parseDouble(cvParam.getValue());
                            else if (cvParam.getName().equals("charge state"))
                                parentCharge = Integer.parseInt(cvParam.getValue());
                        }
                    }
                }
            }

            List<double[]> dataContainer = new ArrayList<>();
            for (BinaryDataArray bda : bdaList) {
                double[] numberArray = new double[bda.getBinaryDataAsNumberArray().length];
                int i = 0;
                for (Number number : bda.getBinaryDataAsNumberArray()) {
                    numberArray[i] = number.doubleValue();
                    i++;
                }
                dataContainer.add(numberArray);
            }
            XYList xydata = new XYList();
            for (int i = 0; i < dataContainer.get(0).length; i++) {
                xydata.add(new XYPoint(dataContainer.get(0)[i], dataContainer.get(1)[i]));
            }
            Constants.ACQUISITION_MODE acquisitionMode = ScanUtils.isCentroided(
                    xydata) ? Constants.ACQUISITION_MODE.CENTROID : Constants.ACQUISITION_MODE.PROFILE;
            XYList optimizedXYList = ScanUtils.removeZeroAndDuplicateDataPoints(xydata, acquisitionMode);
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
        // wrap up loose ends
        scanContainer.finaliseFile(creationTime);
        return scanContainer;
    }
}
