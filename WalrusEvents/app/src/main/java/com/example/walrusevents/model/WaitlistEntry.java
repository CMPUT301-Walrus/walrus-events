package com.example.walrusevents.model;

import com.google.firebase.Timestamp;

/**
 * WaitlistEntry
 * Represents a single entrant's position and status within one event's waitlist.
 * Stored as a Firestore document at: events/{eventId}/waitlist/{entrantId}
 */

public class WaitlistEntry {

    /**
     * Possible states for an entrant within a waitlist.
     */
    public enum Status {
        PENDING,    // Entrant is on the waitlist, waiting for the lottery
        NOT_CHOSEN,
        INVITED,    // Entrant was selected by the lottery and invited to confirm
        ACCEPTED,   // Entrant accepted the invitation and is registered
        DECLINED,   // Entrant declined the invitation
        CANCELED   // Entrant left voluntarily, or was removed by the organizer
    }

    private String entrantId;
    private String eventId;
    private Status status;
    private Timestamp joinedAt;
    private Double latitude;
    private Double longitude;

    public WaitlistEntry() {}

    /**
     * Constructor method for an entry on the waitlist for an event
     * @param entrantId id of entrant
     * @param eventId id of event
     */
    public WaitlistEntry(String entrantId, String eventId) {
        this.entrantId = entrantId;
        this.eventId = eventId;
        this.status = Status.PENDING;
        this.joinedAt = Timestamp.now();
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    /**
     * Gets the entrant id
     * @return
     */
    public String getEntrantId() { return entrantId; }

    /**
     * Sets the entrant id
     * @param entrantId
     */
    public void setEntrantId(String entrantId) { this.entrantId = entrantId; }

    /**
     * Gets the event id
     * @return
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event id
     * @param eventId
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the status of the entrant
     * @return
     */
    public Status getStatus() { return status; }

    /**
     * Sets the status of the entrant
     * @param status new status
     */
    public void setStatus(Status status) { this.status = status; }

    /**
     * Gets the time the entrant joined the waitlist
     * @return
     */
    public Timestamp getJoinedAt() { return joinedAt; }

    /**
     * Sets the time the entrant joined the waitlist
     * @param joinedAt time at which event was joined
     */
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }

    /**
     * Gets the latitude captured when this entrant joined the waitlist
     * @return
     */
    public Double getLatitude() { return latitude; }

    /**
     * Sets the latitude captured when this entrant joined the waitlist
     * @param latitude new latitude
     */
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    /**
     * Gets the longitude captured when this entrant joined the waitlist
     * @return
     */
    public Double getLongitude() { return longitude; }

    /**
     * Sets the longitude captured when this entrant joined the waitlist
     * @param longitude new longitude
     */
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    /**
     * Sets the geolocation captured when this entrant joined the waitlist.
     */
    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns true if this entry has a recorded location.
     */
    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }
}