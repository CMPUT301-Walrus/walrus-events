package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.DeviceIdManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.util.UEventHistoryAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UEventsActivity
 * Shows the signed-in user's event history: every event they are (or were)
 * on a waitlist for, along with their current WaitlistEntry status.
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UEventHistoryAdapter.HistoryItem historyItem = (UEventHistoryAdapter.HistoryItem) adapterView.getItemAtPosition(i);
                Event selectedEvent = historyItem.getEvent();
                Intent passToUserEventDetails = new Intent(UEventsActivity.this, UEventDetailsActivity.class);

                // packaging serializable object into Intent referenced from Peter Mortensen in Stack Overflow https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable. March 12, 2026
                Bundle bundle = new Bundle();
                bundle.putSerializable("Event", selectedEvent);
                passToUserEventDetails.putExtras(bundle);
                startActivity(passToUserEventDetails);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(); // refresh when returning to this Activity
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