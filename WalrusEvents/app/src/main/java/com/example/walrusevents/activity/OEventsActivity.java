package com.example.walrusevents.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.OEventListController;
import com.example.walrusevents.R;
import com.example.walrusevents.ui.OEventListView;

/**
 * Shows the "My Events" view for the organizer. Initializes and connects event repository, the
 * organizer event list view and organizer event list controller.
 */
public class OEventsActivity extends AppCompatActivity {
    private OEventListView eventListView;
    private EventRepository eventRepository;
    private OEventListController eventListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events);

        eventListView = new OEventListView(findViewById(R.id.listView), findViewById(R.id.addButton));
        eventRepository = new EventRepository();
        eventListController = new OEventListController(this, eventRepository, eventListView.getEventList());
        eventListController.loadEvents("ABCDEF");     //**Currently using a placeholder owner id

        eventListView.getAddButton().setOnClickListener(v -> {
            eventListController.startAddEvent(getSupportFragmentManager());
        });

        eventListView.getEventList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventListController.openEvent(OEventsActivity.this, position);
            }
        });
    }
}
