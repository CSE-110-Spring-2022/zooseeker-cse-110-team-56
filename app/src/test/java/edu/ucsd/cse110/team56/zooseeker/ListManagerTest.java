package edu.ucsd.cse110.team56.zooseeker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ListManagerTest {
    @Before
    public void refreshDb() {
        ZooDatabase.refreshDb(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void addItemTest() {
        NodeInfo nodeInfo = new NodeInfo("id", "name", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
        ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).add(nodeInfo);
        assert(nodeInfo.getStatus() == NodeInfo.Status.ADDED);
    }

    @Test
    public void removeItemTest() {
        NodeInfo nodeInfo = new NodeInfo("id", "name", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
        ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).remove(nodeInfo);
        assert(nodeInfo.getStatus() == NodeInfo.Status.LOADED);
    }

//    @Test
//    public void getAddedListTest() {
//        NodeInfo nodeInfo0 = new NodeInfo("id", "name", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//        NodeInfo nodeInfo1 = new NodeInfo("id", "name", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//        NodeInfo nodeInfo2 = new NodeInfo("id", "name", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//        NodeInfo nodeInfo3 = new NodeInfo("id", "name", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//
//
//        ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).add(nodeInfo0);
//        ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).add(nodeInfo2);
//
//        assertEquals(2, ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).getAddedList().size());
//    }

//    @Test
//    public void getAddedListNamesTest() {
//        NodeInfo nodeInfo0 = new NodeInfo("id", "name0", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//        NodeInfo nodeInfo1 = new NodeInfo("id", "name1", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//        NodeInfo nodeInfo2 = new NodeInfo("id", "name2", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
//
//        ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
//        nodes.add(nodeInfo0);
//        nodes.add(nodeInfo1);
//        nodes.add(nodeInfo2);
//
//        ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).add(nodeInfo1);
//        ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).add(nodeInfo2);
//
//        List<String> addedNames = ExhibitsManager.getSingleton(ApplicationProvider.getApplicationContext()).getAddedAndVisitedNames();
//        assert("name1".compareTo(addedNames.get(0)) == 0);
//        assert("name2".compareTo(addedNames.get(1)) == 0);
//    }

    @Test
    public void getNamesTest() {
        NodeInfo nodeInfo0 = new NodeInfo("id", "name0", NodeInfo.Kind.EXHIBIT, new ArrayList<>());
        NodeInfo nodeInfo1 = new NodeInfo("id", "name1", NodeInfo.Kind.EXHIBIT, new ArrayList<>());

        ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
        nodes.add(nodeInfo0);
        nodes.add(nodeInfo1);

        List<String> names = ExhibitsManager.getNames(nodes);
        assert("name0".equals(names.get(0)));
        assert("name1".equals(names.get(1)));
    }
}
