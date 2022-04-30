package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
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

    // for the search bar
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<NodeInfo> nodes = ZooDatabase.getSingleton(this).zooDao().getAllNodes();
        List<EdgeInfo> edges = ZooDatabase.getSingleton(this).zooDao().getAllEdges();

        Log.d("Nodes", nodes.toString());


        /*
            Search Bar search items trial
         */
        ListView listView = findViewById(R.id.data_list);
        List<String> mylist = new ArrayList<>();
        mylist.add("Eraser");
        mylist.add("Pencils");
        mylist.add("Pen");
        mylist.add("Books");
        mylist.add("Rulers");
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        listView.setAdapter(arrayAdapter);

        ListView nameList = (ListView) findViewById(R.id.data_list);
        nameList.setVisibility(View.INVISIBLE);

    }

    /*
        For the search bar draw down menu
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
                ListView nameList = findViewById(R.id.data_list);
                nameList.setVisibility(View.VISIBLE);
                arrayAdapter.getFilter().filter(s);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ListView nameList = findViewById(R.id.data_list);
                nameList.setVisibility(View.INVISIBLE);
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