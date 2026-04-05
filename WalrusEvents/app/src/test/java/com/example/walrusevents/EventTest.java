package com.example.walrusevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.walrusevents.model.Event;

import org.junit.Test;

public class EventTest {

    @Test
    public void applicantCapacity_allowsPositiveValueWhenEntrantCapacityIsUnlimited() {
        Event event = new Event("Test Event", "event-1", "owner-1");

        assertTrue(event.setEntrantCapacity(0));
        assertTrue(event.setApplicantCapacity(25));
        assertEquals(25, event.getApplicantCapacity());
    }

    @Test
    public void loweringEntrantCapacity_clampsApplicantCapacity() {
        Event event = new Event("Test Event", "event-1", "owner-1");

        assertTrue(event.setEntrantCapacity(20));
        assertTrue(event.setApplicantCapacity(15));
        assertTrue(event.setEntrantCapacity(10));
        assertEquals(10, event.getApplicantCapacity());
    }

    @Test
    public void secondaryOwner_isRecognizedAsCoOrganizer() {
        Event event = new Event("Test Event", "event-1", "owner-1");

        event.addOwner("owner-2");

        assertTrue(event.isCoOrganizer("owner-2"));
    }

    @Test
    public void primaryOwner_isNotRecognizedAsCoOrganizer() {
        Event event = new Event("Test Event", "event-1", "owner-1");

        event.addOwner("owner-2");

        assertTrue(event.isOwner("owner-1"));
        assertEquals(false, event.isCoOrganizer("owner-1"));
    }
}
