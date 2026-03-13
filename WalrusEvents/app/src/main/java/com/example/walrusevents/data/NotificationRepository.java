package com.example.walrusevents.data;

import com.example.walrusevents.model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class NotificationRepository {
    private final FirebaseFirestore db;
    private final String collectionPath = "notifications";

    public NotificationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void sendNotification(Notification notification, OnCompleteListener<DocumentReference> listener) {
        db.collection(collectionPath)
                .add(notification)
                .addOnCompleteListener(listener);
    }

    // This is what the "Inbox" will call while syncing after refresh
    public void getNotificationsForEvent(String eventId, String userStatus, OnSuccessListener<QuerySnapshot> listener) {
        db.collection(collectionPath)
                .whereEqualTo("eventId", eventId)
                .whereIn("targetGroup", Arrays.asList("all", userStatus))
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(listener);
    }
}