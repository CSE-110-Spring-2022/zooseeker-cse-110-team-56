package edu.ucsd.cse110.team56.zooseeker.activity;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.NodeInfoAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.ArrayAdapterHelper;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.R;
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

    // LatLng Current Location
    protected LatLng currLocation;

    private Location lastVisitedLocation;
    public final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), perms -> {
                perms.forEach((perm, isGranted) -> {
                    Log.i("UserLocation", String.format("Permission %s granted: %s", perm, isGranted));
                });
            });

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request User Location Permission
        if (ensureLocationPermission()) return;

        /* Listen for Location Updates */
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("CurrLocation", String.format("Location changed: %s", location));
                    currLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    System.out.println("Christina is at: ");
                    System.out.println(currLocation.latitude);
                    System.out.println(currLocation.longitude);
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }


        // Retrieve local data
        allNodes = ExhibitsManager.getAllExhibits(this);

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
        final var addedNames = ExhibitsManager.getAddedListNames(allNodes);
        addedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addedExhibitsListView.setAdapter(addedListAdapter);

        // Setup checkmarks
        searchListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);
        searchListView.setOnItemClickListener(this::handleCheckboxClick);

        // Update count from database
        updateCount();
    }

    private boolean ensureLocationPermission() {
        /* Permission Setup */
        {
            var requiredPermissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

            var hasNoLocationPerms = Arrays.stream(requiredPermissions)
                    .map(perm -> ContextCompat.checkSelfPermission(this, perm))
                    .allMatch(status -> status == PackageManager.PERMISSION_DENIED);

            if (hasNoLocationPerms) {
                requestPermissionLauncher.launch(requiredPermissions);
                return true;
            }

        }
        return false;
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
        final var allNames = ExhibitsManager.getNames(allNodes);
        final var selectedItem = allNodes.get(allNames.indexOf(selectedItemName));
        if (((CheckedTextView) view).isChecked()) {
            ExhibitsManager.addItem(searchListView.getContext(), selectedItem);
        } else {
            ExhibitsManager.removeItem(searchListView.getContext(), selectedItem);
        }

        // update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getAddedListNames(allNodes));
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);
    }

    private boolean closeSearchHandler() {
        UIOperations.hideViews(List.of(searchListView, noResultView));
        UIOperations.showViews(List.of(addedExhibitsListView, addedCountView));

        updateCount();
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getAddedListNames(allNodes));

        return false;
    }

    private void updateCount() {
        // Update added exhibits count
        final var displayCount = getString(R.string.added_count_msg_prefix)
                + ExhibitsManager.getAddedCount(allNodes);
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
                    if (!s.isEmpty()) {
                        UIOperations.showView(searchListView);
                    }
                    // update the visibility of `noResultView`
                    runOnUiThread(() -> noResultView.setVisibility(
                            searchListView.getCount() == 0 ? View.VISIBLE : View.INVISIBLE
                    ));
                });

                return true;
            }
        };
    }

    // -------- Plan button handler --------

    public void onPlanBtnClicked(View view) {
        if (ExhibitsManager.getAddedListNames(allNodes).size() == 0) {
            PlanButton.displayNoExhibitsSelectedAlert(this);
        } else {
            PlanButton.startPlanListActivity(this, addedListAdapter);
        }
    }

    // -------- Temp GPS Button Clicked ------------
    // -------- Plan button handler --------

    public void onGPSBtnClicked(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }




}