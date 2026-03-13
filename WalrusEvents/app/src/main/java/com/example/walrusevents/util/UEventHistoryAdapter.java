package com.example.walrusevents.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.model.Event;

import java.util.ArrayList;

/**
 * UEventHistoryAdapter
 * Drives the user's event history ListView.
 * Each row shows the event title, registration deadline, and a colour-coded
 * status badge reflecting the user's WaitlistEntry.Status for that event.
 */
public class UEventHistoryAdapter extends ArrayAdapter<UEventHistoryAdapter.HistoryItem> {

    /**
     * Pairs an Event with the current user's waitlist status for it.
     */
    public static class HistoryItem {
        public final Event event;
        public final WaitlistEntry.Status status;

        public HistoryItem(Event event, WaitlistEntry.Status status) {
            this.event = event;
            this.status = status;
        }
    }

    private final ArrayList<HistoryItem> items;

    public UEventHistoryAdapter(@NonNull Context context, ArrayList<HistoryItem> items) {
        super(context, 0, items);
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.organizer_events_list_item, parent, false);
        }

        HistoryItem item = getItem(position);
        Event event = item.event;

        TextView eventTitle  = convertView.findViewById(R.id.title);
        TextView eventDescription   = convertView.findViewById(R.id.description);
        TextView statusChip = convertView.findViewById(R.id.history_event_status);

        eventTitle.setText(event.getTitle());

        String desc = event.getDescription();

        statusChip.setVisibility(View.VISIBLE);
        statusChip.setText(statusLabel(item.status));
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(statusColor(item.status));
        statusChip.setBackground(bg);

        return convertView;
    }

    private String statusLabel(WaitlistEntry.Status status) {
        switch (status) {
            case INVITED:   return "Selected";
            case ACCEPTED:  return "Registered";
            case DECLINED:  return "Declined";
            case CANCELLED: return "Cancelled";
            default:        return "Pending";
        }
    }

    private int statusColor(WaitlistEntry.Status status) {
        switch (status) {
            case INVITED:   return 0xFF4CAF50; // green
            case ACCEPTED:  return 0xFF00BCD4; // teal
            case DECLINED:  return 0xFFB91C1C; // red
            case CANCELLED: return 0xFF6B7280; // grey
            default:        return 0xFFD24B; // yellow (PENDING)
        }
    }
}