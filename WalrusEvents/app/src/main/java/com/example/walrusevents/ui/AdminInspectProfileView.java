/**
 * View popes up when admin wants to inspect a profile
 * It handles the interface for admins removing or banning entrants and organizers
 */

package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.walrusevents.R;

public class AdminInspectProfileView {
    private ImageView backButton;
    private Button removeOrganizerButton;
    private Button removeEntrantbutton;
    private Button banProfileButton;
    private TextView roleText;

    public AdminInspectProfileView(Activity activity) {
        backButton = activity.findViewById(R.id.admin_profile_inspect_back_button);
        removeOrganizerButton = activity.findViewById(R.id.admin_remove_organizer_button);
        removeEntrantbutton = activity.findViewById(R.id.admin_remove_entrant_button);
        banProfileButton = activity.findViewById(R.id.admin_ban_profile);
        roleText = activity.findViewById(R.id.admin_profile_inspect_role);
    }

    public ImageView getBackButton() {
        return backButton;
    }

    public Button getRemoveOrganizerButton() {
        return removeOrganizerButton;
    }

    public Button getRemoveEntrantbutton() {
        return removeEntrantbutton;
    }

    public Button getBanProfileButton() {
        return banProfileButton;
    }

    public TextView getRoleText() {
        return roleText;
    }
}
