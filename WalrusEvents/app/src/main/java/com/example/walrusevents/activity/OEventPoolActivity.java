package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.model.Lottery;
import com.example.walrusevents.controllers.OEventPoolController;
import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.OEventPoolView;

import java.util.List;

public class OEventPoolActivity extends AppCompatActivity implements WaitlistRepository.EntryListCallback {
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

        /*
        * Currently draws the lottery automatically upon clicking. More deliberate forms of execution can be done later.
         */
        view.getLotteryButton().setOnClickListener(v -> {
            //TODO: Do lottery
            WaitlistRepository collectForLottery = new WaitlistRepository();
            collectForLottery.getAllEntries(eventModel.getEventId(), this);

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


        view.getShowQrCodeButton().setOnClickListener(v -> {
            if (eventModel != null) {
                // Create intent to go to QRCode Activity
                Intent intent = new Intent(OEventPoolActivity.this, QRCodeActivity.class);

                // Pass eventId and eventName to new activity
                intent.putExtra("EVENT_ID", eventModel.getEventId());
                intent.putExtra("EVENT_NAME", eventModel.getTitle());

                startActivity(intent);
            }
        });
    }

    @Override
    public void onEntriesLoaded(List<WaitlistEntry> entries) {
        Lottery lottery = new Lottery();
        // Draw the lottery
        lottery.drawToCapacity(entries, eventModel.getApplicantCapacity());
        // Update the waitlist with the new state of the list
        for(WaitlistEntry entrant: entries) {
            lottery.updateWaitlist(eventModel.getEventId(), entrant, this);
        }
    }
}
