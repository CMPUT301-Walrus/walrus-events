package com.example.walrusevents.util;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.ui.NameEventFragment;

import java.util.ArrayList;

public class MainSEventListController implements NameEventFragment.NameEventListener, EventRepository.EventListCallback {
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
     * Creates an event with the specified title and adds it to eventList and the database
     * @param title The title to be given to the new event
     */
    public void addEvent(String title) {
        Event event = new Event(title, "");
        eventRepository.addEvent(event);

        eventList.add(event);
        eventListAdapter.applyFilters();
        //eventListAdapter.notifyDataSetChanged();
    }

    /**
     * Load All the Events
     *
     */
    public void loadEvents() {
        eventList.clear();
        eventRepository.initiateGetAllEvents(this);
       // eventRepository.getAllEvents(this);
        eventListAdapter.applyFilters();
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
        System.out.printf("%d event(s) loaded", events.size());
        //eventListAdapter.applyFilters();
        //eventListAdapter.notifyDataSetChanged();
        eventListAdapter.updateData(events);
    }

    public void setKeyword(String keyword) {
        eventListAdapter.setKeyword(keyword);
    }

    public void setOpenSeatsFilter(boolean onlyOpenSeats) {
        eventListAdapter.setOnlyOpenSeats(onlyOpenSeats);
    }


    public Filter getSearchFilter(){

        return eventListAdapter.getFilter();
    }

    public Filter getCapacityFilter(){
        return null;
    }



    /*
    * Easier Query Way to do Filters
     */
    public void loadEventsbyKeyword(String keyword){
        //eventRepository.getEventsByKeyword(this,keyword);
    }

}
