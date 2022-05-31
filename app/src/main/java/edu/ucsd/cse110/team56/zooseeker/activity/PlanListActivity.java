package edu.ucsd.cse110.team56.zooseeker.activity;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationPermissionsManager;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationUpdatesManager;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphVertex;
import edu.ucsd.cse110.team56.zooseeker.path.Path;

public class PlanListActivity extends AppCompatActivity {
    private SimpleAdapter planAdapter;
    private ListView destinationsListView = null;
    private List<Pair<NodeInfo, Double>> plan;
    public static Activity planActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        planActivity = this;
        setContentView(R.layout.activity_plan_list);
        destinationsListView = findViewById(R.id.destination);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setUpData();
    }

    private void setUpData() {
        ExhibitsManager.getSingleton(this).plan(null);
        this.plan = ExhibitsManager.getSingleton(this).getPlan();
         /*
            List of Plan Views with Distance Hints
         */

        // Populate Lists View
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> exhibitItem = new HashMap<String, String>();
        exhibitItem.put("Exhibit", "Destinations");
        exhibitItem.put("Hint", "Distance From You");
        data.add(exhibitItem);

        double distance = 0.0;

        for (Pair<NodeInfo, Double> item: plan) { //titleArray.length
            exhibitItem = new HashMap<String, String>();

            exhibitItem.put("Exhibit", item.first.name);
            if (item.first.status == NodeInfo.Status.VISITED) {
                exhibitItem.put("Hint", "visited");
            } else {
                distance += item.second;
                exhibitItem.put("Hint", distance + "ft");
            }

            data.add(exhibitItem);
        }

        // Set View
        planAdapter = new SimpleAdapter(this, data,
                R.layout.plan_list_items,
                new String[]{"Exhibit", "Hint"},
                new int[]{R.id.exhibit_planed, R.id.distance});

        destinationsListView.setAdapter(planAdapter);
    }

    public void onDirectionBtnClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }

    public void onResetButtonClicked(View view) {
        List<NodeInfo> list = ExhibitsManager.getSingleton(this).getVisitedList();
        for(NodeInfo node: list) {
            ExhibitsManager.getSingleton(this).add(node);
        }
        setUpData();
    }
}




