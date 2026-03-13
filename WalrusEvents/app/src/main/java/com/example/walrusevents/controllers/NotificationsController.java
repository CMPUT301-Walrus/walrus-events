package com.example.walrusevents.controllers;

import android.content.Context;
import android.widget.Toast;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.data.NotificationRepository;
import com.example.walrusevents.model.Notification;

import java.util.List;

/**
 * NotificationController
 * Manages the logic for sending and receiving notifications by bridging
 * EventRepository, WaitlistRepository, and NotificationRepository.
 */
public class NotificationsController {
    private final EventRepository eventRepo;
    private final NotificationRepository notifRepo;
    private final WaitlistRepository waitlistRepo;

    public NotificationsController() {
        this.eventRepo = new EventRepository();
        this.notifRepo = new NotificationRepository();
        this.waitlistRepo = new WaitlistRepository();
    }

    /**
     * Checks if there is a notification for the user for the particular event
     */
    public void checkForEventNotification(String userId, String eventId, NotificationCallback callback) {
        // 1. Get user status for this specific event
        waitlistRepo.getEntry(eventId, userId, entry -> {
            if (entry != null) {
                String targetGroup = mapStatusToGroup(entry.getStatus());

                // 2. Look for messages for this event/status
                notifRepo.getNotificationsForEvent(eventId, targetGroup, result -> {
                    if (result != null && !result.isEmpty()) {
                        // Send back the most recent notification
                        List<Notification> notifs = result.toObjects(Notification.class);
                        callback.onNotificationsLoaded(notifs);
                    }
                });
            }
        });
    }
    private String mapStatusToGroup(WaitlistEntry.Status status) {
        if (status == null) return "all";
        switch (status) {
            case INVITED:
            case ACCEPTED:
                return "selected";
            case PENDING:
                return "waiting_list";
            default:
                return "all";
        }
    }

    /**
     * This method is used when the organizer wants to send notifications to entrants
     * @param eventId
     * @param title
     * @param message
     * @param targetGroup Must be "waiting_list", "selected", or "all"
     */
    public void sendBroadcast(Context context, String eventId, String title, String message, String targetGroup) {
        // Create the notification
        Notification notification = new Notification(title, message, eventId, targetGroup);

        // Save to the repository
        notifRepo.sendNotification(notification, task -> {
            if (task.isSuccessful()) {
                // Optional: You could add a callback here to tell the UI it sent successfully
                Toast.makeText(context, "Notification has been sent to " + targetGroup, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(context, "Notification failed to send :(", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * Interface to communicate results back to the Fragment/UI
     */
    public interface NotificationCallback {
        void onNotificationsLoaded(List<Notification> notifications);
    }
}