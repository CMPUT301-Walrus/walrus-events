package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.controllers.NotificationsController;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Notification;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.NotificationInboxFragment;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.MainSEventListController;
import com.example.walrusevents.util.PermissionGatekeeper;
import com.example.walrusevents.util.MainSFilterManager;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
        PermissionGatekeeper.requireNotBanned(this, true, permissions -> initializeUi());
    }

    private void initializeUi() {
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
         */
        SearchView searchBar = findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                //to reset to all public events to filter
                eventListController.setKeyword(newText);
                //eventListController.resetFilters();
                //eventListController.loadEvents();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //eventListController.getSearchFilter().filter(query);
                eventListController.setKeyword(query);
                //eventListController.loadEventsbyKeyword(query);
                return true;
            }
        });
        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                eventListController.setKeyword("");
                eventListController.loadEvents();
                return false;
            }
        });

        /*
        * Capacity and Availability Filters
         */
        CheckBox capacitySortCheckbox=findViewById(R.id.capacity_sort_button);
        CheckBox availabilitySortCheckbox=findViewById(R.id.availability_sort_button);

        capacitySortCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->{
            if(isChecked){
                eventListController.setOpenSeatsFilter(true);

            }
            else{
                eventListController.setOpenSeatsFilter(false);
                //eventListController.loadEvents();

            }
        });

        availabilitySortCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                //GO TO SCHEDULE FRAGMENT
                //TODO: setup fragment and get the ModalDateRangePicker to pick the selected dates
                showDateRangePicker();


            }else{
                //revert?
                eventListController.setDateRange(null,null);
            }
        });

        /*
        * When set to User, OnItemClick an event goes to event_details from eventListView
         */
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event_selected = (Event) adapterView.getItemAtPosition(i);
                Intent passToUserEventDetails = new Intent(MainActivity.this, UEventDetailsActivity.class);
                passToUserEventDetails.putExtra("Event", event_selected);
                startActivity(passToUserEventDetails);
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
        ImageButton inboxButton = findViewById(R.id.inbox_button);

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

        /**
         * Inbox onClick
         */
        inboxButton.setOnClickListener(v -> {
            NotificationInboxFragment inboxFragment = new NotificationInboxFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, inboxFragment)
                    .addToBackStack(null)
                    .commit();
        });

        /**
         * Test notifications (will be removed later
         * Test code was generated by Gemini 3, Google DeepMind
         * Fed MainActivity and linked XML file
         * 01/04/26
         */
        // 1. Initialize the Controller (if you haven't yet)
        NotificationsController testController = new NotificationsController();

        // 2. Find your placeholder button
        Button testSendButton = findViewById(R.id.button2);

        testSendButton.setOnClickListener(v -> {
            // Replace "TEST_EVENT_ID" with an actual event ID from your Firestore
            // if you want to see it appear in a real user's inbox.
            String testEventId = "f6IxP1BkYXMZmyT1r914";
            String title = "Test Broadcast";
            String message = "This is a test notification sent at " + new java.util.Date().toString();

            // We use "all" to ensure it hits everyone on the waitlist regardless of status
            testController.sendNotifications(this, testEventId, title, message, Notification.NotificationTarget.ALL);

            Toast.makeText(this, "Attempting to send test broadcast...", Toast.LENGTH_SHORT).show();
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
        if (eventListController == null || profileRepository == null) {
            return;
        }
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

    private void showDateRangePicker() {

        MaterialDatePicker<Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select date range")
                        .build();

        picker.addOnPositiveButtonClickListener(selection -> {

            if (selection == null) return;

            Long startMillis = selection.first;
            Long endMillis = selection.second;

            if (startMillis != null && endMillis != null) {

                LocalDateTime start = Instant.ofEpochMilli(startMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .atStartOfDay();

                LocalDateTime end = Instant.ofEpochMilli(endMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .atTime(23, 59, 59);

                //apply
                eventListController.setDateRange(start,end);
            }
        });

        picker.addOnNegativeButtonClickListener(dialog -> {
            // Optional: reset checkbox if user cancels
            CheckBox checkbox = findViewById(R.id.availability_sort_button);
            checkbox.setChecked(false);
        });

        picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }

}
