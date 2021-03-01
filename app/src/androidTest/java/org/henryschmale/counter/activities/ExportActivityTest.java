package org.henryschmale.counter.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.henryschmale.counter.R;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ExportActivityTest {


    @Test
    public void testExportActivity() {
        try (ActivityScenario<ExportActivity> scenario = ActivityScenario.launch(ExportActivity.class)) {
            onView(withId(R.id.progress_text))
                    .check(matches(withText("")));

            onView(withId(R.id.the_progress_bar))
                    .check(matches(isDisplayed()));

            // The share export button should be disabled until needed
            onView(withText(R.string.share_export_via))
                    .check(matches(isDisplayed()))
                    .check(matches(not(isEnabled())));

            onView(withText(R.string.start_export))
                    .check(matches(isEnabled()))
                    .perform(click())
                    .check(matches(not(isEnabled())));

            onView(withId(R.id.progress_text))
                    .check(matches(withText(containsString(" of "))))
                    .check(matches(withText(containsString(" exported to"))));

            // After export complete the share via button should be enabled
            onView(withText(R.string.share_export_via))
                    .check(matches(isDisplayed()))
                    .check(matches(isEnabled()));
        }
    }
}