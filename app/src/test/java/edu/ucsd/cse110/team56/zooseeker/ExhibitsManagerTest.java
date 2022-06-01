package edu.ucsd.cse110.team56.zooseeker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExhibitsManagerTest {
    @Before
    public void refreshDb() {
        ZooDatabase.refreshDb(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void addItemTest() {
        final var context = ApplicationProvider.getApplicationContext();
        final var manager = ExhibitsManager.getSingleton(context);

        final var nodeInfo1 = manager.getAllExhibits().get(0);
        manager.add(nodeInfo1);

        assert(nodeInfo1.getStatus() == NodeInfo.Status.ADDED);

        final var addedList = manager.getAddedList();
        assert(addedList.contains(nodeInfo1));
    }

    @Test
    public void removeItemTest() {
        final var context = ApplicationProvider.getApplicationContext();
        final var manager = ExhibitsManager.getSingleton(context);

        final var nodeInfo1 = manager.getAllExhibits().get(0);
        manager.add(nodeInfo1);
        manager.remove(nodeInfo1);

        assert(nodeInfo1.getStatus() == NodeInfo.Status.LOADED);

        final var addedList = manager.getAddedList();
        assert(!addedList.contains(nodeInfo1));
    }

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

    @Test
    public void canSkipTest() {
        final var context = ApplicationProvider.getApplicationContext();
        final var manager = ExhibitsManager.getSingleton(context);

        // before add
        assert(!manager.canSkip());

        // add
        final var nodeInfo1 = manager.getAllExhibits().get(0);
        manager.add(nodeInfo1);

        // after add
        assert(manager.canSkip());
    }

    @Test
    public void planTest() {
        final var context = ApplicationProvider.getApplicationContext();
        final var manager = ExhibitsManager.getSingleton(context);

        // add
        final var nodeInfo0 = manager.getAllExhibits().get(0);
        manager.add(nodeInfo0);
        final var nodeInfo1 = manager.getAllExhibits().get(1);
        manager.add(nodeInfo1);
        final var nodeInfo2 = manager.getAllExhibits().get(2);
        manager.add(nodeInfo2);

        // plan
        manager.plan(nodeInfo1);
        final var planList = manager.getPlan().stream()
                .map(pair -> pair.first)
                .collect(Collectors.toList());

        // check
        assert(planList.contains(nodeInfo0));
        assert(planList.contains(nodeInfo1));
        assert(planList.contains(nodeInfo2));
        assert(planList.contains(manager.getGate()));
    }

    @Test
    public void nextTest() {

        final var context = ApplicationProvider.getApplicationContext();
        final var manager = ExhibitsManager.getSingleton(context);

        // add
        final var nodeInfo0 = manager.getAllExhibits().get(0);
        manager.add(nodeInfo0);
        final var nodeInfo1 = manager.getAllExhibits().get(1);
        manager.add(nodeInfo1);

        // plan
        manager.plan(nodeInfo0);
        manager.next();
        final var planList = manager.getPlan().stream()
                .map(pair -> pair.first)
                .collect(Collectors.toList());

        // check
        assert(manager.getVisitedList().contains(nodeInfo0));
        assert(!manager.getVisitedList().contains(nodeInfo1));
    }
}
