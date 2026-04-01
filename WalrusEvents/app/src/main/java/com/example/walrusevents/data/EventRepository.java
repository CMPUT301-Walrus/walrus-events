package com.example.walrusevents.data;

import com.example.walrusevents.model.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

/**
 * EventRepository
 * Class that manages communication with the events database in Firestore
 */
public class EventRepository {

    private FirebaseFirestore db;   // Firestore instance

    private CollectionReference eventsCollection;   // Reference to the events collection

    // Constructor: connects to Firestore
    public EventRepository() {
        db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
    }

    /**
     * Store a new event in the database
     * @param event The event being added
     */
    public String addEvent(Event event) {
        DocumentReference docRef = eventsCollection.document();
        String newId = docRef.getId(); // Grab the ID
        event.setEventId(newId);
        docRef.set(event);
        return newId; // Return it to the caller
    }
    /**
     * Sets an event in the database
     * @param event The event to be set/overwritten
     */
    public void setEvent(Event event) {
        DocumentReference docRef = eventsCollection.document(event.getEventId());
        docRef.set(event, SetOptions.merge());
    }

    /**
     * Retrieve one event by its ID
     * @param eventId The ID of the event being retrieved
     * @param callback Callback to pass the event to (Firestore is asynchronous)
     * Returns event information for given id, or null if event does not exist or retrieval fails
     */
    public void getEvent(String eventId, EventCallback callback) {
        // Make sure event with given id exists
        if (eventId == null || eventId.isEmpty()) {
            callback.onEventLoaded(null);
            return;
        }

        eventsCollection
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // converting doc to event object
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.onEventLoaded(event);
                    } else {
                        callback.onEventLoaded(null);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onEventLoaded(null);
                });
    }

    /**
     * Gets the all the events made by the specified user
     * @param id The ID of the user
     * @param callback Callback to pass the events to (Firestore is asynchronous)
     */
    public void getEventsFromUser(String id, EventListCallback callback) {
        eventsCollection
                .whereEqualTo("ownerId", id)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Event event = doc.toObject(Event.class);
                        events.add(event);
                        event.setOwnerId(doc.get("ownerId").toString());
                        System.out.println(event.getOwnerId());
                    }
                    callback.onEventsLoaded(events);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    /**
     * Retrieve all events in the database
     * @param callback Callback to pass the events to (Firestore is asynchronous)
     */
    public void getAllEvents(EventListCallback callback) {

        eventsCollection
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    ArrayList<Event> events = new ArrayList<>();

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
     * Delete an event (Admin)
     * @param eventId ID of the event to be deleted
     */
    public void deleteEvent(String eventId) {

        eventsCollection
                .document(eventId)
                .delete();
    }

    /**
     * Callback interface for single event
     * method made to get the event that we want from the Event class
     */
    public interface EventCallback {
        void onEventLoaded(Event event);
    }

    /**
     * Callback interface for event list
     */
    public interface EventListCallback {
        void onEventsLoaded(ArrayList<Event> events);
    }

    /**
     * This method was generated by Gemini 3, Google DeepMind
     * Fed EventRepo and Notification classes with US descriptions
     * 12/03/26
     * Retrieves all events where the user is an entrant
     * @param userId The ID of the current user
     * @param callback Callback to return the list of events
     */
    public void getEventsForEntrant(String userId, EventListCallback callback) {
        eventsCollection
                .whereArrayContains("entrantIds", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        events.add(doc.toObject(Event.class));
                    }
                    callback.onEventsLoaded(events);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    /*
    * Another way to get keyword search
     */
    public void getEventsByCapacity(EventListCallback callback,String keyword){
        eventsCollection
                .where(Filter
                        .or(
                                Filter.arrayContains("title",keyword),
                                Filter.arrayContains("description",keyword))
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    ArrayList<Event> events = new ArrayList<>();

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

    //For filtering - get a copy of the collections
}