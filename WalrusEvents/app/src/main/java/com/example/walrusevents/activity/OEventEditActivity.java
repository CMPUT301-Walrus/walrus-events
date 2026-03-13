package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.EventRepository;
import com.example.walrusevents.OEventEditController;
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
        model = getIntent().getSerializableExtra("Event", Event.class);

        eventEditView = new OEventEditView(this, model);
        eventEditController = new OEventEditController(model, eventEditView);

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
            eventEditController.setTitle(eventEditView.getTitleView().getText().toString());
            eventEditController.setDescription(eventEditView.getEditDescription().getText().toString());
            eventEditController.setEntrantCapacity(eventEditView.getEditEntrantCapacity().getText().toString());
            eventEditController.setApplicantCapacity(eventEditView.getEditApplicantCapacity().getText().toString());

            eventEditController.saveModel();

            Intent doneIntent = new Intent(this, OEventPoolActivity.class);
            doneIntent.putExtra("Event", model);
            startActivity(doneIntent);
        });

        eventEditView.getBackButton().setOnClickListener(v -> {
            Intent backIntent = new Intent(this, OEventsActivity.class);
            startActivity(backIntent);
        });
    }
}
