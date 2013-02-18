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
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumList;
import uk.ac.ebi.masscascade.core.container.file.spectrum.FileSpectrumContainer;

import java.util.Map;

/**
 * Class for CML-based spectra serialization.
 */
public class SpectrumSerializer extends ACmlSerializer {

    private static final Logger LOGGER = Logger.getLogger(SpectrumSerializer.class);

    private FileSpectrumContainer spectrumContainer;

    /**
     * Constructs a serialization task.
     *
     * @param spectrumContainer a mass spectrometry data container
     */
    public SpectrumSerializer(FileSpectrumContainer spectrumContainer) {

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

        return cmlMetadataList;
    }

    /**
     * Gets the spectrum and profile list.
     *
     * @return the spectrum list
     */
    private CMLSpectrumList getSpectrumList() {

        Map<Integer, Long> spectraNumbers = spectrumContainer.getSpectraNumbers();

        CMLSpectrumList cmlSpectrumList = new CMLSpectrumList();

        for (int id : spectraNumbers.keySet()) {

            CMLSpectrum cmlSpectrum = new CMLSpectrum();
            cmlSpectrum.setId("" + id);
            cmlSpectrum.setAttribute(POINTER, spectraNumbers.get(id) + "");

            cmlSpectrumList.appendChild(cmlSpectrum);
        }

        return cmlSpectrumList;
    }
}
