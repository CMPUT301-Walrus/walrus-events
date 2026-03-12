package com.example.walrusevents;

public class Profile {

    private String deviceId;
    private String name;
    private String email;
    private String phone;
    private boolean notificationsEnabled;

    public Profile() {}

    public Profile(String deviceId, String name, String email) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.notificationsEnabled = true;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
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