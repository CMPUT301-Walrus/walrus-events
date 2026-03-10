package com.example.walrusevents;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class OrganizerEventsActivity extends AppCompatActivity {
    //TODO: Refactor into MV/MVC architecture
    private ArrayList<Event> eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events);

        eventsList = new ArrayList<>();

        Button addEventButton = findViewById(R.id.addButton);
        addEventButton.setOnClickListener(v -> {
            //TODO: Popup asking for the new event's name
            Event event = new Event("New Event");
            eventsList.add(event);
            //TODO: Add event to database
        });
    }
}
