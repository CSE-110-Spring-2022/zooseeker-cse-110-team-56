package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class LocationUpdatesManager {
    public static void setupListener(Context context, LocationListener locationListener) throws SecurityException {
        setupListener(context, false, locationListener);
    }

    public static void setupListener(Context context, boolean shouldLog, LocationListener locationListener) throws SecurityException {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (shouldLog) {
            Log.d("CurrentLocation", "requested");
        }

        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);

        if (shouldLog) {
            var location = locationManager.getLastKnownLocation(provider);
            Log.d("LastLocation", String.format("%s", location));
        }
    }
}
