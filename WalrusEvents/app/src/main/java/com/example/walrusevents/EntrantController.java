package com.example.walrusevents;

/**
 * EntrantController
 * Controller layer for all Entrant-related actions.
 * Bridges the UI with the Entrant model, WaitlistRepository, and ProfileRepository.
 */

public class EntrantController {

    private final Entrant entrant;
    private final WaitlistRepository waitlistRepository;
    private final ProfileRepository profileRepository;

    public EntrantController(Entrant entrant,
                             WaitlistRepository waitlistRepository,
                             ProfileRepository profileRepository) {
        this.entrant = entrant;
        this.waitlistRepository = waitlistRepository;
        this.profileRepository = profileRepository;
    }

    // ─── Waitlist Actions ─────────────────────────────────────────────────────

    /**
     * Joins the waitlist for a given event.
     * Checks the entrant isn't already on it before writing.
     */
    public void joinWaitlist(String eventId, ActionCallback callback) {
        waitlistRepository.getEntry(eventId, entrant.getEntrantId(), existing -> {
            if (existing != null && existing.getStatus() != WaitlistEntry.Status.CANCELLED) {
                callback.onFailure("Already on the waitlist for this event.");
                return;
            }
            WaitlistEntry entry = new WaitlistEntry(entrant.getEntrantId());
            waitlistRepository.addEntry(eventId, entry, new WaitlistRepository.SaveCallback() {
                @Override public void onSuccess() { callback.onSuccess(); }
                @Override public void onFailure(String error) { callback.onFailure(error); }
            });
        });
    }

    /**
     * Joins the waitlist with a captured geolocation.
     */
    public void joinWaitlistWithLocation(String eventId, double latitude, double longitude,
                                         ActionCallback callback) {
        waitlistRepository.getEntry(eventId, entrant.getEntrantId(), existing -> {
            if (existing != null && existing.getStatus() != WaitlistEntry.Status.CANCELLED) {
                callback.onFailure("Already on the waitlist for this event.");
                return;
            }
            WaitlistEntry entry = new WaitlistEntry(entrant.getEntrantId());
            entry.setLocation(latitude, longitude);
            waitlistRepository.addEntry(eventId, entry, new WaitlistRepository.SaveCallback() {
                @Override public void onSuccess() { callback.onSuccess(); }
                @Override public void onFailure(String error) { callback.onFailure(error); }
            });
        });
    }

    /**
     * Leaves the waitlist by setting status to CANCELLED.
     * Entrants who have already ACCEPTED cannot self-cancel.
     */
    public void leaveWaitlist(String eventId, ActionCallback callback) {
        waitlistRepository.getEntry(eventId, entrant.getEntrantId(), entry -> {
            if (entry == null) {
                callback.onFailure("Not on the waitlist for this event.");
                return;
            }
            if (entry.getStatus() == WaitlistEntry.Status.ACCEPTED) {
                callback.onFailure("Cannot leave, registration already confirmed.");
                return;
            }
            waitlistRepository.updateStatus(eventId, entrant.getEntrantId(),
                    WaitlistEntry.Status.CANCELLED, new WaitlistRepository.SaveCallback() {
                        @Override public void onSuccess() { callback.onSuccess(); }
                        @Override public void onFailure(String error) { callback.onFailure(error); }
                    });
        });
    }

    /**
     * Accepts a lottery invitation for an event.
     * Only valid if the entrant's current status is INVITED.
     */
    public void acceptInvitation(String eventId, ActionCallback callback) {
        waitlistRepository.getEntry(eventId, entrant.getEntrantId(), entry -> {
            if (entry == null || entry.getStatus() != WaitlistEntry.Status.INVITED) {
                callback.onFailure("No pending invitation to accept.");
                return;
            }
            waitlistRepository.updateStatus(eventId, entrant.getEntrantId(),
                    WaitlistEntry.Status.ACCEPTED, new WaitlistRepository.SaveCallback() {
                        @Override public void onSuccess() { callback.onSuccess(); }
                        @Override public void onFailure(String error) { callback.onFailure(error); }
                    });
        });
    }

    /**
     * Declines a lottery invitation for an event.
     * Only valid if the entrant's current status is INVITED.
     */
    public void declineInvitation(String eventId, ActionCallback callback) {
        waitlistRepository.getEntry(eventId, entrant.getEntrantId(), entry -> {
            if (entry == null || entry.getStatus() != WaitlistEntry.Status.INVITED) {
                callback.onFailure("No pending invitation to decline.");
                return;
            }
            waitlistRepository.updateStatus(eventId, entrant.getEntrantId(),
                    WaitlistEntry.Status.DECLINED, new WaitlistRepository.SaveCallback() {
                        @Override public void onSuccess() { callback.onSuccess(); }
                        @Override public void onFailure(String error) { callback.onFailure(error); }
                    });
        });
    }

    // ─── Profile Actions ──────────────────────────────────────────────────────

    /**
     * Updates the entrant's contact info and saves to Firestore.
     */
    public void updateProfile(String name, String email, String phone, ActionCallback callback) {
        entrant.updateContactInfo(name, email, phone);
        profileRepository.saveProfile(entrant, new ProfileRepository.SaveCallback() {
            @Override public void onSuccess() { callback.onSuccess(); }
            @Override public void onFailure(String error) { callback.onFailure(error); }
        });
    }

    /**
     * Opts in to notifications and saves the preference.
     */
    public void enableNotifications(ActionCallback callback) {
        entrant.optInToNotifications();
        profileRepository.saveProfile(entrant, new ProfileRepository.SaveCallback() {
            @Override public void onSuccess() { callback.onSuccess(); }
            @Override public void onFailure(String error) { callback.onFailure(error); }
        });
    }

    /**
     * Opts out of notifications and saves the preference.
     */
    public void disableNotifications(ActionCallback callback) {
        entrant.optOutOfNotifications();
        profileRepository.saveProfile(entrant, new ProfileRepository.SaveCallback() {
            @Override public void onSuccess() { callback.onSuccess(); }
            @Override public void onFailure(String error) { callback.onFailure(error); }
        });
    }

    /**
     * Deletes this entrant's profile and all personal data from Firestore.
     */
    public void deleteProfile(ActionCallback callback) {
        entrant.requestProfileDeletion();
        profileRepository.deleteProfile(entrant.getEntrantId(), new ProfileRepository.SaveCallback() {
            @Override public void onSuccess() { callback.onSuccess(); }
            @Override public void onFailure(String error) { callback.onFailure(error); }
        });
    }

    // ─── Callback Interface ───────────────────────────────────────────────────

    public interface ActionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
