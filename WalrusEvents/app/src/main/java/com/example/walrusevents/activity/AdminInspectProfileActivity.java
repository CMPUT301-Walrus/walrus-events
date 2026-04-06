/**
 * Load all profile related activities and data
 * This is meant for the Admin so everything must be loaded with transparency
 * This is where removal and banning of profiles can be done as well as deleting events
 */

package com.example.walrusevents.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.data.ProfilePermissionsRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.AccountRole;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.ProfilePermissions;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.ui.AdminInspectProfileView;

public class AdminInspectProfileActivity extends AppCompatActivity {
    private ProfilePermissionsRepository profilePermissionsRepository;
    private ProfileRepository profileRepository;
    private ProfilePermissions permissions;
    private AdminInspectProfileView adminInspectProvileView;
    private String deviceId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_inspect_profile);

        deviceId = getIntent().getStringExtra("Device ID");
        adminInspectProvileView = new AdminInspectProfileView(this);

        profilePermissionsRepository = new ProfilePermissionsRepository();
        profilePermissionsRepository.getPermissions(deviceId, this::onPermissionsLoaded);

        profileRepository = new ProfileRepository();

        adminInspectProvileView.getBackButton().setOnClickListener(v -> finish());
    }

    public void onPermissionsLoaded(ProfilePermissions permissions) {
        this.permissions = permissions;

        if (permissions.isBanned()) {
            adminInspectProvileView.getRoleText().setText("BANNED");
        }
        else {
            adminInspectProvileView.getRoleText().setText(permissions.getRole());
        }

        if (permissions.getRole() == null) {
            return;
        }

        if (permissions.getRole().equals("ADMIN")) {
            adminInspectProvileView.getRemoveOrganizerButton().setVisibility(View.GONE);
            adminInspectProvileView.getRemoveEntrantbutton().setVisibility(View.GONE);
            adminInspectProvileView.getBanProfileButton().setVisibility(View.GONE);
        }
        else if (permissions.getRole().equals("ORGANIZER")) {
            adminInspectProvileView.getRemoveOrganizerButton().setOnClickListener(this::onRemoveOrganizer);
            adminInspectProvileView.getRemoveEntrantbutton().setOnClickListener(this::onRemoveEntrant);
        }
        else if (permissions.getRole().equals("ENTRANT")) {
            adminInspectProvileView.getRemoveOrganizerButton().setVisibility(View.GONE);
            adminInspectProvileView.getRemoveEntrantbutton().setOnClickListener(this::onRemoveEntrant);
        }

        adminInspectProvileView.getBanProfileButton().setOnClickListener(this::onBanProfile);
    }

    public void onRemoveOrganizer(View view) {
        permissions.setRole(AccountRole.ENTRANT);
        profilePermissionsRepository.savePermissions(permissions, new ProfilePermissionsRepository.SaveCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String error) {

            }
        });
        adminInspectProvileView.getRoleText().setText(permissions.getRole());
        deleteEvents();
    }

    /**
     * Removes the entrant from the profile list
     * @param view
     */
    public void onRemoveEntrant(View view) {
        onBanProfile(view);
        profileRepository.deleteProfile(deviceId, new ProfileRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminInspectProfileActivity.this, "Entrant successfully removed", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {

            }
        });
        deleteWaitlistEntries();
    }

    public void onBanProfile(View view) {
        permissions.setBanned(true);
        profilePermissionsRepository.savePermissions(permissions, new ProfilePermissionsRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminInspectProfileActivity.this, "Profile banned", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * Deletes the events connected to the user
     */
    private void deleteEvents() {
        EventRepository eventRepository = new EventRepository();
        WaitlistRepository waitlistRepository = new WaitlistRepository();
        eventRepository.getEventsFromUser(deviceId, events -> {
            for (Event event : events) {
                eventRepository.deleteEvent(event.getEventId());
                waitlistRepository.getAllEntries(event.getEventId(), entries -> {
                    for (WaitlistEntry entry : entries)
                    {
                        waitlistRepository.removeEntry(entry.getEventId(), entry.getEntrantId(), new WaitlistRepository.SaveCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });
                    }
                });
            }
        });
    }

    private void deleteWaitlistEntries() {
        WaitlistRepository waitlistRepository = new WaitlistRepository();
        waitlistRepository.getEntriesByEntrant(deviceId, entries -> {
            for (WaitlistEntry entry : entries) {
                waitlistRepository.removeEntry(entry.getEventId(), deviceId, new WaitlistRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            }
        });
    }
}
