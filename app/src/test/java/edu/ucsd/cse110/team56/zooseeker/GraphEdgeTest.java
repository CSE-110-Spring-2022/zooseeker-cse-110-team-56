package edu.ucsd.cse110.team56.zooseeker;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;

import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

@RunWith(AndroidJUnit4.class)
public class GraphEdgeTest {

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
    public void testBuildGraphEdge() {
        GraphEdge graphEdge1 = new GraphEdge();
        assertEquals(null, graphEdge1.getId());
    }

    @Test
    public void testSetID() {
        GraphEdge graphEdge1 = new GraphEdge();
        graphEdge1.setId(gE1ID);
        assertEquals(gE1ID, graphEdge1.getId());
    }

    @Test
    public void destinationGraphEdgeTest() {
        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        startNode.id = start;
        endNode.id = end;
        graph.addVertex(start);
        graph.addVertex(end);
        testEdge.source = start;
        testEdge.target = end;
        GraphEdge graphEdge1 = graph.addEdge(testEdge.source, testEdge.target);

        assertEquals(end, graphEdge1.getDestination());
    }

    @Test
    public void originGraphEdgeTest() {
        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        startNode.id = start;
        endNode.id = end;
        graph.addVertex(start);
        graph.addVertex(end);
        testEdge.source = start;
        testEdge.target = end;
        GraphEdge graphEdge1 = graph.addEdge(testEdge.source, testEdge.target);

        assertEquals(start, graphEdge1.getOrigin());
    }

    @Test
    public void weightGraphEdgeTest() {
        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        startNode.id = start;
        endNode.id = end;
        graph.addVertex(start);
        graph.addVertex(end);
        testEdge.source = start;
        testEdge.target = end;
        GraphEdge graphEdge1 = graph.addEdge(testEdge.source, testEdge.target);

        assertEquals(edgeLength, graphEdge1.getLength(), 0.0f);
    }


    @Test
    public void toStringtGraphEdgeTest() {
        SimpleDirectedWeightedGraph<String, GraphEdge> graph = new SimpleDirectedWeightedGraph<String, GraphEdge>(GraphEdge.class);
        startNode.id = start;
        endNode.id = end;
        graph.addVertex(start);
        graph.addVertex(end);
        testEdge.source = start;
        testEdge.target = end;
        GraphEdge graphEdge1 = graph.addEdge(testEdge.source, testEdge.target);
        graphEdge1.setId(gE1ID);

        String result = graphEdge1.toString();

        assertEquals(toStringResult, result);
    }



}
