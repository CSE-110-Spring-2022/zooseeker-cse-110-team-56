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
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    // Search Bar Adaptor
    private ArrayAdapter<NodeInfo> searchAdapter;
    // AddedList Adaptor
    private ArrayAdapter<String> addedAdapter;

    // Search ListView
    private ListView searchAnimalView;
    // Added ListView
    private ListView addAnimalView;
    // Added Number TextView
    private TextView addedCountView;
    private final String added_count_msg = "Added Animals: ";
    // Error Msg Text Views
    private TextView noResultView;

    private List<NodeInfo> allNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        allNodes = DatabaseGetterManager.getAllNodes(this);

        hideSearchListView();

        // Initialize Views
        searchAnimalView = findViewById(R.id.data_list);
        addAnimalView = findViewById(R.id.added_list);
        addedCountView = findViewById(R.id.added_count);
        noResultView = findViewById(R.id.no_result_view);

        hideView(noResultView);
        List<String> allNames = ListManager.getNames(allNodes);

        // Populate All Names List View
        searchAdapter = new NodeInfoAdapter(this, android.R.layout.simple_list_item_multiple_choice, allNodes);
        searchAnimalView.setAdapter(searchAdapter);

        // Populate Added Names List View
        List<String> addedNames = ListManager.getAddedListNames(allNodes);
        addedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addAnimalView.setAdapter(addedAdapter);

        // Enable CheckMark
        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateSearchedCheckBoxes(allNodes);

        searchAnimalView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: " + position);
            // get the name of the animal
            String selectedItemName = ((NodeInfo) searchAnimalView.getItemAtPosition(position)).name;

            // add or remove the selected item based on `isChecked()` state
            NodeInfo selectedItem = allNodes.get(allNames.indexOf(selectedItemName));
            if (((CheckedTextView) view).isChecked()) {
                ListManager.addItem(searchAnimalView.getContext(), selectedItem);
            } else {
                ListManager.removeItem(searchAnimalView.getContext(), selectedItem);
            }

            // update UI elements
            ArrayAdapterHelper.updateAdapter(addedAdapter, ListManager.getAddedListNames(allNodes));
            updateSearchedCheckBoxes(allNodes);
        });
    }

    // -------- Show & hide list views --------

    // remove?
    public void hideSearchListView (){
        searchAnimalView = findViewById(R.id.data_list);
        hideView(searchAnimalView);
    }

    // remove?
    public void hideAddedListView (){
        addAnimalView = findViewById(R.id.added_list);
        hideView(addAnimalView);
    }

    public void hideView(View view) { view.setVisibility(View.INVISIBLE); }

    public void showView(View view){
        view.setVisibility(View.VISIBLE);
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
            // loops through the current list of search suggestions
            for (int i = 0; i < searchAnimalView.getCount(); i++) {
                String currentItemName = ((NodeInfo) searchAnimalView.getItemAtPosition(i)).name;

                // the index of the current item within the `allNodes` list
                int currentItemIndex = ListManager.getNames(nodes).indexOf(currentItemName);

                // retrieve the node
                NodeInfo currentItem = nodes.get(currentItemIndex);

                if (searchAnimalView.isItemChecked(i) != currentItem.isAdded()) {
                    searchAnimalView.setItemChecked(i, currentItem.isAdded());
                }
            }
            mutex.release();
        });
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void noResultDisplay() {
        runOnUiThread(() -> {
            if (searchAnimalView.getCount() == 0){
                showView(noResultView);
            } else {
                hideView(noResultView);
            }
        });
    }

    /*
        Return the Number of Added Animals
     */
    public int addedAnimalCount() {
        return (int) allNodes.stream()
                .filter(NodeInfo::isAdded)
                .count();
    }

    /// -------- Search handler --------

    public void closeSearch() {
        hideSearchListView();
        showView(addAnimalView);
        showView(addedCountView);
        hideView(noResultView);
        // Update the Added Animal Count
        String display_count = added_count_msg + addedAnimalCount();
        addedCountView.setText(display_count);
    }

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

    /*
        Search Bar dropdown Search Function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_bar_dropdown, menu);
        MenuItem menuItem = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search an animal here...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                hideAddedListView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                hideAddedListView();
                hideSearchListView();
//                hideAddedListView();
//                showView(searchAnimalView);
                hideView(addedCountView);

                // `Filter.filter()` is asynchronous and has an optional listener;
                // run update code using the listener to ensure that the UI updates
                // only **after** changes are made, and not **before**
                searchAdapter.getFilter().filter(s, i -> {
//                        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    updateSearchedCheckBoxes(allNodes); // Update after filtering
//                        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // workaround to disable check animation
                    if(!s.isEmpty()){
                        showView(searchAnimalView);
                    }
                    noResultDisplay();
                });

                return true;
            }
        });

        /*
            Disable the all Names view
         */
        searchView.setOnCloseListener(() -> {
            closeSearch();
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    // -------- Plan button handler --------

    /*
        Jump to the PLAN view when the PLAN button is clicked
    */
    public void onPlanBtnClicked(View view) {
        if (ListManager.getAddedListNames(allNodes).size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No exhibits selected. Please select at least one.")
                    .setPositiveButton("OK", (DialogInterface dialog, int id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Intent intent = new Intent(this, PlanListActivity.class);
            addedAdapter.notifyDataSetChanged();
            startActivity(intent);
        }
    }

}