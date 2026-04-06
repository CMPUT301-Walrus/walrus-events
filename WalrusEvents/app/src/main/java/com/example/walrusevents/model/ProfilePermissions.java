package com.example.walrusevents.model;

/**
 * ProfilePermissions
 * Stores non-personal account state separately from the profile document.
 */
public class ProfilePermissions {

    private String deviceId;
    private String role = AccountRole.ENTRANT.name();
    private boolean banned;

    public ProfilePermissions() {}

    /**
     * Constructor method for profile permissions with less input
     * @param deviceId unique id of device user is running the app on
     */
    public ProfilePermissions(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Constructor method for profile permissions with more detail
     * @param deviceId unique id of device user is running the app on
     * @param role role of user
     * @param banned is user banned
     */
    public ProfilePermissions(String deviceId, AccountRole role, boolean banned) {
        this.deviceId = deviceId;
        setRole(role);
        this.banned = banned;
    }

    /**
     * Gets unique id for device
     * @return
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device id
     * @param deviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets role of user
     * @return
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of user
     * @param role
     */
    public void setRole(AccountRole role) {
        this.role = role == null ? AccountRole.ENTRANT.name() : role.name();
    }

    /**
     * Gets the role of user
     * @param role role of user
     * @return
     */
    public AccountRole getRoleEnum() {
        try {
            return AccountRole.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return null;
        }
    }

    /**
     * Returns whether user is banned
     * @return
     */
    public boolean isBanned() {
        return banned;
    }

    /**
     * Sets user to to be banned or not
     * @param banned
     */
    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
