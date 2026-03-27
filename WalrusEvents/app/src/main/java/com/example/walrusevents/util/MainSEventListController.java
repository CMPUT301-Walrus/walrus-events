package com.example.walrusevents.util;

import android.content.Context;
import android.widget.Filter;
import android.widget.ListView;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.EventRepository;
import com.example.walrusevents.OEventArrayAdapter;
import com.example.walrusevents.ui.NameEventFragment;

import java.util.ArrayList;

public class MainSEventListController implements NameEventFragment.NameEventListener, EventRepository.EventListCallback {
    private ArrayList<Event> eventList;
    private MainSEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;

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
    }

    /**
     * Creates an event with the specified title and adds it to eventList and the database
     * @param title The title to be given to the new event
     */
    public void addEvent(String title) {
        Event event = new Event(title, "");
        eventRepository.addEvent(event);

        eventList.add(event);
        eventListAdapter.notifyDataSetChanged();
    }

    /**
     * Load All the Events
     *
     */
    public void loadEvents() {
        eventRepository.getAllEvents(this);
    }

    /**
     * Receives the results of an event query initiated by loadEvents()
     * @param events The list of events received from the database
     */
    @Override
    public void onEventsLoaded(ArrayList<Event> events) {
        eventList.clear();
        eventList.addAll(events);
        System.out.printf("%d event(s) loaded", events.size());
        eventListAdapter.notifyDataSetChanged();
    }

    public Filter getFilter(){
        return eventListAdapter.getFilter();
    }

}
