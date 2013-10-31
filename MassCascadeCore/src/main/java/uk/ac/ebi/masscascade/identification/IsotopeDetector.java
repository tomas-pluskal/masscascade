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

import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.comparator.FeatureMassComparator;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing an isotope search method for a list of profiles.
 */
@Deprecated
public class IsotopeDetector {

    private static final Logger LOGGER = Logger.getLogger(IsotopeDetector.class);

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
            put(2, 0.90);
            put(3, 0.90);
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
     * Finds identification within the charge range and sets any detected identification as properties in the feature
     * object.
     */
    public void findIsotopes(final FeatureSet pseudoFeatureSet) {

        List<Feature> featureList = ((FeatureSetImpl) pseudoFeatureSet).getFeaturesList();
        Collections.sort(featureList, new FeatureMassComparator());
        double[][] profileDeltas = getProfileMassDeltas(featureList);

        DirectedMultigraph<Feature, DefaultEdge>[] graphs = new DirectedMultigraph[charge];
        for (int i = 0; i < charge; i++)
            graphs[i] = new DirectedMultigraph<>(DefaultEdge.class);

        for (int row = 0; row < profileDeltas.length; row++) {
            Range[] protonDeltas = getProtonDeltas(charge, featureList.get(row).getMz());
            for (int col = 0; col < profileDeltas.length; col++) {

                double profileDelta = profileDeltas[row][col];
                if (profileDelta == 0) break;

                int chargeCount = 0;
                for (Range protonDelta : protonDeltas) {
                    if (protonDelta.contains(profileDelta))
                        JGraphTSync.addEdgeWithVertices(graphs[chargeCount], featureList.get(row),
                                featureList.get(col));
                    chargeCount++;
                }
            }
        }

        adjustIsotopeLabels(graphs);

        graphs = null;
        featureList = null;
        profileDeltas = null;
    }

    /**
     * Calculates the proton differences based on the given charge range.
     *
     * @param charge the charge range
     * @return the list of proton differences
     */
    private Range[] getProtonDeltas(int charge, double mz) {

        Range[] protonDeltas = new Range[charge];
        for (int curCharge = 1; curCharge <= charge; curCharge++) {
            double sigma = mz * massTolerance / Constants.PPM;
            protonDeltas[curCharge - 1] = new ExtendableRange((ISOTOPE_DIFFERENCE - sigma) / curCharge,
                    (ISOTOPE_DIFFERENCE + sigma) / curCharge);
        }
        return protonDeltas;
    }

    /**
     * Takes all isotope-detected profiles and sets the nominal isotope positions from the main M feature (0, 1,
     * 2, ..).
     */
    private void adjustIsotopeLabels(DirectedMultigraph<Feature, DefaultEdge>[] graphs) {

        for (DirectedMultigraph<Feature, DefaultEdge> graph : graphs) {

            List<Set<Feature>> connectedSets = JGraphTSync.getConnectedSets(graph);

            List<Feature> rootV = new ArrayList<>();
            List<Feature> leafV = new ArrayList<>();

            Feature vertex;
            for (Set<Feature> featureSet : connectedSets) {

                for (final Feature feature : featureSet) {
                    if (graph.inDegreeOf(feature) == 0) rootV.add(feature);
                    else if (graph.outDegreeOf(feature) == 0) leafV.add(feature);
                }

                KShortestPaths<Feature, DefaultEdge> pathFinder;
                List<GraphPath<Feature, DefaultEdge>> paths;

                for (Feature root : rootV) {
                    pathFinder = new KShortestPaths<>(graph, root, MAX_PATH);
                    for (Feature leaf : leafV) {
                        paths = pathFinder.getPaths(leaf);
                        if (paths == null) continue;
                        for (GraphPath<Feature, DefaultEdge> path : paths) {
                            int mainId = 0;
                            double maxIntensity = 0;
                            final List<Feature> pathVertices = JGraphTSync.getPathVertexList(path);
                            Collections.sort(pathVertices, new FeatureMassComparator());
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
                            for (int j = mainId + 1, k = 1; (j < pathVertices.size() && j < 4); j++, k++) {
                                Feature isoVertex = pathVertices.get(j);
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
                        paths = null;
                    }
                    pathFinder = null;
                }
            }

            rootV = null;
            leafV = null;
            connectedSets = null;
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
    private double[][] getProfileMassDeltas(List<Feature> peakList) {

        double[][] massDeltas = new double[peakList.size()][peakList.size()];

        int row = 0;
        Iterator<Feature> itRow = peakList.iterator();
        while (itRow.hasNext()) {
            double rowMass = itRow.next().getMz();

            int col = 0;
            Iterator<Feature> itCol = peakList.iterator();
            while (itCol.hasNext()) {
                double colMass = itCol.next().getMz();

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
