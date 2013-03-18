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

package uk.ac.ebi.masscascade.distance;

import org.jgrapht.GraphHelper;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Calculates the pairwise cosine similarity for the list of profiles and generates the resulting pseudospectra based
 * on the given similarity threshold. Connected component labelling is used to extract correlated traces, i.e.,
 * pseudospectra also contain traces with indirect correlations to other traces in the pseudospectra.
 * <ul>
 * <li>Parameter <code> BINS </code>- The number of bins.</li>
 * <li>Parameter <code> CORRELATION THRESHOLD </code>- The similarity threshold.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class CosineSimilarityDistance extends CallableTask {

    private ProfileContainer profileContainer;
    private SpectrumContainer spectrumContainer;

    private int bins;
    private double threshold;
    private CosineSimilarity cosineSimilarity;

    /**
     * Constructs a cosine similarity task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public CosineSimilarityDistance(ParameterMap params) throws MassCascadeException {

        super(CosineSimilarityDistance.class);

        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
        threshold = params.get(Parameter.CORRELATION_THRESHOLD, Double.class);
        bins = params.get(Parameter.BINS, Integer.class);

        cosineSimilarity = new CosineSimilarity(bins);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    @Override
    public SpectrumContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        spectrumContainer = profileContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                profileContainer.getWorkingDirectory());

        correlate(profileContainer.getProfileList());

        spectrumContainer.finaliseFile();
        return spectrumContainer;
    }

    /**
     * Performs a pairwise correlation of all profiles in the list and finds connected components above the threshold
     * in the resulting matrix. Connected and unconnected components are parsed into separated pseudospectra.
     *
     * @param profileList the list of profiles
     */
    @SuppressWarnings("deprecation")
    public void correlate(List<Profile> profileList) {

        double vectorDistance;
        XYList rowData;
        XYList colData;

        UndirectedGraph graph = new SimpleGraph(DefaultEdge.class);
        for (int row = 0; row < profileList.size(); row++) {
            for (int column = 0; column < row; column++) {

                rowData = profileList.get(row).getTrace().getData();
                colData = profileList.get(column).getTrace().getData();

                if (!isOverlapping(rowData, colData)) continue;

                vectorDistance = cosineSimilarity.getDistance(rowData, colData);
                if (vectorDistance >= threshold)
                    GraphHelper.addEdgeWithVertices(graph, profileList.get(row), profileList.get(column));
            }
        }

        ConnectivityInspector connectivityChecker = new ConnectivityInspector(graph);
        List<Set<Profile>> connectedProfiles = connectivityChecker.connectedSets();

        int index = 1;

        for (Set<Profile> profileSet : connectedProfiles) {

            double rt = profileSet.iterator().next().getRetentionTime();
            Range range = new ExtendableRange(rt);
            XYList spectrumData = new XYList();
            rt = 0;
            for (Profile profile : profileSet) {
                spectrumData.add(profile.getMzIntDp());
                range.extendRange(profile.getRetentionTime());
                rt += profile.getRetentionTime();
            }
            rt /= profileSet.size();

            Collections.sort(spectrumData);
            Spectrum pseudoSpectrum = new PseudoSpectrum(index++, spectrumData, range, rt, profileSet);

            spectrumContainer.addSpectrum(pseudoSpectrum);
        }

        profileList.removeAll(graph.vertexSet());
        for (Profile profile : profileList) {
            XYList spectrumData = new XYList();
            spectrumData.add(profile.getMzIntDp());
            Range range = new ExtendableRange(profile.getRetentionTime(), profile.getRetentionTime());
            Spectrum pseudoSpectrum = new PseudoSpectrum(index++, spectrumData, profile.getRetentionTime(), range);
            pseudoSpectrum.addProfile(profile);
            spectrumContainer.addSpectrum(pseudoSpectrum);
        }
    }

    private boolean isOverlapping(XYList data1, XYList data2) {

        double min1 = data1.get(0).x;
        double max1 = data1.get(data1.size() - 1).x;

        double min2 = data2.get(0).x;
        double max2 = data2.get(data2.size() - 1).x;

        return !(max1 < min2 || max2 < min1);
    }
}
