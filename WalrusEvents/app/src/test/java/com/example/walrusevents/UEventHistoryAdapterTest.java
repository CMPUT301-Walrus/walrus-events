package com.example.walrusevents;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.util.UEventHistoryAdapter;

import org.junit.Test;

public class UEventHistoryAdapterTest {

    @Test
    public void coOrganizerHistoryItem_opensOrganizerView() {
        Event event = new Event("Test Event", "event-1", "owner-1");
        event.addOwner("owner-2");

        UEventHistoryAdapter.HistoryItem item = UEventHistoryAdapter.HistoryItem.coOrganizer(event);

        assertTrue(item.opensOrganizerView("owner-2"));
    }

    @Test
    public void waitlistHistoryItem_doesNotOpenOrganizerView() {
        Event event = new Event("Test Event", "event-1", "owner-1");
        event.addOwner("owner-2");

        UEventHistoryAdapter.HistoryItem item =
                new UEventHistoryAdapter.HistoryItem(event, WaitlistEntry.Status.INVITED);

        assertFalse(item.opensOrganizerView("owner-2"));
    }
}
