package com.example.walrusevents;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * ProfileRepository
 * Handles all Firestore reads and writes for Entrant profiles.
 *
 * Firestore structure:
 *   entrants/{deviceId}  →  { name, email, phone, profileImageUrl, notificationsEnabled }
 */

public class ProfileRepository {

    private static final String COLLECTION = "profiles";

    private final FirebaseFirestore db;
    private final CollectionReference profileCollection;

    public ProfileRepository() {
        db = FirebaseFirestore.getInstance();
        profileCollection = db.collection(COLLECTION);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    /**
     * Fetches an Entrant profile from Firestore by device ID.
     * Returns null via the callback if no profile is found.
     *
     * @param deviceId The unique device ID to look up
     * @param callback Receives the loaded Entrant (or null)
     */
    public void getProfile(String deviceId, ProfileCallback callback) {
        profileCollection
                .document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onEntrantLoaded(null);
                        return;
                    }
                    Profile profile = new Profile();
                    profile.setDeviceId(deviceId);
                    profile.setName(doc.getString("name"));
                    profile.setEmail(doc.getString("email"));
                    profile.setPhone(doc.getString("phone"));
                    Boolean notif = doc.getBoolean("notificationsEnabled");
                    profile.setNotificationsEnabled(notif != null ? notif : true);

                    Entrant entrant = new Entrant(deviceId, profile);
                    callback.onEntrantLoaded(entrant);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEntrantLoaded(null);
                });
    }

    // ─── Write ────────────────────────────────────────────────────────────────

    /**
     * Creates or updates an entrant's profile document in Firestore.
     *
     * @param entrant  The entrant whose profile to save
     * @param callback Reports success or failure
     */
    public void saveProfile(Entrant entrant, SaveCallback callback) {
        Profile profile = entrant.getProfile();
        if (profile == null) {
            callback.onFailure("Entrant has no profile to save.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", profile.getName());
        data.put("email", profile.getEmail());
        data.put("phone", profile.getPhone());
        data.put("notificationsEnabled", profile.isNotificationsEnabled());

        profileCollection
                .document(entrant.getEntrantId())
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    /**
     * Completely deletes an entrant's profile document from Firestore.
     * This satisfies the entrant's "right to be forgotten" and admin removal.
     *
     * @param deviceId The ID of the entrant to delete
     * @param callback Reports success or failure
     */
    public void deleteProfile(String deviceId, SaveCallback callback) {
        profileCollection
                .document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    // ─── Callback Interfaces ──────────────────────────────────────────────────

    public interface ProfileCallback {
        void onEntrantLoaded(Entrant entrant);
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String error);
    }
}