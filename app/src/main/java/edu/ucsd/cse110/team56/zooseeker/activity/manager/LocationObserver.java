package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.location.Location;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public interface LocationObserver {
    //    void updateLocation(Location location);
    void updateClosestNode(NodeInfo node, Location location);
}
