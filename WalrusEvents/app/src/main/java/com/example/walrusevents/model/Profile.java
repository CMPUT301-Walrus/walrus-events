package com.example.walrusevents.model;

/**
 * Profile
 * Data container holding a user's personal information and device id.
 * Used for passwordless authentication via device ID.
 */
public class Profile {

    private String deviceId;
    private String name;
    private String email;
    private String phone;
    private boolean notificationsEnabled;

    /**
     * No args constructor?
     */
    public Profile() {}

    /**
     * Basic constructor method
     * @param deviceId unique id of device user is running the app on
     */
    public Profile(String deviceId) {
        this.deviceId = deviceId;
        this.notificationsEnabled = true;
    }

    /**
     * Detailed constructor method
     * @param deviceId unique id of device user is running the app on
     * @param name name of user affiliated to profile
     * @param email email affiliated with profile
     */
    public Profile(String deviceId, String name, String email) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.notificationsEnabled = true;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    /**
     * Gets unique id for device
     * @return deviceId
     */
    public String getDeviceId() { return deviceId; }

    /**
     * Sets the device id
     * @param deviceId unique id of device user is running the app on
     */
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    /**
     * Get name affiliated with profile
     * @return name
     */
    public String getName() { return name; }

    /**
     * Set the name affiliated with the profile
     * @param name name of user
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get name affiliated with profile
     * @return email
     */
    public String getEmail() { return email; }

    /**
     * Set email affiliated with profile
     * @param email email address of profile
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Get phone number affiliated with profile
     * @return phone
     */
    public String getPhone() { return phone; }

    /**
     * Set the phone number
     * @param phone number affiliated with profile
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Return whether notifications have been turned off or on for profile
     * @return notificationsEnabled
     */
    public boolean isNotificationsEnabled() { return notificationsEnabled; }

    /**
     * Toggle notifications
     * @param notificationsEnabled On ? True : False
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * Wipes all personal data from this profile object.
     * Should be called before deleting the profile from the database.
     */
    public void clearPersonalData() {
        this.name = null;
        this.email = null;
        this.phone = null;
    }
}