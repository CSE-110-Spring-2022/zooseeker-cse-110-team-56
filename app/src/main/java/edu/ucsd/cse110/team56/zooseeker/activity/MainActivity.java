package edu.ucsd.cse110.team56.zooseeker.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Semaphore;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.NodeInfoAdapter;
import edu.ucsd.cse110.team56.zooseeker.activity.adapter.ArrayAdapterHelper;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.DatabaseGetterManager;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ListManager;
import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents.PlanButton;
import edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents.SearchSuggestionsList;
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
        List<String> addedNames = ListManager.getAddedListNames(allNodes);
        addedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addedExhibitsListView.setAdapter(addedListAdapter);

        // Setup checkmarks
        searchListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        SearchSuggestionsList.updateSearchedCheckBoxes(this, allNodes, searchListView);
        searchListView.setOnItemClickListener(this::handleCheckboxClick);
    }

    /// -------- Search handlers --------

    @Override
    public void onBackPressed() {
        SearchView searchView = findViewById(R.id.search_btn);
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // UI setup
        getMenuInflater().inflate(R.menu.search_bar_dropdown, menu);
        MenuItem menuItem = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_animal_hint));

        // set listeners
        searchView.setOnQueryTextListener(makeQueryTextListener());
        searchView.setOnCloseListener(this::closeSearchHandler);

        // return
        return super.onCreateOptionsMenu(menu);
    }

    private void handleCheckboxClick(AdapterView parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: " + position);
        // get the name of the animal
        String selectedItemName = ((NodeInfo) searchListView.getItemAtPosition(position)).name;

        // add or remove the selected item based on `isChecked()` state
        var allNames = ListManager.getNames(allNodes);
        NodeInfo selectedItem = allNodes.get(allNames.indexOf(selectedItemName));
        if (((CheckedTextView) view).isChecked()) {
            ListManager.addItem(searchListView.getContext(), selectedItem);
        } else {
            ListManager.removeItem(searchListView.getContext(), selectedItem);
        }

        // update UI elements
        ArrayAdapterHelper.updateAdapter(addedListAdapter, ListManager.getAddedListNames(allNodes));
        SearchSuggestionsList.updateSearchedCheckBoxes(this, allNodes, searchListView);
    }

    private boolean closeSearchHandler() {
        UIOperations.hideViews(List.of(searchListView, noResultView));
        UIOperations.showViews(List.of(addedExhibitsListView, addedCountView));

        // Update added exhibits count
        String displayCount = getString(R.string.added_count_msg_prefix)
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
                    SearchSuggestionsList.updateSearchedCheckBoxes(activity, allNodes, searchListView); // Update after filtering

                    if(!s.isEmpty()){
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