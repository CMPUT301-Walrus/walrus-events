package com.example.walrusevents.util;

import android.app.Activity;
import android.widget.Toast;

import com.example.walrusevents.R;
import com.example.walrusevents.data.ProfilePermissionsRepository;
import com.example.walrusevents.model.ProfilePermissions;

import java.security.Permissions;

/**
 * Shared activity gate that ensures permissions exist and banned accounts stop
 * before normal activity setup continues.
 */
public final class PermissionGatekeeper {
    public interface AllowedCallback {
        void onAllowed(ProfilePermissions permissions);
    }

    private PermissionGatekeeper() {}

    public static void requireNotBanned(Activity activity,
                                        boolean finishTaskOnBlock,
                                        AllowedCallback callback) {
        String deviceId = DeviceIdManager.getOrCreate(activity);
        ProfilePermissionsRepository repository = new ProfilePermissionsRepository();

        repository.getOrCreatePermissions(deviceId, permissions -> activity.runOnUiThread(() -> {
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }

            if (permissions == null) {
                Toast.makeText(activity, R.string.permissions_load_error, Toast.LENGTH_SHORT).show();
                closeBlockedActivity(activity, finishTaskOnBlock);
                return;
            }

            if (permissions.isBanned()) {
                Toast.makeText(activity, R.string.permissions_banned_message, Toast.LENGTH_SHORT).show();
                closeBlockedActivity(activity, finishTaskOnBlock);
                return;
            }

            callback.onAllowed(permissions);
        }));
    }

    private static void closeBlockedActivity(Activity activity, boolean finishTaskOnBlock) {
        if (finishTaskOnBlock) {
            activity.finishAffinity();
            return;
        }
        activity.finish();
    }
}
