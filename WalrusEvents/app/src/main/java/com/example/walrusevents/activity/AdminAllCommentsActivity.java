/**
 * Load all comment related activities and data
 * This is meant for the Admin so everything must be loaded with transparency
 * Allow options to manage comments as per project description
 */

package com.example.walrusevents.activity;

import android.app.appsearch.EmbeddingVector;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.AdminAllCommentsView;
import com.example.walrusevents.ui.AdminAllProfilesView;
import com.example.walrusevents.util.AdminCommentsAdapter;

import java.util.ArrayList;

public class AdminAllCommentsActivity extends AppCompatActivity {
    private AdminAllCommentsView view;
    private ArrayList<Event> eventList;
    private ArrayList<String> linkedEvents;
    private ArrayList<Comment> commentsList;
    private AdminCommentsAdapter commentsAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_all_comments);
        view = new AdminAllCommentsView(this);
        eventList = new ArrayList<>();
        linkedEvents = new ArrayList<>();
        commentsList = new ArrayList<>();
        commentsAdapter = new AdminCommentsAdapter(this, commentsList, linkedEvents);
        view.getCommentsList().setAdapter(commentsAdapter);

        EventRepository eventRepository = new EventRepository();
        eventRepository.initiateGetAllEvents(events -> {
            onEventsLoaded(events, eventRepository);
        });

        view.getBackButton().setOnClickListener(v -> finish());
    }

    private void onEventsLoaded(ArrayList<Event> events, EventRepository eventRepository) {
        if (events != null && !events.isEmpty())
        {
            eventList.addAll(events);
            eventRepository.getNextEventBatch(ev -> {
                onEventsLoaded(ev, eventRepository);
            });
        }
        else {
            loadComments();
        }
    }

    private void loadComments() {
        for (Event event : eventList) {
            //Need new event repositories every time, so that the batching is correct
            EventRepository eventRepository = new EventRepository();
            eventRepository.initiateGetCommentsFromEvent(event.getEventId(), comments -> {
                onCommentsLoaded(comments, event.getEventId(), eventRepository);
            });
        }
    }

    private void onCommentsLoaded(ArrayList<Comment> comments, String eventId, EventRepository eventRepository) {
        if (comments != null && !comments.isEmpty()) {
            commentsList.addAll(comments);

            for (int i = 0; i < comments.size(); i++) {
                linkedEvents.add(eventId);
            }
            eventRepository.getNextCommentBatch(eventId, comm -> {
                onCommentsLoaded(comm, eventId, eventRepository);
            });

            commentsAdapter.notifyDataSetChanged();
        }
    }
}
