package com.example.walrusevents;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventController {
    private ArrayList<Event> eventList;
    private EventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;

    public EventController(Context context, EventRepository eventRepository, ListView eventListView) {
        //Initialize EventController
        eventList = new ArrayList<>();
        eventListAdapter = new EventArrayAdapter(context, eventList);
        eventListView.setAdapter(eventListAdapter);
        this.eventRepository = eventRepository;
    }

    public void setTitle(int index, String title) {
        eventList.get(index).setTitle(title);
    }

    public void setStartRegistrationTime(int index, LocalDateTime startRegistrationTime) {
        eventList.get(index).setStartRegistrationTime(startRegistrationTime);
    }

    public void setEndRegistrationTime(int index, LocalDateTime endRegistrationTime) {
        eventList.get(index).setEndRegistrationTime(endRegistrationTime);
    }

    public void setStartConfirmationTime(int index, LocalDateTime startConfirmationTime) {
        eventList.get(index).setStartConfirmationTime(startConfirmationTime);
    }

    public void setEndConfirmationTIme(int index, LocalDateTime endConfirmationTIme) {
        eventList.get(index).setEndConfirmationTIme(endConfirmationTIme);
    }
    
    public void setEntrantCapacity(int index,int entrantCapacity) {
        eventList.get(index).setEntrantCapacity(entrantCapacity);
    }

    public void setPoster(int index, Image poster) {
        eventList.get(index).setPoster(poster);
    }

    public void setThumbnail(int index, Image thumbnail) {
        eventList.get(index).setThumbnail(thumbnail);
    }

    /**
     * Toggles whether or not to use geolocation
     * @return the value of useGeolocation after toggling
     */
    public boolean toggleGeolocation(int index) {
        eventList.get(index).setUseGeolocation(!eventList.get(index).getUseGeolocation());
        return eventList.get(index).getUseGeolocation();
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean inRegistrationPhase(int index) {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(eventList.get(index).getStartRegistrationTime()) && now.isBefore(eventList.get(index).getEndRegistrationTime());
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean inConfirmationPhase(int index) {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(eventList.get(index).getStartConfirmationTime()) && now.isBefore(eventList.get(index).getEndConfirmationTIme());
    }

    public void addEvent(Event event) {
        eventRepository.addEvent(event);
        eventList.add(event);
        eventListAdapter.notifyDataSetChanged();
    }
}
