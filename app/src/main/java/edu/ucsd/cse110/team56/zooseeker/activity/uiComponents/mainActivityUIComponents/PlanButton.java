package edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.PlanListActivity;
import edu.ucsd.cse110.team56.zooseeker.activity.manager.UIOperations;

public class PlanButton {
    public static void displayNoExhibitsSelectedAlert(Context context) {
        UIOperations.showDefaultAlert(context, context.getString(R.string.no_exhibits_selected_msg));
    }

    public static void startPlanListActivity(Context context, ArrayAdapter<?> addedListAdapter) {
        final var intent = new Intent(context, PlanListActivity.class);
        addedListAdapter.notifyDataSetChanged();
        context.startActivity(intent);
    }
}
