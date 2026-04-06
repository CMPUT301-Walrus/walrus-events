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
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.model.Event;

import java.util.ArrayList;

/**
 * UEventHistoryAdapter
 * Drives the user's event history ListView.
 * Each row shows the event title, description, and a colour-coded
 * status badge reflecting the user's relationship to that event.
 */
public class UEventHistoryAdapter extends ArrayAdapter<UEventHistoryAdapter.HistoryItem> {

    /**
     * Encapsulates the chip content so the history list can show waitlist
     * statuses and role-based statuses without changing the row layout.
     */
    public static final class HistoryStatus {
        private final String label;
        private final int color;
        private final int priority;
        private final boolean organizerView;

        private HistoryStatus(String label, int color, int priority) {
            this(label, color, priority, false);
        }

        private HistoryStatus(String label, int color, int priority, boolean organizerView) {
            this.label = label;
            this.color = color;
            this.priority = priority;
            this.organizerView = organizerView;
        }

        public String getLabel() {
            return label;
        }

        public int getColor() {
            return color;
        }

        public int getPriority() {
            return priority;
        }

        public boolean opensOrganizerView() {
            return organizerView;
        }

        public static HistoryStatus fromWaitlistStatus(@Nullable WaitlistEntry.Status status) {
            if (status == null) {
                return new HistoryStatus("Pending", 0xFFFFD24B, 1);
            }

            switch (status) {
                case INVITED:
                    return new HistoryStatus("Invited", 0xFF4CAF50, 1);
                case NOT_CHOSEN:
                    return new HistoryStatus("Not Selected", 0xFFEB78FF, 1);
                case ACCEPTED:
                    return new HistoryStatus("Confirmed", 0xFF00BCD4, 1);
                case DECLINED:
                    return new HistoryStatus("Declined", 0xFFB91C1C, 1);
                case CANCELED:
                    return new HistoryStatus("Cancelled", 0xFF6B7280, 1);
                default:
                    return new HistoryStatus("Pending", 0xFFFFD24B, 1);
            }
        }

        public static HistoryStatus coOrganizer() {
            return new HistoryStatus("Co-Organizer", 0xFF2563EB, 2, true);
        }
    }

    /**
     * Pairs an Event with the current user's history status for it.
     */
    public static class HistoryItem {
        public final Event event;
        public final HistoryStatus status;

        public HistoryItem(Event event, WaitlistEntry.Status status) {
            this(event, HistoryStatus.fromWaitlistStatus(status));
        }

        public HistoryItem(Event event, HistoryStatus status) {
            this.event = event;
            this.status = status;
        }

        public static HistoryItem coOrganizer(Event event) {
            return new HistoryItem(event, HistoryStatus.coOrganizer());
        }

        public Event getEvent() {
            return event;
        }

        public HistoryStatus getStatus() {
            return status;
        }

        public int getStatusPriority() {
            return status.getPriority();
        }

        public boolean opensOrganizerView(@Nullable String deviceId) {
            return status != null
                    && status.opensOrganizerView()
                    && event != null
                    && event.isCoOrganizer(deviceId);
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

        eventDescription.setText(event.getDescription());

        statusChip.setVisibility(View.VISIBLE);
        HistoryStatus status = item.getStatus();
        statusChip.setText(status.getLabel());
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(status.getColor());
        statusChip.setBackground(bg);

        return convertView;
    }
}
