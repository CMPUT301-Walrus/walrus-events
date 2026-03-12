package com.example.walrusevents;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import com.example.walrusevents.activity.OEventsActivity;
import com.example.walrusevents.ui.NameEventFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OEventListController  implements NameEventFragment.NameEventListener{
    private ArrayList<Event> eventList;
    private OEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;

    public OEventListController(Context context, EventRepository eventRepository, ListView eventListView) {
        //Initialize EventController
        eventList = new ArrayList<>();
        eventListAdapter = new OEventArrayAdapter(context, eventList);
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
    public boolean inRegistrationPhase(int index) {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(eventList.get(index).getStartRegistrationTime()) && now.isBefore(eventList.get(index).getEndRegistrationTime());
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    public boolean inConfirmationPhase(int index) {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(eventList.get(index).getStartConfirmationTime()) && now.isBefore(eventList.get(index).getEndConfirmationTIme());
    }

    public void addEvent(FragmentManager fragmentManager) {
        NameEventFragment nameEventFragment = NameEventFragment.newInstance(this);
        nameEventFragment.show(fragmentManager, "Name Event");
    }

    public void openEvent(Context context, int position) {
        Intent goViewEventIntent = new Intent(context, OEventsActivity.class);
        goViewEventIntent.putExtra("eventId", eventList.get(position).getId());
        context.startActivity(goViewEventIntent);
    }

    public void updateEventTitle(String title) {
        Event event = new Event(title, "");
        event = eventRepository.addEvent(event);

        eventList.add(event);
        eventListAdapter.notifyDataSetChanged();
    }
}
