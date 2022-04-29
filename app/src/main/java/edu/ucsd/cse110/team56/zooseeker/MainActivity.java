package edu.ucsd.cse110.team56.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import edu.ucsd.cse110.team56.zooseeker.entity.Graph;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            InputStream input = this.getAssets().open("map/sample_graph.JSON");
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();

            Graph graph = gson.fromJson(reader, Graph.class);
            Log.d("Graph", graph.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}