package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.model.Event;

import java.util.List;

public class UViewWaitlistActivity extends AppCompatActivity implements WaitlistRepository.EntryListCallback {
    private Event event;
    private Button backToEvent;
    private TextView eventTitle;
    private TextView countEntrants;
    private List<WaitlistEntry> waitlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_view_waitlist_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            event = bundle.getSerializable("Event", Event.class);
            if (event == null) { /* If event doesn't load, return to main activity */
                throw new NullPointerException("Selected Event not found");
            }
        } catch (NullPointerException e) {
            Log.e("Walrus Events", "Missing Object", e);
        }

        /*
         * Set 'Back to Event' button to return to UEventDetailsActivity
         */
        backToEvent = findViewById(R.id.back_to_event);
        backToEvent.setOnClickListener(v -> {
            finish();
        });

        // TODO: Display Waitlist entries in ListView
        countEntrants = findViewById(R.id.count_entrants);
        WaitlistRepository getWaitlist = new WaitlistRepository();
        getWaitlist.getAllEntries(event.getEventId(), this);

    }

    public Integer countValidEntrants(List<WaitlistEntry> entries) {
        Integer count = 0;
        for(WaitlistEntry entry: entries) {
            if(entry.getStatus() != WaitlistEntry.Status.DECLINED && entry.getStatus() != WaitlistEntry.Status.CANCELLED) count++;
        }
        return count;
    }

    @Override
    public void onEntriesLoaded(List<WaitlistEntry> entries) {
        Integer eCount = countValidEntrants(entries);
        countEntrants.setText(eCount.toString());
    }
}