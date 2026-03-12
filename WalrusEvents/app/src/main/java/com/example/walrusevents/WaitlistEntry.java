package com.example.walrusevents;

import com.google.firebase.Timestamp;

public class WaitlistEntry {

    /**
     * Possible states for an entrant within a waitlist.
     */
    public enum Status {
        PENDING,    // Entrant is on the waitlist, waiting for the lottery
        INVITED,    // Entrant was selected by the lottery and invited to confirm
        ACCEPTED,   // Entrant accepted the invitation and is registered
        DECLINED,   // Entrant declined the invitation
        CANCELLED   // Entrant left voluntarily, or was removed by the organizer
    }

    private String entrantId;
    private Status status;
    private Timestamp joinedAt;
    private Double latitude;
    private Double longitude;

    public WaitlistEntry(String entrantId) {
        this.entrantId = entrantId;
        this.status = Status.PENDING;
        this.joinedAt = Timestamp.now();
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getEntrantId() { return entrantId; }
    public void setEntrantId(String entrantId) { this.entrantId = entrantId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
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