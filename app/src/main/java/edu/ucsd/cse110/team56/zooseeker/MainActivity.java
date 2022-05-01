package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
// edu.ucsd.cse110.team56.zooseeker.entity.Graph;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<NodeInfo> nodes = ZooDatabase.getSingleton(this).zooDao().getAllNodes();
        List<EdgeInfo> edges = ZooDatabase.getSingleton(this).zooDao().getAllEdges();

        Log.d("Nodes", nodes.toString());

    }

    public void onPlanBtnClicked(View view) {
        Intent intent = new Intent(this, PlanListActivity.class);
        startActivity(intent);
    }

}