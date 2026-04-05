package com.example.walrusevents.util;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.ui.NameEventFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainSEventListController implements EventRepository.EventListCallback {
    private ArrayList<Event> eventList;
    private MainSEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;

    private MainSFilterManager filterManager;
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
        this.filterManager=new MainSFilterManager(eventList,eventListAdapter);
    }

    /**
     * Load All the Events
     *
     */
    public void loadEvents() {
        eventList.clear();
        eventRepository.resetPagination();
        eventRepository.initiateGetAllEvents(this);
        //filterManager.applyFilters();
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
        //eventList.addAll(events);
        for (Event newEvent : events) {
            boolean exists = false;

            for (Event existing : eventList) {
                if (existing.getEventId().equals(newEvent.getEventId())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                eventList.add(newEvent);
            }
        }
        //filterManager.applyFilters();
        eventListAdapter.notifyDataSetChanged();
        //eventRepository.getNextEventBatch(this);
        if (events.size() == 2) {
            eventRepository.getNextEventBatch(this);
        }
        System.out.printf("%d event(s) loaded", events.size());
    }

    public void setKeyword(String keyword) {
        //eventListAdapter.setKeyword(keyword);
        filterManager.setKeyword(keyword);
    }

    public void setOpenSeatsFilter(boolean onlyOpenSeats) {

        filterManager.setOnlyOpenSeats(onlyOpenSeats);
        //eventListAdapter.setOnlyOpenSeats(onlyOpenSeats);
    }

    public void resetFilters(){
        filterManager.setKeyword("");
        filterManager.setSelectedRange(null,null);
        filterManager.setOnlyOpenSeats(false);
    }

    public void setDateRange(LocalDateTime start, LocalDateTime end){
        filterManager.setSelectedRange(start,end);
    }
}
