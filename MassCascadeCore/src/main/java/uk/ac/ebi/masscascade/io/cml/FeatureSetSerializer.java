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
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumList;
import uk.ac.ebi.masscascade.core.container.file.featureset.FileFeatureSetContainer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Map;

/**
 * Class for CML-based spectra serialization.
 */
public class FeatureSetSerializer extends ACmlSerializer {

    private static final Logger LOGGER = Logger.getLogger(FeatureSetSerializer.class);

    private FileFeatureSetContainer spectrumContainer;

    /**
     * Constructs a serialization task.
     *
     * @param spectrumContainer a mass spectrometry data container
     */
    public FeatureSetSerializer(FileFeatureSetContainer spectrumContainer) {
        this.spectrumContainer = spectrumContainer;
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

        CMLMetadataList cmlMetadataList = new CMLMetadataList();

        cmlMetadataList.addMetadata(getCMLMetaData(FILENAME, spectrumContainer.getId()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEDATA, spectrumContainer.getDataFile().getName()));
        cmlMetadataList.addMetadata(getCMLMetaData(IONMODE, spectrumContainer.getIonMode().name()));

        return cmlMetadataList;
    }

    /**
     * Gets the featureset and feature list.
     *
     * @return the featureset list
     */
    private CMLSpectrumList getSpectrumList() {

        Map<Integer, Long> spectraNumbers = spectrumContainer.getFeatureNumbers();

        CMLSpectrumList cmlSpectrumList = new CMLSpectrumList();

        int i = 0;
        for (int id : spectraNumbers.keySet()) {

            CMLSpectrum cmlSpectrum = new CMLSpectrum();
            cmlSpectrum.setId("" + id);
            cmlSpectrum.setAttribute(POINTER, spectraNumbers.get(id) + "");
            XYPoint basePeak = spectrumContainer.getBasePeaks().get(i++);
            cmlSpectrum.setAttribute("x", basePeak.x + "");
            cmlSpectrum.setAttribute("y", basePeak.y + "");

            cmlSpectrumList.appendChild(cmlSpectrum);
        }

        return cmlSpectrumList;
    }
}
