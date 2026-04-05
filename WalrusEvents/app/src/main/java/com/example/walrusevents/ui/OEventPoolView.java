package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentOnAttachListener;

import com.example.walrusevents.R;

public class OEventPoolView {
    private Button settingsButton;
    private Button backButton;
    private Button lotteryButton;
    private ImageView mapButton;
    private Button removeButton;
    private FragmentContainerView fragmentContainer;
    private TextView titleText;
    private TextView entrantCountText;

    public OEventPoolView(Activity context) {
        settingsButton = context.findViewById(R.id.settings_org_button);
        backButton = context.findViewById(R.id.back_waiting_list_org);
        lotteryButton = context.findViewById(R.id.lottery_button);
        mapButton = context.findViewById(R.id.map_button);
        removeButton = context.findViewById(R.id.remove_applicant);
        fragmentContainer = context.findViewById(R.id.waiting_list_fragment);
        titleText = context.findViewById(R.id.org_waitlist_title);
        entrantCountText = context.findViewById(R.id.org_entrant_count);
    }

    public FragmentContainerView getFragmentContainerView() {
        return fragmentContainer;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
    public Button getBackButton() {
        return backButton;
    }

    public Button getLotteryButton() {
        return lotteryButton;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public TextView getEntrantCountText() {
        return entrantCountText;
    }

    public TextView getTitleText() {
        return titleText;
    }

    public ImageView getMapButton() {
        return mapButton;
    }
}
