package com.example.walrusevents;

import java.util.ArrayList;
import java.util.List;

public class Waitlist {

    private String eventId;
    private int maxCapacity;            // 0 = unlimited
    private List<EntrantStatus> entries;

    public Waitlist() {
        this.entries = new ArrayList<>();
    }

    public Waitlist(String eventId, int maxCapacity) {
        this.eventId = eventId;
        this.maxCapacity = maxCapacity;
        this.entries = new ArrayList<>();
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public List<EntrantStatus> getEntries() { return entries; }
    public void setEntries(List<EntrantStatus> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }

    // ─── Entrant Management ───────────────────────────────────────────────────

    /**
     * Adds an entrant to the waitlist with PENDING status.
     * Fails silently if the entrant is already on the list or the waitlist is full.
     *
     * @param entrantId The ID of the entrant joining the waitlist
     * @return true if the entrant was successfully added, false otherwise
     */
    public boolean addEntrant(String entrantId) {
        if (findEntry(entrantId) != null) {
            return false; // Already on the waitlist
        }
        if (maxCapacity > 0 && getTotalCount() >= maxCapacity) {
            return false; // Waitlist is full
        }
        entries.add(new EntrantStatus(entrantId));
        return true;
    }

    /**
     * Adds an entrant to the waitlist with a recorded geolocation.
     *
     * @param entrantId The ID of the entrant joining
     * @param latitude  The entrant's latitude at join time
     * @param longitude The entrant's longitude at join time
     * @return true if added successfully, false otherwise
     */
    public boolean addEntrantWithLocation(String entrantId, double latitude, double longitude) {
        if (!addEntrant(entrantId)) return false;
        EntrantStatus entry = findEntry(entrantId);
        if (entry != null) {
            entry.setLocation(latitude, longitude);
        }
        return true;
    }

    /**
     * Removes an entrant from the waitlist entirely (sets status to CANCELLED).
     * Entrants with ACCEPTED status cannot leave — the organizer must cancel them.
     *
     * @param entrantId The ID of the entrant leaving
     * @return true if successfully cancelled, false if not found or already accepted
     */
    public boolean removeEntrant(String entrantId) {
        EntrantStatus entry = findEntry(entrantId);
        if (entry == null) return false;
        if (entry.getStatus() == EntrantStatus.Status.ACCEPTED) return false;
        entry.setStatus(EntrantStatus.Status.CANCELLED);
        return true;
    }

    /**
     * Updates an entrant's status to ACCEPTED.
     * Only valid if the entrant is currently INVITED.
     *
     * @param entrantId The ID of the entrant accepting
     * @return true if successful, false if the entrant is not in INVITED state
     */
    public boolean acceptInvitation(String entrantId) {
        EntrantStatus entry = findEntry(entrantId);
        if (entry == null || entry.getStatus() != EntrantStatus.Status.INVITED) return false;
        entry.setStatus(EntrantStatus.Status.ACCEPTED);
        return true;
    }

    /**
     * Updates an entrant's status to DECLINED.
     * Only valid if the entrant is currently INVITED.
     *
     * @param entrantId The ID of the entrant declining
     * @return true if successful, false if the entrant is not in INVITED state
     */
    public boolean declineInvitation(String entrantId) {
        EntrantStatus entry = findEntry(entrantId);
        if (entry == null || entry.getStatus() != EntrantStatus.Status.INVITED) return false;
        entry.setStatus(EntrantStatus.Status.DECLINED);
        return true;
    }

    /**
     * Marks an entrant as INVITED. Called by the LotteryEngine after a draw.
     * Only valid if the entrant is currently PENDING.
     *
     * @param entrantId The ID of the entrant being invited
     * @return true if successful, false if the entrant is not PENDING
     */
    public boolean inviteEntrant(String entrantId) {
        EntrantStatus entry = findEntry(entrantId);
        if (entry == null || entry.getStatus() != EntrantStatus.Status.PENDING) return false;
        entry.setStatus(EntrantStatus.Status.INVITED);
        return true;
    }

    /**
     * Cancels an entrant regardless of their current status. Used by Organizers.
     *
     * @param entrantId The ID of the entrant to cancel
     * @return true if found and cancelled, false if not found
     */
    public boolean cancelEntrant(String entrantId) {
        EntrantStatus entry = findEntry(entrantId);
        if (entry == null) return false;
        entry.setStatus(EntrantStatus.Status.CANCELLED);
        return true;
    }

    // ─── Queries ──────────────────────────────────────────────────────────────

    /**
     * Returns the total number of active entrants (excludes CANCELLED).
     */
    public int getTotalCount() {
        int count = 0;
        for (EntrantStatus e : entries) {
            if (e.getStatus() != EntrantStatus.Status.CANCELLED) count++;
        }
        return count;
    }

    /**
     * Returns all entries with a specific status.
     *
     * @param status The status to filter by
     * @return A list of matching WaitlistEntry objects
     */
    public List<EntrantStatus> getEntriesByStatus(EntrantStatus.Status status) {
        List<EntrantStatus> result = new ArrayList<>();
        for (EntrantStatus e : entries) {
            if (e.getStatus() == status) result.add(e);
        }
        return result;
    }

    /**
     * Returns the count of entries with a specific status.
     */
    public int getCountByStatus(EntrantStatus.Status status) {
        return getEntriesByStatus(status).size();
    }

    /**
     * Returns whether the waitlist has reached its maximum capacity.
     * Always returns false when maxCapacity is 0 (unlimited).
     */
    public boolean isFull() {
        if (maxCapacity == 0) return false;
        return getTotalCount() >= maxCapacity;
    }

    /**
     * Finds the WaitlistEntry for a given entrant ID, or null if not present.
     */
    public EntrantStatus findEntry(String entrantId) {
        for (EntrantStatus e : entries) {
            if (e.getEntrantId().equals(entrantId)) return e;
        }
        return null;
    }

    /**
     * Returns true if the entrant is on this waitlist (in any non-cancelled state).
     */
    public boolean containsEntrant(String entrantId) {
        EntrantStatus entry = findEntry(entrantId);
        return entry != null && entry.getStatus() != EntrantStatus.Status.CANCELLED;
    }

    /**
     * Returns all entries that have a recorded geolocation.
     * Used by the Organizer to view a waitlist map.
     */
    public List<EntrantStatus> getEntriesWithLocation() {
        List<EntrantStatus> result = new ArrayList<>();
        for (EntrantStatus e : entries) {
            if (e.hasLocation()) result.add(e);
        }
        return result;
    }
}
