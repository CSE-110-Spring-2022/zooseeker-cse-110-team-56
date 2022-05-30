package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.location.Location;

public class MockLocationManager {




    public Location readLocations() {

        Location mockedLocation = new Location("mocked");
        mockedLocation.setLatitude(42.7353);
        mockedLocation.setLongitude(-124.1490);

        return mockedLocation;

    }
}
