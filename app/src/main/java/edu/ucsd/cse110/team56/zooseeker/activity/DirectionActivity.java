package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.directions);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        destination = findViewById(R.id.destination_text);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        List<NodeInfo> addedNode = ExhibitsManager.getAddedList(ZooDatabase.getSingleton(this).zooDao().getAllNodes());
        this.directions = Graph.load(this).generatePaths(ExhibitsManager.getListId(addedNode), "entrance_exit_gate", "entrance_exit_gate");

        // hide pre_btn for the first one
        FloatingActionButton button = findViewById(R.id.pre_btn);
        button.setVisibility(View.GONE);

        // Previoius
        findViewById(R.id.pre_btn).setOnClickListener((view -> {
            previous();
        }));

        // Skip Next
        findViewById(R.id.skip_btn).setOnClickListener((view -> {
            if (current >= this.directions.size() - 2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Skip Disabled.\nThere's less than one exhibit left")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                //find current node
                NodeInfo currInfo = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).getStartVertex());
                //remove all node before next exhibit (include next exhibit)
                current+=2;
                while(current > -1){
                    addedNode.remove(ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).getStartVertex()));
                    current--;
                }
                // regenerate route for the rest of the exhibits
                this.directions = Graph.load(this).generatePaths(ExhibitsManager.getListId(addedNode), currInfo.id, "entrance_exit_gate");
                current=-1;
                next();
            }
        }));

        //hide last button for the second last one
        if (current >= this.directions.size() - 2) {
            FloatingActionButton b = findViewById(R.id.skip_btn);
            b.setVisibility(View.GONE);
        }

        // Next
        findViewById(R.id.next_btn).setOnClickListener((view -> {
            next();
        }));

        next();
    }

    //next exhibit direction
    @VisibleForTesting
    public void next() {
        current++;
        adapter.setPaths(this.directions.get(current).getEdgeList(), this);
        NodeInfo info = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).getEndVertex());
        this.destination.setText("Next: " + info.name);
        // hide skipNextBtn for the last one
        nextBtnView();
        // hide pre_btn for the first one
        if (current > 0) {
            FloatingActionButton button = findViewById(R.id.pre_btn);
            button.setVisibility(View.VISIBLE);
        }
    }

    //previous exhibit direction
    public void previous() {
        System.out.println(current);
        current--;
        System.out.println(current);
        adapter.setPaths(this.directions.get(current).getEdgeList(), this);
        NodeInfo info = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).getEndVertex());
        this.destination.setText("Next: " + info.name);
        // hide pre_btn for the first one
        preBtnView();
    }

    public void nextBtnView(){
        if (current == this.directions.size() - 1) {
            FloatingActionButton button = findViewById(R.id.next_btn);
            button.setVisibility(View.GONE);
        } else {
            FloatingActionButton button = findViewById(R.id.next_btn);
            button.setVisibility(View.VISIBLE);
        }
    }

    public void preBtnView(){
        if (current > 0) {
            FloatingActionButton button = findViewById(R.id.pre_btn);
            button.setVisibility(View.VISIBLE);
        } else {
            FloatingActionButton button = findViewById(R.id.pre_btn);
            button.setVisibility(View.GONE);
        }
    }

}