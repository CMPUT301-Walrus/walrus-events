package com.example.walrusevents;

public class Entrant {

    private String entrantId;
    private Profile profile;

    public Entrant(String entrantId, Profile profile) {
        this.entrantId = entrantId;
        this.profile = profile;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    // ─── Notification Preferences ────────────────────────────────────────────

    /**
     * Opt in to system notifications.
     */
    public void optInToNotifications() {
        if (profile != null) {
            profile.setNotificationsEnabled(true);
        }
    }

    /**
     * Opt out of system notifications.
     */
    public void optOutOfNotifications() {
        if (profile != null) {
            profile.setNotificationsEnabled(false);
        }
    }

    /**
     * Returns whether this entrant has notifications enabled.
     */
    public boolean hasNotificationsEnabled() {
        return profile != null && profile.isNotificationsEnabled();
    }

    // ─── Profile Management ───────────────────────────────────────────────────

    /**
     * Updates this entrant's contact information.
     * @param name  New display name
     * @param email New email address
     * @param phone New phone number (may be null)
     */
    public void updateContactInfo(String name, String email, String phone) {
        if (profile == null) return;
        profile.setName(name);
        profile.setEmail(email);
        profile.setPhone(phone);
    }

    /**
     * Clears all personal data from the profile in preparation for account deletion.
     * The actual Firestore deletion must be triggered via ProfileRepository.
     */
    public void requestProfileDeletion() {
        if (profile != null) {
            profile.clearPersonalData();
        }
    }
}