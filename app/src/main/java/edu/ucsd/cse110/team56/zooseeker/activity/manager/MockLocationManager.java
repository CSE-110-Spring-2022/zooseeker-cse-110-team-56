package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;
import android.location.Location;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.misc.JsonReader;

public class MockLocationManager {

    private MutableLiveData<Location> mockSource;
    private Context context;

    public MockLocationManager(Context context) {
        this.context = context;
        mockSource = new MutableLiveData<>();
    }

    public Location readLocations() {

        List<Location> locations = JsonReader.parseJsonList(this.context, "gps/trial_mocked_locations.json", Location.class);

        for (Location location : locations) {
            System.out.print("Locations: ");
            System.out.print(location.toString());
        }

        Location mockedLocation = new Location("mocked");
        mockedLocation.setLatitude(42.7353);
        mockedLocation.setLongitude(-124.1490);

        mockLocation(mockedLocation);

        return mockedLocation;

    }

    @VisibleForTesting
    public void mockLocation(Location location) {
        mockSource.postValue(location);
    }

}
