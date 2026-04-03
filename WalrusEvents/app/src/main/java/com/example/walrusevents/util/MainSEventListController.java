package com.example.walrusevents.util;

import android.content.Context;
import android.widget.Filter;
import android.widget.ListView;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.ui.NameEventFragment;

import java.util.ArrayList;

public class MainSEventListController implements EventRepository.EventListCallback {
    private ArrayList<Event> eventList;
    private MainSEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;
    private Context context;

    /**
     * Constructor for the Main Screen Event List
     * @param context
     * @param eventRepository
     * @param eventListView
     */
    public MainSEventListController(Context context, EventRepository eventRepository, ListView eventListView) {
        //Initialize EventController
        eventList = new ArrayList<>();
        eventListAdapter = new MainSEventArrayAdapter(context, eventList);
        eventListView.setAdapter(eventListAdapter);
        this.eventRepository = eventRepository;
        this.context = context;
    }

    /**
     * Load All the Events
     *
     */
    public void loadEvents() {
        eventList.clear();
        eventRepository.initiateGetAllEvents(this);
    }

    /**
     * Receives the results of an event query initiated by loadEvents()
     * @param events The list of events received from the database
     */
    @Override
    public void onEventsLoaded(ArrayList<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        eventList.addAll(events);
        eventListAdapter.notifyDataSetChanged();
        eventRepository.getNextEventBatch(this);
    }

    public Filter getFilter(){
        return eventListAdapter.getFilter();
    }

}
