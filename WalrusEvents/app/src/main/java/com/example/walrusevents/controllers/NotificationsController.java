package com.example.walrusevents.controllers;

import android.content.Context;
import android.widget.Toast;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.data.NotificationRepository;
import com.example.walrusevents.data.ProfileRepository;
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
    private final ProfileRepository profileRepo;

    public NotificationsController() {
        this.notifRepo = new NotificationRepository();
        this.waitlistRepo = new WaitlistRepository();
        this.profileRepo = new ProfileRepository();
    }

    /**
     * Organizer can send a message, this function finds all eligible users
     * and puts the message into their inbox
     */
    public void sendNotifications(Context context, String eventId, String title, String message, String targetGroup) {
        Notification notification = new Notification(title, message, eventId, targetGroup);

        // Find all users in this event who match the target group
        waitlistRepo.getAllEntries(eventId, entries -> {
            final int[] processedCount = {0};
            for (WaitlistEntry entry : entries) {
                String userGroup = mapStatusToGroup(entry.getStatus());

                // If the message is for 'all' or matches their specific status
                if (targetGroup.equals("all") || targetGroup.equals(userGroup)) {
                    profileRepo.getProfile(entry.getEntrantId(), profile -> {
                        if (profile != null && profile.hasNotificationsEnabled()) {
                            // Only send if the user has opted-in
                            notifRepo.sendNotificationToUser(entry.getEntrantId(), notification);
                            processedCount[0]++;
                        }
                    });
                }
            }
            Toast.makeText(context, "Sent to " + targetGroup +  " users", Toast.LENGTH_SHORT).show();
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