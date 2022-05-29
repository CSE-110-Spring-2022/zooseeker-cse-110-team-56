package edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.directionActivityUIComponents;

import android.content.Context;
import android.content.Intent;

import edu.ucsd.cse110.team56.zooseeker.activity.SettingsActivity;

public class SettingsButton {
    public static void startActivity(Context context) {
        final var intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
