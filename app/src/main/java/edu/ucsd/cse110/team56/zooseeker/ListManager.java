package edu.ucsd.cse110.team56.zooseeker;

import android.content.Context;
import android.widget.CheckedTextView;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

/**
 * A container of static methods for managing the state of `NodeInfo` lists
 */
public class ListManager {
    /**
     * sets `isAdded` to true
     */
    public static void addItem(Context context, NodeInfo item) {
        item.setAdded(true);
        ZooDatabase.getSingleton(context).zooDao().updateNode(item);
    }

    /**
     * sets `isAdded` to false
     */
    public static void removeItem(Context context, NodeInfo item) {
        item.setAdded(false);
        ZooDatabase.getSingleton(context).zooDao().updateNode(item);
    }

    /**
     * @param allList the list containing all items to filter from
     * @return a list of items where `isAdded` is true
     */
    public static List<NodeInfo> getAddedList(List<NodeInfo> allList) {
        return allList.stream()
                .filter(NodeInfo::isAdded)
                .collect(Collectors.toList());
    }

    /**
     * @param allList the list containing all items to filter from
     * @return a list of the names of items where `isAdded` is true
     */
    public static List<String> getAddedListNames(List<NodeInfo> allList) {
        return getNames(getAddedList(allList));
    }

    public static List<String> getListId(List<NodeInfo> list){
        return list.stream()
                .map(NodeInfo::getId)
                .collect(Collectors.toList());
    }

    /**
     * @param list a list of `NodeInfo` objects to map from
     * @return a list of the name attributes of all `NodeInfo` objects
     *  from the given list
     */
    public static List<String> getNames(List<NodeInfo> list) {
        return list.stream()
                .map(NodeInfo::getName)
                .collect(Collectors.toList());
    }
}
