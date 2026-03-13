package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import com.example.walrusevents.util.DeviceIdManager;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.util.UEventHistoryAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UEventsActivity
 * Shows the signed-in user's event history: every event they are (or were)
 * on a waitlist for, along with their current WaitlistEntry status.
 *
 * Data flow:
 *   1. Resolve the device ID — used as the entrant ID throughout the app.
 *   2. Load all events from EventRepository.
 *   3. For each event, call WaitlistRepository.getEntry() to check whether
 *      this user has a waitlist entry.
 *   4. Collect the (Event, Status) pairs and hand them to the adapter.
 *
 * Note: getEventsForEntrant() in EventRepository uses whereArrayContains()
 * on a field that doesn't exist in the Event model, so we fan out per-event
 * instead — which matches the actual Firestore subcollection schema.
 */
public class UEventsActivity extends AppCompatActivity {

    private ListView listView;
    private TextView emptyText;
    private UEventHistoryAdapter adapter;
    private ArrayList<UEventHistoryAdapter.HistoryItem> historyItems;

    private EventRepository eventRepository;
    private WaitlistRepository waitlistRepository;

    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_history_events);

        listView  = findViewById(R.id.user_history_list_view);
        emptyText = findViewById(R.id.empty_history_text);

        ImageView backButton = findViewById(R.id.back_button_user_history);
        backButton.setOnClickListener(v -> {
            finish();
        });

        historyItems = new ArrayList<>();
        adapter = new UEventHistoryAdapter(this, historyItems);
        listView.setAdapter(adapter);

        // Device ID is the entrant ID (passwordless auth and ProfileRepository)
        deviceId = DeviceIdManager.getOrCreate(this);

        eventRepository   = new EventRepository();
        waitlistRepository = new WaitlistRepository();

        loadHistory();
    }

    /**
     * Loads all events, then checks each one for a waitlist entry belonging
     * to this device. Only events where an entry exists are shown.
     */
    private void loadHistory() {
        waitlistRepository.getEntriesByEntrant(deviceId, entries -> {
            if (entries == null || entries.isEmpty()) {
                showEmptyState();
                return;
            }

            int total = entries.size();
            AtomicInteger remaining = new AtomicInteger(entries.size());
            ArrayList<UEventHistoryAdapter.HistoryItem> results = new ArrayList<>();

            for (WaitlistEntry entry : entries) {
                eventRepository.getEvent(entry.getEventId(), event -> {
                    if (event != null) {
                        results.add(new UEventHistoryAdapter.HistoryItem(event, entry.getStatus()));
                    }
                    if (remaining.decrementAndGet() == 0) {
                        publishResults(results);
                    }
                });
            }
        });
    }

    /**
     * Pushes the collected results onto the UI thread and refreshes the adapter.
     * Firestore callbacks arrive on the main thread, but runOnUiThread keeps
     * this safe if the threading model changes.
     */
    private void publishResults(ArrayList<UEventHistoryAdapter.HistoryItem> results) {
        runOnUiThread(() -> {
            historyItems.clear();
            historyItems.addAll(results);
            adapter.notifyDataSetChanged();

            if (historyItems.isEmpty()) {
                showEmptyState();
            } else {
                listView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);
            }
        });
    }

    private void showEmptyState() {
        runOnUiThread(() -> {
            listView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        });
    }
}