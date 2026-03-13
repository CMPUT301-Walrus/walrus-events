package com.example.walrusevents.activity;

import android.app.role.RoleManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.Entrant;
import com.example.walrusevents.EventRepository;
import com.example.walrusevents.Profile;
import com.example.walrusevents.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.MainSEventListController;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;

public class MainActivity extends AppCompatActivity implements ProfileRepository.SaveCallback {

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

        /*
        * When set to User, OnItemClick an event goes to event_details from eventListView
         */
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(UserRoleManager.getRole() == UserRole.USER) {
                    Event event_selected = (Event) adapterView.getItemAtPosition(i);
                    Intent passToUserEventDetails = new Intent(MainActivity.this, UEventDetailsActivity.class);
                    // packaging serializable object into Intent referenced from Peter Mortensen in Stack Overflow https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable. March 12, 2026
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event",event_selected);
                    passToUserEventDetails.putExtras(bundle);
                    startActivity(passToUserEventDetails);
                }
            }
        });



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

        ProfileRepository profileRepository = new ProfileRepository();
        Profile placeholderProfile = new Profile("placeholderDevice","placeholderName","placeholderEmail");
        profileRepository.saveProfile(new Entrant("placeholderID", placeholderProfile), this);
    }

    private void updateRoleText(){
        UserRole role = UserRoleManager.getRole();
        changeUserRoleButton.setText("Role:"+role.toString());

    }

//TEMPORARY
    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(String error) {

    }
}