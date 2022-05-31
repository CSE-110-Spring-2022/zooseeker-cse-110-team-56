package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.DirectionListAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationObserver;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationUpdatesManager;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.directionActivityUIComponents.SettingsButton;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.Path;

public class DirectionActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public DirectionListAdapter adapter;
    private Set<String> pathNodes = new HashSet<>();
    private TextView destination;
    private View nextButton, previousButton, skipButton;
    private NodeInfo current;
    private DemoLocationObserver observer = new DemoLocationObserver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.directions);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DirectionListAdapter(this);
        recyclerView.setAdapter(adapter);
        destination = findViewById(R.id.destination_text);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // setup buttons
        previousButton = findViewById(R.id.pre_btn);
        nextButton = findViewById(R.id.next_btn);
        skipButton = findViewById(R.id.skip_btn);

        previousButton.setOnClickListener(view -> onPrevious());
        nextButton.setOnClickListener(view -> onNext());
        skipButton.setOnClickListener(view -> onSkip());

        setupLocationUpdatesListener();

        // update
        updateUI();
    }

    // hide the previous button for the first one
    private boolean previousButtonAssertion() { return ExhibitsManager.getSingleton(this).canStepBack(); }

    // hide the next button for the last one
    private boolean nextButtonAssertion() { return ExhibitsManager.getSingleton(this).getNextNode().kind != NodeInfo.Kind.GATE; }

    // hide the skip button for the last two
    private boolean skipButtonAssertion() { return ExhibitsManager.getSingleton(this).canSkip(); }

    /**
     * @apiNote precondition: current > 0
     */
    public void onPrevious() {
        assert previousButtonAssertion();
        ExhibitsManager.getSingleton(this).stepBack();
        updateUI();
    }

    /**
     * @apiNote precondition: current < this.directions.size() - 1
     */
    public void onNext() {
        assert nextButtonAssertion();
        current = ExhibitsManager.getSingleton(this).getNextNode(); // preview, assume already at the next exhibit
        ExhibitsManager.getSingleton(this).next();
        updateUI();
        current = observer.getLastNode(); // restore back
    }

    /**
     * @apiNote precondition: current < this.directions.size() - 2
     */
    public void onSkip() {
        assert skipButtonAssertion();
        ExhibitsManager.getSingleton(this).skip();
        updateUI();
    }

    /**
     * retrieves data, updates texts and button visibility
     */
    private void updateUI() {
        Path path = ExhibitsManager.getSingleton(this).getPathToNextExhibit(current);
        adapter.setPaths(this, path);
        this.pathNodes = path.nodesSet();
        NodeInfo info = path.endInfo.getActualExhibit();
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

    public void onSettingsBtnClicked(MenuItem item) {
        SettingsButton.startActivity(this);
    }

    public void onMockBtnClicked(MenuItem item) {

    }

    private void setupLocationUpdatesListener() {
        LocationUpdatesManager.getSingleton(getApplicationContext()).registerObserver(observer);
    }

    class DemoLocationObserver implements LocationObserver {
        private NodeInfo lastNode;
        public NodeInfo getLastNode() {
            return lastNode;
        }

        @Override
        public void updateLocation(Location location) {
            Log.d("DirectionActivity", String.format("location: %s", location));
        }

        @Override
        public void updateClosestNode(NodeInfo node) {
            if (node.equals(current)) return;

            lastNode = node;
            current = node;

            if (!pathNodes.contains(node.id)) {
                UIOperations.showDialog(
                        DirectionActivity.this,
                        "It seems that you've gone offtrack. Do you want to re-plan?",
                        "No",
                        "Yes",
                        (dialog, value) -> {
                            ExhibitsManager.getSingleton(DirectionActivity.this).plan(current);
                            updateUI();
                        }
                );
            }
            updateUI();
            Log.d("DirectionActivity", String.format("exhibit: %s", node));
        }
    }

}