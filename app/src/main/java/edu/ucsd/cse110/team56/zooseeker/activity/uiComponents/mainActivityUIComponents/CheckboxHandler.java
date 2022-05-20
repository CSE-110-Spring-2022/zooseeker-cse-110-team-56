package edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents;

import android.app.Activity;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.Semaphore;

import edu.ucsd.cse110.team56.zooseeker.activity.manager.ListManager;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class CheckboxHandler {
    /**
     * Updates the "search suggestions" list using the `isAdded` attribute of
     * all items currently shown in the list
     * @param nodes the list of nodes that contains the added nodes for which
     *              you want to mark checkboxes as "checked"
     */
    public static void updateSearchedCheckBoxes(Activity activity, List<NodeInfo> nodes, ListView searchListView) {
        Semaphore mutex = new Semaphore(0);
        activity.runOnUiThread(() -> {
            updateSearchedCheckBoxesInternal(nodes, searchListView);
            mutex.release();
        });
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void updateSearchedCheckBoxesInternal(List<NodeInfo> nodes, ListView searchListView) {
        // loops through the current list of search suggestions
        for (int i = 0; i < searchListView.getCount(); i++) {
            String currentItemName = ((NodeInfo) searchListView.getItemAtPosition(i)).name;

            // the index of the current item within the `allNodes` list
            int currentItemIndex = ListManager.getNames(nodes).indexOf(currentItemName);

            // retrieve the node
            NodeInfo currentItem = nodes.get(currentItemIndex);

            if (searchListView.isItemChecked(i) != currentItem.isAdded()) {
                searchListView.setItemChecked(i, currentItem.isAdded());
            }
        }
    }
}
