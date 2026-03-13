package com.example.walrusevents.util;

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
