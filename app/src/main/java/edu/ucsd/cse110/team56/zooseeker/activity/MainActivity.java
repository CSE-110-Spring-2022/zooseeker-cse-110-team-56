package edu.ucsd.cse110.team56.zooseeker.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.NodeInfoAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.ArrayAdapterHelper;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.DatabaseGetterManager;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ListManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve local data
        allNodes = DatabaseGetterManager.getAllNodes(this);

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
        final var addedNames = ListManager.getAddedListNames(allNodes);
        addedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addedExhibitsListView.setAdapter(addedListAdapter);

        // Setup checkmarks
        searchListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);
        searchListView.setOnItemClickListener(this::handleCheckboxClick);
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
        final var allNames = ListManager.getNames(allNodes);
        final var selectedItem = allNodes.get(allNames.indexOf(selectedItemName));
        if (((CheckedTextView) view).isChecked()) {
            ListManager.addItem(searchListView.getContext(), selectedItem);
        } else {
            ListManager.removeItem(searchListView.getContext(), selectedItem);
        }

        // update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ListManager.getAddedListNames(allNodes));
        CheckboxHandler.updateSearchedCheckBoxes(this, allNodes, searchListView);
    }

    private boolean closeSearchHandler() {
        UIOperations.hideViews(List.of(searchListView, noResultView));
        UIOperations.showViews(List.of(addedExhibitsListView, addedCountView));

        // Update added exhibits count
        final var displayCount = getString(R.string.added_count_msg_prefix)
                + ListManager.getAddedCount(allNodes);
        addedCountView.setText(displayCount);

        return false;
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
                    // `Filter.filter()` is asynchronous and has an optional listener;
                    // run update code using the listener to ensure that the UI updates
                    // only **after** changes are made, and not **before**

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
        if (ListManager.getAddedListNames(allNodes).size() == 0) {
            PlanButton.displayNoExhibitsSelectedAlert(this);
        } else {
            PlanButton.startPlanListActivity(this, addedListAdapter);
        }
    }
}