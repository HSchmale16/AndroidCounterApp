package org.henryschmale.counter.activities;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.henryschmale.counter.R;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class ListEventTypesActivityTest {

    @Test
    public void doE2E() {
        try (ActivityScenario<ListEventTypesActivity> scenario = ActivityScenario.launch(ListEventTypesActivity.class)) {
            String faker = "Terrible" + System.currentTimeMillis();

            createEventType(faker, "");

            IdlingPolicies.setMasterPolicyTimeout(800, TimeUnit.MILLISECONDS);

            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());

            onView(withText(R.string.most_recently_created)).perform(click());

            onView(withId(R.id.event_type_list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.btn_increment)))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.btn_increment)))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

            onView(withId(R.id.vote_list))
                    .check(matches(hasChildCount(2)));

            pressBack();

            onView(withId(R.id.event_type_list))
                    .check(matches(atPosition(0, hasDescendant(withText("2")))));


            onView(withId(R.id.event_type_list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.btn_increment)))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.btn_increment)))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.btn_increment)))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.btn_decrement)))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
            onView(withId(R.id.vote_list))
                    .check(matches(hasChildCount(6)));

            pressBack();

            onView(withId(R.id.event_type_list)).check(matches(atPosition(0, hasDescendant(withText("4")))));

        }
    }

    private void createEventType(String name, String description) {
        onView(withId(R.id.fab))
                .perform(click());

        onView(withId(R.id.event_type_name))
                .perform(clearText())
                .perform(typeText(name));

        onView(withId(R.id.event_type_description))
                .perform(clearText())
                .perform(typeText(description));

        onView(withId(R.id.submit_button)).perform(click());
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }


    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }

                Log.d("StupidTest", viewHolder.toString());

                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}