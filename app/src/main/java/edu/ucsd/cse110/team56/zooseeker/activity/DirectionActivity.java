package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

public class DirectionActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public DirectionListAdapter adapter = new DirectionListAdapter();
    private ArrayList<GraphPath<String, GraphEdge>> directions;
    private int current = 0;
    private TextView destination;
    private View nextButton, previousButton, skipButton;

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
        this.directions = Graph.load(this).generatePaths(ExhibitsManager.getListId(addedNode), "entrance_exit_gate");

        // setup buttons

        previousButton = findViewById(R.id.pre_btn);
        nextButton = findViewById(R.id.next_btn);
        skipButton = findViewById(R.id.skip_btn);

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
        current += 2;
        updateUI();
    }

    /**
     * retrieves data, updates texts and button visibility
     */
    private void updateUI() {
        adapter.setPaths(this.directions.get(current).getEdgeList(), this);
        NodeInfo info = ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).getEndVertex());
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

}