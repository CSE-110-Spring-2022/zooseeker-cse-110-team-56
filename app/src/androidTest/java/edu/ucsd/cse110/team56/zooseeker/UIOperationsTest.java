package edu.ucsd.cse110.team56.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.MainActivity;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;

@RunWith(AndroidJUnit4.class)
public class UIOperationsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainRule = new ActivityScenarioRule<>(MainActivity.class);


    /**
     * Integration Test for UIOperations
     */
    @Test
    public void testUIOperations() {

        ActivityScenario<MainActivity> scenario = mainRule.getScenario();

        // Make sure the activity is in the created state (so onCreated is called).
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {


            View added = activity.findViewById(R.id.added_count);
            View added_list = activity.findViewById(R.id.added_list);
            List<View> list = new ArrayList<>();
            list.add(added);
            list.add(added_list);

            // Test showView
            UIOperations.showView(added);
            assertEquals(View.VISIBLE, added.getVisibility());

            // Test hideView
            UIOperations.hideView(added);
            assertEquals(View.INVISIBLE, added.getVisibility());

            // Test setVisibility
            UIOperations.setVisibility(added, true);
            assertEquals(View.VISIBLE, added.getVisibility());
            UIOperations.setVisibility(added, false);
            assertEquals(View.INVISIBLE, added.getVisibility());

            // Test showViews
            UIOperations.showViews(list);
            for (View view : list) {
                assertEquals(View.VISIBLE, view.getVisibility());
            }

            // Test hideViews
            UIOperations.hideViews(list);
            for (View view : list) {
                assertEquals(View.INVISIBLE, view.getVisibility());
            }

            //Test setVisibility for Views
            UIOperations.setVisibility(list, true);
            for (View view : list) {
                assertEquals(View.VISIBLE, view.getVisibility());
            }
            UIOperations.setVisibility(list, false);
            for (View view : list) {
                assertEquals(View.INVISIBLE, view.getVisibility());
            }

        });
    }


}
