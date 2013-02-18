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

package uk.ac.ebi.masscascade.io.cml;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLConditionList;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumList;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.RawInfo;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.ScanUtils;

import java.util.List;
import java.util.Map;

/**
 * Class writing mass spec data to a string or file in CML CmlSpectrumFactory.
 */
public class RawSerializer extends ACmlSerializer {

    private static final Logger LOGGER = Logger.getLogger(RawSerializer.class);

    private FileRawContainer msFile;

    /**
     * Constructs a serialization task.
     *
     * @param msFile a mass spectrometry data container
     */
    public RawSerializer(FileRawContainer msFile) {

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

        RawInfo rawInfo = msFile.getRawInfo();
        CMLMetadataList cmlMetadataList = new CMLMetadataList();

        cmlMetadataList.addMetadata(getCMLMetaData(FILENAME, msFile.getId()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILETITLE, rawInfo.getId()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEDATE, rawInfo.getDate()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEOWNER, rawInfo.getAuthors()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEDATA, msFile.getDataFile().getName()));

        return cmlMetadataList;
    }

    /**
     * Gets the spectrum list.
     *
     * @return the spectrum list
     */
    private CMLSpectrumList getSpectrumList() {

        List<RawLevel> rawLevels = msFile.getRawLevels();
        CMLSpectrumList cmlSpectrumList = new CMLSpectrumList();
        Long basePeakNumber = msFile.getBasePeakNumber();
        cmlSpectrumList.setAttribute(BASEPEAK, basePeakNumber + "");

        for (int i = 0; i < rawLevels.size(); i++) {
            RawLevel rawLevel = rawLevels.get(i);

            CMLMetadataList cmlMetadataList = getSpectrumMetadataList(rawLevel);
            cmlSpectrumList.appendChild(cmlMetadataList);

            CMLConditionList cmlConditionList = getSpectrumConditionList(rawLevel);
            cmlSpectrumList.appendChild(cmlConditionList);
        }

        for (int i = 1; i <= rawLevels.size(); i++) {
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
     * @param rawLevel a experimental setup container
     * @return the parsed meta data list
     */
    public CMLMetadataList getSpectrumMetadataList(RawLevel rawLevel) {

        CMLMetadataList cmlMetadataList = new CMLMetadataList();
        cmlMetadataList.setId(rawLevel.getMsn() + "");
        CMLMetadata cmlMetadata = new CMLMetadata();
        if (rawLevel.getAcqusitionMode() != null) {
            cmlMetadataList.appendChild(getCMLMetaData(ACQUISITIONMODE, rawLevel.getAcqusitionMode().name()));
        }

        return cmlMetadataList;
    }

    /**
     * Gets the condition list.
     *
     * @param rawLevel a experimental setup container
     * @return the parsed condition list
     */
    public CMLConditionList getSpectrumConditionList(RawLevel rawLevel) {

        CMLConditionList cmlConditionList = new CMLConditionList();

        cmlConditionList.setId(rawLevel.getMsn() + "");
        cmlConditionList.appendChild(getCMLScalar(IONMODE, STRING, rawLevel.getIonMode().name()));
        cmlConditionList.appendChild(getCMLScalar(MZRANGE, FLOAT,
                rawLevel.getMzRange().getLowerBounds() + "-" + rawLevel.getMzRange().getUpperBounds()));
        cmlConditionList.appendChild(getCMLScalar(SCANRANGE, FLOAT,
                rawLevel.getScanRange().getLowerBounds() + "-" + rawLevel.getScanRange().getUpperBounds()));
        cmlConditionList.appendChild(getCMLScalar(IONENERGY, FLOAT, rawLevel.getFragmentationEnergy()));
        cmlConditionList.appendChild(getCMLScalar(LEVEL, INTEGER, rawLevel.getMsn().getLvl()));

        return cmlConditionList;
    }
}
