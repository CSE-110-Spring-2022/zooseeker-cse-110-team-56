package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.CombinedGraphEdge;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;
import edu.ucsd.cse110.team56.zooseeker.path.GraphVertex;

public class GraphVertexTest {

    NodeInfo curr = new NodeInfo("currId", "currName", null, null);
    NodeInfo next = new NodeInfo("nextId", "nextName", null, null);

    @Test
    public void testbuildGetVertex() {
        GraphVertex graphVertex = new GraphVertex(curr);
        assertEquals("currId", graphVertex.getNavigatableId());

    }

    @Test
    public void testGetActualExhibit() {
        GraphVertex noChild = new GraphVertex(curr);
        assertEquals(curr, noChild.getActualExhibit());

        GraphVertex hasChild = new GraphVertex(curr, next);
        assertEquals(next, hasChild.getActualExhibit());

    }

}
