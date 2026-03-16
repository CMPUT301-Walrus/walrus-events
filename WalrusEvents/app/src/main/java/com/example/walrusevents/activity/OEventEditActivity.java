package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.controllers.OEventEditController;
import com.example.walrusevents.R;
import com.example.walrusevents.ui.OEventEditView;

public class OEventEditActivity extends AppCompatActivity {
    private OEventEditView eventEditView;
    private OEventEditController eventEditController;
    private Event model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_event);
        // Take event class info and move it here
        model = getIntent().getSerializableExtra("Event", Event.class);

        eventEditView = new OEventEditView(this, model);
        eventEditController = new OEventEditController(model, eventEditView);

        // Back to event list for organizer button
        ImageView backButton = findViewById(R.id.backButton_organizer_to_main);
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        if (model != null) {
            eventEditView = new OEventEditView(this, model); // Initialize View first
            eventEditController = new OEventEditController(model, eventEditView); // Then Controller
        }

        // Get event title text view and put actual event title in it
        TextView eventNameTitle = findViewById(R.id.eventName);
        if (model != null) {
            eventNameTitle.setText(model.getTitle()); // Or getEventName() depending on your model
        }

        // Event poster
        if (model != null) {
            String eventId = model.getEventId();
            ImageView posterImageView = findViewById(R.id.editPoster);

            // Get APIMgr and IMGRepo
            FirebaseAPIManager apiMgr = new FirebaseAPIManager();
            ImageRepository imageRepo = new ImageRepository(apiMgr);

            // Get poster image from Firebase
            imageRepo.retrieveImage(eventId, new FirebaseAPIManager.OnDownloadCompleteListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Glide handles the background download and UI thread update
                    Glide.with(OEventEditActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.rounded_light_blue_square) // Show while loading
                            .error(R.drawable.image_not_found_placeholder)   // Show if missing
                            .into(posterImageView);
                }

                @Override
                public void onFailure(String error) {
                    Log.e("POSTER_LOAD", "Could not find poster: " + error);
                }
            });
        }

        eventEditView.getEditPosterImage().setOnClickListener(v -> {
            //TODO: Allow for selection of poster
        });

        eventEditView.getEditThumbnail().setOnClickListener(v -> {
            //TODO: Allow for selection of thumbnail
        });

        eventEditView.getEditRegistrationStart().setOnClickListener(v -> {
            eventEditController.openStartRegistrationDialog(this);
        });
        eventEditView.getEditRegistrationEnd().setOnClickListener(v -> {
            eventEditController.openEndRegistrationDialog(this);
        });
        eventEditView.getEditConfirmationStart().setOnClickListener(v -> {
            eventEditController.openStartConfirmationDialog(this);
        });
        eventEditView.getEditConfirmationEnd().setOnClickListener(v -> {
            eventEditController.openEndConfirmationDialog(this);
        });

        eventEditView.getDoneButton().setOnClickListener(v -> {
            // Get updated info from app
            String newTitle = eventEditView.getTitleView().getText().toString();
            String newDescription = eventEditView.getEditDescription().getText().toString();

            // Update local info and Firebase info
            eventEditController.setTitle(newTitle);
            eventEditController.setDescription(newDescription);
            eventEditController.setEntrantCapacity(eventEditView.getEditEntrantCapacity().getText().toString());
            eventEditController.setApplicantCapacity(eventEditView.getEditApplicantCapacity().getText().toString());
            eventEditController.saveModel();

            finish();
        });
    }
}
