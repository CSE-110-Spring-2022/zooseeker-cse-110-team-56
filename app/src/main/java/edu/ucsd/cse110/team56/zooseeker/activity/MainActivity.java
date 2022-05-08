package edu.ucsd.cse110.team56.zooseeker.activity;

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
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.activity.adapter.NodeInfoAdapter;
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

        searchAnimalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        return ZooDatabase.getSingleton(this).zooDao().getAllNodes().stream().filter(n -> !n.id.equals("entrance_exit_gate")).collect(Collectors.toList());
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
        Semaphore mutex = new Semaphore(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public void closeSearch() {
        hideSearchListView();
        showListView(addAnimalView);
        showTextView(addedCountView);
        hideTextView(noResultView);
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
//                showListView(searchAnimalView);
                hideTextView(addedCountView);

                searchAdapter.getFilter().filter(s, new Filter.FilterListener() {
                    // `Filter.filter()` is asynchronous and has an optional listener;
                    // run update code using the listener to ensure that the UI updates
                    // only **after** changes are made, and not **before**
                    @Override
                    public void onFilterComplete(int i) {
                        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        updateSearchedCheckBoxes(allNodes); // Update after filtering
                        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // workaround to disable check animation
                        if(!s.isEmpty()){
                            showListView(searchAnimalView);
                        }
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
                closeSearch();
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