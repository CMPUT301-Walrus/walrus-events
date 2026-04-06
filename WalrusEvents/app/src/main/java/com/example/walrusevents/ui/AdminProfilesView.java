package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.ListView;

import com.example.walrusevents.R;

public class AdminProfilesView {
    private ListView profilesListView;

    public AdminProfilesView(Activity activity) {
        profilesListView = activity.findViewById(R.id.admin_profiles_list);
    }

    public ListView getProfilesListView() {
        return profilesListView;
    }
}
