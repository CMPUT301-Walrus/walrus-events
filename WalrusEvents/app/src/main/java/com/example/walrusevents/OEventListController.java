package com.example.walrusevents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import com.example.walrusevents.activity.OEventEditActivity;
import com.example.walrusevents.activity.OEventsActivity;
import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.NameEventFragment;
import com.example.walrusevents.util.PosterGenerator;
import com.example.walrusevents.util.QRGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OEventListController implements NameEventFragment.NameEventListener, EventRepository.EventListCallback {
    private ArrayList<Event> eventList;
    private OEventArrayAdapter eventListAdapter;
    private EventRepository eventRepository;

    /**
     * Constructor for the organizer event list controller
     * @param context
     * @param eventRepository
     * @param eventListView
     */
    public OEventListController(Context context, EventRepository eventRepository, ListView eventListView) {
        //Initialize EventController
        eventList = new ArrayList<>();
        eventListAdapter = new OEventArrayAdapter(context, eventList);
        eventListView.setAdapter(eventListAdapter);
        this.eventRepository = eventRepository;
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    public boolean inRegistrationPhase(int index) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.parse(eventList.get(index).getStartRegistrationTime());
        LocalDateTime end = LocalDateTime.parse(eventList.get(index).getEndRegistrationTime());


        return now.isAfter(start) && now.isBefore(end);
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    public boolean inConfirmationPhase(int index) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.parse(eventList.get(index).getStartConfirmationTime());
        LocalDateTime end = LocalDateTime.parse(eventList.get(index).getEndConfirmationTIme());
        return now.isAfter(start) && now.isBefore(end);
    }

    /**
     * Goes to the details page of the selected event
     * @param context The current Activity context
     * @param position The position of the selected event in the list view
     */
    public void openEvent(Context context, int position) {
        Intent goViewEventIntent = new Intent(context, OEventEditActivity.class);
        goViewEventIntent.putExtra("Event", eventList.get(position));
        context.startActivity(goViewEventIntent);
    }

    /**
     * Opens the fragment dialog to name the new event to be created
     * @param fragmentManager The FragmentManager that handles the current activity's fragments
     */
    public void startAddEvent(FragmentManager fragmentManager) {
        NameEventFragment nameEventFragment = NameEventFragment.newInstance(this);
        nameEventFragment.show(fragmentManager, "Name Event");
    }

    /**
     * Creates an event with the specified title and adds it to eventList and the database
     * @param title The title to be given to the new event
     */
    public void addEvent(String title) {
        // Create new event
        Event event = new Event(title, "Default description");

        // Get new eventId from creating the event in eventRepo
        String eventId = eventRepository.addEvent(event);

        // Generate QR code and poster
        Bitmap qrCode = QRGenerator.generateQRCode(eventId);
        Bitmap poster = PosterGenerator.createEventPoster(title, event.getDescription(), qrCode);

        // Upload poster to Firebase
        // EventPosterFragment will download it via eventId.jpg
        FirebaseAPIManager apiManager = new FirebaseAPIManager();
        apiManager.uploadBitmap(poster, eventId, new FirebaseAPIManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d("QR_LOG", "Poster and QR generated/uploaded for ID: " + eventId);
            }

            @Override
            public void onFailure(String error) {
                Log.e("QR_LOG", "Failed to upload poster: " + error);
            }
        });

        // Update local UI
        eventList.add(event);
        eventListAdapter.notifyDataSetChanged();
    }
     /**
     * Loads the events made by a specific user to be put into eventList
     * @param ownerID The ID of the event organizer
     */
    public void loadEvents(String ownerID) {
        eventRepository.getEventsFromUser(ownerID, this);
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
}
