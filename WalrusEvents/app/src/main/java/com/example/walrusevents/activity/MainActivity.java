package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.EventRepository;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.DeviceIdManager;
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
         * Search Bar
        SearchView searchBar = findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                eventListController.getFilter().filter(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                eventListController.loadEvents();
                return false;
            }
        });
    */

        /*
        * When set to User, OnItemClick an event goes to event_details from eventListView
         */
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(UserRoleManager.getRole() == UserRole.USER) {
                    Event event_selected = (Event) adapterView.getItemAtPosition(i);
                    Intent passToUserEventDetails = new Intent(MainActivity.this, UEventDetailsActivity.class);

                    passToUserEventDetails.putExtra("Event", event_selected);
                    startActivity(passToUserEventDetails);
                }
            }
        });


        /*
        * Admin View Button
        * CURRENTLY connected to MAin Button (instead of back button in StoryBoards)
         */
        Button adminViewButton = findViewById(R.id.main_button);
        adminViewButton.setOnClickListener(v -> {
            //Go to "Admin View" from this button
            Intent goAdminViewActivityIntent = new Intent(MainActivity.this,AdminViewActivity.class);
            startActivity(goAdminViewActivityIntent);

        });

        //TODO: Main Buttons for MainView - Settings, MainScreen, MyEvents
        Button settingsButton = findViewById(R.id.settings_button);
        Button eventsButton = findViewById(R.id.my_events_button);

        /*
        * Role Change Button
        * Button to change between admin / user / organizer
         */
        changeUserRoleButton = findViewById(R.id.changeRoleButton);
        updateRoleText();
        changeUserRoleButton.setOnClickListener(v -> {
            //Changes role in a loop user-organizer-admin
            UserRoleManager.nextRole();
            updateRoleText();

            //Handling the View for Admin
            if(UserRoleManager.getRole()==UserRole.ADMIN){
                //show button for adminView
                adminViewButton.setVisibility(View.VISIBLE);
                adminViewButton.setText("Admin");
                settingsButton.setVisibility(View.INVISIBLE);
                eventsButton.setVisibility(View.INVISIBLE);

            } else {
                adminViewButton.setVisibility(View.INVISIBLE);
                settingsButton.setVisibility(View.VISIBLE);
                eventsButton.setVisibility(View.VISIBLE);

            }
        });

        //TODO: Views for User:  Settings(Profile), MyEvents(Signed in Events)

        /*
        * My Events onClick
         */
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
        String deviceId = DeviceIdManager.getOrCreate(this);
        Profile placeholderProfile = new Profile(deviceId,"placeholderName","placeholderEmail");
        profileRepository.saveProfile(new Entrant(placeholderProfile), this);
        /*
        * Settings onClick
         */
        settingsButton.setOnClickListener(v -> {
            Intent goUSettingsActivityIntent = new Intent(MainActivity.this, USettingsActivity.class);
            startActivity(goUSettingsActivityIntent);
        });

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