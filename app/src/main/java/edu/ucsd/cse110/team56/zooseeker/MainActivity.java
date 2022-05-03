package edu.ucsd.cse110.team56.zooseeker;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import java.util.stream.Collectors;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    // Search Bar Adaptor
    private ArrayAdapter<String> searchAdapter;
    // AddedList Adaptor
    private ArrayAdapter<String> addedAdapter;

    // Search ListView
    private ListView searchAnimalView;
    // Added ListView
    private ListView addAnimalView;

    // All Animal Names
    private List<String> allNames;
    // Added Animal Names
    private List<String> addedNames;
    // Added Animals
    protected List<NodeInfo> addedAnimals;
    private static final String ADDED_LIST = "added_list";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
        List<NodeInfo> nodes = ZooDatabase.getSingleton(this).zooDao().getAllNodes();
        List<EdgeInfo> edges = ZooDatabase.getSingleton(this).zooDao().getAllEdges();
        Log.d("Nodes", nodes.toString());

        hideSearchListView();
        
        // Initialize Added
        addedAnimals = new ArrayList<>();
        updateAddedList(nodes);
        addedNames = addedAnimals.stream().map(NodeInfo::getName).collect(Collectors.toList());
        //addedNames.add("Christina");
        //addedNames.add("Miranda");

        // Initialize Views
        searchAnimalView = findViewById(R.id.data_list);
        addAnimalView = findViewById(R.id.added_list);

        // Populate All Names List View
        allNames = nodes.stream().map(NodeInfo::getName).collect(Collectors.toList());
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, allNames);
        searchAnimalView.setAdapter(searchAdapter);

        // Populate Added Names List View
        addedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedNames);
        addAnimalView.setAdapter(addedAdapter);

        // Enable CheckMark
        searchAnimalView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //checkedAnimal = new boolean[allNames.size()];
        setAddedAnimalsChecked(nodes);
/*
        searchAnimalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " +position);
                // get the name of the animal
                String selected = (String) searchAnimalView.getItemAtPosition(position);
                // set the NodeInfo.added = true
                nodes.get(allNames.indexOf(selected)).setAdded(((CheckedTextView) view).isChecked());
                updateAddedList(nodes);
                addedAdapter.notifyDataSetChanged();
            }
        });
*/
        searchAnimalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + position);
                // get the name of the animal
                String selected = (String) searchAnimalView.getItemAtPosition(position);
                // set the NodeInfo.added = true
                nodes.get(allNames.indexOf(selected)).setAdded(((CheckedTextView) view).isChecked());
                updateAddedList(nodes);
                //addedAdapter.notifyDataSetChanged();
                /* try to run on the UI thread, didn't work
                final ArrayAdapter adapter = addedAdapter;
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                */

                //trying to update the adapter to the newest list, worked!!
                new AdapterHelper().update(addedAdapter, new ArrayList<Object>(addedNames));
                addedAdapter.notifyDataSetChanged();
            }

        });

    }

    public class AdapterHelper {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void update(ArrayAdapter arrayAdapter, ArrayList<Object> listOfObject){
            arrayAdapter.clear();
            for (Object object : listOfObject){
                arrayAdapter.add(object);
            }
        }
    }

    private void setAddedAnimalsChecked(List<NodeInfo> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).isAdded());
            searchAnimalView.setItemChecked(i, nodes.get(i).isAdded());
        }
    }

    /*
        Update the list of Added Animals
     */
    protected void updateAddedList(List<NodeInfo> nodes) {
        for (NodeInfo node : nodes) {
            if (node.isAdded() && !addedAnimals.contains(node)) { //add new animals
                addedAnimals.add(node);
            } else if (!node.isAdded() && addedAnimals.contains(node)){ //remove added animals
                addedAnimals.remove(node);
            }
        }
        addedNames = addedAnimals.stream().map(NodeInfo::getName).collect(Collectors.toList());

    }

    /*
        Return the List of Added Animals
     */
    protected List<NodeInfo> getAddedAnimals() {
        return addedAnimals;
    }

    /*
        Hide the All Animals Search List View
     */
    public void hideSearchListView (){
        searchAnimalView = findViewById(R.id.data_list);
        searchAnimalView.setVisibility(View.INVISIBLE);
    }

    /*
    Hide the Added Animals Search List View
 */
    public void hideAddedListView (){
        addAnimalView = findViewById(R.id.added_list);
        addAnimalView.setVisibility(View.INVISIBLE);
    }

    /*
        Show the All Animals Search List View
     */
    public void showListView (ListView view){
        view.setVisibility(View.VISIBLE);
    }

    /*
        Search Bar Drawdown Search Function
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_bar_drawdown, menu);
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
                showListView(searchAnimalView);
                searchAdapter.getFilter().filter(s);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideSearchListView();
                showListView(addAnimalView);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    /*
        Jump to the PLAN view when the PLAN button is clicked
     */
    public void onPlanBtnClicked(View view) {
        Intent intent = new Intent(this, PlanListActivity.class);
        addedAdapter.notifyDataSetChanged();
        startActivity(intent);
    }

}