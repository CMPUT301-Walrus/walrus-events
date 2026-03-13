package com.example.walrusevents;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * WaitlistRepository
 * Handles all Firestore reads and writes for waitlist entries.
 *
 * Firestore structure (subcollection per event):
 *   events/{eventId}/waitlist/{entrantId} -> { status, joinedAt, latitude, longitude }
 *
 * Firestore's subcollection is the waitlist. One entrant can appear in many events' waitlists,
 * each with their own independent status.
 */

public class WaitlistRepository {

    private static final String EVENTS = "events";
    private static final String WAITLIST = "waitlist";

    private final FirebaseFirestore db;
    private final CollectionReference waitListCollection;

    public WaitlistRepository() {
        db = FirebaseFirestore.getInstance();
        waitListCollection = db.collection(WAITLIST);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

        /**
         * Fetches a single entrant's entry for a given event.
         * Returns null if the entrant is not on the waitlist.
         */
    public void getEntry(String eventId, String entrantId, EntryCallback callback) {
        waitListCollection
                .document(entrantId + eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onEntryLoaded(null);
                        return;
                    }
                    WaitlistEntry entry = doc.toObject(WaitlistEntry.class);
                    callback.onEntryLoaded(entry);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEntryLoaded(null);
                });
    }

    /**
     * Fetches all entries for an event with a specific status.
     * Useful for the lottery (get all PENDING) or organizer views.
     */
    public void getEntriesByStatus(String eventId, WaitlistEntry.Status status, EntryListCallback callback) {
        waitListCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", status.name())
                .get()
                .addOnSuccessListener(query -> {
                    List<WaitlistEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        WaitlistEntry entry = doc.toObject(WaitlistEntry.class);
                        entries.add(entry);
                    }
                    callback.onEntriesLoaded(entries);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEntriesLoaded(new ArrayList<>());
                });
    }

    /**
     * Fetches all entries for an event regardless of status.
     * Used by organizers to see the full waitlist.
     */
    public void getAllEntries(String eventId, EntryListCallback callback) {
        waitListCollection
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(query -> {
                    List<WaitlistEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        WaitlistEntry entry = doc.toObject(WaitlistEntry.class);
                        entries.add(entry);
                    }
                    callback.onEntriesLoaded(entries);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEntriesLoaded(new ArrayList<>());
                });
    }

    /**
     * Fetches all waitlist entries for a given entrant across all events.
     * Used by the user history screen — replaces the old fan-out approach.
     */
    public void getEntriesByEntrant(String entrantId, EntryListCallback callback) {
        waitListCollection
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnSuccessListener(query -> {
                    List<WaitlistEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        WaitlistEntry entry = doc.toObject(WaitlistEntry.class);
                        entries.add(entry);
                    }
                    callback.onEntriesLoaded(entries);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEntriesLoaded(new ArrayList<>());
                });
    }

    // ─── Write ────────────────────────────────────────────────────────────────

    /**
     * Adds a new entry for an entrant joining an event's waitlist.
     * Uses the entrantId and eventId as the document ID so duplicate joins are naturally prevented.
     */
    public void addEntry(WaitlistEntry entry, SaveCallback callback) {
        waitListCollection
                .document(entry.getEntrantId() + entry.getEventId())
                .set(entry)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Updates an entrant's status for a given event.
     * Used for all status transitions: invite, accept, decline, cancel.
     */
    public void updateStatus(String eventId, String entrantId,
                             WaitlistEntry.Status newStatus, SaveCallback callback) {
        waitListCollection
                .document(entrantId + eventId)
                .update("status", newStatus.name())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Removes an entrant's entry document from the waitlist subcollection.
     * Used when an event is deleted, or for hard removal by an admin.
     */
    public void removeEntry(String eventId, String entrantId, SaveCallback callback) {
        waitListCollection
                .document(entrantId + eventId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    // ─── Callback Interfaces ──────────────────────────────────────────────────

    public interface EntryCallback {
        void onEntryLoaded(WaitlistEntry entry);
    }

    public interface EntryListCallback {
        void onEntriesLoaded(List<WaitlistEntry> entries);
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String error);
    }
}