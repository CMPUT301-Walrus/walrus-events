package com.example.walrusevents;

import com.example.walrusevents.Event;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 EventRepository
 db class that takes the events from the events firebase db
 Lowkey can't access the firebase for some reason now, BUT the actual funcs SHOULD work
*/
public class EventRepository {

    // Firestore instance
    private FirebaseFirestore db;

    // Reference to the events collection
    private CollectionReference eventsCollection;

    // Constructor: connects to Firestore
    public EventRepository() {
        //db = FirebaseFirestore.getInstance();
        //eventsCollection = db.collection("events");
    }

    /**
     Store a new event in db
     Document ID = event.getId()
     ADMIN
    */
    public void addEvent(Event event) {
        eventsCollection
                .document(event.getId())
                .set(event);
    }

    /**
     Retrieve one event by ID
     Firestore is asynchronous so we use a callback
    */
    public void getEvent(String eventId, EventCallback callback) {

        eventsCollection
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        // converting doc to event object
                        Event event = documentSnapshot.toObject(Event.class);

                        callback.onEventLoaded(event);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    /**
     Retrieve all events
    */

    public void getAllEvents(EventListCallback callback) {

        eventsCollection
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Event> events = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Event event = doc.toObject(Event.class);

                        events.add(event);
                    }

                    callback.onEventsLoaded(events);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    /**
     Delete an event ADMIN
    */

    public void deleteEvent(String eventId) {

        eventsCollection
                .document(eventId)
                .delete();
    }

    /**
     Callback interface for single event
     method made to get the event that we want from the Event class
    */
    public interface EventCallback {
        void onEventLoaded(Event event);
    }

    /**
     Callback interface for event list
    */
    public interface EventListCallback {
        void onEventsLoaded(List<Event> events);
    }
}