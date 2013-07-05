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
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.comparator.ProfileMassComparator;
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
 * Class implementing an isotope search method for a list of profiles. This class does not use external dependencies.
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
     * Finds identification within the charge range and sets any detected identification as properties in the profile
     * object.
     */
    public void findIsotopes(final Spectrum pseudoSpectrum) {

        List<Profile> profiles = new ArrayList<>(pseudoSpectrum.getProfileMap().values());
        Collections.sort(profiles, new ProfileMassComparator());

        Set<Integer> skipSet = new HashSet<>();

        for (int c = 1; c <= charge; c++)
            traverse(profiles, 0, c, skipSet);
    }

    /**
     * Traverses over all profiles and builds isotopic envelopes.
     *
     * @param profiles the profile list
     * @param p        the parent id
     * @param c        the current charge
     * @param skipSet  ids of already annotated profiles
     */
    private void traverse(List<Profile> profiles, int p, int c, Set<Integer> skipSet) {

        if (p == profiles.size()) return;
        else if (skipSet.contains(p)) { // skip profile is already annotated
            traverse(profiles, ++p, c, skipSet);
            return;
        }

        List<Integer[]> paths = new ArrayList<>();
        // build isotopic envelope for this profile
        build(profiles, paths, p, 0, 0, c);
        resolve(profiles, paths, p, c, skipSet);
        // move to next profile
        traverse(profiles, ++p, c, skipSet);
    }

    /**
     * Builds all possible isotopic envelopes for a given profile
     *
     * @param profiles the profile list
     * @param paths    all isotope envelopes described as paths with profile ids
     * @param p        the parent id
     * @param d        the distance to the parent
     * @param pi       the daughter id
     * @param c        the current charge
     */
    private void build(List<Profile> profiles, List<Integer[]> paths, int p, int d, int pi, int c) {

        for (int cp = p + 1; cp < profiles.size(); cp++) {

            double nmz = profiles.get(cp).getMz();
            double ndelta = nmz * ppm / Constants.PPM;

            double pmz = profiles.get(p).getMz();
            double ul = pmz + ISOTOPE_DIFFERENCE / c + ndelta;
            double ll = pmz + ISOTOPE_DIFFERENCE / c - ndelta;

            double r = profiles.get(cp).getIntensity() / profiles.get(p).getIntensity();

            if (nmz >= ul) {
                break;
            } else if (isPotentialIsotope(ll, ul, nmz, r, c)) {

                if (paths.size() == pi) paths.add(new Integer[NUM_ISOTOPES]);

                if (d == paths.get(pi).length) paths.set(pi, Arrays.copyOf(paths.get(pi), d * 2)); // grow isotope array
                if (d != 0 && paths.get(pi)[0] == null)
                    paths.set(pi, paths.get(pi - 1).clone()); // copy and add isotope array if path branches
                paths.get(pi)[d] = cp;
                build(profiles, paths, cp, ++d, pi++, c); // continue to build isotopic envelope
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
     * @param profiles the profile list
     * @param paths    all isotope envelopes described as paths with profile ids
     * @param p        the parent id
     * @param c        the current charge
     * @param skipSet  ids of already annotated profiles
     */
    private void resolve(List<Profile> profiles, List<Integer[]> paths, int p, int c, Set<Integer> skipSet) {

        if (paths.size() == 0 || paths.get(0)[0] == null) {}
        else if (paths.size() == 1) annotate(profiles, paths.get(0), p, skipSet); // only one isotopic envelope
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
            if (paths.size() == 1) annotate(profiles, paths.get(0), p, skipSet);
            else { // (2) select best path by total min m/z deviation
                Integer[] bestPath = null;
                double maxDevSum = Double.MAX_VALUE;
                for (Integer[] path : paths) {
                    double pmz = profiles.get(p).getMz();
                    double devSum = 0;
                    for (Integer i : path) {
                        if (i == null) break;
                        double nmz = profiles.get(i).getMz();
                        devSum += FastMath.abs(nmz - pmz - ISOTOPE_DIFFERENCE);
                        pmz = nmz;
                    }
                    if (devSum < maxDevSum) {
                        maxDevSum = devSum;
                        bestPath = path;
                    }
                }

                annotate(profiles, bestPath, p, skipSet);
            }
        }
    }

    /**
     * Annotates the profiles with isotope information from the envelope.
     *
     * @param profiles the profile list
     * @param path     the best isotopic envelope path
     * @param p        the parent id
     * @param skipSet  ids of already annotated profiles
     */
    private void annotate(List<Profile> profiles, Integer[] path, int p, Set<Integer> skipSet) {

        int pid = profiles.get(p).getId();
        int depth = 0;
        profiles.get(p).setProperty(new Isotope("M", depth++, pid, pid));
        skipSet.add(p);

        for (Integer pi : path) {
            if (pi == null) break;
            profiles.get(p).setProperty(new Isotope("M+" + depth, depth, pid, profiles.get(pi).getId()));
            profiles.get(pi).setProperty(new Isotope("M+" + depth, depth++, pid, profiles.get(pi).getId()));
            skipSet.add(pi);
        }
    }
}
