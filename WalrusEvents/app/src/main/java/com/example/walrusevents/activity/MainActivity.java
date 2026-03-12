package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.util.MainSEventListController;

public class MainActivity extends AppCompatActivity {

    private EventRepository eventRepository;

    private ListView eventListView;

    private MainSEventListController eventListController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
        * Scrolling ListView All the Events
        *
         */

        eventListView = findViewById(R.id.mainScreenEventList);
        eventRepository = new EventRepository();
        eventListController = new MainSEventListController(this, eventRepository, eventListView);
        eventListController.loadEvents();

        //TODO: Button to change between admin / user / organizer(?)
            // Ex: admin - leave blank for now, organizer - OEventActivity, user - UEventActivity
        // labels when
        //TODO: Main Buttons for MainView - Settings, MainScreen, MyEvents
            // MyEvents - UEventActivity, OEventActivity
            //Settings - USettingsActivity
            //MainScreen - go to main screen?? (even though youre on the main screen... - change that
        //TODO: Views for User:  Settings(Profile), MyEvents(Signed in Events)


        Button eventsButton = findViewById(R.id.myEventsButton);
        eventsButton.setOnClickListener(v -> {
            //Button goes to "My Events" activity for organizer
            Intent goOrganizerEventsIntent = new Intent(MainActivity.this, OEventsActivity.class);
            startActivity(goOrganizerEventsIntent);
        });

    }


}