package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.DirectionActivity;
import edu.ucsd.cse110.team56.zooseeker.activity.MainActivity;
import edu.ucsd.cse110.team56.zooseeker.activity.PlanListActivity;
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

@RunWith(AndroidJUnit4.class)
public class PlanListActivityTest {
    @Before
    public void refreshDb() {
        ZooDatabase.refreshDb(ApplicationProvider.getApplicationContext());
        ZooDao dao = ZooDatabase.getSingleton(ApplicationProvider.getApplicationContext()).zooDao();

        NodeInfo node1 = dao.getNode("elephant_odyssey");
        node1.setStatus(NodeInfo.Status.ADDED);
        dao.updateNode(node1);

        NodeInfo node2 = dao.getNode("gorillas");
        node1.setStatus(NodeInfo.Status.ADDED);
        dao.updateNode(node2);

        NodeInfo node3 = dao.getNode("arctic_foxes");
        node1.setStatus(NodeInfo.Status.ADDED);
        dao.updateNode(node3);
    }


    @Test
    public void testPlanDisplay() {
        ActivityScenario<PlanListActivity> scenario = ActivityScenario.launch(PlanListActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            ListView view = activity.findViewById(R.id.destination);
            assertEquals("Arctic Foxes", view.getItemAtPosition(0));
            assertEquals("Gorillas", view.getItemAtPosition(1));
            assertEquals("Elephant Odyssey", view.getItemAtPosition(2));
            assertEquals("Entrance and Exit Gate", view.getItemAtPosition(3));
        });
    }
}
