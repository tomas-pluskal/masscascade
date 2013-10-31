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

package uk.ac.ebi.masscascade.io.cml;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLConditionList;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumList;
import uk.ac.ebi.masscascade.core.container.file.scan.FileScanContainer;
import uk.ac.ebi.masscascade.core.scan.ScanInfo;
import uk.ac.ebi.masscascade.core.scan.ScanLevel;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.ScanUtils;

import java.util.List;
import java.util.Map;

/**
 * Class writing mass spec data to a string or file in CML CmlSpectrumFactory.
 */
public class ScanSerializer extends ACmlSerializer {

    private static final Logger LOGGER = Logger.getLogger(ScanSerializer.class);

    private FileScanContainer msFile;

    /**
     * Constructs a serialization task.
     *
     * @param msFile a mass spectrometry data container
     */
    public ScanSerializer(FileScanContainer msFile) {

        this.msFile = msFile;
    }

    /**
     * Wraps the spectral information in CML.
     *
     * @return the cml container
     */
    public CMLCml getCml() {

        rootCML = getHeader();
        CMLMetadataList metadataList = getMetadataList();
        CMLSpectrumList spectrumList = getSpectrumList();

        rootCML.appendChild(metadataList);
        rootCML.appendChild(spectrumList);

        return rootCML;
    }

    /**
     * Gets the meta data list.
     *
     * @return the meta data list
     */
    private CMLMetadataList getMetadataList() {

        ScanInfo scanInfo = msFile.getScanInfo();
        CMLMetadataList cmlMetadataList = new CMLMetadataList();

        cmlMetadataList.addMetadata(getCMLMetaData(FILENAME, msFile.getId()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILETITLE, scanInfo.getId()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEDATE, scanInfo.getDate()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEOWNER, scanInfo.getAuthors()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEDATA, msFile.getDataFile().getName()));

        return cmlMetadataList;
    }

    /**
     * Gets the featureset list.
     *
     * @return the featureset list
     */
    private CMLSpectrumList getSpectrumList() {

        List<ScanLevel> scanLevels = msFile.getScanLevels();
        CMLSpectrumList cmlSpectrumList = new CMLSpectrumList();
        Long basePeakNumber = msFile.getBasePeakNumber();
        cmlSpectrumList.setAttribute(BASEPEAK, basePeakNumber + "");

        for (int i = 0; i < scanLevels.size(); i++) {
            ScanLevel scanLevel = scanLevels.get(i);

            CMLMetadataList cmlMetadataList = getSpectrumMetadataList(scanLevel);
            cmlSpectrumList.appendChild(cmlMetadataList);

            CMLConditionList cmlConditionList = getSpectrumConditionList(scanLevel);
            cmlSpectrumList.appendChild(cmlConditionList);
        }

        for (int i = 1; i <= scanLevels.size(); i++) {
            Map<Integer, Long> scanList = msFile.getScanNumbers(uk.ac.ebi.masscascade.parameters.Constants.MSN.get(i));
            Long ticNumber = msFile.getTicNumber(Constants.MSN.get(i));

            CMLSpectrum cmlSpectrum = new CMLSpectrum();
            cmlSpectrum.setId("" + i);
            cmlSpectrum.setAttribute("tic", ticNumber + "");

            Long[] values = new Long[scanList.size() * 2];
            int j = 0;
            for (int value : scanList.keySet()) {
                values[j] = (long) value;
                values[j + 1] = scanList.get(value);
                j += 2;
            }
            int size = ScanUtils.set64BitLongArrayAsBinaryData(values, true, cmlSpectrum);
            cmlSpectrum.setAttribute("size", size + "");
            cmlSpectrum.setAttribute("dataType", BINARY32);

            cmlSpectrumList.appendChild(cmlSpectrum);
        }

        return cmlSpectrumList;
    }

    /**
     * Gets the meta data list.
     *
     * @param scanLevel a experimental setup container
     * @return the parsed meta data list
     */
    public CMLMetadataList getSpectrumMetadataList(ScanLevel scanLevel) {

        CMLMetadataList cmlMetadataList = new CMLMetadataList();
        cmlMetadataList.setId(scanLevel.getMsn() + "");
        if (scanLevel.getAcqusitionMode() != null) {
            cmlMetadataList.appendChild(getCMLMetaData(ACQUISITIONMODE, scanLevel.getAcqusitionMode().name()));
        }

        return cmlMetadataList;
    }

    /**
     * Gets the condition list.
     *
     * @param scanLevel a experimental setup container
     * @return the parsed condition list
     */
    public CMLConditionList getSpectrumConditionList(ScanLevel scanLevel) {

        CMLConditionList cmlConditionList = new CMLConditionList();

        cmlConditionList.setId(scanLevel.getMsn() + "");
        cmlConditionList.appendChild(getCMLScalar(IONMODE, STRING, scanLevel.getIonMode().name()));
        cmlConditionList.appendChild(getCMLScalar(MZRANGE, FLOAT,
                scanLevel.getMzRange().getLowerBounds() + "-" + scanLevel.getMzRange().getUpperBounds()));
        cmlConditionList.appendChild(getCMLScalar(SCANRANGE, FLOAT,
                scanLevel.getScanRange().getLowerBounds() + "-" + scanLevel.getScanRange().getUpperBounds()));
        cmlConditionList.appendChild(getCMLScalar(IONENERGY, FLOAT, scanLevel.getFragmentationEnergy()));
        cmlConditionList.appendChild(getCMLScalar(LEVEL, INTEGER, scanLevel.getMsn().getLvl()));

        return cmlConditionList;
    }
}
