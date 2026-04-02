package com.example.walrusevents.data;

import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Profile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

/**
 * ProfileRepository
 * Handles all Firestore reads and writes for Entrant profiles.
 *
 * Firestore structure:
 *   entrants/{deviceId}  →  { name, email, phone, profileImageUrl, notificationsEnabled }
 */

public class ProfileRepository {

    private static final String COLLECTION = "profiles";
    private static final String WAITLIST_COLLECTION = "waitlist";

    private final FirebaseFirestore db;
    private final CollectionReference profileCollection;
    private final CollectionReference waitlistCollection;

    public ProfileRepository() {
        db = FirebaseFirestore.getInstance();
        profileCollection = db.collection(COLLECTION);
        waitlistCollection = db.collection(WAITLIST_COLLECTION);
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
                    // TODO: notifications

                    Entrant entrant = new Entrant(profile);
                    callback.onEntrantLoaded(entrant);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEntrantLoaded(null);
                });
    }

    /**
     * Gets all the profiles with IDs specified in deviceIdList
     * @param deviceIdList The IDs to look for
     */
    public void getProfilesInList(List<String> deviceIdList, ProfileCallback callback) {
        //Must batch the IDs to look for in lists of size 30 due to Firebase limitations.
        int batches = Math.ceilDiv(deviceIdList.size(), 30);
        for (int i = 0; i < batches; i++)
        {
            List<String> batchList = deviceIdList.subList(i * 30, Math.min((i + 1) * 30, deviceIdList.size()));
            profileCollection.
                    whereIn("deviceId", batchList)
                    .get()
                    .addOnSuccessListener(query -> {
                        for (QueryDocumentSnapshot doc : query) {
                            Profile profile = doc.toObject(Profile.class);
                            Entrant entrant = new Entrant(profile);
                            callback.onEntrantLoaded(entrant);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        callback.onEntrantLoaded(null);
                    });
        }
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

        profileCollection
                .document(entrant.getDeviceId())
                .set(profile)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    /**
     * Completely deletes an entrant's profile document from Firestore.
     * Also removes any waitlist entries tied to the same device ID.
     * This satisfies the entrant's "right to be forgotten" and admin removal.
     *
     * @param deviceId The ID of the entrant to delete
     * @param callback Reports success or failure
     */
    public void deleteProfile(String deviceId, SaveCallback callback) {
        waitlistCollection
                .whereEqualTo("entrantId", deviceId)
                .get()
                .addOnSuccessListener(query -> {
                    WriteBatch batch = db.batch();

                    for (QueryDocumentSnapshot doc : query) {
                        batch.delete(doc.getReference());
                    }

                    batch.delete(profileCollection.document(deviceId));

                    batch.commit()
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                callback.onFailure(e.getMessage());
                            });
                })
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