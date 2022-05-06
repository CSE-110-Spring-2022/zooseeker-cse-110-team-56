package edu.ucsd.cse110.team56.zooseeker;

import static android.content.ContentValues.TAG;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;

public class MainActivity extends AppCompatActivity {

    // Search Bar Adaptor
    private ArrayAdapter<String> searchAdapter;
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
//        Intent intent = new Intent(this, DirectionActivity.class);
//        startActivity(intent);

        setContentView(R.layout.activity_main);
        allNodes = getAllNodes();

        hideSearchListView();

        // Initialize Views
        searchAnimalView = findViewById(R.id.data_list);
        addAnimalView = findViewById(R.id.added_list);
        addedCountView = findViewById(R.id.added_count);
        noResultView = findViewById(R.id.no_result_view);

        hideTextView(noResultView);

        // Populate All Names List View
        List<String> allNames = ListManager.getNames(allNodes);
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, allNames);
        searchAnimalView.setAdapter(searchAdapter);

        // Populate Added Names List View
        List<String> addedNames = ListManager.getAddedListNames(allNodes);
        addedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addAnimalView.setAdapter(addedAdapter);

        // Enable CheckMark
        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateSearchedCheckBoxes(allNodes);

        searchAnimalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + position);
                // get the name of the animal
                String selectedItemName = (String) searchAnimalView.getItemAtPosition(position);

                // add or remove the selected item based on `isChecked()` state
                NodeInfo selectedItem = allNodes.get(allNames.indexOf(selectedItemName));
                if (((CheckedTextView) view).isChecked()) {
                    ListManager.addItem(selectedItem);
                } else {
                    ListManager.removeItem(selectedItem);
                }

                // update UI
                updateAddedAdapter();
                updateSearchedCheckBoxes(allNodes);
            }
        });
    }

    public class AdapterHelper {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void update(ArrayAdapter arrayAdapter, ArrayList<Object> listOfObjects){
            arrayAdapter.clear();
            for (Object object : listOfObjects){
                arrayAdapter.add(object);
            }
        }
    }

    // -------- Retrieve data from database --------

    private List<NodeInfo> getAllNodes() {
        return ZooDatabase.getSingleton(this).zooDao().getAllNodes();
    }

    private List<EdgeInfo> getAllEdges() {
        return ZooDatabase.getSingleton(this).zooDao().getAllEdges();
    }

    // -------- Show & hide list views --------

    public void hideSearchListView (){
        searchAnimalView = findViewById(R.id.data_list);
        searchAnimalView.setVisibility(View.INVISIBLE);
    }

    public void hideAddedListView (){
        addAnimalView = findViewById(R.id.added_list);
        addAnimalView.setVisibility(View.INVISIBLE);
    }

    public void showListView (ListView view){
        view.setVisibility(View.VISIBLE);
    }

    public void showTextView (TextView view) {
        view.setVisibility(View.VISIBLE);
    }

    public void hideTextView (TextView view) {
        view.setVisibility(View.INVISIBLE);
    }

    // -------- Update list views --------

    /**
     * Updates the "added" list by updating & notifying its adapter
     */
    private void updateAddedAdapter() {
        new AdapterHelper().update(
                addedAdapter,
                new ArrayList<Object>(ListManager.getAddedListNames(allNodes))
        );
        addedAdapter.notifyDataSetChanged();
    }

    /**
     * Updates the "search suggestions" list using the `isAdded` attribute of
     * all items currently shown in the list
     * @param nodes the list of nodes that contains the added nodes for which
     *              you want to mark checkboxes as "checked"
     */
    private void updateSearchedCheckBoxes(List<NodeInfo> nodes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // loops through the current list of search suggestions
                for (int i = 0; i < searchAnimalView.getCount(); i++) {
                    String currentItemName = (String) searchAnimalView.getItemAtPosition(i);

                    // the index of the current item within the `allNodes` list
                    int currentItemIndex = ListManager.getNames(nodes).indexOf(currentItemName);

                    // retrieve the node
                    NodeInfo currentItem = nodes.get(currentItemIndex);

                    searchAnimalView.setItemChecked(i, currentItem.isAdded());

                }
            }
        });
    }

    private void noResultDisplay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (searchAnimalView.getCount() == 0){
                    showTextView(noResultView);
                } else {
                    hideTextView(noResultView);
                }
            }
        });
    }

    /*
        Return the Number of Added Animals
     */
    public int addedAnimalCount() {
        return (int) allNodes.stream()
                .filter(node -> node.isAdded())
                .count();
    }

    /// -------- Search handler --------

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
                hideAddedListView();
                showListView(searchAnimalView);
                hideTextView(addedCountView);

                searchAdapter.getFilter().filter(s, new Filter.FilterListener() {
                    // `Filter.filter()` is asynchronous and has an optional listener;
                    // run update code using the listener to ensure that the UI updates
                    // only **after** changes are made, and not **before**
                    @Override
                    public void onFilterComplete(int i) {
                        updateSearchedCheckBoxes(allNodes); // Update after filtering
                        noResultDisplay();
                    }
                });

                return true;
            }
        });

        /*
            Disable the all Names view
         */
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideSearchListView();
                showListView(addAnimalView);
                showTextView(addedCountView);
                hideTextView(noResultView);
                // Update the Added Animal Count
                String display_count = added_count_msg + addedAnimalCount();
                addedCountView.setText(display_count);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // -------- Plan button handler --------

    /*
        Jump to the PLAN view when the PLAN button is clicked
    */
    public void onPlanBtnClicked(View view) {
        Intent intent = new Intent(this, PlanListActivity.class);
        addedAdapter.notifyDataSetChanged();
        startActivity(intent);
    }

}