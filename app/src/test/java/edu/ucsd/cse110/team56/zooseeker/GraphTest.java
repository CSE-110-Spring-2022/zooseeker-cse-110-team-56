package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.GraphPath;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

@RunWith(AndroidJUnit4.class)
public class GraphTest {
    @Test
    public void testLoad() {
        Context context = ApplicationProvider.getApplicationContext();
        Graph graph = Graph.load(context);

        assertEquals(7, graph.nodes.size());
        assertEquals(7, graph.edges.size());
    }

    @Test
    public void testGenerateGraph() {
        Context context = ApplicationProvider.getApplicationContext();
        org.jgrapht.Graph<String, GraphEdge> graph = Graph.load(context).generateGraph(context);

        assertEquals(7, graph.edgeSet().size());
        assertEquals(7, graph.vertexSet().size());
    }

    @Test
    public void testFindPaths() {
        Context context = ApplicationProvider.getApplicationContext();
        List<String> toVisit = List.of("entrance_exit_gate", "lions", "gators", "entrance_exit_gate");
        ArrayList<GraphPath<String, GraphEdge>> paths = Graph.load(context).findPaths(context, toVisit);

        assertEquals(toVisit.size() - 1, paths.size());
        for(int i = 0; i < paths.size(); ++i) {
            GraphPath<String, GraphEdge> path = paths.get(i);
            assertEquals(toVisit.get(i), path.getStartVertex());
            assertEquals(toVisit.get(i + 1), path.getEndVertex());
        }
    }
}
