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

package uk.ac.ebi.masscascade.identification;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.comparator.FeatureMassComparator;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing an isotope search method for a list of features. This class does not use external dependencies.
 */
public class IsotopeDetectorRec {

    private static final Logger LOGGER = Logger.getLogger(IsotopeDetectorRec.class);

    // describe isotope intensities for masses between 0 and 900 for the major isotopes with distance 1-3
    // x values are masses divided by 100
    private Map<Integer, LinearEquation> isotopeToEquation = new HashMap<Integer, LinearEquation>() {

        {
            put(1, new LinearEquation(0.006359, -0.001681));
            put(2, new LinearEquation(0.0009969, -0.0068281));
            put(3, new LinearEquation(0.0001437, -0.0016528));
        }
    };

    // isotope intensity tolerance values relative to 1
    private Map<Integer, Double> isotopeToTolerance = new HashMap<Integer, Double>() {

        {
            put(1, 0.50);
            put(2, 0.90);
            put(3, 0.90);
        }
    };

    // bin size used to infer the functions above
    private double BIN_SIZE = 10d;
    // default minimum isotope array size
    private int NUM_ISOTOPES = 3;

    /**
     * Reference: "The isotopic Mass Defect", E. Thurman, et al.
     * Mass defect: +/- 0.003 u
     */
    private static final double ISOTOPE_DIFFERENCE = 1.0033;

    private final double ppm;
    private final int charge;

    /**
     * Constructor for the isotope detector.
     *
     * @param charge the maximum charge possible
     */
    public IsotopeDetectorRec(int charge, double massTolerance) {

        this.ppm = massTolerance;
        this.charge = charge;
    }

    /**
     * Finds identification within the charge range and sets any detected identification as properties in the feature
     * object.
     */
    public void findIsotopes(final FeatureSet pseudoFeatureSet) {

        List<Feature> features = new ArrayList<>(pseudoFeatureSet.getFeaturesMap().values());
        Collections.sort(features, new FeatureMassComparator());

        Set<Integer> skipSet = new HashSet<>();

        for (int c = 1; c <= charge; c++)
            traverse(features, 0, c, skipSet);
    }

    /**
     * Traverses over all features and builds isotopic envelopes.
     *
     * @param features the feature list
     * @param p        the parent id
     * @param c        the current charge
     * @param skipSet  ids of already annotated features
     */
    private void traverse(List<Feature> features, int p, int c, Set<Integer> skipSet) {

        if (p == features.size()) return;
        else if (skipSet.contains(p)) { // skip feature is already annotated
            traverse(features, ++p, c, skipSet);
            return;
        }

        List<Integer[]> paths = new ArrayList<>();
        // build isotopic envelope for this feature
        build(features, paths, p, 0, 0, c);
        resolve(features, paths, p, c, skipSet);
        // move to next feature
        traverse(features, ++p, c, skipSet);
    }

    /**
     * Builds all possible isotopic envelopes for a given feature
     *
     * @param features the feature list
     * @param paths    all isotope envelopes described as paths with feature ids
     * @param p        the parent id
     * @param d        the distance to the parent
     * @param pi       the daughter id
     * @param c        the current charge
     */
    private void build(List<Feature> features, List<Integer[]> paths, int p, int d, int pi, int c) {

        for (int cp = p + 1; cp < features.size(); cp++) {

            double nmz = features.get(cp).getMz();
            double ndelta = nmz * ppm / Constants.PPM;

            double pmz = features.get(p).getMz();
            double ul = pmz + ISOTOPE_DIFFERENCE / c + ndelta;
            double ll = pmz + ISOTOPE_DIFFERENCE / c - ndelta;

            double r = features.get(cp).getIntensity() / features.get(p).getIntensity();

            if (nmz >= ul) {
                break;
            } else if (isPotentialIsotope(ll, ul, nmz, r, c)) {

                if (paths.size() == pi) paths.add(new Integer[NUM_ISOTOPES]);

                if (d == paths.get(pi).length) paths.set(pi, Arrays.copyOf(paths.get(pi), d * 2)); // grow isotope array
                if (d != 0 && paths.get(pi)[0] == null)
                    paths.set(pi, paths.get(pi - 1).clone()); // copy and add isotope array if path branches
                paths.get(pi)[d] = cp;
                build(features, paths, cp, ++d, pi++, c); // continue to build isotopic envelope
                d--;
            }
        }
    }

