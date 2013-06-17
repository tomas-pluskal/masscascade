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
import uk.ac.ebi.masscascade.alignment.profilebins.ProfileBinGenerator;
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.comparator.ProfileMassComparator;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompoundSpectrumAdapter {

    private int gid;
    private double missingness;

    public CompoundSpectrumAdapter() {
        gid = 1;
        missingness = 0;
    }

    public void setMissingness(double missingness) {
        this.missingness = missingness;
    }

    public double getMissingness() {
        return missingness;
    }

    public List<CompoundSpectrum> getSpectra(SpectrumContainer... spectraContainer) {

        List<CompoundSpectrum> compoundSpectra = new ArrayList<>();

        int i = 0;
        for (SpectrumContainer spectrumContainer : spectraContainer) {
            for (Spectrum spectrum : spectrumContainer) {
                generateCompoundSpectra(spectrum, i++, compoundSpectra);
            }
        }

        return compoundSpectra;
    }

    public List<CompoundSpectrum> getSpectra(HashMultimap<Integer, Integer> cToPIdMap, int index,
            SpectrumContainer... spectraContainer) {

        List<CompoundSpectrum> compoundSpectra = new ArrayList<>();

        for (SpectrumContainer spectrumContainer : spectraContainer) {
            for (Spectrum spectrum : spectrumContainer) {
                generateCompoundSpectra(spectrum, index, compoundSpectra, cToPIdMap);
            }
        }

        return compoundSpectra;
    }

    private void generateCompoundSpectra(Spectrum spectrum, int spectrumI, List<CompoundSpectrum> compoundSpectra) {
        generateCompoundSpectra(spectrum, spectrumI, compoundSpectra, null);
    }

    private void generateCompoundSpectra(Spectrum spectrum, int spectrumI, List<CompoundSpectrum> compoundSpectra,
            HashMultimap<Integer, Integer> cToPIdMap) {

        List<Profile> profiles = new ArrayList<>(spectrum.getProfileMap().values());
        Collections.sort(profiles, new ProfileMassComparator());

        int mainPeak = 1;

        Map<Integer, Integer> drawIndexToId = new HashMap<>();
        XYList data = new XYList();
        for (Profile profile : profiles) {
            data.add(profile.getMzIntDp());
            drawIndexToId.put(profile.getId(), mainPeak++);
        }

        mainPeak = 1;

        for (Profile profile : profiles) {

            if (cToPIdMap != null) {
                if (!cToPIdMap.containsKey(spectrumI) || !cToPIdMap.get(spectrumI).contains(profile.getId())) continue;
            }

            if (profile.hasProperty(PropertyType.Identity)) {
                CompoundSpectrum compoundSpectrum = new CompoundSpectrum(gid++);
                compoundSpectrum.setMajorPeak(mainPeak);
                compoundSpectrum.setPeakList(data);
                compoundSpectrum.setRetentionTime(profile.getRetentionTime());

                if (profile.hasMsnSpectra(Constants.MSN.MS2)) {
                    XYList msnData = new XYList();
                    for (Profile msnProfile : profile.getMsnSpectra(Constants.MSN.MS2).get(0)) {
                        msnData.add(msnProfile.getMzIntDp());
                    }
                    compoundSpectrum.setPeakList2(msnData);
                }

                for (Identity identity : profile.getProperty(PropertyType.Identity, Identity.class)) {
                    Map<Integer, Identity> identMap = new HashMap<>();
                    identMap.put(mainPeak, identity);
                    CompoundEntity ce =
                            new CompoundEntity(mainPeak, (int) identity.getScore(), identity.getName(), Status.WEAK,
                                    Evidence.MSI_4, identMap, null);
                    compoundSpectrum.addCompound(ce);
                }

                if (profile.hasProperty(PropertyType.Adduct)) {
                    HashMultimap<Integer, Adduct> adductMap = HashMultimap.create();
                    for (Adduct adduct : profile.getProperty(PropertyType.Adduct, Adduct.class)) {
                        if (adduct.getParentId().equals(profile.getId()))
                            adductMap.put(drawIndexToId.get(adduct.getChildId()), adduct);
                        else adductMap.put(drawIndexToId.get(adduct.getParentId()), adduct);
                    }
                    compoundSpectrum.setIndexToAdduct(adductMap);
                }

                if (profile.hasProperty(PropertyType.Isotope)) {
                    Map<Integer, Isotope> isoMap = new HashMap<>();
                    Set<Integer> parentIds = new HashSet<>();
                    for (Isotope isotope : profile.getProperty(PropertyType.Isotope, Isotope.class)) {
                        parentIds.add(isotope.getParentId());
                    }
                    for (int id : parentIds) {
                        for (Isotope isotope : spectrum.getProfile(id).getProperty(PropertyType.Isotope,
                                Isotope.class)) {
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
