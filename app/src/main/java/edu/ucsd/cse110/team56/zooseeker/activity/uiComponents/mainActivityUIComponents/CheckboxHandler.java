package edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents;

import android.app.Activity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Semaphore;

import edu.ucsd.cse110.team56.zooseeker.activity.MainActivity;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.ExhibitsManager;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class CheckboxHandler {
    /**
     * Updates the "search suggestions" list using the `isAdded` attribute of
     * all items currently shown in the list
     * @param nodes the list of nodes that contains the added nodes for which
     *              you want to mark checkboxes as "checked"
     */
    public static void updateSearchedCheckBoxes(Activity activity, ListView searchListView) {
        activity.runOnUiThread(() -> {
            updateSearchedCheckBoxesInternal(ExhibitsManager.getSingleton(searchListView.getContext()).getAllExhibits(), searchListView);
        });
    }

    private static void updateSearchedCheckBoxesInternal(List<NodeInfo> nodes, ListView searchListView) {
        // loops through the current list of search suggestions
        for (var i = 0; i < searchListView.getCount(); i++) {
            final var currentItemName = ((NodeInfo) searchListView.getItemAtPosition(i)).name;

            // the index of the current item within the `allNodes` list
            final var currentItemIndex = ExhibitsManager.getNames(nodes).indexOf(currentItemName);

            // retrieve the node
            final var currentItem = nodes.get(currentItemIndex);

            if (searchListView.isItemChecked(i) != (currentItem.getStatus() == NodeInfo.Status.ADDED)) {
                searchListView.setItemChecked(i, (currentItem.getStatus() == NodeInfo.Status.ADDED));
                searchListView.setItemsCanFocus(false);
            }
        }
    }
}
