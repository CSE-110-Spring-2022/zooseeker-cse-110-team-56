package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    // Search Bar Adaptor
    ArrayAdapter<String> arrayAdapter;

    // Search ListView
    ListView searchAnimalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<NodeInfo> nodes = ZooDatabase.getSingleton(this).zooDao().getAllNodes();
        List<EdgeInfo> edges = ZooDatabase.getSingleton(this).zooDao().getAllEdges();
        Log.d("Nodes", nodes.toString());


        hideSearchListView();

        //Populate All Names List View
        List<String> allNames = nodes.stream().map(NodeInfo::getName).collect(Collectors.toList());
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, allNames);
        searchAnimalView.setAdapter(arrayAdapter);

    }

    /*
        Hide the All Animals Search List View
     */
    public void hideSearchListView (){
        searchAnimalView = findViewById(R.id.data_list);
        searchAnimalView.setVisibility(View.INVISIBLE);
    }

    /*
        Show the All Animals Search List View
     */
    public void showSearchListView (){
        searchAnimalView = findViewById(R.id.data_list);
        searchAnimalView.setVisibility(View.VISIBLE);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                showSearchListView();
                arrayAdapter.getFilter().filter(s);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showSearchListView();
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
        startActivity(intent);
    }




}