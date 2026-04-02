package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.MainSEventListController;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;

public class MainActivity extends AppCompatActivity {

    private EventRepository eventRepository;
    private ProfileRepository profileRepository;

    private ListView eventListView;

    private MainSEventListController eventListController;

    private Button changeUserRoleButton;

    private Button scanQRCodeButton;
    private boolean initialProfileSetupLaunched;


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
        profileRepository = new ProfileRepository();
        eventListController = new MainSEventListController(this, eventRepository, eventListView);
        eventListController.loadEvents();

        /*
         * Search Bar
         * TODO: fix refresh issue with filter
         *  (once you submit keyword, it will only refresh to all events when you close)
         *
         */
        SearchView searchBar = findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                eventListController.loadEvents();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                eventListController.getFilter().filter(query);
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

        // Click listener for qr code scanner button
        scanQRCodeButton = findViewById(R.id.scan_qr_code_button);
        scanQRCodeButton.setOnClickListener(v -> {
            Intent goScannerIntent = new Intent(MainActivity.this, UQRCodeScannerActivity.class);
            startActivity(goScannerIntent);
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
        updateVisibility(adminViewButton, settingsButton, eventsButton);
        changeUserRoleButton.setOnClickListener(v -> {
            // Changes role in a loop user-organizer-admin
            UserRoleManager.nextRole();
            updateRoleText();

            //Handling the View for Admin
            updateVisibility(adminViewButton, settingsButton, eventsButton);
        });


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

        ensureProfileSetupState();
        /*
        * Settings onClick
         */
        settingsButton.setOnClickListener(v -> {
            Intent goUSettingsActivityIntent = new Intent(MainActivity.this, USettingsActivity.class);
            startActivity(goUSettingsActivityIntent);
        });

    }

    /**
     * This method handles displaying the correct UI depending on which role is active
     * @param adminBtn Button should be dispalyed admin role only
     * @param settingsBtn button to access settings
     * @param eventsBtn button to access my events
     */
    private void updateVisibility(Button adminBtn, Button settingsBtn, Button eventsBtn) {
        UserRole currentRole = UserRoleManager.getRole();

        if(currentRole == UserRole.ADMIN) {
            adminBtn.setVisibility(View.VISIBLE);
            adminBtn.setText("Admin");
            scanQRCodeButton.setVisibility(View.VISIBLE);
            settingsBtn.setVisibility(View.INVISIBLE);
            eventsBtn.setVisibility(View.INVISIBLE);
        } else if (currentRole == UserRole.USER) {
            adminBtn.setVisibility(View.GONE);
            scanQRCodeButton.setVisibility(View.VISIBLE);
            settingsBtn.setVisibility(View.VISIBLE);
            eventsBtn.setVisibility(View.VISIBLE);
        } else {
            adminBtn.setVisibility(View.GONE);
            scanQRCodeButton.setVisibility(View.VISIBLE);
            settingsBtn.setVisibility(View.VISIBLE);
            eventsBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        initialProfileSetupLaunched = false;
        eventListController.loadEvents();
        ensureProfileSetupState();
    }

    private void updateRoleText(){
        UserRole role = UserRoleManager.getRole();
        changeUserRoleButton.setText("Role:"+role.toString());
    }

    private void ensureProfileSetupState() {
        String deviceId = DeviceIdManager.getOrCreate(this);
        profileRepository.getProfile(deviceId, entrant -> runOnUiThread(() -> {
            if (entrant == null) {
                createProfileAndLaunchSettings(deviceId);
                return;
            }

            Profile profile = entrant.getProfile();
            if (profile == null || !profile.hasRequiredContactInfo()) {
                launchSettings();
            }
        }));
    }

    private void createProfileAndLaunchSettings(String deviceId) {
        Profile profile = new Profile(deviceId);
        profileRepository.saveProfile(new Entrant(profile), new ProfileRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> launchSettings());
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            MainActivity.this,
                            error != null ? error : "Unable to create profile.",
                            Toast.LENGTH_SHORT
                    ).show();
                    launchSettings();
                });
            }
        });
    }

    private void launchSettings() {
        if (initialProfileSetupLaunched || isFinishing() || isDestroyed()) {
            return;
        }

        initialProfileSetupLaunched = true;
        Intent intent = new Intent(MainActivity.this, USettingsActivity.class);
        intent.putExtra(USettingsActivity.INITIAL_PROFILE_SETUP, initialProfileSetupLaunched);
        startActivity(intent);
    }
}
