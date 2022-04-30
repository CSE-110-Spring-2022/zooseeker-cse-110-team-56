package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PlanListActivity extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);
        ListView listView = findViewById(R.id.routeList);
        ExhibitsList mylist = new ExhibitsList();
        //will replace this with correct route later when finished calculating the shortest route
        mylist.addItem("去往长颈怒：直走30米，右转，走到糖果屋路，长颈怒就在路的左侧");
        mylist.addItem("去往小脑斧：直走到森林路十字路口，小脑斧就在您右侧");
        mylist.addItem("去往大狮纸：沿森林路向北走200米，左转沿拉拉路向西走30米，大狮纸就在您的正前方");
        //
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist.getList());
        listView.setAdapter(arrayAdapter);

    }

    public void onBackToMainClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}