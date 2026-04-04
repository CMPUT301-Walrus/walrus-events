package com.example.walrusevents.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.controllers.OEventListController;
import com.example.walrusevents.R;
import com.example.walrusevents.ui.OEventListView;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.PermissionGatekeeper;

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
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> initializeUi());
    }

    private void initializeUi() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events);

        String deviceId = DeviceIdManager.getOrCreate(this);

        eventListView = new OEventListView(this);
        eventRepository = new EventRepository();
        eventListController = new OEventListController(this, deviceId, eventListView.getEventList());
        eventListController.loadEvents(deviceId);

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
    protected void onRestart() {
        super.onRestart();

        if (eventListController != null) {
            eventListController.loadEvents(DeviceIdManager.getOrCreate(this));
        }
    }
}