    /**
     * Return whether the two signals can be considered isotopes based on m/z difference and signal intensity.
     *
     * @param ll  the lower m/z limit
     * @param ul  the upper m/z limit
     * @param nmz the child m/z
     * @param r   the child to parent intensity ratio
     * @param c   the current charge
     * @return whether the two signals are potential isotopes
     */
    private boolean isPotentialIsotope(double ll, double ul, double nmz, double r, int c) {

        if (ll <= nmz && nmz < ul) {
            double rT = isotopeToEquation.get(c).getY(nmz / BIN_SIZE);
            return (r >= rT - (rT * isotopeToTolerance.get(c)) && r < rT + (rT * isotopeToTolerance.get(c)));
        }

        return false;
    }

    /**
     * Finds the most likely isotopic envelope from an array of isotope paths.
     *
     * @param features the feature list
     * @param paths    all isotope envelopes described as paths with feature ids
     * @param p        the parent id
     * @param c        the current charge
     * @param skipSet  ids of already annotated features
     */
    private void resolve(List<Feature> features, List<Integer[]> paths, int p, int c, Set<Integer> skipSet) {

        if (paths.size() == 0 || paths.get(0)[0] == null) {}
        else if (paths.size() == 1) annotate(features, paths.get(0), p, skipSet); // only one isotopic envelope
        else { // multiple isotopic envelopes
            int max = 0;
            // (1) find longest path
            for (Integer[] path : paths) {
                if (path.length >= max) max = path.length;
            }
            Iterator<Integer[]> iter = paths.iterator();
            while (iter.hasNext()) {
                Integer[] path = iter.next();
                for (Integer i : path) {
                    if (i == null) break;
                    else if (path.length < max) iter.remove();
                }
            }
            // if only one path left: annotate
            if (paths.size() == 1) annotate(features, paths.get(0), p, skipSet);
            else { // (2) select best path by total min m/z deviation
                Integer[] bestPath = null;
                double maxDevSum = Double.MAX_VALUE;
                for (Integer[] path : paths) {
                    double pmz = features.get(p).getMz();
                    double devSum = 0;
                    for (Integer i : path) {
                        if (i == null) break;
                        double nmz = features.get(i).getMz();
                        devSum += FastMath.abs(nmz - pmz - ISOTOPE_DIFFERENCE);
                        pmz = nmz;
                    }
                    if (devSum < maxDevSum) {
                        maxDevSum = devSum;
                        bestPath = path;
                    }
                }

                annotate(features, bestPath, p, skipSet);
            }
        }
    }

    /**
     * Annotates the features with isotope information from the envelope.
     *
     * @param features the feature list
     * @param path     the best isotopic envelope path
     * @param p        the parent id
     * @param skipSet  ids of already annotated features
     */
    private void annotate(List<Feature> features, Integer[] path, int p, Set<Integer> skipSet) {

        int pid = features.get(p).getId();
        int depth = 0;
        features.get(p).setProperty(new Isotope("M", depth++, pid, pid));
        skipSet.add(p);

        for (Integer pi : path) {
            if (pi == null) break;
            features.get(p).setProperty(new Isotope("M+" + depth, depth, pid, features.get(pi).getId()));
            features.get(pi).setProperty(new Isotope("M+" + depth, depth++, pid, features.get(pi).getId()));
            skipSet.add(pi);
        }
    }
}
