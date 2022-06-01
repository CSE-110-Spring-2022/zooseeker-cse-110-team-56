package edu.ucsd.cse110.team56.zooseeker.activity;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import edu.ucsd.cse110.team56.zooseeker.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoReplanMockTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void noReplanMockTest() throws InterruptedException {
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

        ViewInteraction overflowMenuButton = onView(
allOf(withContentDescription("More options"),
childAtPosition(
childAtPosition(
withId(androidx.appcompat.R.id.action_bar),
1),
0),
isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction materialTextView = onView(
allOf(withId(androidx.appcompat.R.id.title), withText("Mock Location"),
childAtPosition(
childAtPosition(
withId(androidx.appcompat.R.id.content),
0),
0),
isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
allOf(withId(R.id.latitude),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
0),
isDisplayed()));
        appCompatEditText.perform(replaceText("32.747"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
allOf(withId(R.id.longitude),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText2.perform(replaceText("117.795"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
allOf(withId(R.id.longitude), withText("117.795"),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
allOf(withId(R.id.longitude), withText("117.795"),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText4.perform(replaceText("-117.795"));

        ViewInteraction appCompatEditText5 = onView(
allOf(withId(R.id.longitude), withText("-117.795"),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
allOf(withId(R.id.longitude), withText("-117.795"),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
allOf(withId(R.id.longitude), withText("-117.795"),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText7.perform(replaceText("-117.195"));

        ViewInteraction appCompatEditText8 = onView(
allOf(withId(R.id.longitude), withText("-117.195"),
childAtPosition(
childAtPosition(
withId(android.R.id.custom),
0),
1),
isDisplayed()));
        appCompatEditText8.perform(closeSoftKeyboard());

        ViewInteraction materialButton = onView(
allOf(withId(android.R.id.button1), withText("OK"),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.ScrollView")),
0),
3)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1500);

        ViewInteraction materialButton2 = onView(
allOf(withId(android.R.id.button2), withText("No"),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.ScrollView")),
0),
2)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(1500);

        ViewInteraction textView = onView(
allOf(withId(R.id.destination_text), withText("Next: Capuchin Monkeys"),
withParent(allOf(withId(R.id.linearLayout),
withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
isDisplayed()));
        textView.check(matches(withText("Next: Capuchin Monkeys")));

        ViewInteraction textView2 = onView(
allOf(withId(R.id.direction_text), withText("Proceed on Monkey Trail 1400.0 ft towards Gorillas "),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
isDisplayed()));
        textView2.check(matches(withText("Proceed on Monkey Trail 1400.0 ft towards Gorillas ")));

        ViewInteraction textView3 = onView(
allOf(withId(R.id.direction_text), withText("Continue on Monkey Trail 1200.0 ft towards Scripps Aviary "),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
isDisplayed()));
        textView3.check(matches(withText("Continue on Monkey Trail 1200.0 ft towards Scripps Aviary ")));

        ViewInteraction textView4 = onView(
allOf(withId(R.id.direction_text), withText("Continue on Monkey Trail 1200.0 ft towards Monkey Trail / Hippo Trail "),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
isDisplayed()));
        textView4.check(matches(withText("Continue on Monkey Trail 1200.0 ft towards Monkey Trail / Hippo Trail ")));

        ViewInteraction textView5 = onView(
allOf(withId(R.id.direction_text), withText("Continue on Monkey Trail 2300.0 ft towards Capuchin Monkeys "),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
isDisplayed()));
        textView5.check(matches(withText("Continue on Monkey Trail 2300.0 ft towards Capuchin Monkeys ")));

        ViewInteraction textView6 = onView(
allOf(withId(R.id.direction_text), withText("Continue on Monkey Trail 2300.0 ft towards Capuchin Monkeys "),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
isDisplayed()));
        textView6.check(matches(withText("Continue on Monkey Trail 2300.0 ft towards Capuchin Monkeys ")));

        pressBack();

        pressBack();

        ViewInteraction materialButton3 = onView(
allOf(withId(R.id.clear_btn), withText("Clear"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
1),
isDisplayed()));
        materialButton3.perform(click());
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
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
