/**
 * This view is responsible for showing users the details of an event
 */

package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;

public class UEventDetailsView {
    private TextView eventName;
    private TextView description;
    private ImageView eventPoster;
    private ImageView backButton;
    private Button joinButton;
    private Button seePoolButton;
    private Button viewCommentsButton;

    public UEventDetailsView(Activity activity, Event eventModel) {
        /*
         * Display selected event information
         */
        eventName = activity.findViewById(R.id.event_name);
        description = activity.findViewById(R.id.Event_Details);
        eventName.setText(eventModel.getTitle());
        eventPoster = activity.findViewById(R.id.event_poster);
        backButton = activity.findViewById(R.id.back_button);
        seePoolButton = activity.findViewById(R.id.see_pool_button);
        joinButton = activity.findViewById(R.id.join_event_button);
        viewCommentsButton = activity.findViewById(R.id.view_comments_button);
    }

    public TextView getEventName() {
        return eventName;
    }

    public TextView getDescription() {
        return description;
    }

    public ImageView getEventPoster() {
        return eventPoster;
    }

    public ImageView getBackButton() {
        return backButton;
    }

    public Button getJoinButton() {
        return joinButton;
    }

    public Button getSeePoolButton() {
        return seePoolButton;
    }

    public Button getViewCommentsButton() {
        return viewCommentsButton;
    }
}
