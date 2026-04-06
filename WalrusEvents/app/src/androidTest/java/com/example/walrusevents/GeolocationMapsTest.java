package com.example.walrusevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import com.example.walrusevents.activity.MapsActivity;
import com.example.walrusevents.activity.OEventPoolActivity;
import com.example.walrusevents.activity.UEventDetailsActivity;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This test was written by Gemini 3, Google DeepMind
 * Fed in the context for geolocation testing
 * 06/04/26
 */

@RunWith(AndroidJUnit4.class)
public class GeolocationMapsTest {

    private String testEventId;
    private Event testEvent;
    private String deviceId;

    // Grant location permissions before the test starts to avoid dialog issues
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    );

    @Before
    public void setUp() throws Exception {
        Intents.init();
        Context context = ApplicationProvider.getApplicationContext();
        deviceId = DeviceIdManager.getOrCreate(context);
        testEventId = "test-geo-event-" + System.currentTimeMillis();

        // Setup a test event that requires geolocation
        testEvent = new Event("Geo Test Party", testEventId, "test-owner");
        testEvent.setUseGeolocation(true);
        testEvent.setIsPrivate(false);

        CountDownLatch latch = new CountDownLatch(2);

        FirebaseFirestore.getInstance().collection("events").document(testEventId).set(testEvent)
                .addOnCompleteListener(task -> latch.countDown());

        ProfileRepository profileRepo = new ProfileRepository();
        Profile profile = new Profile(deviceId, "Geo Tester", "geo@test.com");
        profileRepo.saveProfile(new Entrant(profile), new ProfileRepository.SaveCallback() {
            @Override public void onSuccess() { latch.countDown(); }
            @Override public void onFailure(String error) { latch.countDown(); }
        });

        latch.await(15, TimeUnit.SECONDS);

        // Set a mock location on the emulator so getLastLocation() isn't null
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.executeShellCommand("content insert --uri content://settings/secure --bind name:s:location_mode --bind value:i:3");
        device.executeShellCommand("am startservice -a com.google.android.gms.location.mock.SET_MOCK_LOCATION --es lat 53.5 --es lon -113.5");
        // Fallback: use telnet emulator command if running on standard emulator
        device.executeShellCommand("geo fix -113.5 53.5");
    }

    @After
    public void tearDown() {
        Intents.release();
        new WaitlistRepository().removeEntry(testEventId, deviceId, new WaitlistRepository.SaveCallback() {
            @Override public void onSuccess() {}
            @Override public void onFailure(String error) {}
        });
    }

    @Test
    public void testJoinWaitlistCapturesLocation() throws Exception {
        UserRoleManager.setRole(UserRole.USER);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UEventDetailsActivity.class);
        intent.putExtra("Event", testEvent);

        try (ActivityScenario<UEventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);

            // Permissions are already granted by the @Rule
            onView(withId(R.id.join_event_button)).perform(click());

            // Wait for async join operation to complete
            Thread.sleep(7000);

            // Verify location in database
            CountDownLatch verifyLatch = new CountDownLatch(1);
            final WaitlistEntry[] capturedEntry = new WaitlistEntry[1];

            new WaitlistRepository().getEntry(testEventId, deviceId, entry -> {
                capturedEntry[0] = entry;
                verifyLatch.countDown();
            });

            verifyLatch.await(15, TimeUnit.SECONDS);
            Assert.assertNotNull("Waitlist entry should exist", capturedEntry[0]);
            Assert.assertTrue("Entry should have location captured", capturedEntry[0].hasLocation());
        }
    }

    @Test
    public void testNavigationToMaps() throws InterruptedException {
        UserRoleManager.setRole(UserRole.ORGANIZER);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OEventPoolActivity.class);
        intent.putExtra("Event", testEvent);

        try (ActivityScenario<OEventPoolActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            onView(withId(R.id.map_button)).perform(click());

            intended(allOf(
                    hasComponent(MapsActivity.class.getName()),
                    hasExtra("eventId", testEventId)
            ));
        }
    }
}