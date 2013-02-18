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

package uk.ac.ebi.masscascade.utilities;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.properties.Isotope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class providing utility functions for profile operations.
 */
public class ProfUtils {

    /**
     * Groups all profiles in the container by retention time using the given tolerance.
     *
     * @return the map containing the retention time-profile groups.
     */
    public static Map<Double, List<Profile>> groupByRetentionTime(double rtTolerance,
            ProfileContainer profileContainer) {

        Map<Double, List<Profile>> rtGroupedprofiles = new TreeMap<Double, List<Profile>>();

        for (Profile profile : profileContainer) {

            double profileListIndex =
                    DataUtils.getNearestIndexAbs(profile.getRetentionTime(), rtTolerance, rtGroupedprofiles.keySet());

            if (profileListIndex == -1) {
                List<Profile> profileList = new ArrayList<Profile>();
                profileList.add(profile);
                rtGroupedprofiles.put(profile.getRetentionTime(), profileList);
            } else rtGroupedprofiles.get(profileListIndex).add(profile);
        }

        return rtGroupedprofiles;
    }

    /**
     * Compiles the profile information string.
     *
     * @param profile a profile
     * @return the information strings
     */
    public static String[] getProfileInfo(Profile profile) {

        String[] info = new String[]{"", "", ""};
        if (profile.hasProperty(PropertyManager.TYPE.Identity)) {

            for (Property prop : profile.getProperty(PropertyManager.TYPE.Identity)) {
                info[0] += prop.toString() + " | ";
            }
        }

        if (profile.hasProperty(PropertyManager.TYPE.Isotope)) {
            Set<Property> isotopeProperties = profile.getProperty(PropertyManager.TYPE.Isotope);

            for (Property prop : isotopeProperties) {
                Isotope isoProp = (Isotope) prop;
                info[1] = info[1] + isoProp.toString() + " | ";
            }
        }

        if (profile.hasProperty(PropertyManager.TYPE.Adduct)) {
            Set<Property> adductProperties = profile.getProperty(PropertyManager.TYPE.Adduct);

            for (Property prop : adductProperties) {
                Adduct adduct = (Adduct) prop;
                info[2] += adduct.toString() + " | ";
            }
        }

        return info;
    }

    /**
     * Compiles the profile information label.
     *
     * @param profile a profile
     * @return the information strings
     */
    public static String getProfileLabel(Profile profile) {

        String label = "";

        if (profile.hasProperty(PropertyManager.TYPE.Identity)) {

            double score = 0;
            Identity identity = null;
            for (Property prop : profile.getProperty(PropertyManager.TYPE.Identity)) {
                if (score < prop.getValue(Double.class)) {
                    score = prop.getValue(Double.class);
                    identity = (Identity) prop;
                }
            }
            label = identity.getName();
            if (profile.hasProperty(PropertyManager.TYPE.Isotope) || profile.hasProperty(PropertyManager.TYPE.Adduct))
                label = label + "*";
        } else if (profile.hasProperty(PropertyManager.TYPE.Isotope)) {
            Set<Property> isotopeProperties = profile.getProperty(PropertyManager.TYPE.Isotope);

            for (Property prop : isotopeProperties) {
                Isotope isoProp = (Isotope) prop;
                label += isoProp.getName() + " | ";
            }
            if (profile.hasProperty(PropertyManager.TYPE.Adduct)) label = label + "*";
        } else if (profile.hasProperty(PropertyManager.TYPE.Adduct)) {
            Set<Property> adductProperties = profile.getProperty(PropertyManager.TYPE.Adduct);

            for (Property prop : adductProperties) {
                Adduct adduct = (Adduct) prop;
                label += adduct.getName() + " | ";
            }
        }

        if (label.lastIndexOf(Constants.SEPARATOR) != -1)
            label = label.substring(0, label.lastIndexOf(Constants.SEPARATOR));

        return label;
    }
}