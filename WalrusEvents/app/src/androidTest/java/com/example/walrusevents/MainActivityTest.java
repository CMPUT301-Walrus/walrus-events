package com.example.walrusevents;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.walrusevents.activity.AdminHubActivity;
import com.example.walrusevents.activity.MainActivity;
import com.example.walrusevents.activity.OEventsActivity;
import com.example.walrusevents.activity.UEventDetailsActivity;
import com.example.walrusevents.activity.UEventsActivity;
import com.example.walrusevents.activity.USettingsActivity;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
* UI Unit Test Suite
* Developed with the assistance of Gemini 3
* Fed in the parts of code main activity with Intents & Button onClickListeners on MainActivity.java
*  Tests button functionality and role-specific UI functionality
* 3/13/2026
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        // Ensure we start from a known state (USER role)
        while (UserRoleManager.getRole() != UserRole.USER) {
            UserRoleManager.nextRole();
        }
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Verifies clicking through the button cycles through the roles correctly
     */
    @Test
    public void testRoleCycleUpdatesButtonText() {
        // 2. Launch Activity manually so it picks up the reset state
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {

            // Initial state should now be USER
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:USER")));

            // Click to change to ORGANIZER
            onView(withId(R.id.changeRoleButton)).perform(click());
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:ORGANIZER")));

            // Click to change to ADMIN
            onView(withId(R.id.changeRoleButton)).perform(click());
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:ADMIN")));
        }
    }

    /**
     * Verifies Role visibility - Admin button should only show in ADMIN role.
     */
    @Test
    public void testAdminButtonVisibilityLogic() {
        // Switch to ADMIN role
        onView(withId(R.id.changeRoleButton)).perform(click()); // to ORGANIZER
        onView(withId(R.id.changeRoleButton)).perform(click()); // to ADMIN

        // Admin button should be visible, others invisible
        onView(withId(R.id.main_button)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.my_events_button)).check(matches(not(isDisplayed())));
    }

    /**
     * Tests navigation for the settings button, navigates to USettingsActivity
     */
    @Test
    public void testSettingsNavigation() {
        onView(withId(R.id.settings_button)).perform(click());
        intended(hasComponent(USettingsActivity.class.getName()));
    }

    /**
     * Verifies  Admin button correctly navigates to AdminViewActivity.
     */
    @Test
    public void testAdminButtonNavigation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // 1. Change role to ADMIN (USER -> ORGANIZER -> ADMIN)
            onView(withId(R.id.changeRoleButton)).perform(click());
            onView(withId(R.id.changeRoleButton)).perform(click());

            // 2. Click the Admin Button (main_button)
            onView(withId(R.id.main_button)).perform(click());

            // 3. Verify Intent
            intended(hasComponent(AdminHubActivity.class.getName()));
        }
    }

    /**
     * Verifies that 'My Events' opens UEventsActivity when the role is USER.
     */
    @Test
    public void testMyEventsNavigationAsUser() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Ensure we are in USER role
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:USER")));

            // Click My Events button
            onView(withId(R.id.my_events_button)).perform(click());

            // Should go to the User version of events
            intended(hasComponent(UEventsActivity.class.getName()));
        }
    }

    /**
     * Verifies that 'My Events' opens OEventsActivity when the role is ORGANIZER.
     */
    @Test
    public void testMyEventsNavigationAsOrganizer() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // 1. Change role to ORGANIZER (USER -> ORGANIZER)
            onView(withId(R.id.changeRoleButton)).perform(click());
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:ORGANIZER")));

            // 2. Click My Events button
            onView(withId(R.id.my_events_button)).perform(click());

            // 3. Should go to the Organizer version of events
            intended(hasComponent(OEventsActivity.class.getName()));
        }
    }

    /**
     * Verifies that clicking an event list item opens details ONLY when role is USER.
     */
    @Test
    public void testEventListClickNavigation() {
        // Ensure role is USER
        onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:USER")));

        // Click the first item in the ListView
        onData(is(instanceOf(Event.class)))
                .atPosition(0)
                .perform(click());

        intended(hasComponent(UEventDetailsActivity.class.getName()));
    }
}
