/**
 * This controller is in charge of the pool of entrants for a particular event
 * It gives organizers the power to send invites and other notifications by communicating with the rest of the app
 * It also gives organizers the power to export the list of entrants to a CSV file
 */

package com.example.walrusevents.controllers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.walrusevents.controllers.NotificationsController;
import com.example.walrusevents.data.NotificationRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.Notification;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OEventPoolController {
    private WaitlistRepository waitlistRepository;
    private ProfileRepository profileRepository;
    private NotificationRepository notificationRepository;
    private Event eventModel;
    private NotificationsController notificationsController;
    private ArrayList<Integer> selectedForRemoval;

    public interface FillListCallback {
        void onListFilled(ArrayList<Entrant> entrantList);
    }

    public OEventPoolController(Event eventModel) {
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

    public void sendCoOwnerInvite(Context context, String entrantId, String notifTitle, String notifMessage) {
        Notification notification = new Notification(notifTitle, notifMessage, eventModel.getEventId(), Notification.NotificationTarget.SELECTED);
        notification.setCoOwnerInvite(true);
        sendNotifToEntrant(entrantId, notification);
    }

    public void sendNotifications(Context context, String title, String message, Notification.NotificationTarget targetGroup) {
        String eventId = eventModel.getEventId();
        Notification notification = new Notification(title, message, eventId, targetGroup);

        // Find all users in this event who match the target group
        switch (targetGroup) {
            case ALL:
                waitlistRepository.getAllEntries(eventId, entries -> {
                    sendNotifToEntries(entries, notification);
                    Toast.makeText(context, "Notification sent to all users", Toast.LENGTH_SHORT).show();
                });
                break;
            case SELECTED:
                waitlistRepository.getEntriesByStatus(eventId, WaitlistEntry.Status.INVITED, entries -> {
                    sendNotifToEntries(entries, notification);
                    Toast.makeText(context, "Notification sent to selected users", Toast.LENGTH_SHORT).show();
                });
                break;
            case NOT_SELECTED:
                waitlistRepository.getEntriesByStatus(eventId, WaitlistEntry.Status.NOT_CHOSEN, entries -> {
                    sendNotifToEntries(entries, notification);
                    Toast.makeText(context, "Notification sent to non-selected users", Toast.LENGTH_SHORT).show();
                });
                break;
            case WAITING_LIST:
                waitlistRepository.getEntriesByStatus(eventId, WaitlistEntry.Status.PENDING, entries -> {
                    sendNotifToEntries(entries, notification);
                    Toast.makeText(context, "Notification sent to pending users", Toast.LENGTH_SHORT).show();
                });
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

    public void fillEntrantListByStatus(ArrayList<Entrant> entrantList, EntrantArrayAdapter adapter, WaitlistEntry.Status status) {
        waitlistRepository.getEntriesByStatus(eventModel.getEventId(), status, entries -> {
            ArrayList<String> deviceIds = new ArrayList<>();
            for (WaitlistEntry entry: entries) {
                deviceIds.add(entry.getEntrantId());
            }
            profileRepository.getProfilesInList(deviceIds, entrant -> {
                if (entrant == null)
                {
                    System.out.println("null entrant");
                }
                else {
                    entrantList.add(entrant);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        });
    }

    public void writeCSV(Context context, Uri uri, ContentResolver contentResolver, ArrayList<Entrant> finalList){
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Entrant list");

        for (Entrant entrant : finalList) {
            csvBuilder.append("\n").append(entrant.getProfile().getName());
        }

        DocumentFile directory = DocumentFile.fromTreeUri(context, uri);

        String fileName = String.format(Locale.getDefault(), "%s Final List.csv", eventModel.getTitle());
        DocumentFile newFile = directory.createFile("text/csv", fileName);
        try {
            OutputStream outputStream = contentResolver.openOutputStream(newFile.getUri());
            outputStream.write(csvBuilder.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
