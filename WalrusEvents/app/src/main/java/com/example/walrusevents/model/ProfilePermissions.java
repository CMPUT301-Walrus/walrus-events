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

    public ProfilePermissions(String deviceId) {
        this.deviceId = deviceId;
    }

    public ProfilePermissions(String deviceId, AccountRole role, boolean banned) {
        this.deviceId = deviceId;
        setRole(role);
        this.banned = banned;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role == null ? AccountRole.ENTRANT.name() : role.name();
    }

    public AccountRole getRoleEnum() {
        try {
            return AccountRole.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return null;
        }
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
