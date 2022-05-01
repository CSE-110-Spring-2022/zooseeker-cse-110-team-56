package edu.ucsd.cse110.team56.zooseeker.path;

import android.content.Context;
import android.util.Log;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.Utility;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

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
     */
    public ArrayList<GraphPath<String, GraphEdge>> findPaths(Context context, List<String> toVisit) {
        ArrayList<String> nodes = new ArrayList<>(toVisit);

        org.jgrapht.Graph<String, GraphEdge> graph = this.generateGraph(context);
        DijkstraShortestPath<String, GraphEdge> searcher = new DijkstraShortestPath<String, GraphEdge>(graph);
        ArrayList<GraphPath<String, GraphEdge>> paths = new ArrayList<>();

        while(nodes.size() > 1) {
            GraphPath<String, GraphEdge> results = searcher.getPath(nodes.get(0), nodes.get(1));
            nodes.remove(0);
            paths.add(results);
        }

        Log.d("Paths", paths.toString());

        return paths;
    }

    public static Graph load(Context context) {
        Graph rawGraph = Utility.parseSingleJson(context, "map/assets/sample_zoo_graph.json", Graph.class).get();
        return rawGraph;
    }
}

