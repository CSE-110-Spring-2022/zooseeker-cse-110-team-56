package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class DatabaseGetterManager {
    public static List<NodeInfo> getAllNodes(Context context) {
        return ZooDatabase.getSingleton(context)
                .zooDao()
                .getAllNodes()
                .stream()
                .filter(n -> n.kind.equals("exhibit"))
                .collect(Collectors.toList());
    }

    public static List<EdgeInfo> getAllEdges(Context context) {
        return ZooDatabase.getSingleton(context)
                .zooDao()
                .getAllEdges();
    }
}
