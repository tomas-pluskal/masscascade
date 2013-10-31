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

package uk.ac.ebi.masscascade.core;

import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for Feature-based MSn handling.
 */
public class MsnManager {

    private Map<Constants.MSN, Set<Integer>> msnToScanIds;
    private Map<Constants.MSN, List<FeatureSet>> msnToSpectra;

    public MsnManager() {

        msnToScanIds = new HashMap<>();
        msnToSpectra = new HashMap<>();
    }

    public void addMsnToScanIds(Map<Constants.MSN, Set<Integer>> msnToScanIds) {
        this.msnToScanIds.putAll(msnToScanIds);
    }

    public void addMsnToSpectra(Map<Constants.MSN, List<FeatureSet>> msnToFeatures) {
        this.msnToSpectra.putAll(msnToFeatures);
    }

    public void addMsnSpectrum(Constants.MSN msn, FeatureSet featureSet) {

        if (msnToSpectra.containsKey(msn)) {
            msnToSpectra.get(msn).add(featureSet);
        } else {
            List<FeatureSet> spectra = new ArrayList<>();
            spectra.add(featureSet);
            msnToSpectra.put(msn, spectra);
        }
    }

    public Map<Constants.MSN, Set<Integer>> getMsnToScanIds() {
        return msnToScanIds;
    }

    public Map<Constants.MSN, List<FeatureSet>> getMsnToSpectra() {
        return msnToSpectra;
    }

    public List<FeatureSet> getSpectra(Constants.MSN msn) {
        List<FeatureSet> spectra = msnToSpectra.get(msn);
        return (spectra == null) ? new ArrayList<FeatureSet>() : spectra;
    }
}
