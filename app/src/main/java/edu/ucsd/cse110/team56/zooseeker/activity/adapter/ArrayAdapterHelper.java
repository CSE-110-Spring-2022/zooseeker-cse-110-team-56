package edu.ucsd.cse110.team56.zooseeker.activity.adapter;

import android.widget.ArrayAdapter;

import java.util.List;

public class ArrayAdapterHelper {

    /**
     * forces an adapter to update, using a list of data objects
     * @param arrayAdapter the `ArrayAdapter` to update
     * @param listOfObjects the list of data to update the adapter with
     */
    public static void updateAdapter(ArrayAdapter arrayAdapter, List<?> listOfObjects){
        arrayAdapter.clear();
        for (Object object : listOfObjects){
            arrayAdapter.add(object);
        }
        arrayAdapter.notifyDataSetChanged();
    }
}
