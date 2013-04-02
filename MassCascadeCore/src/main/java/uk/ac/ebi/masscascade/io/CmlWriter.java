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

import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumData;
import org.xmlcml.cml.element.CMLSpectrumList;
import org.xmlcml.cml.element.CMLXaxis;
import org.xmlcml.cml.element.CMLYaxis;
import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.RawInfo;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.CmlUtils;
import uk.ac.ebi.masscascade.utilities.ScanUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Class for writing CML files following the 'msnmetabolomics' convention.
 * <ul>
 * <li>Parameter <code> OUTPUT DIRECTORY </code>- The output directory path.</li>
 * <li>Parameter <code> RAW FILE </code>- The raw container to be saved.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The profile container to be saved.</li>
 * </ul>
 */
@Deprecated
public class CmlWriter extends CallableTask {

    private FileRawContainer rawContainer;
    private ProfileContainer peakContainer;
    private CMLCml rootCML;
    private String path;

    /**
     * Constructs a custom CML writer task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public CmlWriter(ParameterMap params) throws MassCascadeException {

        super(CmlWriter.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the CML writer.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        path = params.get(Parameter.OUTPUT_DIRECTORY, String.class);

        if (params.containsKey(Parameter.RAW_CONTAINER)) rawContainer = params.get(Parameter.RAW_CONTAINER, FileRawContainer.class);

        if (params.containsKey(Parameter.PROFILE_CONTAINER))
            peakContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
    }

    /**
     * Wraps the spectral information in CML.
     *
     * @return the cml container
     */
    @Override
    public Container call() {

        // HEADER
        rootCML = CmlUtils.getHeader();

        // META DATA
        CMLMetadataList metadataList = getMetadataList();

        // PARAMETERS

        // SAMPLE

        // SPECTRA
        CMLSpectrumList spectrumList = getSpectrumList();

        // PEAKS

        // MOLECULES

        // append to root
        rootCML.appendChild(metadataList);
        rootCML.appendChild(spectrumList);

        // write out
        String fileName = (rawContainer.getId() + ".cml");
        try {
            Writer writer = new BufferedWriter(new FileWriter(path + File.separator + fileName));
            writer.write(CmlUtils.toFormattedString(rootCML.toXML(), 2));
            writer.flush();
            writer.close();

            rootCML = null;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the meta data list.
     *
     * @return the meta data list
     */
    private CMLMetadataList getMetadataList() {

        RawInfo rawInfo = rawContainer.getRawInfo();
        CMLMetadataList cmlMetadataList = new CMLMetadataList();

        CMLMetadata metadata = new CMLMetadata();
        metadata.setName("mzml:fileName");
        metadata.setContent(rawInfo.getId());
        cmlMetadataList.addMetadata(metadata);

        metadata = new CMLMetadata();
        metadata.setName("mzml:fileType");
        metadata.setContent(rawInfo.getId());
        cmlMetadataList.addMetadata(metadata);

        metadata = new CMLMetadata();
        metadata.setName("cml:date");
        metadata.setContent(rawInfo.getDate());
        cmlMetadataList.addMetadata(metadata);

        metadata = new CMLMetadata();
        metadata.setName("owner");
        metadata.setContent(rawInfo.getAuthors());
        cmlMetadataList.addMetadata(metadata);

        return cmlMetadataList;
    }

    /**
     * Gets the spectrum list.
     *
     * @return the spectrum list
     */
    private CMLSpectrumList getSpectrumList() {

        List<RawLevel> rawLevels = rawContainer.getRawLevels();
        CMLSpectrumList cmlSpectrumList = new CMLSpectrumList();

        Scan scan;
        for (RawLevel rawLevel : rawLevels) {

            for (int value : rawContainer.getScanNumbers(rawLevel.getMsn()).keySet()) {

                scan = rawContainer.getScan(value);

                CMLMetadataList cmlMetadataList = getSpectrumMetadataList(rawLevel, scan);

                CMLSpectrumData cmlSpectrumData = new CMLSpectrumData();

                CMLXaxis cmlXaxis = new CMLXaxis();

                CMLArray cmlXarray = new CMLArray();
                int x = ScanUtils.set64BitFloatArrayAsBinaryData(scan.getData().getXs(), true, cmlXarray);
                cmlXarray.setAttribute("size", "" + x);
                cmlXarray.setAttribute("units", "units:mz");
                cmlXarray.setAttribute("dataType", "xsd:base64Binary");
                cmlXarray.removeAttribute("delimiter");
                cmlXaxis.addArray(cmlXarray);
                cmlSpectrumData.addXaxis(cmlXaxis);

                CMLYaxis cmlYaxis = new CMLYaxis();
                CMLArray cmlYarray = new CMLArray();
                int y = ScanUtils.set64BitFloatArrayAsBinaryData(scan.getData().getYs(), true, cmlYarray);
                cmlYarray.setAttribute("size", "" + y);
                cmlYarray.setAttribute("units", "units:cps");
                cmlYarray.setAttribute("dataType", "xsd:base64Binary");
                cmlYarray.removeAttribute("delimiter");
                cmlYaxis.addArray(cmlYarray);
                cmlSpectrumData.addYaxis(cmlYaxis);

                CMLSpectrum cmlSpectrum = new CMLSpectrum();
                cmlSpectrum.setAttribute("id", "spectrum" + scan.getIndex());
                cmlSpectrum.addMetadataList(cmlMetadataList);
                cmlSpectrum.addSpectrumData(cmlSpectrumData);

                cmlSpectrumList.appendChild(cmlSpectrum);

                scan = null;
            }
        }

        return cmlSpectrumList;
    }

    /**
     * Gets the meta data list.
     *
     * @param rawLevel the MSn information container
     * @return the parsed meta data list
     */
    public CMLMetadataList getSpectrumMetadataList(RawLevel rawLevel, Scan scan) {

        CMLMetadataList cmlMetadataList = new CMLMetadataList();
        CMLMetadata cmlMetadata = new CMLMetadata();

        if (rawLevel.getAcqusitionMode() != null) {
            cmlMetadata.setName("mzml:centroided");
            if (rawLevel.getAcqusitionMode().equals(Constants.ACQUISITION_MODE.CENTROID)) {
                cmlMetadata.setContent("" + true);
            } else {
                cmlMetadata.setContent("" + false);
            }

            cmlMetadataList.appendChild(cmlMetadata);
        }

        this.addMetadata("mzml:retentionTime", "" + scan.getRetentionTime(), cmlMetadataList);
        this.addMetadata("mzml:msIonisation", rawLevel.getIonMode().name(), cmlMetadataList);
        this.addMetadata("mzml:lowMz", "" + scan.getMzRange().getLowerBounds(), cmlMetadataList);
        this.addMetadata("mzml:highMz", "" + scan.getMzRange().getUpperBounds(), cmlMetadataList);
        this.addMetadata("mzml:ionisationEnergy", "" + rawLevel.getFragmentationEnergy(), cmlMetadataList);
        this.addMetadata("mzml:msLevel", "" + rawLevel.getMsn(), cmlMetadataList);
        this.addMetadata("mzml:basePeakMz", "" + scan.getBasePeak().get(0).x, cmlMetadataList);
        this.addMetadata("mzml:basePeakIntensity", "" + scan.getBasePeak().get(0).y, cmlMetadataList);
        this.addMetadata("mzml:totalIonCurrent", "" + scan.getTotalIonCurrent(), cmlMetadataList);

        if (scan.getParentScan() != -1) {
            this.addMetadata("mzml:precursorCharge", "" + scan.getParentCharge(), cmlMetadataList);
            this.addMetadata("mzml:precursorScanNum", "" + scan.getParentScan(), cmlMetadataList);
            this.addMetadata("mzml:precursorMz", "" + scan.getParentMz(), cmlMetadataList);
        }

        return cmlMetadataList;
    }

    private void addMetadata(String name, String content, CMLMetadataList cmlMetadataList) {

        CMLMetadata cmlMetadata = new CMLMetadata();
        cmlMetadata.setName(name);
        cmlMetadata.setContent(content);
        cmlMetadataList.appendChild(cmlMetadata);
    }
}
