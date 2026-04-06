package com.example.walrusevents.util;

import android.content.Context;
import android.widget.ListView;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainSEventListController implements EventRepository.EventListCallback {
    private ArrayList<Event> eventList;
    private MainSEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;

    private MainSFilterManager filterManager;

    /**
     * Constructor for the Main Screen Event List
     * @param context
     * @param eventRepository
     * @param eventListView
     */
    public MainSEventListController(Context context, EventRepository eventRepository, ListView eventListView) {
        //Initialize EventController
        eventList = new ArrayList<>();
        ArrayList<Event> displayedEvents = new ArrayList<>();
        eventListAdapter = new MainSEventArrayAdapter(context, displayedEvents);
        eventListView.setAdapter(eventListAdapter);
        this.eventRepository = eventRepository;
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
        if (events == null) {
            return;
        }
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
        filterManager.updateData(eventList);
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
