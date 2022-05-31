package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class LocationUpdatesManager {
    private List<LocationObserver> observerList = new ArrayList<>();
    private static LocationUpdatesManager singleton = null;
    private Context context;
    private NodeInfo prevNode;

    private LocationListener listener = this::notifyObservers;

    private LocationUpdatesManager(Context context) {
        this.context = context;
        this.setupListener();
    }

    public synchronized static LocationUpdatesManager getSingleton(Context context) {
        if (singleton == null) {
            singleton = new LocationUpdatesManager(context);
        }
        return singleton;
    }

    public void registerObserver(LocationObserver observer) {
        this.observerList.add(observer);
    }

    private void notifyObservers(Location location) {
        List<NodeInfo> nodes = ZooDatabase.getSingleton(context).zooDao().getAllNodes();
        float minDist = Float.MAX_VALUE;
        NodeInfo minNode = null;
        for(LocationObserver observer: observerList) {
            observer.updateLocation(location);
        }

        for(NodeInfo node: nodes) {
            if (node.getLocation().isPresent() && location.distanceTo(node.getLocation().get()) <= minDist) {
                minDist = location.distanceTo(node.getLocation().get());
                minNode = node;
            }
        }

        for(LocationObserver observer: observerList) {
            observer.updateClosestNode(minNode);
        }
    }

    private void setupListener() throws SecurityException {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d("LocationManager", "requested");

        locationManager.requestLocationUpdates(provider, 0, 0f, listener);

        var location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            this.notifyObservers(location);
        }

        Log.d("LastLocation", String.format("last location: %s", location));
    }

    public void useMockLocation(Location location) {
        try {
            var locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(listener);
        } catch (Exception e) {
            // do nothing
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyObservers(location);
            }
        }, 1000);
    }
}
