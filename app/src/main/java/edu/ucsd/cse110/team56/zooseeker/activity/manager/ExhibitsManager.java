package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

/**
 * A container of static methods for managing the state of `NodeInfo` lists
 */
public class ExhibitsManager {
    private static ExhibitsManager singleton = null;
    private Context context;
    private ZooDao dao;

    private ExhibitsManager(Context context) {
        this.context = context;
        this.dao = ZooDatabase.getSingleton(context).zooDao();
    }

    public synchronized static ExhibitsManager getSingleton(Context context) {
        if (singleton == null) {
            singleton = new ExhibitsManager(context);
        }
        return singleton;
    }

    /**
     * sets `isAdded` to true
     */
    public void addItem(NodeInfo item) {
        item.setStatus(NodeInfo.Status.ADDED);
        dao.updateNode(item);
    }

    /**
     * sets `isAdded` to false
     */
    public void removeItem(NodeInfo item) {
        item.setStatus(NodeInfo.Status.LOADED);
        dao.updateNode(item);
    }

    /**
     * @param allList the list containing all items to count
     * @return the number of items in `allList` where `isAdded` is true
     */
    public int getAddedCount(List<NodeInfo> allList) {
        return (int) allList.stream()
                .filter((nodeInfo -> nodeInfo.getStatus() == NodeInfo.Status.ADDED))
                .count();
    }

    /**
     * @param allList the list containing all items to filter from
     * @return a list of items where `isAdded` is true
     */
    public List<NodeInfo> getAddedList(List<NodeInfo> allList) {
        return allList.stream()
                .filter(nodeInfo -> nodeInfo.getStatus() == NodeInfo.Status.ADDED)
                .collect(Collectors.toList());
    }

    /**
     * @param allList the list containing all items to filter from
     * @return a list of the names of items where `isAdded` is true
     */
    public List<String> getAddedListNames(List<NodeInfo> allList) {
        return getNames(getAddedList(allList));
    }

    /**
     * @param list the list to map to IDs
     * @return the IDs of all items in the list
     */
    public List<String> getListId(List<NodeInfo> list){
        return list.stream()
                .map(NodeInfo::getId)
                .collect(Collectors.toList());
    }

    /**
     * @param list a list of `NodeInfo` objects to map from
     * @return a list of the name attributes of all `NodeInfo` objects
     *  from the given list
     */
    public List<String> getNames(List<NodeInfo> list) {
        return list.stream()
                .map(NodeInfo::getName)
                .collect(Collectors.toList());
    }

    /**
     * retrieves EXHIBIT nodes from the database
     * @return the list of all nodes that are of kind EXHIBIT
     */
    public List<NodeInfo> getAllExhibits(Context context) {
        return ZooDatabase.getSingleton(context)
                .zooDao()
                .getNodesWithKind(NodeInfo.Kind.EXHIBIT);
    }
}
