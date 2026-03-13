package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.walrusevents.R;

public class OEventListView {
    private ListView eventList;
    private Button addButton;
    private ImageView backButton;

    public OEventListView(Activity context) {
        eventList = context.findViewById(R.id.listView);
        addButton = context.findViewById(R.id.addButton);
        backButton = context.findViewById(R.id.backButton);
    }

    public ListView getEventList() {
        return eventList;
    }
    public void setEventList(ListView eventList) {
        this.eventList = eventList;
    }
    public Button getAddButton() {
        return addButton;
    }
    public ImageView getBackButton() {
        return backButton;
    }
}
