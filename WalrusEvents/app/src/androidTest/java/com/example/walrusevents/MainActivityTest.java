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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.walrusevents.activity.AdminHubActivity;
import com.example.walrusevents.activity.MainActivity;
import com.example.walrusevents.activity.OEventsActivity;
import com.example.walrusevents.activity.UEventDetailsActivity;
import com.example.walrusevents.activity.UEventsActivity;
import com.example.walrusevents.activity.USettingsActivity;
import com.example.walrusevents.data.ProfilePermissionsRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.AccountRole;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.model.ProfilePermissions;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI Unit Test Suite for MainActivity.
 * This suite verifies navigation and role-specific visibility.
 * It pre-configures Firebase to match the expected hierarchy: ENTRANT, ORGANIZER, ADMIN.
 * This test was generated/edited by Gemini 3, Google DeepMind
 * Fed in the context for main activity testing
 * 06/04/26
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() throws InterruptedException {
        Intents.init();
        // Ensure we start from a known state
        UserRoleManager.setRole(UserRole.USER);
        
        // Pre-setup a complete profile to avoid redirection to USettingsActivity
        setupTestProfile();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private void setupTestProfile() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Context context = ApplicationProvider.getApplicationContext();
        String deviceId = DeviceIdManager.getOrCreate(context);
        ProfileRepository repo = new ProfileRepository();
        
        Profile profile = new Profile(deviceId, "UI Test User", "test@example.com");
        repo.saveProfile(new Entrant(profile), new ProfileRepository.SaveCallback() {
            @Override
            public void onSuccess() { latch.countDown(); }
            @Override
            public void onFailure(String error) { latch.countDown(); }
        });
        latch.await(10, TimeUnit.SECONDS);
    }

    private void setupTestEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Event event = new Event("UI Test Event", "test-event-ui", "test-owner");
        event.setIsPrivate(false);
        db.collection("events").document("test-event-ui").set(event)
                .addOnCompleteListener(task -> latch.countDown());
        latch.await(10, TimeUnit.SECONDS);
    }

    private void grantPermission(AccountRole role) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Context context = ApplicationProvider.getApplicationContext();
        String deviceId = DeviceIdManager.getOrCreate(context);
        ProfilePermissionsRepository repo = new ProfilePermissionsRepository();
        ProfilePermissions permissions = new ProfilePermissions(deviceId, role, false);
        repo.savePermissions(permissions, new ProfilePermissionsRepository.SaveCallback() {
            @Override
            public void onSuccess() { latch.countDown(); }
            @Override
            public void onFailure(String error) { latch.countDown(); }
        });
        latch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testRoleCycleUpdatesButtonText() throws InterruptedException {
        grantPermission(AccountRole.ADMIN);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Wait for MainActivity to settle
            Thread.sleep(2000);

            // Initial state should be USER
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:USER")));

            // Click to change to ORGANIZER
            onView(withId(R.id.changeRoleButton)).perform(click());
            Thread.sleep(2000); // Wait for async role change logic
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:ORGANIZER")));

            // Click to change to ADMIN
            onView(withId(R.id.changeRoleButton)).perform(click());
            Thread.sleep(2000);
            onView(withId(R.id.changeRoleButton)).check(matches(withText("Role:ADMIN")));
        }
    }

    @Test
    public void testAdminButtonVisibilityLogic() throws InterruptedException {
        grantPermission(AccountRole.ADMIN);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(2000);

            // Navigate to ADMIN role
            onView(withId(R.id.changeRoleButton)).perform(click()); // to ORGANIZER
            Thread.sleep(2000);
            onView(withId(R.id.changeRoleButton)).perform(click()); // to ADMIN
            Thread.sleep(2000);

            // Admin button (main_button) should be visible, others invisible
            onView(withId(R.id.main_button)).check(matches(isDisplayed()));
            onView(withId(R.id.settings_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.my_events_button)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testSettingsNavigation() throws InterruptedException {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.settings_button)).perform(click());
            intended(hasComponent(USettingsActivity.class.getName()));
        }
    }

    @Test
    public void testAdminButtonNavigation() throws InterruptedException {
        grantPermission(AccountRole.ADMIN);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.changeRoleButton)).perform(click());
            Thread.sleep(2000);
            onView(withId(R.id.changeRoleButton)).perform(click());
            Thread.sleep(2000);

            onView(withId(R.id.main_button)).perform(click());
            intended(hasComponent(AdminHubActivity.class.getName()));
        }
    }

    @Test
    public void testMyEventsNavigationAsUser() throws InterruptedException {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.my_events_button)).perform(click());
            intended(hasComponent(UEventsActivity.class.getName()));
        }
    }

    @Test
    public void testMyEventsNavigationAsOrganizer() throws InterruptedException {
        grantPermission(AccountRole.ORGANIZER);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.changeRoleButton)).perform(click());
            Thread.sleep(2000);
            
            onView(withId(R.id.my_events_button)).perform(click());
            intended(hasComponent(OEventsActivity.class.getName()));
        }
    }

    @Test
    public void testEventListClickNavigation() throws InterruptedException {
        setupTestEvent();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Wait for events to load from Firebase
            Thread.sleep(5000);
            
            onData(is(instanceOf(Event.class)))
                    .atPosition(0)
                    .perform(click());

            intended(hasComponent(UEventDetailsActivity.class.getName()));
        }
    }
}
