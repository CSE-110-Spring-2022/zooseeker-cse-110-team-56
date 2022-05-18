package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.view.View;

import java.util.List;

public class UIOperations {
    public static void showView(View view){
        view.setVisibility(View.VISIBLE);
    }
    public static void hideView(View view) { view.setVisibility(View.INVISIBLE); }

    public static void showViews(List<View> views) {
        views.stream().forEach(UIOperations::showView);
    }

    public static void hideViews(List<View> views) {
        views.stream().forEach(UIOperations::hideView);
    }
}
