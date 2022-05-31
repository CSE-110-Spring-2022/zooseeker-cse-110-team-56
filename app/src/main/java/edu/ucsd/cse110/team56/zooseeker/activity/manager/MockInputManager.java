package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.team56.zooseeker.R;

public class MockInputManager {
    public static void promptMockLocationInput(Activity activity) {
        final var builder = new AlertDialog.Builder(activity);
        final var inflater = activity.getLayoutInflater();
        final var innerView = inflater.inflate(R.layout.mock_dialog, null);

        builder.setView(innerView)
                .setPositiveButton(R.string.ok, (DialogInterface dialog, int id) -> {
                    final var lat = (EditText) innerView.findViewById(R.id.latitude);
                    final var lon = (EditText) innerView.findViewById(R.id.longitude);
                    final var location = new Location("Mock");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        location.setMock(true);
                    }

                    location.setLatitude(Double.parseDouble(lat.getText().toString()));
                    location.setLongitude(Double.parseDouble(lon.getText().toString()));

                    LocationUpdatesManager.getSingleton(activity).useMockLocation(location);
                })
                .setNegativeButton("Cancel", (DialogInterface dialog, int id) -> dialog.cancel());

        final var dialog = builder.create();

        dialog.show();
    }

    public static void promptMockRouteInput(Activity activity) {
        final var builder = new AlertDialog.Builder(activity);
        final var inflater = activity.getLayoutInflater();
        final var innerView = inflater.inflate(R.layout.mock_route_dialog, null);

        builder.setView(innerView)
                .setPositiveButton(R.string.ok, (DialogInterface dialog, int id) -> {
                    final var json = (EditText) innerView.findViewById(R.id.list);
                    final var interval = (EditText) innerView.findViewById(R.id.interval);

                    try {
                        List<JsonLocation> locations = new Gson().fromJson(json.getText().toString(), new TypeToken<List<JsonLocation>>(){}.getType());
                        Log.d("Mock", locations.toString());
                        Executors.newSingleThreadExecutor().execute(() -> {
                            for(JsonLocation loc: locations) {


                                final var location = new Location("Mock");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    location.setMock(true);
                                }
                                location.setLatitude(loc.lat);
                                location.setLongitude(loc.lng);
                                LocationUpdatesManager.getSingleton(activity).useMockLocation(location);

                                try {
                                    Thread.sleep(Long.parseLong(interval.getText().toString()));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    } catch (Exception e) {
                        Log.e("Mock", "failed to mock", e);
                    }

                })
                .setNegativeButton("Cancel", (DialogInterface dialog, int id) -> dialog.cancel());

        final var dialog = builder.create();

        dialog.show();
    }


}

class JsonLocation {
    public double lat;
    public double lng;
}