package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ListView;

import com.example.walrusevents.R;

public class OEventPoolView {
    private ListView waitingList;
    private Button backButton;
    private Button lotteryButton;
    private Button editDetailsButton;
    private Button showQrCodeButton;
    private Button viewEventPageButton;

    public OEventPoolView(Activity context) {
        waitingList = context.findViewById(R.id.org_entrant_list_view);
        backButton = context.findViewById(R.id.back_waiting_list_org);
        lotteryButton = context.findViewById(R.id.lottery_button);
        editDetailsButton = context.findViewById(R.id.edit_details_button);
        showQrCodeButton = context.findViewById(R.id.show_qr_code_button);
        viewEventPageButton = context.findViewById(R.id.view_event_page_button);
    }

    public ListView getWaitingListView() {
        return waitingList;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getLotteryButton() {
        return lotteryButton;
    }

    public Button getEditDetailsButton() {
        return editDetailsButton;
    }

    public Button getShowQrCodeButton() {
        return showQrCodeButton;
    }

    public Button getViewEventPageButton() {
        return viewEventPageButton;
    }
}
