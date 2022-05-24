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
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

@RunWith(AndroidJUnit4.class)
public class DirectionActivityTest {
    @Before
    public void refreshDb() {
        ZooDatabase.refreshDb(ApplicationProvider.getApplicationContext());
        ZooDao dao = ZooDatabase.getSingleton(ApplicationProvider.getApplicationContext()).zooDao();

        NodeInfo node1 = dao.getNode("elephant_odyssey");
        node1.added = true;
        dao.updateNode(node1);

        NodeInfo node2 = dao.getNode("gorillas");
        node2.added = true;
        dao.updateNode(node2);

        NodeInfo node3 = dao.getNode("arctic_foxes");
        node3.added = true;
        dao.updateNode(node3);
    }

    /**
     * Make sure a direction consists of different paths can be displayed correctly.
     * gate -> plaza -> foxes
     */
    @Test
    public void testDisplaySimplePaths() {
        ActivityScenario<DirectionActivity> scenario = ActivityScenario.launch(DirectionActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            assertEquals(2, recyclerView.getAdapter().getItemCount());

            TextView destination  = activity.findViewById(R.id.destination_text);
            assertEquals("Next: Arctic Foxes", destination.getText());

            DirectionListAdapter.ViewHolder edge1VH = (DirectionListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(edge1VH);
            TextView direction1 = edge1VH.itemView.findViewById(R.id.direction_text);
            assertEquals("Proceed on Entrance Way 10.0 ft towards Entrance Plaza", direction1.getText().toString());

            DirectionListAdapter.ViewHolder edge2VH = (DirectionListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(1);
            assertNotNull(edge2VH);
            TextView direction2 = edge2VH.itemView.findViewById(R.id.direction_text);
            assertEquals("Proceed on Arctic Avenue 300.0 ft towards Arctic Foxes", direction2.getText().toString());

        });
    }

    /**
     * next button should be invisible after 3 clicks.
     */
    @Test
    public void testNextButton() {
        ActivityScenario<DirectionActivity> scenario = ActivityScenario.launch(DirectionActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;

            FloatingActionButton button  = activity.findViewById(R.id.next_btn);
            assertEquals(View.VISIBLE, button.getVisibility());
            assertEquals(2, recyclerView.getAdapter().getItemCount());

            button.performClick();
            assertEquals(View.VISIBLE, button.getVisibility());
            assertEquals(2, recyclerView.getAdapter().getItemCount());

            button.performClick();
            assertEquals(View.VISIBLE, button.getVisibility());
            assertEquals(2, recyclerView.getAdapter().getItemCount());

            button.performClick();
            assertEquals(View.INVISIBLE, button.getVisibility());
            assertEquals(4, recyclerView.getAdapter().getItemCount());
        });
    }

    /**
     * Make sure continuing on a same path is shown as "Continue On" instead of "Proceed On".
     */
    @Test
    public void testDisplayComplexPaths() {
        ActivityScenario<DirectionActivity> scenario = ActivityScenario.launch(DirectionActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            activity.onNext();
            activity.onNext();
        });
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            FloatingActionButton button  = activity.findViewById(R.id.next_btn);
            RecyclerView recyclerView = activity.recyclerView;
            assertEquals(2, recyclerView.getAdapter().getItemCount());


            try {
                Thread.sleep(1000); // wait for view holder to update.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TextView destination  = activity.findViewById(R.id.destination_text);
            assertEquals("Next: Elephant Odyssey", destination.getText());

            DirectionListAdapter.ViewHolder edge1VH = (DirectionListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(edge1VH);
            TextView direction1 = edge1VH.itemView.findViewById(R.id.direction_text);
            assertEquals("Proceed on Africa Rocks Street 200.0 ft towards Lions", direction1.getText().toString());

            DirectionListAdapter.ViewHolder edge2VH = (DirectionListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(1);
            assertNotNull(edge2VH);
            TextView direction2 = edge2VH.itemView.findViewById(R.id.direction_text);
            assertEquals("Continue on Africa Rocks Street 200.0 ft towards Elephant Odyssey", direction2.getText().toString());

        });
    }
}
