package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

@RunWith(AndroidJUnit4.class)
public class ZooDatabaseTest {
    private ZooDatabase db;
    private ZooDao dao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ZooDatabase.class).allowMainThreadQueries().build();
        dao = db.zooDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testNodes() {
        NodeInfo info1 = new NodeInfo("zoo-id-1", "zoo name", "exhibit", Arrays.asList("a", "b"));
        NodeInfo info2 = new NodeInfo("zoo-id-2", "zoo name2", "intersection", Arrays.asList("c", "d"));

        dao.addNodes(Arrays.asList(info1, info2));
        List<NodeInfo> list = dao.getAllNodes();
        assertEquals(2, list.size());
        assertEquals(info1, list.get(0));
        assertEquals(info2, list.get(1));

        assertEquals(info2, dao.getNode("zoo-id-2"));

        info1.name = "new name";
        dao.updateNode(info1);
        assertEquals(info1, dao.getNode("zoo-id-1"));
    }

    @Test
    public void testEdges() {
        EdgeInfo info1 = new EdgeInfo("id-1", "Gilman Dr");
        EdgeInfo info2 = new EdgeInfo("id-2", "Nobel Dr");

        dao.addEdges(Arrays.asList(info1, info2));
        List<EdgeInfo> list = dao.getAllEdges();
        assertEquals(2, list.size());

        assertEquals(info1, list.get(0));
        assertEquals(info2, list.get(1));

        assertEquals(info2, dao.getEdge("id-2"));
    }
}
