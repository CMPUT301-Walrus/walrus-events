package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.controllers.OEventEditController;
import com.example.walrusevents.R;
import com.example.walrusevents.ui.OEventEditView;
import com.example.walrusevents.util.PermissionGatekeeper;

public class OEventEditActivity extends AppCompatActivity {
    private OEventEditView eventEditView;
    private OEventEditController eventEditController;
    private Event eventModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> initializeUi());
    }

    private void initializeUi() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_event);

        // Take event class info and move it here
        try {
            eventModel = getIntent().getSerializableExtra("Event", Event.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        eventEditView = new OEventEditView(this, eventModel);
        eventEditController = new OEventEditController(eventModel, eventEditView);

        // Back to event list for organizer button
        eventEditView.getBackButton().setOnClickListener(v -> {
            // Passes the updated model back to the previous activity (which should be OEventPoolActivity)
            Intent saveIntent = new Intent();
            saveIntent.putExtra("Event", eventModel);
            setResult(OEventEditActivity.RESULT_CANCELED, saveIntent);
            finish();
        });

        // Get event title text view and put actual event title in it
        eventEditView.getTitleView().setText(eventModel.getTitle()); // Or getEventName() depending on your model

        // Event poster
        String eventId = eventModel.getEventId();

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
                        .into(eventEditView.getEditPosterImage());
            }

            @Override
            public void onFailure(String error) {
                Log.e("POSTER_LOAD", "Could not find poster: " + error);
            }
        });

        eventEditView.getEditPosterImage().setOnClickListener(v -> {
            //TODO: Allow for selection of poster
        });

        //Thumbnail
        eventEditView.getEditThumbnail().setOnClickListener(v -> {
            //TODO: Allow for selection of thumbnail
        });

        //Registration and confirmation dates
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

        // Geolocation toggle
        SwitchCompat geoToggle = findViewById(R.id.geolocation_toggle);
        if (geoToggle != null) {
            geoToggle.setChecked(eventModel.getUseGeolocation());
        }

        //Done button
        eventEditView.getDoneButton().setOnClickListener(v -> {
            // Get updated info from app
            String newTitle = eventEditView.getTitleView().getText().toString();
            String newDescription = eventEditView.getEditDescription().getText().toString();

            boolean isPrivate = eventEditView.getPrivateToggle().isChecked();

            // Update local info and Firebase info
            eventEditController.setTitle(newTitle);
            eventEditController.setIsPrivate(isPrivate);
            eventModel.setUseGeolocation(geoToggle.isChecked());
            eventEditController.setDescription(newDescription);
            eventEditController.setEntrantCapacity(eventEditView.getEditEntrantCapacity().getText().toString());
            eventEditController.setApplicantCapacity(eventEditView.getEditApplicantCapacity().getText().toString());
            eventEditController.saveModel();

            // Passes the updated model back to the previous activity (which should be OEventPoolActivity)
            Intent saveIntent = new Intent();
            saveIntent.putExtra("Event", eventModel);
            setResult(OEventEditActivity.RESULT_OK, saveIntent);
            finish();
        });

        //Private event toggle
        updatePublicSettingsVisibility(eventModel.getIsPrivate());
        eventEditView.getPrivateToggle().setOnCheckedChangeListener((buttonView, isChecked) -> {
            updatePublicSettingsVisibility(isChecked);
        });
    }

    private void updatePublicSettingsVisibility(boolean isPrivate) {
        if (isPrivate) {
            eventEditView.getEditRegistrationEnd().setVisibility(TextView.GONE);
            eventEditView.getEditRegistrationStart().setVisibility(TextView.GONE);
        }
        else {
            eventEditView.getEditRegistrationEnd().setVisibility(TextView.VISIBLE);
            eventEditView.getEditRegistrationStart().setVisibility(TextView.VISIBLE);
        }
    }
}
