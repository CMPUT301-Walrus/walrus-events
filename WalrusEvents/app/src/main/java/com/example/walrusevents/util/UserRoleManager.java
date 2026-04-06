package com.example.walrusevents.util;

import android.content.Context;

import com.example.walrusevents.data.ProfilePermissionsRepository;
import com.example.walrusevents.model.AccountRole;
import com.example.walrusevents.model.ProfilePermissions;
import com.google.firebase.firestore.auth.User;

/*
* Class handles the role of the user for testing and demonstration
* gets the current role the user is
* changes the current role by cycling through the enum UserRole
 */
public class UserRoleManager {
    private static UserRole currentRole=UserRole.USER;

    public static UserRole getRole() {
        return currentRole;
    }

    public static void setRole(UserRole role) {
        currentRole = role;
    }

    public interface NextRoleCallback {
        void onRoleChanged();
    }

    /*
    * Shifts on to the next Role
     */
    public static void nextRole(Context context, NextRoleCallback callback){
        ProfilePermissionsRepository profilePermissionsRepository = new ProfilePermissionsRepository();
        switch (currentRole){
            case USER:
                profilePermissionsRepository.getOrCreatePermissions(DeviceIdManager.getOrCreate(context), permissions -> {
                    if (permissions.getRoleEnum() == null)
                    {
                        return;
                    }
                    if (permissions.getRoleEnum().ordinal() > AccountRole.ENTRANT.ordinal()) {
                        currentRole=UserRole.ORGANIZER;
                        callback.onRoleChanged();
                    }
                });
                break;
            case ORGANIZER:
                profilePermissionsRepository.getOrCreatePermissions(DeviceIdManager.getOrCreate(context), permissions -> {
                    System.out.println(permissions.getRoleEnum());
                    if (permissions.getRoleEnum() == null)
                    {
                        currentRole=UserRole.USER;
                    }
                    else if (permissions.getRoleEnum().ordinal() > AccountRole.ORGANIZER.ordinal()) {
                        currentRole=UserRole.ADMIN;
                    }
                    else {
                        currentRole=UserRole.USER;
                    }
                    callback.onRoleChanged();
                });

                break;
            case ADMIN:
                currentRole=UserRole.USER;
                callback.onRoleChanged();
                break;

        }
    }
}
