package edu.ucsd.cse110.team56.zooseeker.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    private TextView addedCountView;

    private List<View> addedListScreenViews, searchScreenViews;

    private List<NodeInfo> allNodes;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve local data
        allNodes = ExhibitsManager.getSingleton(this).getAllExhibits();

        // Initialize views
        searchListView = findViewById(R.id.data_list);
        addedExhibitsListView = findViewById(R.id.added_list);
        addedCountView = findViewById(R.id.added_count);

        // Organize views into screens
        addedListScreenViews = List.of(addedExhibitsListView, addedCountView, findViewById(R.id.clear_btn), findViewById(R.id.planBtn));
        searchScreenViews = List.of(searchListView);

        // Initialize visibility state
        UIOperations.hideViews(searchScreenViews);

        // Populate search suggestions list view
        searchFilterAdapter = new NodeInfoAdapter(this, android.R.layout.simple_list_item_multiple_choice, allNodes);
        searchListView.setAdapter(searchFilterAdapter);

        // Populate added exhibits list view
        final var addedNames = ExhibitsManager.getSingleton(this).getAddedAndVisitedNames();
        addedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addedExhibitsListView.setAdapter(addedListAdapter);

        // Setup checkmarks
        searchListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        CheckboxHandler.updateSearchedCheckBoxes(this, searchListView);
        searchListView.setOnItemClickListener(this::handleCheckboxClick);

        // Update count from database
        updateCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        closeSearchHandler();
    }

    @Override
    public void onBackPressed() {
        SearchView searchButton = findViewById(R.id.search_btn);
        if (!searchButton.isIconified()) {
            searchButton.onActionViewCollapsed();
            searchButton.setIconified(true);
            closeSearchHandler();
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
            ExhibitsManager.getSingleton(this).add(selectedItem);
        } else {
            ExhibitsManager.getSingleton(this).remove(selectedItem);
        }

        // update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getSingleton(this).getAddedAndVisitedNames());
        CheckboxHandler.updateSearchedCheckBoxes(this, searchListView);
    }

    private boolean closeSearchHandler() {
        UIOperations.hideViews(searchScreenViews);
        updateCount();
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getSingleton(this).getAddedAndVisitedList());
        UIOperations.showViews(addedListScreenViews);
        return false;
    }

    private void updateCount() {
        // Update added exhibits count
        final var displayCount = String.format(getString(R.string.added_visited_count_msg_prefix),
                ExhibitsManager.getSingleton(this).getAddedList().size(), ExhibitsManager.getSingleton(this).getVisitedList().size());
        addedCountView.setText(displayCount);
    }


    private SearchView.OnQueryTextListener makeQueryTextListener() {
        Activity activity = this;
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                UIOperations.hideViews(addedListScreenViews);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                UIOperations.hideViews(addedListScreenViews);

                searchFilterAdapter.getFilter().filter(s, i -> {
                    if (searchListView.getCount() == 0) {
                        runOnUiThread(() -> {
                            Toast errorToast = Toast.makeText(MainActivity.this, "Sorry, there's no matching result. Try to search something else...", Toast.LENGTH_SHORT);
                            errorToast.show();
                        });
                    }
                    CheckboxHandler.updateSearchedCheckBoxes(activity, searchListView);
                    UIOperations.setVisibility(searchScreenViews, !s.isEmpty());
                });

                return true;
            }
        };
    }

    // -------- Plan button handler --------

    public void onPlanBtnClicked(View view) {
        if (ExhibitsManager.getSingleton(this).getAddedAndVisitedList().isEmpty()) {
            PlanButton.displayNoExhibitsSelectedAlert(this);
        } else {
            PlanButton.startActivity(this, addedListAdapter);
        }
    }

    // --------- Clear Button Clicked --------
    public void onClearBtnClicked(View view) {
        // empty case
        if (ExhibitsManager.getSingleton(this).getAddedAndVisitedList().isEmpty()) {
            UIOperations.showDefaultAlert(this, getString(R.string.clear_button_disabled_msg));
            return;
        }

        // Update Database
        for (NodeInfo node : allNodes) {
            ExhibitsManager.getSingleton(this).remove(node);
        }

        // Update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ExhibitsManager.getSingleton(this).getAddedAndVisitedNames());
        CheckboxHandler.updateSearchedCheckBoxes(this, searchListView);

        updateCount();
    }

}