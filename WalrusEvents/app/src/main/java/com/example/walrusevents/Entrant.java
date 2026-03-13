package com.example.walrusevents;

/**
 * Entrant
 * Represents a user who signs up for events through the lottery system.
 */

public class Entrant {

    private String deviceId;
    private Profile profile;

    public Entrant(Profile profile) {
        this.profile = profile;
        this.deviceId = profile.getDeviceId();
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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