package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.ListView;

import com.example.walrusevents.Entrant;
import com.example.walrusevents.R;

import java.util.ArrayList;

public class OEventPoolView {
    public ListView waitingList;

    public OEventPoolView(Activity context) {
        waitingList = context.findViewById(R.id.org_entrant_list_view);
    }

    public ListView getWaitingListView() {
        return waitingList;
    }
}
