package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.location.Location;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

public class MockLocationManager {

    private MutableLiveData<Location> mockSource = null;

    public MockLocationManager() {
        mockSource = new MutableLiveData<>();
    }

    public Location readLocations() {

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
