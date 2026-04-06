package com.example.walrusevents.data;

import com.example.walrusevents.model.AccountRole;
import com.example.walrusevents.model.ProfilePermissions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ProfilePermissionsRepository
 * Handles reads and writes for account permissions state stored separately
 * from personal profile data.
 */
public class ProfilePermissionsRepository {

    private static final String COLLECTION = "profile_permissions";

    private final CollectionReference permissionsCollection;

    public ProfilePermissionsRepository() {
        permissionsCollection = FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public void getPermissions(String deviceId, PermissionCallback callback) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            callback.onPermissionsLoaded(null);
            return;
        }

        permissionsCollection
                .document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onPermissionsLoaded(null);
                        return;
                    }

                    ProfilePermissions permissions = doc.toObject(ProfilePermissions.class);
                    callback.onPermissionsLoaded(normalize(deviceId, permissions));
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onPermissionsLoaded(null);
                });
    }

    public void getOrCreatePermissions(String deviceId, PermissionCallback callback) {
        getPermissions(deviceId, permissions -> {
            if (permissions != null) {
                callback.onPermissionsLoaded(permissions);
                return;
            }

            ProfilePermissions defaultPermissions =
                    new ProfilePermissions(deviceId, AccountRole.ENTRANT, false);

            savePermissions(defaultPermissions, new SaveCallback() {
                @Override
                public void onSuccess() {
                    callback.onPermissionsLoaded(defaultPermissions);
                }

                @Override
                public void onFailure(String error) {
                    callback.onPermissionsLoaded(null);
                }
            });
        });
    }

    public void savePermissions(ProfilePermissions permissions, SaveCallback callback) {
        if (permissions == null) {
            callback.onFailure("Permissions are required.");
            return;
        }
        if (permissions.getDeviceId() == null || permissions.getDeviceId().trim().isEmpty()) {
            callback.onFailure("Device ID is required.");
            return;
        }

        permissionsCollection
                .document(permissions.getDeviceId())
                .set(normalize(permissions.getDeviceId(), permissions))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                });
    }

    private ProfilePermissions normalize(String deviceId, ProfilePermissions permissions) {
        ProfilePermissions normalized = permissions == null ? new ProfilePermissions(deviceId) : permissions;
        normalized.setDeviceId(deviceId);

        if (normalized.getRole() == null || normalized.getRole().trim().isEmpty()) {
            normalized.setRole(AccountRole.ENTRANT);
        }

        return normalized;
    }

    public interface PermissionCallback {
        void onPermissionsLoaded(ProfilePermissions permissions);
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
