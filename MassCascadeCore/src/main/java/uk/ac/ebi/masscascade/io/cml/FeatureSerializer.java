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

import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumList;
import uk.ac.ebi.masscascade.core.container.file.feature.FileFeatureContainer;

import java.util.Map;

/**
 * Class for CML-based feature serialization.
 */
public class FeatureSerializer extends ACmlSerializer {

    private static final Logger LOGGER = Logger.getLogger(FeatureSerializer.class);

    private FileFeatureContainer profileContainer;

    /**
     * Constructs a serialization task.
     *
     * @param profileContainer a mass spectrometry data container
     */
    public FeatureSerializer(FileFeatureContainer profileContainer) {

        this.profileContainer = profileContainer;
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

        cmlMetadataList.addMetadata(getCMLMetaData(FILENAME, profileContainer.getId()));
        cmlMetadataList.addMetadata(getCMLMetaData(FILEDATA, profileContainer.getDataFile().getName()));
        cmlMetadataList.addMetadata(getCMLMetaData(IONMODE, profileContainer.getIonMode().name()));

        return cmlMetadataList;
    }

    /**
     * Gets the feature set and feature list.
     *
     * @return the feature set list
     */
    private CMLSpectrumList getSpectrumList() {

        Map<Integer, Long> peakNumbers = profileContainer.getFeatureNumbers();
        TreeMultimap<Double, Integer> peakTimes = profileContainer.getTimes();

        CMLSpectrumList cmlSpectrumList = new CMLSpectrumList();

        int i = 0;
        for (double rt : peakTimes.keySet()) {

            CMLSpectrum cmlSpectrum = new CMLSpectrum();
            cmlSpectrum.setId("" + i);
            cmlSpectrum.setAttribute("rt", "" + rt);

            CMLPeakList cmlPeakList = new CMLPeakList();

            for (int peakNumber : peakTimes.get(rt)) {

                CMLPeak cmlPeak = new CMLPeak();
                cmlPeak.setId("" + peakNumber);
                cmlPeak.setAttribute(POINTER, peakNumbers.get(peakNumber) + "");

                cmlPeakList.addPeak(cmlPeak);
            }

            cmlSpectrum.appendChild(cmlPeakList);
            cmlSpectrumList.appendChild(cmlSpectrum);
            i++;
        }

        return cmlSpectrumList;
    }
}
