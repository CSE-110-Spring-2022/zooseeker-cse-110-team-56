package edu.ucsd.cse110.team56.zooseeker.activity;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ucsd.cse110.team56.zooseeker.R;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RouteAndDirectionTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void routeAndDirectionTest() {
        ViewInteraction appCompatImageView = onView(
                allOf(withId(androidx.appcompat.R.id.search_button), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_bar),
                                        childAtPosition(
                                                withId(R.id.search_btn),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("a"), closeSoftKeyboard());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.data_list),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                2)))
                .atPosition(0);
        appCompatCheckedTextView.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.data_list),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                2)))
                .atPosition(5);
        appCompatCheckedTextView2.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.data_list),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                2)))
                .atPosition(7);
        appCompatCheckedTextView3.perform(click());

        DataInteraction appCompatCheckedTextView4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.data_list),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                2)))
                .atPosition(8);
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(androidx.appcompat.R.id.search_close_btn), withContentDescription("Clear query"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(androidx.appcompat.R.id.search_close_btn), withContentDescription("Clear query"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction extendedFloatingActionButton = onView(
                allOf(withId(R.id.planBtn), withText("PLAN"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        extendedFloatingActionButton.perform(click());

        ViewInteraction extendedFloatingActionButton2 = onView(
                allOf(withId(R.id.planBtn), withText("GO"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        extendedFloatingActionButton2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.destination_text), withText("Next: Koi Fish"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText("Next: Koi Fish")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.direction_text), withText("Proceed on Gate Path 1100.0 ft towards Front Street / Treetops Way "),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Proceed on Gate Path 1100.0 ft towards Front Street / Treetops Way ")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.direction_text), withText("Proceed on Terrace Lagoon Loop 2200.0 ft towards Koi Fish "),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView3.check(matches(withText("Proceed on Terrace Lagoon Loop 2200.0 ft towards Koi Fish ")));

        pressBack();

        pressBack();

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.clear_btn), withText("Clear"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
