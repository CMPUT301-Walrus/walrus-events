package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentOnAttachListener;

import com.example.walrusevents.R;

public class OEventPoolView {
    private Button settingsButton;
    private Button backButton;
    private Button lotteryButton;
    private Button removeButton;
    private FragmentContainerView fragmentContainer;

    public OEventPoolView(Activity context) {
        settingsButton = context.findViewById(R.id.settings_org_button);
        backButton = context.findViewById(R.id.back_waiting_list_org);
        lotteryButton = context.findViewById(R.id.lottery_button);
        removeButton = context.findViewById(R.id.remove_applicant);
        fragmentContainer = context.findViewById(R.id.waiting_list_fragment);
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
}
