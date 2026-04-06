package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.walrusevents.R;

public class AdminAllProfilesView {
    private ListView profilesListView;
    private ImageView backButton;

    public AdminAllProfilesView(Activity activity) {
        profilesListView = activity.findViewById(R.id.admin_profiles_list);
        backButton = activity.findViewById(R.id.admin_profiles_back_button);
    }

    public ListView getProfilesListView() {
        return profilesListView;
    }

    public ImageView getBackButton() {
        return backButton;
    }
}
