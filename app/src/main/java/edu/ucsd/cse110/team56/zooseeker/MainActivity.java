package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import java.util.Arrays;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<NodeInfo> nodes = ZooDatabase.getSingleton(this).zooDao().getAllNodes();
        List<EdgeInfo> edges = ZooDatabase.getSingleton(this).zooDao().getAllEdges();
    }

    public void onPlanBtnClicked(View view) {
        Intent intent = new Intent(this, PlanListActivity.class);
        startActivity(intent);
    }

}