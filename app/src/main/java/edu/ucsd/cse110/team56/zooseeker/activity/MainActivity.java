package edu.ucsd.cse110.team56.zooseeker.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.LocationPermissionsManager;
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.NodeInfoAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.ArrayAdapterHelper;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationObserver;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.LocationUpdatesManager;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents.PlanButton;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents.CheckboxHandler;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<NodeInfo> searchFilterAdapter;
    private ArrayAdapter<String> addedListAdapter;

    private ListView searchListView, addedExhibitsListView;
    private TextView addedCountView, noResultView;

    private List<NodeInfo> allNodes;
    private List<String> allNodeNames;

    private Location lastVisitedLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request User Location Permission
        if (LocationPermissionsManager.needsLocationPermission(this)) {
            LocationPermissionsManager.requestLocationPermission(this);
            return;
        }

        // Retrieve local data
        allNodes = ExhibitsManager.getAllExhibits(this);
        allNodeNames = ExhibitsManager.getNames(allNodes);


        // Initialize views
        searchListView = findViewById(R.id.data_list);
        addedExhibitsListView = findViewById(R.id.added_list);
        addedCountView = findViewById(R.id.added_count);
        noResultView = findViewById(R.id.no_result_view);

        // Initialize visibility state
        UIOperations.hideViews(List.of(searchListView, noResultView));

        // Populate search suggestions list view
        searchFilterAdapter = new NodeInfoAdapter(this, android.R.layout.simple_list_item_multiple_choice, allNodes);
        searchListView.setAdapter(searchFilterAdapter);

        // Populate added exhibits list view
        final var addedNames = ExhibitsManager.getSingleton(this).getAddedListNames(allNodes);
        addedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addedExhibitsListView.setAdapter(addedListAdapter);

        // Setup checkmarks
        searchListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);
        searchListView.setOnItemClickListener(this::handleCheckboxClick);

        setupLocationUpdatesListener();

        // Update count from database
        updateCount();
    }

    @Override
    public void onBackPressed() {
        SearchView searchButton = findViewById(R.id.search_btn);
        if (!searchButton.isIconified()) {
            // if search field is visible
            searchButton.onActionViewCollapsed();
            searchButton.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // UI setup
        getMenuInflater().inflate(R.menu.search_bar_dropdown, menu); // inflate search menu
        final var searchButton = menu.findItem(R.id.search_btn); // get search button
        final var searchField = (SearchView) searchButton.getActionView(); // get search field
        searchField.setQueryHint(getString(R.string.search_animal_hint)); // set hint

        // set listeners
        searchField.setOnQueryTextListener(makeQueryTextListener());
        searchField.setOnCloseListener(this::closeSearchHandler);

        // return
        return super.onCreateOptionsMenu(menu);
    }

    /// -------- Search handlers --------

    private void handleCheckboxClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: " + position);
        // get the name of the animal
        final var selectedItemName = ((NodeInfo) searchListView.getItemAtPosition(position)).name;

        // add or remove the selected item based on `isChecked()` state
        final var allNames = ExhibitsManager.getSingleton(this).getNames(allNodes);
        final var selectedItem = allNodes.get(allNames.indexOf(selectedItemName));
        if (((CheckedTextView) view).isChecked()) {
            ExhibitsManager.getSingleton(this).addItem(selectedItem);
        } else {
            ExhibitsManager.getSingleton(this).removeItem(selectedItem);
        }

        // update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getSingleton(this).getAddedListNames(allNodes));
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);
    }

    private boolean closeSearchHandler() {
        UIOperations.hideViews(List.of(searchListView, noResultView));
        UIOperations.showViews(List.of(addedExhibitsListView, addedCountView));

        updateCount();
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getSingleton(this).getAddedListNames(allNodes));

        return false;
    }

    private void updateCount() {
        // Update added exhibits count
        final var displayCount = getString(R.string.added_count_msg_prefix)
                + ExhibitsManager.getSingleton(this).getAddedCount(allNodes);
        addedCountView.setText(displayCount);
    }


    private SearchView.OnQueryTextListener makeQueryTextListener() {
        Activity activity = this;
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                UIOperations.hideView(addedExhibitsListView);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                UIOperations.hideViews(List.of(
                        addedExhibitsListView, searchListView, addedCountView
                ));

                searchFilterAdapter.getFilter().filter(s, i -> {
                    CheckboxHandler.updateSearchedCheckBoxes(activity, allNodes, searchListView);
                    UIOperations.setVisibility(searchListView, !s.isEmpty());
                    //UIOperations.setVisibility(noResultView, s.isEmpty());
                });

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!inputIsValid(s)) {
                            Toast errorToast = Toast.makeText(MainActivity.this, "Sorry, there's no matching result. Try to search something else...", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    }
                });
                return true;
            }
        };
    }

    // -------- Plan button handler --------

    public void onPlanBtnClicked(View view) {
        if (ExhibitsManager.getSingleton(this).getAddedListNames(allNodes).size() == 0) {
            PlanButton.displayNoExhibitsSelectedAlert(this);
        } else {
            PlanButton.startPlanListActivity(this, addedListAdapter);
        }
    }

    // -------- Temp GPS Button Clicked --------
    // -------- Plan button handler --------

    public void onGPSBtnClicked(View view) {
//        Intent intent = new Intent(this, LocationActivity.class);
//        startActivity(intent);
    }

    // --------- Clear Button Clicked --------
    public void onClearBtnClicked(View view) {
        // empty case
        if (ExhibitsManager.getSingleton(this).getAddedListNames(allNodes).isEmpty()) {
            UIOperations.showDefaultAlert(this, getString(R.string.clear_button_disabled_msg));
            return;
        }

        // Update Database
        for (NodeInfo node : allNodes) {
            ExhibitsManager.getSingleton(this).removeItem(node);
        }

        // Update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getSingleton(this).getAddedListNames(allNodes));
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);

        // Update added exhibits count
        final var displayCount = getString(R.string.added_count_msg_prefix)
                + ExhibitsManager.getSingleton(this).getAddedCount(allNodes);
        addedCountView.setText(displayCount);
    }

    // -------- Handle location updates --------

    private void setupLocationUpdatesListener() {
        class DemoLocationObserver implements LocationObserver {
            @Override
            public void updateClosestNode(NodeInfo node, Location location) {
                Log.d("CurrentLocation", String.format("location: %s, exhibit: %s", location, node));
            }
        }
        LocationUpdatesManager.getSingleton(getApplicationContext()).registerObserver(new DemoLocationObserver());
    }

    private boolean inputIsValid(String s){
        for(int i = 0; i < allNodeNames.size(); i++){
           s.toLowerCase(Locale.ROOT);
            if(allNodeNames.get(i).toLowerCase(Locale.ROOT).contains(s))
                return true;
        }
        return false;
    }

}