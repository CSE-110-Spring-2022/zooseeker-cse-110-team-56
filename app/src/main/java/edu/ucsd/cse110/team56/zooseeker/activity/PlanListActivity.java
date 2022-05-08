package edu.ucsd.cse110.team56.zooseeker.activity;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ListManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

public class PlanListActivity extends AppCompatActivity {
    private ArrayAdapter<String> addedAdapter;
    public DirectionListAdapter adapter = new DirectionListAdapter();
    private ArrayList<GraphPath<String, GraphEdge>> directions;
    private int current = -1;
    private TextView destination;
    private ListView des = null;
    private List<NodeInfo> addedNode;
    private RecyclerView recyclerView;
    public List<String> desl = new ArrayList<>();
    public List<String> addedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);
        addedNode = ListManager.getAddedList(ZooDatabase.getSingleton(this).zooDao().getAllNodes());
        addedId = ListManager.getListId(addedNode);
        //List<String> toVisit = Arrays.asList("entrance_exit_gate", "lions", "gators", "entrance_exit_gate");
        this.directions = Graph.load(this).generatePaths(addedId, "entrance_exit_gate");
        for (GraphPath<String, GraphEdge> path : this.directions) {
            desl.add(ZooDatabase.getSingleton(this).zooDao().getNode(path.getEndVertex()).getName());
        }


        des = findViewById(R.id.destination);
        addedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, desl);
        des.setAdapter(addedAdapter);

    }

    public void onDirectionBtnClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }

    public void onBackToMainClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}




