package com.example.walrusevents.util;

import com.google.firebase.firestore.auth.User;

/*
* Handles the role of the user for testing and demonstration
 */
public class UserRoleManager {
    private static UserRole currentRole=UserRole.USER;

    public static UserRole getRole() {
        return currentRole;
    }

    public static void setRole(UserRole role) {
        currentRole = role;
    }

    /*
    * Shifts on to the next Role
     */
    public static void nextRole(){
        switch (currentRole){
            case USER:
                currentRole=UserRole.ORGANIZER;
                break;
            case ORGANIZER:
                currentRole=UserRole.ADMIN;
                break;
            case ADMIN:
                currentRole=UserRole.USER;
                break;

        }
    }
}
