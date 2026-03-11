package com.example.walrusevents;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OrganizerEventsActivity extends AppCompatActivity {
    //TODO: Refactor into MV/MVC architecture
    private ListView eventListView;
    private EventRepository eventRepository;
    private EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events);

        eventListView = findViewById(R.id.listView);
        eventRepository = new EventRepository();
        eventController = new EventController(this, eventRepository, eventListView);

        Button addEventButton = findViewById(R.id.addButton);
        addEventButton.setOnClickListener(v -> {
            //TODO: Popup asking for the new event's name and get the appropriate id from database
            Event event = new Event("New Event", "0");
            eventController.addEvent(event);
        });
    }
}
