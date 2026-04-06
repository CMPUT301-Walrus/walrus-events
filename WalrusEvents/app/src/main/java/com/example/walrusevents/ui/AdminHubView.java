package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;

public class AdminHubView extends AppCompatActivity {
    private Button eventsButton;
    private Button imagesButton;
    private Button profilesButton;
    private Button notificationsButton;
    private Button commentsButton;
    private ImageView backButton;

    public AdminHubView(Activity activity) {
        eventsButton = activity.findViewById(R.id.admin_view_events_button);
        imagesButton = activity.findViewById(R.id.admin_view_images_button);
        profilesButton = activity.findViewById(R.id.admin_view_profiles_button);
        notificationsButton = activity.findViewById(R.id.admin_notif_log_button);
        commentsButton = activity.findViewById(R.id.admin_comments_button);
        backButton = activity.findViewById(R.id.admin_hub_back_button);
    }

    public Button getEventsButton() {
        return eventsButton;
    }

    public Button getImagesButton() {
        return imagesButton;
    }

    public Button getProfilesButton() {
        return profilesButton;
    }

    public Button getNotificationsButton() {
        return notificationsButton;
    }

    public Button getCommentsButton() {
        return commentsButton;
    }

    public ImageView getBackButton() {
        return backButton;
    }
}
