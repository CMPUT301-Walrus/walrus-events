package com.example.walrusevents;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Profile;

/**
 * Unit tests for the Entrant class.
 */
public class EntrantTest {

    private static final String DEVICE_ID = "device_test_001";

    private Profile profile;
    private Entrant entrant;

    @Before
    public void setUp() {
        profile = new Profile(DEVICE_ID, "Alice", "alice@example.com");
        entrant = new Entrant(profile);
    }

    @Test
    public void entrant_storesIdAndProfile() {
        assertEquals(DEVICE_ID, entrant.getDeviceId());
        assertNotNull(entrant.getProfile());
    }

    @Test
    public void entrant_notificationsEnabledByDefault() {
        assertTrue(entrant.hasNotificationsEnabled());
    }

    @Test
    public void entrant_optOutOfNotifications() {
        entrant.optOutOfNotifications();
        assertFalse(entrant.hasNotificationsEnabled());
    }

    @Test
    public void entrant_optBackInToNotifications() {
        entrant.optOutOfNotifications();
        entrant.optInToNotifications();
        assertTrue(entrant.hasNotificationsEnabled());
    }

    @Test
    public void entrant_updateContactInfo_updatesProfile() {
        entrant.updateContactInfo("Bob", "bob@example.com", "555-1234");

        assertEquals("Bob", entrant.getProfile().getName());
        assertEquals("bob@example.com", entrant.getProfile().getEmail());
        assertEquals("555-1234", entrant.getProfile().getPhone());
    }

    @Test
    public void entrant_updateContactInfo_allowsNullPhone() {
        entrant.updateContactInfo("Bob", "bob@example.com", null);
        assertNull(entrant.getProfile().getPhone());
    }

    @Test
    public void entrant_requestProfileDeletion_clearsPersonalData() {
        entrant.updateContactInfo("Bob", "bob@example.com", "555-1234");
        entrant.requestProfileDeletion();

        assertNull(entrant.getProfile().getName());
        assertNull(entrant.getProfile().getEmail());
        assertNull(entrant.getProfile().getPhone());
    }

    @Test
    public void entrant_requestProfileDeletion_keepsDeviceId() {
        entrant.requestProfileDeletion();
        // Device ID must survive deletion so the Firestore document can still be found
        assertEquals(DEVICE_ID, entrant.getProfile().getDeviceId());
    }
}
