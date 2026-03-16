package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ListView;

import com.example.walrusevents.R;

public class OEventPoolView {
    private ListView waitingList;
    private Button settingsButton;
    private Button backButton;
    private Button lotteryButton;

    public OEventPoolView(Activity context) {
        waitingList = context.findViewById(R.id.org_entrant_list_view);
        settingsButton = context.findViewById(R.id.settings_org_button);
        backButton = context.findViewById(R.id.back_waiting_list_org);
        lotteryButton = context.findViewById(R.id.lottery_button);
    }

    public ListView getWaitingListView() {
        return waitingList;
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
}
