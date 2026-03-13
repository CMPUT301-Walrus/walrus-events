package com.example.walrusevents;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the WaitlistEntry class.
 */
public class WaitlistEntryTest {

    private static final String DEVICE_ID = "device_test_001";

    @Test
    public void waitlistEntry_defaultStatusIsPending() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        assertEquals(WaitlistEntry.Status.PENDING, entry.getStatus());
    }

    @Test
    public void waitlistEntry_storesEntrantId() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        assertEquals(DEVICE_ID, entry.getEntrantId());
    }

    @Test
    public void waitlistEntry_joinedAtSetOnConstruction() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        assertNotNull(entry.getJoinedAt());
    }

    @Test
    public void waitlistEntry_hasNoLocationByDefault() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        assertFalse(entry.hasLocation());
    }

    @Test
    public void waitlistEntry_setLocation_storesLatLng() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        entry.setLocation(53.5, -113.4);

        assertTrue(entry.hasLocation());
        assertEquals(53.5, entry.getLatitude(), 0.001);
        assertEquals(-113.4, entry.getLongitude(), 0.001);
    }

    @Test
    public void waitlistEntry_canTransitionToInvited() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        entry.setStatus(WaitlistEntry.Status.INVITED);
        assertEquals(WaitlistEntry.Status.INVITED, entry.getStatus());
    }

    @Test
    public void waitlistEntry_canTransitionToAccepted() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        entry.setStatus(WaitlistEntry.Status.ACCEPTED);
        assertEquals(WaitlistEntry.Status.ACCEPTED, entry.getStatus());
    }

    @Test
    public void waitlistEntry_canTransitionToDeclined() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        entry.setStatus(WaitlistEntry.Status.DECLINED);
        assertEquals(WaitlistEntry.Status.DECLINED, entry.getStatus());
    }

    @Test
    public void waitlistEntry_canTransitionToCancelled() {
        WaitlistEntry entry = new WaitlistEntry(DEVICE_ID, "0");
        entry.setStatus(WaitlistEntry.Status.CANCELLED);
        assertEquals(WaitlistEntry.Status.CANCELLED, entry.getStatus());
    }
}