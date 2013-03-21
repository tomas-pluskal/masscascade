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

package uk.ac.ebi.masscascade.identification;

import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.comparator.ProfileMassComparator;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing an isotope search method for a list of profiles.
 */
public class IsotopeDetector {

    private static final Logger LOGGER = Logger.getLogger(AdductDetector.class);

    private static final int MAX_PATH = 50;

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
            put(2, 0.50);
            put(3, 0.75);
        }
    };

    // bin size used to infer the functions above
    private static double BIN_SIZE = 10d;

    /**
     * Reference: "The isotopic Mass Defect", E. Thurman, et al.
     * Mass defect: +/- 0.003 u
     */
    private static final double ISOTOPE_DIFFERENCE = 1.0033;

    private final double massTolerance;
    private final int charge;

    /**
     * Constructor for the isotope detector.
     *
     * @param charge the maximum charge possible
     */
    public IsotopeDetector(int charge, double massTolerance) {

        this.massTolerance = massTolerance;
        this.charge = charge;
    }

    /**
     * Finds identification within the charge range and sets any detected identification as properties in the profile
     * object.
     */
    public void findIsotopes(final Spectrum pseudoSpectrum) {

        final List<Profile> profileList = ((PseudoSpectrum) pseudoSpectrum).getProfileList();
        Collections.sort(profileList, new ProfileMassComparator());
        double[][] profileDeltas = getProfileMassDeltas(profileList);

        List<DirectedMultigraph<Profile, DefaultEdge>> graphs =
                new ArrayList<DirectedMultigraph<Profile, DefaultEdge>>();
        for (int i = 0; i < charge; i++)
            graphs.add(new DirectedMultigraph<Profile, DefaultEdge>(DefaultEdge.class));

        for (int row = 0; row < profileDeltas.length; row++) {
            for (int col = 0; col < profileDeltas.length; col++) {

                double profileDelta = profileDeltas[row][col];
                if (profileDelta == 0) break;

                List<Range> protonDeltas = getProtonDeltas(charge, profileList.get(row).getMzIntDp().x);
                int chargeCount = 0;
                for (Range protonDelta : protonDeltas) {
                    if (protonDelta.contains(profileDelta))
                        Graphs.addEdgeWithVertices(graphs.get(chargeCount), profileList.get(row), profileList.get(col));
                    chargeCount++;
                }
            }
        }

        adjustIsotopeLabels(graphs);
    }

    /**
     * Calculates the proton differences based on the given charge range.
     *
     * @param charge the charge range
     * @return the list of proton differences
     */
    private List<Range> getProtonDeltas(int charge, double mz) {

        List<Range> protonDeltas = new ArrayList<Range>();
        for (int curCharge = 1; curCharge <= charge; curCharge++) {
            double sigma = mz * massTolerance / Constants.PPM;
            protonDeltas.add(new ExtendableRange((ISOTOPE_DIFFERENCE - sigma) / curCharge,
                    (ISOTOPE_DIFFERENCE + sigma) / curCharge));
        }
        return protonDeltas;
    }

    /**
     * Takes all isotope-detected profiles and sets the nominal isotope positions from the main M profile (0, 1,
     * 2, ..).
     */
    private void adjustIsotopeLabels(List<DirectedMultigraph<Profile, DefaultEdge>> graphs) {

        Set<Isotope> isoSet = new HashSet<Isotope>();
        for (DirectedMultigraph<Profile, DefaultEdge> graph : graphs) {

            ConnectivityInspector connectivityChecker = new ConnectivityInspector(graph);
            final List<Set<Profile>> connectedSets = connectivityChecker.connectedSets();

            List<Profile> rootV = new ArrayList<Profile>();
            List<Profile> leafV = new ArrayList<Profile>();

            Profile vertex;
            for (Set<Profile> profileSet : connectedSets) {

                for (final Profile profile : profileSet) {
                    if (graph.inDegreeOf(profile) == 0) rootV.add(profile);
                    else if (graph.outDegreeOf(profile) == 0) leafV.add(profile);
                }

                for (final Profile root : rootV) {
                    KShortestPaths<Profile, DefaultEdge> pathFinder =
                            new KShortestPaths<Profile, DefaultEdge>(graph, root, MAX_PATH);
                    for (final Profile leaf : leafV) {
                        List<GraphPath<Profile, DefaultEdge>> paths = pathFinder.getPaths(leaf);
                        if (paths == null) continue;
                        for (GraphPath<Profile, DefaultEdge> path : paths) {
                            int mainId = 0;
                            double maxIntensity = 0;
                            final List<Profile> pathVertices = Graphs.getPathVertexList(path);
                            Collections.sort(pathVertices, new ProfileMassComparator());
                            for (int i = 0; i < pathVertices.size(); i++) {
                                vertex = pathVertices.get(i);

                                if (vertex.getIntensity() > maxIntensity) {
                                    maxIntensity = vertex.getIntensity();
                                    mainId = i;
                                }
                            }

                            vertex = pathVertices.get(mainId);
                            int vertexId = vertex.getId();

                            if (pathVertices.size() <= mainId + 1) continue;

                            Map<Integer, Isotope> idToIsotope = new HashMap<>();
                            for (int j = mainId + 1, k = 1; j < pathVertices.size(); j++, k++) {
                                Profile isoVertex = pathVertices.get(j);
                                double isoIntensityTheory = isotopeToEquation.get(k).getY(isoVertex.getMz() / BIN_SIZE);

                                if (isInRange(k, vertex.getIntensity(), isoVertex.getIntensity(), isoIntensityTheory))
                                    idToIsotope.put(j, new Isotope("M+" + k, k, vertexId, pathVertices.get(j).getId()));
                                else break;
                            }

                            if (idToIsotope.size() < 1) continue;

                            Isotope isotope = new Isotope("M", 0, vertexId, vertexId);
                            vertex.setProperty(isotope);
                            for (Map.Entry<Integer, Isotope> entry : idToIsotope.entrySet())
                                pathVertices.get(entry.getKey()).setProperty(entry.getValue());
                        }
                    }
                }
            }
        }
    }

    private boolean isInRange(int k, double majorIntensity, double isoIntensity, double isoIntensityTheory) {

        double norm = isoIntensity / majorIntensity;
        return (norm < isoIntensityTheory + (isoIntensityTheory * isotopeToTolerance.get(
                k)) && norm >= isoIntensityTheory - (isoIntensityTheory * isotopeToTolerance.get(k)));
    }

    /**
     * Creates the symmetric mass difference matrix of size n x n.
     *
     * @param peakList the profiles used for the matrix
     * @return the mass difference matrix
     */
    private double[][] getProfileMassDeltas(List<Profile> peakList) {

        double[][] massDeltas = new double[peakList.size()][peakList.size()];

        int row = 0;
        Iterator<Profile> itRow = peakList.iterator();
        while (itRow.hasNext()) {
            double rowMass = itRow.next().getMzIntDp().x;

            int col = 0;
            Iterator<Profile> itCol = peakList.iterator();
            while (itCol.hasNext()) {
                double colMass = itCol.next().getMzIntDp().x;

                double delta = rowMass - colMass;
                if (delta == 0) break;
                massDeltas[row][col] = delta;

                col++;
            }
            row++;
        }

        return massDeltas;
    }
}
