package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationObserver;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.Path;

public class DirectionActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public DirectionListAdapter adapter = new DirectionListAdapter();
    private ArrayList<Path> directions;
    private int current = 0;
    private TextView destination;
    private View nextButton, previousButton, skipButton;
    private Button replanButton;
    private List<NodeInfo> addedNodes;

    private LocationObserver currLocation;

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

        addedNodes = ExhibitsManager.getSingleton(this).getAddedList(ZooDatabase.getSingleton(this).zooDao().getAllNodes());

        NodeInfo gate = ZooDatabase.getSingleton(this).zooDao().getNode("entrance_exit_gate");

        this.directions = Graph.load(this).generatePaths(
                ExhibitsManager.getSingleton(this).getNavigationVertexIds(addedNodes),
                ExhibitsManager.getSingleton(this).getGraphVertex(gate),
                ExhibitsManager.getSingleton(this).getGraphVertex(gate));

        // setup buttons
        previousButton = findViewById(R.id.pre_btn);
        nextButton = findViewById(R.id.next_btn);
        skipButton = findViewById(R.id.skip_btn);
        replanButton = findViewById(R.id.replan_btn);



        previousButton.setOnClickListener(view -> onPrevious());
        nextButton.setOnClickListener(view -> onNext());
        skipButton.setOnClickListener(view -> onSkip());

        // update

        updateUI();
    }

    // hide the previous button for the first one
    private boolean previousButtonAssertion() { return current > 0; }

    // hide the next button for the last one
    private boolean nextButtonAssertion() { return current < directions.size() - 1; }

    // hide the skip button for the last two
    private boolean skipButtonAssertion() { return current < directions.size() - 2; }

    /**
     * @apiNote precondition: current > 0
     */
    public void onPrevious() {
        assert previousButtonAssertion();
        current--;
        updateUI();
    }

    /**
     * @apiNote precondition: current < this.directions.size() - 1
     */
    @VisibleForTesting
    public void onNext() {
        assert nextButtonAssertion();
        current++;
        updateUI();
    }

    /**
     * @apiNote precondition: current < this.directions.size() - 2
     */
    public void onSkip() {
        assert skipButtonAssertion();

        {
            //find current node
            NodeInfo currInfo = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getStartVertex());
            //remove all node before next exhibit (include next exhibit)
            current += 2;
            while(current > -1){
                //addedNodes.remove(ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getStartVertex()));
                NodeInfo currNode = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getStartVertex());
                ExhibitsManager.getSingleton(this).removeItem(currNode);
                current--;
            }

           addedNodes = ExhibitsManager.getSingleton(this).getAddedList(ZooDatabase.getSingleton(this).zooDao().getAllNodes());

            // regenerate route for the rest of the exhibits
            this.directions = Graph.load(this).generatePaths(
                    ExhibitsManager.getSingleton(this).getNavigationVertexIds(addedNodes),
                    ExhibitsManager.getSingleton(this).getGraphVertex(currInfo),
                    ExhibitsManager.getSingleton(this).getGraphVertex(ZooDatabase.getSingleton(this).zooDao().getNode("entrance_exit_gate")));
            current = 0;
        }

        updateUI();
    }

    /**
     * retrieves data, updates texts and button visibility
     */
    private void updateUI() {
        adapter.setPaths(this, this.directions.get(current).path.getEdgeList());
        NodeInfo info = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getEndVertex());
        this.destination.setText(getString(R.string.next_destination, info.name));
        updateButtonsVisibility();
    }

    /**
     * @apiNote postcondition: this method should assure the preconditions of
     * `onNext`, `onPrevious`, and `onSkip`
     */
    private void updateButtonsVisibility() {
        UIOperations.setVisibility(previousButton, previousButtonAssertion());
        UIOperations.setVisibility(nextButton, nextButtonAssertion());
        UIOperations.setVisibility(skipButton, skipButtonAssertion());
    }

    /**
     * Replan when Replan Button is Clicked
     */
    public void onReplanBtnClicked(View view) {

        // Remove the previous nodes
        NodeInfo currNode;
        while(current > -1){
            addedNodes.remove(ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getStartVertex()));
            currNode = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getStartVertex());
            ExhibitsManager.getSingleton(this).removeItem(currNode);
            current--;
        }

        addedNodes = ExhibitsManager.getSingleton(this).getAddedList(ZooDatabase.getSingleton(this).zooDao().getAllNodes());
        for (NodeInfo node : addedNodes) {
            System.out.println(node.name + "are left");
        }

        PlanListActivity.planActivity.finish();
        // Return to PlanActivity for the New Plan
        Intent intent = new Intent(this, PlanListActivity.class);
        startActivity(intent);
        finish();
    }

}