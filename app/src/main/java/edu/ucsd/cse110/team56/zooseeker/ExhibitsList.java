package edu.ucsd.cse110.team56.zooseeker;

import java.util.ArrayList;

public class ExhibitsList {
    private ArrayList<String> list = new ArrayList<String>();

    /**
     * Initializes an empty ExhibitsList
     */
    public ExhibitsList() {
    }

    /**
     * @apiNote the method checks for whether the item already exists
     * in the list; duplicates will not be added
     * @param itemName the name of the item to add
     */
    public void addItem(String itemName) {
        // guard statement
        if (hasItem(itemName)) { return; }

        // add item
        list.add(itemName);
    }

    /**
     * @return the list of exhibits added by the user
     */
    public ArrayList<String> getList() {
        return list;
    }

    /**
     * @param itemName the name of the item to check for
     * @return whether the item with the input name is present in the list
     */
    private boolean hasItem(String itemName) {
        return list.contains(itemName);
    }
}
