package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.controllers.OEventListController;
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

        eventListView = new OEventListView(this);
        eventRepository = new EventRepository();
        eventListController = new OEventListController(this, eventRepository, eventListView.getEventList());
        eventListController.loadEvents("ABCDEF");     //**Currently using a placeholder owner id

        // Back to main button
        ImageView backButton = findViewById(R.id.backButton_organizer_to_main);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Add event button
        eventListView.getAddButton().setOnClickListener(v -> {
            eventListController.startAddEvent(getSupportFragmentManager());
        });

        // Click on a specific event
        eventListView.getEventList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventListController.openEvent(OEventsActivity.this, position);
            }
        });

        eventListView.getBackButton().setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (eventListController != null) {
            eventListController.loadEvents("ABCDEF");
        }
    }
}
