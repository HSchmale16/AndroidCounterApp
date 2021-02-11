package org.henryschmale.counter.activities;

import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.henryschmale.counter.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

import static org.junit.Assert.*;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateEventTypeActivityTest {

    @Test
    public void createActivity() {
        try (ActivityScenario<CreateEventTypeActivity> scenario = ActivityScenario.launch(CreateEventTypeActivity.class)) {

                onView(withId(R.id.event_type_name))
                        .perform(clearText())
                        .perform(typeText("Hello World"));


                onView(withId(R.id.event_type_description))
                        .perform(clearText())
                        .perform(typeText("This is a description"));



        }
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                return editText.getError().toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}