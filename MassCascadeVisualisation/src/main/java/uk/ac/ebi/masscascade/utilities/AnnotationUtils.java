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

package uk.ac.ebi.masscascade.utilities;

import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities class to facilitate data point annotation.
 */
public class AnnotationUtils {

    /**
     * Gets the annotations from the list of peaks.
     *
     * @param peakList the annotated list of peaks
     * @return the annotation map: data point - annotation
     */
    public static Map<XYPoint, String> getAnnotations(Collection<Profile> peakList) {

        Map<XYPoint, String> annotations = new HashMap<>();

        String annotation = "";
        for (Profile peak : peakList) {
            annotation = ProfUtils.getProfileLabel(peak);
            if (!annotation.isEmpty()) annotations.put(peak.getMzIntDp(), annotation);
        }

        return annotations;
    }
}
