package com.example.walrusevents.activity;

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

        eventEditView = new OEventEditView(this);
        eventEditController = new OEventEditController(getIntent().getSerializableExtra("Event", Event.class));

        eventEditView.getEditPosterImage().setOnClickListener(v -> {
            //TODO: Allow for selection of poster
        });

        eventEditView.getEditThumbnail().setOnClickListener(v -> {
            //TODO: Allow for selection of thumbnail
        });

        eventEditView.getDoneButton().setOnClickListener(v -> {
            eventEditController.setTitle(eventEditView.getTitleView().getText().toString());
            eventEditController.setDescription(eventEditView.getEditDescription().getText().toString());
            eventEditController.saveModel();
        });
    }
}
