package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.directionActivityUIComponents.SettingsButton;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents.PlanButton;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;
import edu.ucsd.cse110.team56.zooseeker.path.Path;

public class DirectionActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public DirectionListAdapter adapter = new DirectionListAdapter();
    private ArrayList<Path> directions;
    private int current = 0;
    private TextView destination;
    private View nextButton, previousButton, skipButton;
    private List<NodeInfo> addedNodes;

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
                addedNodes.remove(ZooDatabase.getSingleton(this).zooDao().getNode(this.directions.get(current).path.getStartVertex()));
                current--;
            }
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
        adapter.setPaths(this.directions.get(current).path.getEdgeList(), this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.directions_top_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onSettingsBtnClicked(MenuItem item) { SettingsButton.startActivity(this); }
}