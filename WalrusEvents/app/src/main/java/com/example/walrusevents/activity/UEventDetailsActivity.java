package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.Entrant;
import com.example.walrusevents.EntrantController;
import com.example.walrusevents.Profile;
import com.example.walrusevents.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.DeviceIdManager;


public class UEventDetailsActivity extends AppCompatActivity implements EntrantController.ActionCallback {
    private Event event;
    private TextView event_name;
    private ImageView event_poster;
    private Button backButton;
    private Button joinButton;
    private Button seePoolButton;
    private Button acceptInvite;
    private Button declineInvite;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        /*
        * Retrieve Event object that was clicked in MainActivity
        * Exception handling referenced from https://www.geeksforgeeks.org/android/exceptions-in-android-with-example/. March 12, 2026
         */
        try {
            Intent retrieveEvent = this.getIntent();
            Bundle bundle = retrieveEvent.getExtras();
            if (bundle == null) {
                throw new NullPointerException("Bundle containing selected Event not found");
            }
            event = bundle.getSerializable("event", Event.class);
            if (event == null) { /* If event doesn't load, return to main activity */
                throw new NullPointerException("Selected Event not found");
            }
        } catch (NullPointerException e) {
            Log.e("Event Details", "Missing Object", e);
        }

        /*
        * Display selected event information
         */
        event_name = findViewById(R.id.event_name);
        event_name.setText(event.getTitle());

        // TODO: add images
        event_poster = findViewById(R.id.event_poster);

        TextView event_details = findViewById(R.id.Event_Details);
        String desc = event.getDescription();
        if(!desc.isEmpty()) {
            event_details.setText(desc);
        }

        /*
        * Set 'Back' button to return to MainActivity
         */
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent back = new Intent(UEventDetailsActivity.this, MainActivity.class);
            startActivity(back);
        });

        /*
        * Set seePoolButton to go to OEventPoolActivity
         */
        seePoolButton = findViewById(R.id.see_pool_button);
        seePoolButton.setOnClickListener(v -> {
            Intent seePool = new Intent(UEventDetailsActivity.this, UViewWaitlistActivity.class);
            // packaging serializable object into Intent referenced from Peter Mortensen in Stack Overflow https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable. March 12, 2026
            Bundle bundle = new Bundle();
            bundle.putSerializable("Event", event);
            seePool.putExtras(bundle);
            startActivity(seePool);
        });

        // Test Entrant. TODO: Implement full profile function
        String deviceId = DeviceIdManager.getOrCreate(this);
        Entrant me = new Entrant(new Profile(deviceId,"placeholderName","placeholderEmail"));

        /*
        * Set joinButton to submit an Entrant to the Waitlist
         */
        joinButton = findViewById(R.id.join_event_button);
        joinButton.setOnClickListener(v -> {
            WaitlistRepository waitRep = new WaitlistRepository();
            ProfileRepository pfRep = new ProfileRepository();
            EntrantController entrantController = new EntrantController(me, waitRep, pfRep);
            entrantController.joinWaitlist(event.getEventId(), this);
        });
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}