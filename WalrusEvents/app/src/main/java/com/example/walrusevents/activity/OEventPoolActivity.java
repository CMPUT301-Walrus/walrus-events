package com.example.walrusevents.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.model.Lottery;
import com.example.walrusevents.controllers.OEventPoolController;
import com.example.walrusevents.R;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.OEventPoolView;
import com.example.walrusevents.ui.PostLotteryPoolFragment;
import com.example.walrusevents.ui.PreLotteryPoolFragment;
import com.example.walrusevents.util.PermissionGatekeeper;

import java.util.List;

public class OEventPoolActivity extends AppCompatActivity implements WaitlistRepository.EntryListCallback {
    private Event eventModel;
    private OEventPoolView view;
    private OEventPoolController controller;
    private PreLotteryPoolFragment preLotteryFragment;
    private PostLotteryPoolFragment postLotteryFragment;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Event updatedEvent = o.getData().getSerializableExtra("Event", Event.class);

                        if (updatedEvent != null)
                        {
                            eventModel = updatedEvent;
                            refresh();
                        }
                    }
                    else if (o.getResultCode() == Activity.RESULT_CANCELED) {
                        // If back button is pressed, do not update eventModel
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> initializeUi());
    }

    private void initializeUi() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.waiting_list_org);

        eventModel = getIntent().getSerializableExtra("Event", Event.class);

        view = new OEventPoolView(this);

        refresh();

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
                    activityResultLauncher.launch(goEditDetails);
                    //startActivity(goEditDetails);
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

    /**
     * Updates the activity based on the stored event. Call when any event details may have changed.
     */
    public void refresh() {
        if (eventModel.isInRegistration()) {
            preLotteryFragment = new PreLotteryPoolFragment(eventModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_list_fragment, preLotteryFragment)
                    .commit();
            controller = new OEventPoolController(this, eventModel.getEventId(), eventModel.isInConfirmation(), view.getFragmentContainerView(), preLotteryFragment);
        }
        else if (eventModel.isInConfirmation()) {
            postLotteryFragment = new PostLotteryPoolFragment(eventModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_list_fragment, postLotteryFragment)
                    .commit();
            controller = new OEventPoolController(this, eventModel.getEventId(), eventModel.isInConfirmation(), view.getFragmentContainerView(), postLotteryFragment);
        }
        else {

        }
    }

    /**
     * Sends an invite notification to the specified entrant. Can only invite if the event is private
     * @param entrantId ID of the entrant to be invited
     */
    public void sendInvite(String entrantId) {
        //TODO: add functionality
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
