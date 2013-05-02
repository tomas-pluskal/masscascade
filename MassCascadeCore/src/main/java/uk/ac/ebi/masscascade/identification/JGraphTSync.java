package uk.ac.ebi.masscascade.identification;

import org.jgrapht.GraphHelper;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import uk.ac.ebi.masscascade.interfaces.Profile;

import java.util.List;
import java.util.Set;

public class JGraphTSync {

    public synchronized static void addEdgeWithVertices(DirectedMultigraph<Profile, DefaultEdge> multigraph,
            Profile profile1, Profile profile2) {
        Graphs.addEdgeWithVertices(multigraph, profile1, profile2);
    }

    public synchronized static void addEdgeWithVertices(UndirectedGraph multigraph, Profile profile1,
            Profile profile2) {
        Graphs.addEdgeWithVertices(multigraph, profile1, profile2);
    }

    public synchronized static List<Set<Profile>> getConnectedSets(DirectedMultigraph<Profile, DefaultEdge> graph) {
        ConnectivityInspector connectivityChecker = new ConnectivityInspector(graph);
        return connectivityChecker.connectedSets();
    }

    public synchronized static List<Profile> getPathVertexList(GraphPath<Profile, DefaultEdge> path) {
        return Graphs.getPathVertexList(path);
    }
}
