package com.example.walrusevents.controllers;

import android.content.Context;
import android.widget.Toast;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.data.NotificationRepository;
import com.example.walrusevents.model.Notification;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationController
 * Manages the logic for sending and receiving notifications by bridging
 * EventRepository, WaitlistRepository, and NotificationRepository.
 */
public class NotificationsController {
    private final NotificationRepository notifRepo;
    private final WaitlistRepository waitlistRepo;

    public NotificationsController() {
        this.notifRepo = new NotificationRepository();
        this.waitlistRepo = new WaitlistRepository();
    }

    /**
     * Organizer sends a message. The controller finds all eligible users
     * and "dumps" the notification into their individual inboxes.
     */
    public void sendNotifications(Context context, String eventId, String title, String message, String targetGroup) {
        Notification notification = new Notification(title, message, eventId, targetGroup);

        // 1. Find all users in this event who match the target group
        waitlistRepo.getAllEntries(eventId, entries -> {
            int count = 0;
            for (WaitlistEntry entry : entries) {
                String userGroup = mapStatusToGroup(entry.getStatus());

                // If the message is for 'all' or matches their specific status
                if (targetGroup.equals("all") || targetGroup.equals(userGroup)) {
                    notifRepo.sendNotificationToUser(entry.getEntrantId(), notification);
                    count++;
                }
            }
            Toast.makeText(context, "Sent to " + count + " users", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Called by the Fragment to load the Universal Inbox
     */
    public void fetchUniversalInbox(String userId, NotificationCallback callback) {
        notifRepo.getNotificationsForUser(userId, result -> {
            List<Notification> inbox = result.toObjects(Notification.class);
            callback.onNotificationsLoaded(inbox);
        });
    }

    public interface NotificationCallback {
        void onNotificationsLoaded(List<Notification> notifications);
    }

    private String mapStatusToGroup(WaitlistEntry.Status status) {
        if (status == null) return "all";
        switch (status) {
            case INVITED:
            case ACCEPTED: return "selected";
            case PENDING: return "waiting_list";
            default: return "all";
        }
    }
}