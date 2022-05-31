package edu.ucsd.cse110.team56.zooseeker.path;

import android.content.Context;
import android.util.Log;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.misc.JsonReader;

public class Graph {
    public class Node {
        public String id;
    }

    public class Edge {
        public String id;
        public String source;
        public String target;
        public float weight;
    }

    public List<Node> nodes;
    public List<Edge> edges;

    /**
     * Convert the graph into a JGraph.
     * @return a jgrapht.Graph instance of the same graph
     */
    public org.jgrapht.Graph<String, GraphEdge> toJGraph() {
        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        for(Graph.Node node: nodes){
            graph.addVertex(node.id);
        }

        for(Graph.Edge edge: edges){
            GraphEdge eForward = graph.addEdge(edge.source, edge.target);
            eForward.setId(edge.id);
            graph.setEdgeWeight(eForward, edge.weight);

            GraphEdge eBackward = graph.addEdge(edge.target, edge.source);
            eBackward.setId(edge.id);
            graph.setEdgeWeight(eBackward, edge.weight);
        }

        return graph;
    }

    /**
     * Find a path between 2 specific exhibits.
     */
    public Path findPath(GraphVertex start, GraphVertex end) {
        org.jgrapht.Graph<String, GraphEdge> graph = this.toJGraph();
        DijkstraShortestPath<String, GraphEdge> searcher = new DijkstraShortestPath<String, GraphEdge>(graph);
        GraphPath<String, GraphEdge> result = searcher.getPath(start.getNavigatableId(), end.getNavigatableId());

        return new Path(result, start, end);
    }


    /**
     * Find paths to go over all exhibits
     */
    public ArrayList<Path> findPaths(GraphVertex current, List<GraphVertex> list, GraphVertex end) {
        org.jgrapht.Graph<String, GraphEdge> graph = this.toJGraph();
        DijkstraShortestPath<String, GraphEdge> searcher = new DijkstraShortestPath<String, GraphEdge>(graph);
        ArrayList<Path> paths = new ArrayList<>();

        Set<GraphVertex> locSet = new HashSet<>(list);

        while(locSet.size() > 0) {
            ShortestPathAlgorithm.SingleSourcePaths<String, GraphEdge> results = searcher.getPaths(current.getNavigatableId());
            GraphVertex nextDestination = locSet.iterator().next();
            GraphPath<String, GraphEdge> shortest = results.getPath(nextDestination.getNavigatableId());

            for(GraphVertex loc: locSet) {
                GraphPath<String, GraphEdge> path = results.getPath(loc.getNavigatableId());
                if (path.getWeight() < shortest.getWeight()) {
                    shortest = path;
                    nextDestination = loc;
                }
            }

            paths.add(new Path(shortest, current, nextDestination));
            locSet.remove(nextDestination);

            current = nextDestination;
        }

        ShortestPathAlgorithm.SingleSourcePaths<String, GraphEdge> results = searcher.getPaths(current.getNavigatableId());
        GraphPath<String, GraphEdge> path = results.getPath(end.getNavigatableId());
        paths.add(new Path(path, current, end));

        Log.d("Paths", paths.toString());
        return paths;
    }

    /**
     * Load the graph into memory
     * @param context application context
     * @return Graph instance
     */
    public static Graph load(Context context) {
        Graph rawGraph = JsonReader.parseJson(context, "gps/zoo_graph.json", Graph.class).get();
        return rawGraph;
    }
}

