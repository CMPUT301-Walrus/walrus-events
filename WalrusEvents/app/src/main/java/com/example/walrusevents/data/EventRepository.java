package com.example.walrusevents.data;

import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Event;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

/**
 * EventRepository
 * Class that manages communication with the events database in Firestore
 * This repo manages getting events and comments from the database
 * It retrieves said data from Firebase in batches to reduce latency
 * It also manages add/delete/removing events and comments from a particular event
 */
public class EventRepository {

    private FirebaseFirestore db;   // Firestore instance

    private CollectionReference eventsCollection;   // Reference to the events collection

    private DocumentSnapshot lastFetchedEvent;
    private DocumentSnapshot lastFetchedComment;

    private int eventBatchSize;
    private int commentBatchSize;

    // Constructor: connects to Firestore
    public EventRepository() {
        db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
        eventBatchSize = 2;
        commentBatchSize = 2;
    }

    /**
     * Set the batch size limiting the number of events that can be retrieved at time from the database
     * @param eventBatchSize Batch size when getting events from the database
     */
    public void setEventBatchSize(int eventBatchSize) {
        this.eventBatchSize = eventBatchSize;
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
     * @param ownerId The ID of the user
     * @param callback Callback to pass the events to (Firestore is asynchronous)
     */
    public void getEventsFromUser(String ownerId, EventListCallback callback) {
        eventsCollection
                .whereArrayContains("owners", ownerId)
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
                    callback.onEventsLoaded(new ArrayList<>());
                });
    }

    /**
     * Initiate retrieval of all public events in the database
     * @param callback Callback to pass the events to (Firestore is asynchronous)
     */
    public void initiateGetAllEvents(EventListCallback callback) {
        eventsCollection
                .whereEqualTo("isPrivate", false)
                .limit(eventBatchSize)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Event> events = new ArrayList<>();

                    if (!querySnapshot.isEmpty()) {
                        lastFetchedEvent = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    }
                    else {
                        callback.onEventsLoaded(null);
                    }

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
     * Get the next batch of public events from the database, starting from the last fetched document
     * @param callback Callback to pass the events to
     */
    public void getNextEventBatch(EventListCallback callback) {
        eventsCollection
                .whereEqualTo("isPrivate", false)
                .limit(eventBatchSize)
                .startAfter(lastFetchedEvent)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot == null || querySnapshot.isEmpty())
                    {
                        callback.onEventsLoaded(null);
                        return;
                    }
                    ArrayList<Event> events = new ArrayList<>();

                    if (!querySnapshot.isEmpty()) {
                        lastFetchedEvent = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    }
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
     * Store a new event in the database
     * @param eventId ID of the event the comment is being added to
     * @param comment The comment being added
     * @return commend ID
     */
    public String addComment(String eventId, Comment comment) {
        DocumentReference docRef = eventsCollection
                .document(eventId)
                .collection("comments")
                .document();

        String newId = docRef.getId();
        comment.setCommentId(newId);
        docRef.set(comment);

        return docRef.getId();
    }
    /**
     * Sets a comment in the database
     * @param comment The comment to be set/overwritten
     */
    public void setComment(String eventId, Comment comment) {
        DocumentReference docRef = eventsCollection
                .document(eventId)
                .collection("comments")
                .document(comment.getCommentId());
        docRef.set(comment, SetOptions.merge());
    }
    /**
     * Delete an comment
     * @param eventId ID of the event
     * @param commentId ID of the comment to be deleted
     */
    public void deleteComment(String eventId, String commentId) {
        eventsCollection
                .document(eventId)
                .collection("comments")
                .whereEqualTo("parentId", commentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null || querySnapshot.isEmpty())
                    {
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot) {
                        Comment comment = doc.toObject(Comment.class);
                        deleteComment(eventId, comment.getCommentId());
                    }
                });

        eventsCollection
                .document(eventId)
                .collection("comments")
                .document(commentId)
                .delete();
    }

    public void initiateGetCommentsFromEvent(String eventId, String parentId, CommentListCallback callback) {
        eventsCollection
                .document(eventId)
                .collection("comments")
                .limit(commentBatchSize)
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null || querySnapshot.isEmpty())
                    {
                        callback.onCommentsLoaded(null);
                        return;
                    }
                    ArrayList<Comment> comments = new ArrayList<>();

                    lastFetchedComment = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Comment comment = doc.toObject(Comment.class);

                        comments.add(comment);
                    }

                    callback.onCommentsLoaded(comments);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    /**
     * Initiate getting all comments from an event, regardless of parent
     * @param eventId
     * @param callback
     */
    public void initiateGetCommentsFromEvent(String eventId, CommentListCallback callback) {
        eventsCollection
                .document(eventId)
                .collection("comments")
                .limit(commentBatchSize)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null || querySnapshot.isEmpty())
                    {
                        callback.onCommentsLoaded(null);
                        return;
                    }
                    ArrayList<Comment> comments = new ArrayList<>();

                    lastFetchedComment = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Comment comment = doc.toObject(Comment.class);

                        comments.add(comment);
                    }

                    callback.onCommentsLoaded(comments);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void getNextCommentBatch(String eventId, String parentId, CommentListCallback callback) {
        eventsCollection
                .document(eventId)
                .collection("comments")
                .limit(commentBatchSize)
                .startAfter(lastFetchedComment)
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot == null || querySnapshot.isEmpty())
                    {
                        callback.onCommentsLoaded(null);
                        return;
                    }
                    ArrayList<Comment> comments = new ArrayList<>();

                    lastFetchedComment = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Comment comment = doc.toObject(Comment.class);

                        comments.add(comment);
                    }

                    callback.onCommentsLoaded(comments);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    /**
     * Get the next batch of comments from an event, regardless of parent
     * @param eventId
     * @param callback
     */
    public void getNextCommentBatch(String eventId, CommentListCallback callback) {
        eventsCollection
                .document(eventId)
                .collection("comments")
                .limit(commentBatchSize)
                .startAfter(lastFetchedComment)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot == null || querySnapshot.isEmpty())
                    {
                        callback.onCommentsLoaded(null);
                        return;
                    }
                    ArrayList<Comment> comments = new ArrayList<>();

                    lastFetchedComment = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Comment comment = doc.toObject(Comment.class);

                        comments.add(comment);
                    }

                    callback.onCommentsLoaded(comments);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void resetPagination(){
        lastFetchedEvent=null;
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

    public interface CommentListCallback {
        void onCommentsLoaded(ArrayList<Comment> comments);
    }
}
