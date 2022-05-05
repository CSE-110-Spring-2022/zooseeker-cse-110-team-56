package edu.ucsd.cse110.team56.zooseeker;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ListManagerTest {
    @Test
    public void addItemTest() {
        NodeInfo nodeInfo = new NodeInfo("id", "name", "kind", new ArrayList<>());
        ListManager.addItem(nodeInfo);
        assert(nodeInfo.isAdded());
    }

    @Test
    public void removeItemTest() {
        NodeInfo nodeInfo = new NodeInfo("id", "name", "kind", new ArrayList<>());
        ListManager.removeItem(nodeInfo);
        assert(!nodeInfo.isAdded());
    }

    @Test
    public void getAddedListTest() {
        NodeInfo nodeInfo0 = new NodeInfo("id", "name", "kind", new ArrayList<>());
        NodeInfo nodeInfo1 = new NodeInfo("id", "name", "kind", new ArrayList<>());
        NodeInfo nodeInfo2 = new NodeInfo("id", "name", "kind", new ArrayList<>());
        NodeInfo nodeInfo3 = new NodeInfo("id", "name", "kind", new ArrayList<>());

        ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
        nodes.add(nodeInfo0);
        nodes.add(nodeInfo1);
        nodes.add(nodeInfo2);
        nodes.add(nodeInfo3);

        ListManager.addItem(nodeInfo0);
        ListManager.addItem(nodeInfo2);

        assertEquals(4, nodes.size());
        assertEquals(2, ListManager.getAddedList(nodes).size());
    }

    @Test
    public void getAddedListNamesTest() {
        NodeInfo nodeInfo0 = new NodeInfo("id", "name0", "kind", new ArrayList<>());
        NodeInfo nodeInfo1 = new NodeInfo("id", "name1", "kind", new ArrayList<>());
        NodeInfo nodeInfo2 = new NodeInfo("id", "name2", "kind", new ArrayList<>());

        ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
        nodes.add(nodeInfo0);
        nodes.add(nodeInfo1);
        nodes.add(nodeInfo2);

        ListManager.addItem(nodeInfo1);
        ListManager.addItem(nodeInfo2);

        List<String> addedNames = ListManager.getAddedListNames(nodes);
        assert("name1".compareTo(addedNames.get(0)) == 0);
        assert("name2".compareTo(addedNames.get(1)) == 0);
    }

    @Test
    public void getNamesTest() {
        NodeInfo nodeInfo0 = new NodeInfo("id", "name0", "kind", new ArrayList<>());
        NodeInfo nodeInfo1 = new NodeInfo("id", "name1", "kind", new ArrayList<>());

        ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
        nodes.add(nodeInfo0);
        nodes.add(nodeInfo1);

        ListManager.addItem(nodeInfo1);

        List<String> names = ListManager.getNames(nodes);
        assert("name0".compareTo(names.get(0)) == 0);
        assert("name1".compareTo(names.get(1)) == 0);
    }
}
