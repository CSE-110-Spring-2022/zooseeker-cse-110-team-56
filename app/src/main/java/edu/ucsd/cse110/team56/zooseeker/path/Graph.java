package edu.ucsd.cse110.team56.zooseeker.path;

import android.content.Context;
import android.util.Log;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucsd.cse110.team56.zooseeker.misc.Utility;

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

    public org.jgrapht.Graph<String, GraphEdge> generateGraph(Context context) {
        SimpleWeightedGraph<String, GraphEdge> graph = new SimpleWeightedGraph<String, GraphEdge>(GraphEdge.class);
        for(Graph.Node node: nodes){
            graph.addVertex(node.id);
        }

        for(Graph.Edge edge: edges){
            GraphEdge e = graph.addEdge(edge.source, edge.target);
            e.setId(edge.id);
            graph.setEdgeWeight(e, edge.weight);
        }

        return graph;
    }


    /**
     * Create paths to visit all specified nodes in the graph
     *
     * @param context Android Context
     * @param toVisit exhibits to visit (doesn't include the gate)
     * @param start the start and the end point, probably the gate
     */
    public ArrayList<GraphPath<String, GraphEdge>> findPaths(Context context, List<String> toVisit, String start) {
        org.jgrapht.Graph<String, GraphEdge> graph = this.generateGraph(context);
        DijkstraShortestPath<String, GraphEdge> searcher = new DijkstraShortestPath<String, GraphEdge>(graph);
        ArrayList<GraphPath<String, GraphEdge>> paths = new ArrayList<>();

        Set<String> locSet = new HashSet<>(toVisit);

        String current = start;
        while(locSet.size() > 0) {
            ShortestPathAlgorithm.SingleSourcePaths<String, GraphEdge> results = searcher.getPaths(current);
            GraphPath<String, GraphEdge> shortest = results.getPath(locSet.iterator().next());
            for(String loc: locSet) {
                GraphPath<String, GraphEdge> path = results.getPath(loc);
                if (path.getLength() < shortest.getLength()) {
                    shortest = path;
                }
            }
            paths.add(shortest);
            locSet.remove(shortest.getEndVertex());
            current = shortest.getEndVertex();
        }

        ShortestPathAlgorithm.SingleSourcePaths<String, GraphEdge> results = searcher.getPaths(current);
        GraphPath<String, GraphEdge> path = results.getPath(start);
        paths.add(path);

        Log.d("Paths", paths.toString());
        return paths;
    }

    public static Graph load(Context context) {
        Graph rawGraph = Utility.parseSingleJson(context, "map/assets/sample_zoo_graph.json", Graph.class).get();
        return rawGraph;
    }
}

