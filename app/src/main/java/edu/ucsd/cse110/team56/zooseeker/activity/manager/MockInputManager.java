package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import android.widget.EditText;

import edu.ucsd.cse110.team56.zooseeker.R;

public class MockInputManager {
    public static void promptMockInput(Activity activity) {
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
}
