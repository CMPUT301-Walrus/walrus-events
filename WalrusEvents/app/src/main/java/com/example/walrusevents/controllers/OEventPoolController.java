package com.example.walrusevents.controllers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.walrusevents.data.NotificationRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.Notification;
import com.example.walrusevents.model.WaitlistEntry;

import java.util.List;

public class OEventPoolController {
    private WaitlistRepository waitlistRepository;
    private ProfileRepository profileRepository;
    private NotificationRepository notificationRepository;
    private Event eventModel;
    private NotificationsController notificationsController;

    public OEventPoolController(Activity context, Event eventModel, FragmentContainerView fragmentContainerView, @NonNull Fragment fragment) {
        this.eventModel = eventModel;

        waitlistRepository = new WaitlistRepository();
        profileRepository = new ProfileRepository();
        notificationRepository = new NotificationRepository();
    }

    /**
     * Sends an invite notification to the specified entrant. Can only invite if the event is private
     * @param context
     * @param entrantId ID of the entrant to be invited
     * @param notifTitle
     * @param notifMessage
     */
    public void sendInvite(Context context, String entrantId, String notifTitle, String notifMessage) {
        Notification notification = new Notification(notifTitle, notifMessage, eventModel.getEventId(), Notification.NotificationTarget.SELECTED);

        WaitlistEntry waitlistEntry = new WaitlistEntry(entrantId, eventModel.getEventId());
        waitlistEntry.setStatus(WaitlistEntry.Status.INVITED);
        waitlistRepository.addEntry(waitlistEntry, new WaitlistRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                sendNotifToEntrant(entrantId, notification);
                Toast.makeText(context, "Invite sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    public void sendNotifications(Context context, String title, String message, Notification.NotificationTarget targetGroup) {
        String eventId = eventModel.getEventId();
        Notification notification = new Notification(title, message, eventId, targetGroup);

        // Find all users in this event who match the target group
        switch (targetGroup) {
            case ALL:
                waitlistRepository.getAllEntries(eventId, entries -> {
                    sendNotifToEntries(entries, notification);
                });
                Toast.makeText(context, "Sent to all users", Toast.LENGTH_SHORT).show();
                break;
            case SELECTED:
                waitlistRepository.getEntriesByStatus(eventId, WaitlistEntry.Status.INVITED, entries -> {
                    sendNotifToEntries(entries, notification);
                });
                Toast.makeText(context, "Sent to selected users", Toast.LENGTH_SHORT).show();
                break;
            case WAITING_LIST:
                waitlistRepository.getEntriesByStatus(eventId, WaitlistEntry.Status.PENDING, entries -> {
                    sendNotifToEntries(entries, notification);
                });
                Toast.makeText(context, "Sent to pending users", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendNotifToEntries(List<WaitlistEntry> entries, Notification notification) {
        for (WaitlistEntry entry : entries) {
            sendNotifToEntrant(entry.getEntrantId(), notification);
        }
    }

    private void sendNotifToEntrant(String entrantId, Notification notification) {
        profileRepository.getProfile(entrantId, entrant -> {
            if (entrant != null && entrant.hasNotificationsEnabled()) {
                // Only send if the user has opted-in
                notificationRepository.sendNotificationToUser(entrant.getDeviceId(), notification);
            }
        });
    }

    public void addOwner(Context context, String entrantId) {
        if (!eventModel.getOwners().contains(entrantId)) {
            eventModel.addOwner(entrantId);
        }
        else {
            Toast.makeText(context, "User is already an owner", Toast.LENGTH_SHORT).show();
        }
    }
}
