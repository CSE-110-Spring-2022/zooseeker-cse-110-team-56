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
     * @param itemName the name of the item to add
     */
    public void addItem(String itemName) {
        list.add(itemName);
    }

    /**
     * @return the list of exhibits added by the user
     */
    public ArrayList<String> getList() {
        return list;
    }
}
