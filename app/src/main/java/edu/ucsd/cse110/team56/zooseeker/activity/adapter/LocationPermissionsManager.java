package edu.ucsd.cse110.team56.zooseeker.activity.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;

public class LocationPermissionsManager {
    public static final String[] requiredPermissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static ActivityResultLauncher<String[]> getRequestPermissionLauncher(AppCompatActivity activity) {
        return activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), perms -> {
            perms.forEach((perm, isGranted) -> Log.i("UserLocation", String.format("Permission %s granted: %s", perm, isGranted)));

            if (hasLocationPermissionGranted(activity)) {
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);
            } else {
                UIOperations.showDefaultAlert(activity, "No location permissions.");
            }
        });
    }

    public static boolean needsLocationPermission(Context context) {
        return Arrays.stream(requiredPermissions)
                .map(perm -> ContextCompat.checkSelfPermission(context, perm))
                .allMatch(status -> status == PackageManager.PERMISSION_DENIED);
    }

    public static boolean hasLocationPermissionGranted(Context context) {
        return Arrays.stream(requiredPermissions)
                .map(perm -> ContextCompat.checkSelfPermission(context, perm))
                .allMatch(status -> status == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestLocationPermission(AppCompatActivity activity) {
        if (!hasLocationPermissionGranted(activity)) {
            getRequestPermissionLauncher(activity).launch(requiredPermissions);
        }
    }
}
