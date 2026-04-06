/**
 * Hub for Admin-related activities
 * Meant as a landing platform for the admin
 * Can then navigate to different admin functionalities
 */

package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.activity.AdminAllCommentsActivity;
import com.example.walrusevents.activity.AdminAllProfilesActivity;
import com.example.walrusevents.ui.AdminHubView;
/*
 Admin View where the admin will redirect to all admin functionalities
 wip: base view w/o admin functionalities yet
 */

public class AdminHubActivity extends AppCompatActivity {
    private AdminHubView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        //Future Buttons: Events, Images, Profiles, Notif Log
        view = new AdminHubView(this);

        view.getEventsButton().setOnClickListener(v -> {
            //TODO: go to admin events view
            Intent goToAdminEvents = new Intent(AdminHubActivity.this,AdminAllEventsActivity.class);
            startActivity(goToAdminEvents);
        });

        view.getImagesButton().setOnClickListener(v -> {
            //TODO: go to admin images view
            Intent goToAdminImages = new Intent(AdminHubActivity.this,AdminAllImagesActivity.class);
            startActivity(goToAdminImages);
        });

        view.getProfilesButton().setOnClickListener(v -> {
            Intent goToAdminProfiles = new Intent(AdminHubActivity.this, AdminAllProfilesActivity.class);
            startActivity(goToAdminProfiles);
        });

        view.getNotificationsButton().setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAllNotificationsActivity.class);
            startActivity(intent);
        });

        view.getCommentsButton().setOnClickListener(v -> {
            Intent goToAdminComments = new Intent(AdminHubActivity.this, AdminAllCommentsActivity.class);
            startActivity(goToAdminComments);
        });

        view.getBackButton().setOnClickListener(v -> {
            finish();
        });
    }
}
