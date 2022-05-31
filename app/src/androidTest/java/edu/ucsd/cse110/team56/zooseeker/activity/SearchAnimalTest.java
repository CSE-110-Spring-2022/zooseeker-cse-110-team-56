package edu.ucsd.cse110.team56.zooseeker.activity;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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
public class SearchAnimalTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void searchAnimalTest() {
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
        searchAutoComplete.perform(replaceText("hi"), closeSoftKeyboard());

        ViewInteraction checkedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Capuchin Monkeys"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));

        ViewInteraction checkedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("Hippos"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView2.check(matches(isDisplayed()));

        ViewInteraction checkedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText("Hippos"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView3.check(matches(isDisplayed()));

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("hi"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(click());

        ViewInteraction searchAutoComplete3 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("hi"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete3.perform(replaceText("f"));

        ViewInteraction searchAutoComplete4 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("f"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete4.perform(closeSoftKeyboard());

        ViewInteraction checkedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText("Koi Fish"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView4.check(matches(isDisplayed()));

        ViewInteraction checkedTextView5 = onView(
                allOf(withId(android.R.id.text1), withText("Spoonbill"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView5.check(matches(isDisplayed()));

        ViewInteraction checkedTextView6 = onView(
                allOf(withId(android.R.id.text1), withText("Spoonbill"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView6.check(matches(isDisplayed()));

        ViewInteraction searchAutoComplete5 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("f"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete5.perform(click());

        ViewInteraction searchAutoComplete6 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("f"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete6.perform(replaceText("gorillas"));

        ViewInteraction searchAutoComplete7 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("gorillas"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete7.perform(closeSoftKeyboard());

        ViewInteraction checkedTextView7 = onView(
                allOf(withId(android.R.id.text1), withText("Gorillas"),
                        withParent(allOf(withId(R.id.data_list),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        checkedTextView7.check(matches(isDisplayed()));
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
