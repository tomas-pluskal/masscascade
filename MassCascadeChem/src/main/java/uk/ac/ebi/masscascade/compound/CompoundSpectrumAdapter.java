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

package uk.ac.ebi.masscascade.compound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.comparator.FeatureMassComparator;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts a list of pseudo spectra into a list of compound spectra.
 * <p/>
 * For every identity annotation in a pseudo spectra for a single peak, a compound featureset is generated.
 */
public class CompoundSpectrumAdapter {

    private int gid;
    private int startingScore;

    public CompoundSpectrumAdapter() {
        this(0);
    }

    /**
     * Constructs a new adapter with an initial minimum score for new compound entities that are assigned to compound
     * spectra.
     *
     * @param startingScore the initial starting score
     */
    public CompoundSpectrumAdapter(int startingScore) {

        gid = 1;
        this.startingScore = startingScore;
    }

    /**
     * Takes a list of featureset containers and returns a list of compound spectra.
     *
     * @param spectraContainer the input list of featureset containers
     * @return the converted list of compound spectra
     */
    public List<CompoundSpectrum> getSpectra(FeatureSetContainer... spectraContainer) {

        List<CompoundSpectrum> compoundSpectra = new ArrayList<>();

        int i = 0;
        for (FeatureSetContainer featureSetContainer : spectraContainer) {
            for (FeatureSet featureSet : featureSetContainer) {
                generateCompoundSpectra(featureSet, i++, compoundSpectra);
            }
        }

        return compoundSpectra;
    }

    /**
     * Takes a list of featureset containers and returns a list of compound spectra.
     *
     * @param cToPIdMap        the container to feature id map
     * @param index            the index of the current container
     * @param spectraContainer the input list of featureset containers
     * @return the converted list of compound spectra
     */
    public List<CompoundSpectrum> getSpectra(HashMultimap<Integer, Integer> cToPIdMap, int index,
                                             FeatureSetContainer... spectraContainer) {

        List<CompoundSpectrum> compoundSpectra = new ArrayList<>();

        for (FeatureSetContainer featureSetContainer : spectraContainer) {
            for (FeatureSet featureSet : featureSetContainer) {
                generateCompoundSpectra(featureSet, index, compoundSpectra, cToPIdMap);
            }
        }

        return compoundSpectra;
    }

    private void generateCompoundSpectra(FeatureSet featureSet, int containerI, List<CompoundSpectrum> compoundSpectra) {
        generateCompoundSpectra(featureSet, containerI, compoundSpectra, null);
    }

    private void generateCompoundSpectra(FeatureSet featureSet, int containerI, List<CompoundSpectrum> compoundSpectra,
                                         HashMultimap<Integer, Integer> cToPIdMap) {

        List<Feature> features = new ArrayList<>(featureSet.getFeaturesMap().values());
        Collections.sort(features, new FeatureMassComparator());

        int mainPeak = 1;

        Map<Integer, Integer> drawIndexToId = new HashMap<>();
        XYList data = new XYList();
        for (Feature feature : features) {
            if (cToPIdMap != null) {
                if (!cToPIdMap.containsKey(containerI) || !cToPIdMap.get(containerI).contains(feature.getId()))
                    continue;
            }
            data.add(feature.getMzIntDp());
            drawIndexToId.put(feature.getId(), mainPeak++);
        }

        mainPeak = 1;

        for (Feature feature : features) {

            if (cToPIdMap != null) {
                if (!cToPIdMap.containsKey(containerI) || !cToPIdMap.get(containerI).contains(feature.getId()))
                    continue;
            }

            if (feature.hasProperty(PropertyType.Identity)) {
                CompoundSpectrum compoundSpectrum = new CompoundSpectrum(gid++);
                compoundSpectrum.setMajorPeak(mainPeak);
                compoundSpectrum.setPeakList(data);
                compoundSpectrum.setRetentionTime(feature.getRetentionTime());

                Map<String, Multimap<Integer, Identity>> msnAssociations = new HashMap<>();
                if (feature.hasMsnSpectra(Constants.MSN.MS2)) {
                    int msnIndex = 1;
                    XYList msnData = new XYList();
                    for (Feature msnFeature : feature.getMsnSpectra(Constants.MSN.MS2).get(0)) {
                        msnData.add(msnFeature.getMzIntDp());
                        if (msnFeature.hasProperty(PropertyType.Identity)) {
                            for (Identity msnIdentity : msnFeature.getProperty(PropertyType.Identity, Identity.class)) {
                                String parentId = msnIdentity.getSource();
                                if (msnAssociations.containsKey(parentId)) {
                                    msnAssociations.get(parentId).put(msnIndex, msnIdentity);
                                } else {
                                    Multimap<Integer, Identity> msnIdToIdent = HashMultimap.create();
                                    msnIdToIdent.put(msnIndex, msnIdentity);
                                    msnAssociations.put(parentId, msnIdToIdent);
                                }
                            }
                        }
                        msnIndex++;
                    }
                    compoundSpectrum.setPeakList2(msnData);
                }

                for (Identity identity : feature.getProperty(PropertyType.Identity, Identity.class)) {
                    Map<Integer, Identity> identMap = new HashMap<>();
                    identMap.put(mainPeak, identity);
                    CompoundEntity ce =
                            new CompoundEntity(mainPeak, startingScore, identity.getName(), Status.WEAK,
                                    Evidence.MSI_3, identMap, msnAssociations.get(identity.getId()));
                    compoundSpectrum.addCompound(ce);
                }

                if (feature.hasProperty(PropertyType.Adduct)) {
                    HashMultimap<Integer, Adduct> adductMap = HashMultimap.create();
                    for (Adduct adduct : feature.getProperty(PropertyType.Adduct, Adduct.class)) {
                        if (drawIndexToId.get(adduct.getChildId()) == null || drawIndexToId.get(adduct.getParentId()) == null) {
                            continue;
                        }
                        if (adduct.getParentId().equals(feature.getId()))
                            adductMap.put(drawIndexToId.get(adduct.getChildId()), adduct);
                        else adductMap.put(drawIndexToId.get(adduct.getParentId()), adduct);
                    }
                    compoundSpectrum.setIndexToAdduct(adductMap);
                }

                if (feature.hasProperty(PropertyType.Isotope)) {
                    Map<Integer, Isotope> isoMap = new HashMap<>();
                    Set<Integer> parentIds = new HashSet<>();
                    for (Isotope isotope : feature.getProperty(PropertyType.Isotope, Isotope.class)) {
                        parentIds.add(isotope.getParentId());
                    }
                    for (int id : parentIds) {
                        for (Isotope isotope : featureSet.getFeature(id).getProperty(PropertyType.Isotope,
                                Isotope.class)) {
                            if (drawIndexToId.get(isotope.getChildId()) == null) {
                                continue;
                            }
                            isoMap.put(drawIndexToId.get(isotope.getChildId()), isotope);
                        }
                    }
                    compoundSpectrum.setIndexToIsotope(isoMap);
                }

                compoundSpectra.add(compoundSpectrum);
            }
            mainPeak++;
        }
    }
}
