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
import com.example.walrusevents.util.PermissionGatekeeper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UEventsActivity
 * Shows the signed-in user's event history: waitlist activity plus any
 * co-organized events, each with a badge describing that relationship.
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
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> initializeUi());
    }

    private void initializeUi() {
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
                openHistoryItem(historyItem);
            }
        });
    }

    private void openHistoryItem(@Nullable UEventHistoryAdapter.HistoryItem historyItem) {
        if (historyItem == null || historyItem.getEvent() == null) {
            return;
        }

        Event selectedEvent = historyItem.getEvent();
        Class<?> destination = historyItem.opensOrganizerView(deviceId)
                ? OEventPoolActivity.class
                : UEventDetailsActivity.class;
        Intent eventIntent = new Intent(UEventsActivity.this, destination);

        // packaging serializable object into Intent referenced from Peter Mortensen in Stack Overflow https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable. March 12, 2026
        Bundle bundle = new Bundle();
        bundle.putSerializable("Event", selectedEvent);
        eventIntent.putExtras(bundle);
        startActivity(eventIntent);
    }

    /**
     * Loads all history sources for this device and publishes a single merged list.
     */
    private void loadHistory() {
        LinkedHashMap<String, UEventHistoryAdapter.HistoryItem> results = new LinkedHashMap<>();
        AtomicInteger pendingSources = new AtomicInteger(2);

        loadWaitlistHistory(results, pendingSources);
        loadCoOrganizerHistory(results, pendingSources);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitlistRepository == null || eventRepository == null || deviceId == null) {
            return;
        }
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

    private void loadWaitlistHistory(LinkedHashMap<String, UEventHistoryAdapter.HistoryItem> results,
                                     AtomicInteger pendingSources) {
        waitlistRepository.getEntriesByEntrant(deviceId, entries -> {
            if (entries == null || entries.isEmpty()) {
                markSourceComplete(results, pendingSources);
                return;
            }

            AtomicInteger remaining = new AtomicInteger(entries.size());
            for (WaitlistEntry entry : entries) {
                eventRepository.getEvent(entry.getEventId(), event -> {
                    if (event != null) {
                        mergeHistoryItem(results, new UEventHistoryAdapter.HistoryItem(event, entry.getStatus()));
                    }

                    if (remaining.decrementAndGet() == 0) {
                        markSourceComplete(results, pendingSources);
                    }
                });
            }
        });
    }

    private void loadCoOrganizerHistory(LinkedHashMap<String, UEventHistoryAdapter.HistoryItem> results,
                                        AtomicInteger pendingSources) {
        eventRepository.getEventsFromUser(deviceId, events -> {
            if (events != null) {
                for (Event event : events) {
                    if (event != null && event.isCoOrganizer(deviceId)) {
                        mergeHistoryItem(results, UEventHistoryAdapter.HistoryItem.coOrganizer(event));
                    }
                }
            }

            markSourceComplete(results, pendingSources);
        });
    }

    private void mergeHistoryItem(LinkedHashMap<String, UEventHistoryAdapter.HistoryItem> results,
                                  UEventHistoryAdapter.HistoryItem candidate) {
        if (candidate == null || candidate.getEvent() == null || candidate.getEvent().getEventId() == null) {
            return;
        }

        synchronized (results) {
            String eventId = candidate.getEvent().getEventId();
            UEventHistoryAdapter.HistoryItem existing = results.get(eventId);
            if (existing == null || candidate.getStatusPriority() > existing.getStatusPriority()) {
                results.put(eventId, candidate);
            }
        }
    }

    private void markSourceComplete(LinkedHashMap<String, UEventHistoryAdapter.HistoryItem> results,
                                    AtomicInteger pendingSources) {
        if (pendingSources.decrementAndGet() != 0) {
            return;
        }

        ArrayList<UEventHistoryAdapter.HistoryItem> snapshot;
        synchronized (results) {
            snapshot = new ArrayList<>(results.values());
        }
        publishResults(snapshot);
    }
}
