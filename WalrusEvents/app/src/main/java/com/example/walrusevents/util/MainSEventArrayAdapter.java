package com.example.walrusevents.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapts an array of events to be properly shown in the main screen event list view
 */

public class MainSEventArrayAdapter extends ArrayAdapter<Event> implements Filterable {
    private ArrayList<Event> eventList;
    private Context context;

    KeywordSearchFilter searchFilter;

    private ArrayList<Event> filteredList;
    public MainSEventArrayAdapter(@NonNull Context context, ArrayList<Event> eventList){
        super(context, 0, eventList);
        this.eventList = eventList;
        this.context = context;
        this.filteredList=eventList;
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

        System.out.println(event.getEndRegistrationTime());

        if (event.isInRegistration()) {
            if (event.getEndRegistrationTime() != null) {
                LocalDateTime endRegistration = LocalDateTime.parse(event.getEndRegistrationTime());
                String formattedDeadline = String.format(Locale.CANADA, "Register until %s %d, %d",
                        endRegistration.getMonth(), endRegistration.getDayOfMonth(), endRegistration.getYear());
                eventDeadline.setText(formattedDeadline);
            }
            else {
                String formattedDeadline = "Registration open";
                eventDeadline.setText(formattedDeadline);
            }
        }
        else if (event.isInConfirmation()) {
            if (event.getEndConfirmationTime() != null) {
                LocalDateTime endConfirmation = LocalDateTime.parse(event.getEndRegistrationTime());
                String formattedDeadline = String.format(Locale.CANADA, "Confirming qpplicants until %s %d, %d",
                        endConfirmation.getMonth(), endConfirmation.getDayOfMonth(), endConfirmation.getYear());
                eventDeadline.setText(formattedDeadline);
            }
            else {
                String formattedDeadline = "Confirming applicants";
                eventDeadline.setText(formattedDeadline);
            }
        }
        else {
            eventDeadline.setText("Application Period Ended");
        }


        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(searchFilter==null) searchFilter = new KeywordSearchFilter(filteredList, this);
        return searchFilter;
    }

    public ArrayList<Event> getFilteredList(){
        return filteredList;
    }

    public void resetFilteredList(){
        filteredList=eventList;
    }
}
