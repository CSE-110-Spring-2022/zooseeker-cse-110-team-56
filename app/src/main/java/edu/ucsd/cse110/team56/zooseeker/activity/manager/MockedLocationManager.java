package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MockedLocationManager extends LocationUpdatesManager {



    private Context context;
    Location singleLocation;

    public MockedLocationManager(Context context) {
        this.context = context;
    }

    @Override
    protected void setupListener() throws SecurityException {
        if (singleLocation != null) {
            super.notifyObservers(singleLocation);
            Log.d("MockedLocation", String.format("mocked location: %s", singleLocation));
        }

    }

    public void mockLocation(Location location) {
        this.singleLocation = location;
        setupListener();
        /*if (singleLocation != null) {
            super.notifyObservers(singleLocation);
            Log.d("MockedLocation", String.format("mocked location: %s", singleLocation));
        }*/
    }

}