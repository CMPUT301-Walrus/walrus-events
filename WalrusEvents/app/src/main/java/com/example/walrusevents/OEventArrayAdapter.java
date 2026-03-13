package com.example.walrusevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.model.Event;

import java.util.ArrayList;

/**
 * Adapts an array of events to be properly shown in the organizer event list view
 */
public class OEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Context context;

    /**
     * Constructor for organizer event array adapter
     * @param context
     * @param eventList
     */
    public OEventArrayAdapter(@NonNull Context context, ArrayList<Event> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.organizer_events_list_item, parent, false);
        }

        Event event = eventList.get(position);
        TextView eventTitle = view.findViewById(R.id.title);
        TextView eventDescription = view.findViewById(R.id.description);

        eventTitle.setText(event.getTitle());
        eventDescription.setText(event.getDescription());

        return view;
    }
}
