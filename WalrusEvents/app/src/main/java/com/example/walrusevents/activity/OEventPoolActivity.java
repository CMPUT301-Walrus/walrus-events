package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;

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

        view.getSettingsButton().setOnClickListener(v -> {
            //Followed popup menu example from: https://www.geeksforgeeks.org/android/popup-menu-in-android-with-example/
            PopupMenu popupMenu = new PopupMenu(this, view.getSettingsButton());

            popupMenu.getMenuInflater().inflate(R.menu.event_settings_popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.event_settings_view) {
                    Intent goEventPage = new Intent(this, UEventDetailsActivity.class);
                    goEventPage.putExtra("Event", eventModel);
                    startActivity(goEventPage);
                }
                else if (menuItem.getItemId() == R.id.event_settings_edit) {
                    Intent goEditDetails = new Intent(this, OEventEditActivity.class);
                    goEditDetails.putExtra("Event", eventModel);
                    startActivity(goEditDetails);
                }
                else if (menuItem.getItemId() == R.id.event_settings_qr) {
                    Intent goQrCode = new Intent(this, QRCodeActivity.class);
                    goQrCode.putExtra("EVENT_ID", eventModel.getEventId());
                    goQrCode.putExtra("EVENT_NAME", eventModel.getTitle());
                    startActivity(goQrCode);
                }
                return true;
            });

            popupMenu.show();
        });

        view.getBackButton().setOnClickListener(v -> {
            finish();
        });

        /*
        * Currently draws the lottery automatically upon clicking. More deliberate forms of execution can be done later.
         */
        view.getLotteryButton().setOnClickListener(v -> {
            WaitlistRepository collectForLottery = new WaitlistRepository();
            collectForLottery.getAllEntries(eventModel.getEventId(), this);
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
