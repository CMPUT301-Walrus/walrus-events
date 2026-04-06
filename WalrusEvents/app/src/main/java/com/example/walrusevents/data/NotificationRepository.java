/**
 * This repo is in charge of storing and retrieving notifications from Firebase
 * It manages converting notifications from the app and uploading them to Firebase
 */

package com.example.walrusevents.data;

import com.example.walrusevents.model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationRepository {
    private final FirebaseFirestore db;
    private final String collectionPath = "notifications";
    private final String LOG_PATH = "notification_logs";
    public NotificationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Sends a notification to specified user
     * @param userId
     * @param notification
     */
    public void sendNotificationToUser(String userId, Notification notification) {
        db.collection("profiles")
                .document(userId)
                .collection(collectionPath)
                .add(notification);
    }

    /**
     * Sends a notification to specified users
     * @param userIds
     * @param notification
     */
    public void sendNotificationToUsers(ArrayList<String> userIds, Notification notification) {
        for (String userId : userIds) {
            sendNotificationToUser(userId, notification);
        }
    }

    /**
     * Gets all notifications for specified user
     * @param userId
     * @param listener
     */
    public void getNotificationsForUser(String userId, OnSuccessListener<QuerySnapshot> listener) {
        db.collection("profiles")
                .document(userId)
                .collection(collectionPath)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(listener);
    }

    /**
     * The following two functions were written by Gemini 3, Google DeepMind
     * Fed notification related files and asked for querying for admin
     * 06/04/26
     * @param notification
     */
    public void logGlobalNotification(Notification notification) {
        // This stores a single copy in a top-level collection for the Admin
        db.collection(LOG_PATH).add(notification);
    }

    public void getAllNotificationLogs(OnSuccessListener<QuerySnapshot> listener) {
        // This ignores the 'profile' parent and finds ALL 'notifications' sub-collections
        db.collectionGroup("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(listener)
                .addOnFailureListener(e -> {
                    // This will likely print the index creation link in your Logcat
                    android.util.Log.e("Firestore", "Error fetching group: ", e);
                });
    }}