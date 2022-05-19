package edu.ucsd.cse110.team56.zooseeker.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<NodeInfo> searchFilterAdapter;
    private ArrayAdapter<String> addedListAdapter;

    // Search ListView
    private ListView searchListView;
    // Added ListView
    private ListView addedExhibitsListView;
    // Added Number TextView
    private TextView addedCountView;
    // Error Msg Text Views
    private TextView noResultView;

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
        updateSearchedCheckBoxes(allNodes);
        searchListView.setOnItemClickListener(this::handleCheckboxClick);
    }

    // -------- Update list views --------

    /**
     * Updates the "search suggestions" list using the `isAdded` attribute of
     * all items currently shown in the list
     * @param nodes the list of nodes that contains the added nodes for which
     *              you want to mark checkboxes as "checked"
     */
    private void updateSearchedCheckBoxes(List<NodeInfo> nodes) {
        Semaphore mutex = new Semaphore(0);
        runOnUiThread(() -> {
            updateSearchedCheckBoxesInternal(nodes);
            mutex.release();
        });
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateSearchedCheckBoxesInternal(List<NodeInfo> nodes) {
        // loops through the current list of search suggestions
        for (int i = 0; i < searchListView.getCount(); i++) {
            String currentItemName = ((NodeInfo) searchListView.getItemAtPosition(i)).name;

            // the index of the current item within the `allNodes` list
            int currentItemIndex = ListManager.getNames(nodes).indexOf(currentItemName);

            // retrieve the node
            NodeInfo currentItem = nodes.get(currentItemIndex);

            if (searchListView.isItemChecked(i) != currentItem.isAdded()) {
                searchListView.setItemChecked(i, currentItem.isAdded());
            }
        }
    }

    private void noResultDisplay() {
        runOnUiThread(() -> noResultView.setVisibility(
                searchListView.getCount() == 0 ? View.VISIBLE : View.INVISIBLE
        ));
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
        updateSearchedCheckBoxes(allNodes);
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

                // `Filter.filter()` is asynchronous and has an optional listener;
                // run update code using the listener to ensure that the UI updates
                // only **after** changes are made, and not **before**
                searchFilterAdapter.getFilter().filter(s, i -> {
//                        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    updateSearchedCheckBoxes(allNodes); // Update after filtering
//                        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // workaround to disable check animation
                    if(!s.isEmpty()){
                        UIOperations.showView(searchListView);
                    }
                    noResultDisplay();
                });

                return true;
            }
        };
    }

    // -------- Plan button handler --------

    /*
        Jump to the PLAN view when the PLAN button is clicked
    */
    public void onPlanBtnClicked(View view) {
        if (ListManager.getAddedListNames(allNodes).size() == 0) {
            displayNoExhibitsSelectedAlert();
        } else {
            startPlanListActivity();
        }
    }

    private void displayNoExhibitsSelectedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_exhibits_selected_msg))
                .setPositiveButton(
                        getString(R.string.ok),
                        (DialogInterface dialog, int id) -> dialog.cancel()
                );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startPlanListActivity() {
        Intent intent = new Intent(this, PlanListActivity.class);
        addedListAdapter.notifyDataSetChanged();
        startActivity(intent);
    }

}