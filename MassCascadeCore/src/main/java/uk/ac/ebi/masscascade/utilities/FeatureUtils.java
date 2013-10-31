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

import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.properties.Isotope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class providing utility functions for feature operations.
 */
public class FeatureUtils {

    /**
     * Compiles the feature information string.
     *
     * @param feature a feature
     * @return the information strings
     */
    public static String[] getProfileInfo(Feature feature) {

        String[] info = new String[]{"", "", ""};
        if (feature.hasProperty(PropertyType.Identity)) {

            for (Identity prop : feature.getProperty(PropertyType.Identity, Identity.class)) {
                info[0] += prop.toString() + " | ";
            }
        }

        if (feature.hasProperty(PropertyType.Isotope)) {
            Set<Isotope> isotopeProperties = feature.getProperty(PropertyType.Isotope, Isotope.class);

            for (Property prop : isotopeProperties) {
                Isotope isoProp = (Isotope) prop;
                info[1] = info[1] + isoProp.toString() + " | ";
            }
        }

        if (feature.hasProperty(PropertyType.Adduct)) {
            Set<Adduct> adductProperties = feature.getProperty(PropertyType.Adduct, Adduct.class);

            for (Property prop : adductProperties) {
                Adduct adduct = (Adduct) prop;
                info[2] += adduct.toString() + " | ";
            }
        }

        return info;
    }

    /**
     * Compiles the feature information label.
     *
     * @param feature a feature
     * @return the information strings
     */
    public static String getProfileLabel(Feature feature) {

        String label = "";

        if (feature.hasProperty(PropertyType.Identity)) {

            double score = 0;
            Identity identity = null;
            for (Identity prop : feature.getProperty(PropertyType.Identity, Identity.class)) {
                if (score < prop.getValue(Double.class)) {
                    score = prop.getValue(Double.class);
                    identity = (Identity) prop;
                }
            }
            if (identity != null) label = identity.getName();
            if (feature.hasProperty(PropertyType.Isotope) || feature.hasProperty(
                    PropertyType.Adduct))
                label = label + "*";
        } else if (feature.hasProperty(PropertyType.Isotope)) {
            Set<Isotope> isotopeProperties = feature.getProperty(PropertyType.Isotope, Isotope.class);

            for (Property prop : isotopeProperties) {
                Isotope isoProp = (Isotope) prop;
                label += isoProp.getName() + " | ";
            }
            if (feature.hasProperty(PropertyType.Adduct)) label = label + "*";
        } else if (feature.hasProperty(PropertyType.Adduct)) {
            Set<Adduct> adductProperties = feature.getProperty(PropertyType.Adduct, Adduct.class);

            for (Property prop : adductProperties) {
                Adduct adduct = (Adduct) prop;
                if (!adduct.getParentId().equals(feature.getId()))
                    label += adduct.getName() + " | ";
            }
        }

        if (label.lastIndexOf(Constants.SEPARATOR) != -1)
            label = label.substring(0, label.lastIndexOf(Constants.SEPARATOR));

        return label;
    }

    /**
     * Groups a set of identities by name.
     *
     * @param properties the set of identities
     * @return the grouped list
     */
    public static List<Identity> getGroupedIdentities(Set<? extends Property> properties) {

        Map<String, Identity> nameToIdentity = new HashMap<>();

        for (Property property : properties) {
            if (!(property instanceof Identity)) continue;
            Identity identity = (Identity) property;

            String name = identity.getName();
            if (nameToIdentity.containsKey(name)) {
                Identity existingIdentity = nameToIdentity.get(name);

                String id = existingIdentity.getId() + Constants.DELIMITER + identity.getId();
                if (existingIdentity.getScore() < identity.getScore()) identity =
                        new Identity(id, name, identity.getNotation(), identity.getScore(), identity.getSource(),
                                identity.getEvidence(), identity.getComments());
                else identity = new Identity(id, name, existingIdentity.getNotation(), existingIdentity.getScore(),
                        existingIdentity.getSource(), existingIdentity.getEvidence(), existingIdentity.getComments());
                nameToIdentity.put(identity.getName(), identity);
            } else {
                nameToIdentity.put(identity.getName(), identity);
            }
        }

        return new ArrayList<>(nameToIdentity.values());
    }
}