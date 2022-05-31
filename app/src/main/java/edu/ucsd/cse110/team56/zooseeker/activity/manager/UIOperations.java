package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.content.Context;
import android.view.WindowManager;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.R;

public class UIOperations {
    public static void showView(View view){
        view.setVisibility(View.VISIBLE);
    }
    public static void hideView(View view) { view.setVisibility(View.INVISIBLE); }

    public static void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public static void showViews(List<View> views) {
        views.forEach(UIOperations::showView);
    }
    public static void hideViews(List<View> views) {
        views.forEach(UIOperations::hideView);
    }

    public static void setVisibility(List<View> views, boolean isVisible) {
        views.forEach(view -> UIOperations.setVisibility(view, isVisible));
    }

    public static void showAlert(Context context, String message, String buttonText, DialogInterface.OnClickListener onClickListener) {
        final var builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(buttonText, onClickListener);
        final var dialog = builder.create();
        dialog.show();
    }

    public static void showDialog(Context context, String message, String cancelText, String okText, DialogInterface.OnClickListener onClickListener) {
        final var builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setNegativeButton(cancelText, null)
                .setPositiveButton(okText, onClickListener);
        final var dialog = builder.create();
        dialog.show();
    }

    public static void showDefaultAlert(Context context, String message) {
        showAlert(context, message, context.getString(R.string.ok), (dialog, i) -> dialog.dismiss());
    }
}
