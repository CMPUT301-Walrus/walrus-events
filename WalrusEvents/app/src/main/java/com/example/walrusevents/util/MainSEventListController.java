/**
 * This class manages the collection of all events for the main screen
 * It handles the logic for adjusting the collection of events as an changes occur in the app
 */

package com.example.walrusevents.util;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.walrusevents.activity.MainActivity;
import com.example.walrusevents.activity.UEventDetailsActivity;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.MainSFilterManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class MainSEventListController implements EventRepository.EventListCallback {
    private ArrayList<Event> eventList;
    private MainSEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;
    private MainSFilterManager filterManager;

    private boolean isJustEvents=false;

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
     * Set the featured event
     * @param featuredEventName
     */
    public void setFeatured(MainActivity activity, TextView featuredEventName, ImageView featuredThumbnail) {
        EventRepository featuredEventRepository = new EventRepository();
        featuredEventRepository.initiateGetAllEvents(events -> {
            if (events == null || events.isEmpty()) {
                featuredEventName.setText("");
                return;
            }
            Random random = new Random();

            Event featuredEvent = events.get(random.nextInt(events.size()));
            featuredEventName.setText(featuredEvent.getTitle());

            featuredThumbnail.setOnClickListener(v -> {
                //Swap to role to user if an organizer looks at an event they didn't organize
                UserRole userRole = UserRoleManager.getRole();
                if (userRole == UserRole.ORGANIZER && !featuredEvent.getOwners().contains(DeviceIdManager.getOrCreate(activity))) {
                    UserRoleManager.setRole(UserRole.USER);
                    activity.updateRoleText();
                    Toast.makeText(activity, "Role changed to user", Toast.LENGTH_SHORT).show();
                }
                Intent passToUserEventDetails = new Intent(activity, UEventDetailsActivity.class);
                passToUserEventDetails.putExtra("Event", featuredEvent);
                activity.startActivity(passToUserEventDetails);
            });
        });
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

    public void setIsJustEvent(boolean bool){
        this.isJustEvents=bool;
    }
}
