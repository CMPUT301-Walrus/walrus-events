package com.example.walrusevents.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.Event;
import com.example.walrusevents.R;

import java.util.ArrayList;

public class MainSEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Context context;
    public MainSEventArrayAdapter(@NonNull Context context, ArrayList<Event> eventList){
        super(context, 0, eventList);
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.mainscreen_events_list_item, parent, false);
        }

        Event event = eventList.get(position);
        TextView eventTitle = view.findViewById(R.id.title);
        TextView eventDescription = view.findViewById(R.id.description);
        TextView eventDeadline = view.findViewById(R.id.registrationDeadline);

        eventTitle.setText(event.getTitle());
        eventDescription.setText(event.getDescription());
        eventDeadline.setText(event.getEndRegistrationTime());

        return view;
    }
}
