package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

public class DirectionActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public DirectionListAdapter adapter = new DirectionListAdapter();
    private ArrayList<GraphPath<String, GraphEdge>> directions;
    private int current = -1;
    private TextView destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        recyclerView = findViewById(R.id.directions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        destination = findViewById(R.id.destination_text);

        List<String> toVisit = Arrays.asList("entrance_exit_gate", "lions", "gators", "entrance_exit_gate");
        this.directions = Graph.load(this).findPaths(this, toVisit);

        findViewById(R.id.next_btn).setOnClickListener((view -> {
            next();
        }));

        next();
    }

    private void next() {
        current++;
        adapter.setPaths(this.directions.get(current).getEdgeList());
        NodeInfo info = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).getEndVertex());
        this.destination.setText(info.name);

        if (current == this.directions.size() - 1) {
            FloatingActionButton button = findViewById(R.id.next_btn);
            button.setVisibility(View.GONE);
        }
    }
}