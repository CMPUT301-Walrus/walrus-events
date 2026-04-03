package com.example.walrusevents.data;

import com.example.walrusevents.model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class NotificationRepository {
    private final FirebaseFirestore db;
    private final String collectionPath = "notifications";

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
}