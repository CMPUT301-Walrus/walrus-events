package com.example.walrusevents.ui;

import android.widget.Button;
import android.widget.ListView;

public class OEventListView {
    private ListView eventList;
    private Button addButton;

    public OEventListView(ListView eventListView, Button addButton) {
        this.eventList = eventListView;
        this.addButton = addButton;
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

    public void setAddButton(Button addButton) {
        this.addButton = addButton;
    }
}
