package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.misc.JsonReader;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;

@RunWith(AndroidJUnit4.class)
public class JsonReaderTest {
    @Test
    public void testLoadJsonList() {
        List<NodeInfo> nodes = JsonReader.parseJsonList(ApplicationProvider.getApplicationContext(), "map/assets/sample_node_info.json", NodeInfo.class);
        assertEquals(7, nodes.size());
    }

    @Test
    public void testLoadNonExistenceJsonList() {
        List<NodeInfo> nodes = JsonReader.parseJsonList(ApplicationProvider.getApplicationContext(), "whatever", NodeInfo.class);
        assertEquals(0, nodes.size());
    }

    @Test
    public void testLoadJson() {
        Context context = ApplicationProvider.getApplicationContext();
        Optional<Graph> rawGraph = JsonReader.parseJson(context, "map/assets/sample_zoo_graph.json", Graph.class);
        assertTrue(rawGraph.isPresent());
    }

    @Test
    public void testLoadNonExistenceJson() {
        Context context = ApplicationProvider.getApplicationContext();
        Optional<Graph> rawGraph = JsonReader.parseJson(context, "whatever", Graph.class);
        assertTrue(!rawGraph.isPresent());
    }
}
