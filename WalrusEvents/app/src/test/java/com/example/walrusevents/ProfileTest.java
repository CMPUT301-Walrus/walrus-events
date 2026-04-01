package com.example.walrusevents;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.walrusevents.model.Profile;

/**
 * Unit tests for the Profile class.
 */
public class ProfileTest {

    private static final String DEVICE_ID = "device_test_001";

    private Profile profile;

    @Before
    public void setUp() {
        profile = new Profile(DEVICE_ID, "Alice", "alice@example.com");
    }

    @Test
    public void profile_storesBasicFields() {
        assertEquals(DEVICE_ID, profile.getDeviceId());
        assertEquals("Alice", profile.getName());
        assertEquals("alice@example.com", profile.getEmail());
    }

    @Test
    public void profile_notificationsEnabledByDefault() {
        assertTrue(profile.isNotificationsEnabled());
    }

    @Test
    public void profile_hasRequiredContactInfo_whenNameAndEmailPresent() {
        assertTrue(profile.hasRequiredContactInfo());
    }

    @Test
    public void profile_hasRequiredContactInfo_falseWhenNameMissing() {
        profile.setName("   ");
        assertFalse(profile.hasRequiredContactInfo());
    }

    @Test
    public void profile_hasRequiredContactInfo_falseWhenEmailMissing() {
        profile.setEmail(null);
        assertFalse(profile.hasRequiredContactInfo());
    }

    @Test
    public void profile_canSetPhone() {
        profile.setPhone("555-1234");
        assertEquals("555-1234", profile.getPhone());
    }

    @Test
    public void profile_canToggleNotifications() {
        profile.setNotificationsEnabled(false);
        assertFalse(profile.isNotificationsEnabled());

        profile.setNotificationsEnabled(true);
        assertTrue(profile.isNotificationsEnabled());
    }

    @Test
    public void profile_clearPersonalData_wipesNameEmailPhone() {
        profile.setPhone("555-1234");
        profile.clearPersonalData();

        assertNull(profile.getName());
        assertNull(profile.getEmail());
        assertNull(profile.getPhone());
    }

    @Test
    public void profile_clearPersonalData_keepsDeviceId() {
        // Device ID must survive deletion so the Firestore document can still be found
        profile.clearPersonalData();
        assertEquals(DEVICE_ID, profile.getDeviceId());
    }
}
