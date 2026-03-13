package com.example.walrusevents.activity;

import android.app.role.RoleManager;
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
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;

public class MainActivity extends AppCompatActivity {

    private EventRepository eventRepository;

    private ListView eventListView;

    private MainSEventListController eventListController;

    private Button changeUserRoleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
        * Scrolling ListView All the Events
        *
         */
        eventListView = findViewById(R.id.event_list_view);
        eventRepository = new EventRepository();
        eventListController = new MainSEventListController(this, eventRepository, eventListView);
        eventListController.loadEvents();

        //TODO: Button to change between admin / user / organizer(?)
            // Ex: admin - leave blank for now, organizer - OEventActivity, user - UEventActivity
        changeUserRoleButton = findViewById(R.id.changeRoleButton);
        updateRoleText();
        changeUserRoleButton.setOnClickListener(v -> {
            //Changes role in a loop user-organizer-admin
            UserRoleManager.nextRole();
            updateRoleText();
        });

        //TODO: Main Buttons for MainView - Settings, MainScreen, MyEvents
            // MyEvents - UEventActivity, OEventActivity
            //Settings - USettingsActivity
            //MainScreen - go to main screen?? (even though youre on the main screen... - change that
        //TODO: Views for User:  Settings(Profile), MyEvents(Signed in Events)


        Button eventsButton = findViewById(R.id.my_events_button);
        eventsButton.setOnClickListener(v -> {
            //Button goes to "My Events" activity for organizer
            Intent goOrganizerEventsIntent = new Intent(MainActivity.this, OEventsActivity.class);
            Intent goUserHistoryEventsIntent = new Intent(MainActivity.this,UEventsActivity.class);

            UserRole userRole=UserRoleManager.getRole();
            System.out.println("Current role: " + userRole);
            if(userRole==UserRole.ORGANIZER){
                startActivity(goOrganizerEventsIntent);
            }
            else{ //USER (admin for now ignored)
                startActivity(goUserHistoryEventsIntent);
            }

        });

    }

    private void updateRoleText(){
        UserRole role = UserRoleManager.getRole();
        changeUserRoleButton.setText("Role:"+role.toString());

    }


}