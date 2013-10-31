package uk.ac.ebi.masscascade.identification;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import uk.ac.ebi.masscascade.interfaces.Feature;

import java.util.List;
import java.util.Set;

public class JGraphTSync {

    public synchronized static void addEdgeWithVertices(DirectedMultigraph<Feature, DefaultEdge> multigraph,
            Feature feature1, Feature feature2) {
        Graphs.addEdgeWithVertices(multigraph, feature1, feature2);
    }

    public synchronized static void addEdgeWithVertices(UndirectedGraph multigraph, Feature feature1,
            Feature feature2) {
        Graphs.addEdgeWithVertices(multigraph, feature1, feature2);
    }

    public synchronized static List<Set<Feature>> getConnectedSets(DirectedMultigraph<Feature, DefaultEdge> graph) {
        ConnectivityInspector connectivityChecker = new ConnectivityInspector(graph);
        return connectivityChecker.connectedSets();
    }

    public synchronized static List<Feature> getPathVertexList(GraphPath<Feature, DefaultEdge> path) {
        return Graphs.getPathVertexList(path);
    }
}
