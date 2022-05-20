package edu.ucsd.cse110.team56.zooseeker.activity.uiComponents.mainActivityUIComponents;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.activity.PlanListActivity;

public class PlanButton {
    public static void displayNoExhibitsSelectedAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.no_exhibits_selected_msg))
                .setPositiveButton(
                        context.getString(R.string.ok),
                        (DialogInterface dialog, int id) -> dialog.cancel()
                );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void startPlanListActivity(Context context, ArrayAdapter<?> addedListAdapter) {
        Intent intent = new Intent(context, PlanListActivity.class);
        addedListAdapter.notifyDataSetChanged();
        context.startActivity(intent);
    }
}
