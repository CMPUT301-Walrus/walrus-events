package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.OEventPoolController;
import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.OEventPoolView;

import java.util.ArrayList;
import java.util.List;

public class OEventPoolActivity extends AppCompatActivity {
    private Event eventModel;
    private OEventPoolView view;
    private OEventPoolController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.waiting_list_org);
        eventModel = getIntent().getSerializableExtra("Event", Event.class);

        view = new OEventPoolView(this);
        controller = new OEventPoolController(this, eventModel.getEventId(), view.getWaitingListView());

        view.getBackButton().setOnClickListener(v -> {
            Intent goEvents = new Intent(this, OEventsActivity.class);
            startActivity(goEvents);
        });

        view.getLotteryButton().setOnClickListener(v -> {
            //TODO: Do lottery
        });

        view.getShowQrCodeButton().setOnClickListener(v -> {
            //TODO: Show QR code
        });

        view.getEditDetailsButton().setOnClickListener(v -> {
            Intent goEditDetails = new Intent(this, OEventEditActivity.class);
            goEditDetails.putExtra("Event", eventModel);
            startActivity(goEditDetails);
        });

        view.getViewEventPageButton().setOnClickListener(v -> {
            Intent goEventPage = new Intent(this, UEventDetailsActivity.class);
            goEventPage.putExtra("event", eventModel);
            startActivity(goEventPage);
        });
    }
}
