package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import edu.ucsd.cse110.team56.zooseeker.path.CombinedGraphEdge;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

public class CombinedGraphEdgeTest {

    public class Node {
        public String id;
    }

    public class Edge {
        public String id;
        public String source;
        public String target;
        public float weight;
    }

    private String gE1ID = "graphEdge1";

    private Node startNode = new Node();
    private Node endNode = new Node();
    private String start = "start";
    private String end = "end";
    private final double edgeLength = 1.0;
    private String toStringResult = "(" + start + " :" + gE1ID + ": " + end + ")";

    private Edge testEdge = new Edge();

    @Test
    public void testBuildCombinedGraphEdge() {
        GraphEdge graphEdge1 = new GraphEdge();
        graphEdge1.setId(gE1ID);
        CombinedGraphEdge combined = new CombinedGraphEdge(graphEdge1);
        assertEquals(gE1ID, combined.getId());
    }

    @Test
    public void testDestinationCombinedGraphEdge() {
        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        startNode.id = start;
        endNode.id = end;
        graph.addVertex(start);
        graph.addVertex(end);
        testEdge.source = start;
        testEdge.target = end;
        GraphEdge graphEdge1 = graph.addEdge(testEdge.source, testEdge.target);
        CombinedGraphEdge combined = new CombinedGraphEdge(graphEdge1);
        assertEquals(end, combined.getDestination());
    }

    @Test
    public void testAddCombinedGraphEdge() {

        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        startNode.id = start;
        endNode.id = end;
        graph.addVertex(start);
        graph.addVertex(end);
        testEdge.source = start;
        testEdge.target = end;
        GraphEdge graphEdge2 = graph.addEdge(testEdge.source, testEdge.target);
        CombinedGraphEdge combined = new CombinedGraphEdge(graphEdge2);
        assertEquals(edgeLength, combined.getLength(), 0.0f);
    }


    @Test
    public void testLengthCombinedGraphEdge() {

        GraphEdge graphEdge1 = new GraphEdge();
        GraphEdge graphEdge2 = new GraphEdge();
        graphEdge1.setId(gE1ID);
        CombinedGraphEdge combined = new CombinedGraphEdge(graphEdge1);
        combined.addEdge(graphEdge2);
        assertEquals(gE1ID, combined.getId());
    }

}
