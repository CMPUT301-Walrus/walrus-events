package com.example.walrusevents.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.model.Lottery;
import com.example.walrusevents.controllers.OEventPoolController;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Notification;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.FinalizedPoolFragment;
import com.example.walrusevents.ui.OEventPoolView;
import com.example.walrusevents.ui.PostLotteryPoolFragment;
import com.example.walrusevents.ui.PreLotteryPoolFragment;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.PermissionGatekeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OEventPoolActivity extends AppCompatActivity {
    private Event eventModel;
    private OEventPoolView view;
    private OEventPoolController controller;
    private ArrayList<String> selectedForRemoval;

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
                }
                else if (menuItem.getItemId() == R.id.event_settings_co_owner) {
                    //TODO: search users and select co-owner


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

        view.getRemoveButton().setOnClickListener(v -> {
            if (selectedForRemoval == null) {
                return;
            }

            WaitlistRepository waitlistRepository = new WaitlistRepository();

            for (String entrantId : selectedForRemoval) {
                waitlistRepository.updateStatus(eventModel.getEventId(), entrantId, WaitlistEntry.Status.CANCELED, new WaitlistRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        refresh();
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            }
        });

        view.getBackButton().setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Updates the activity based on the stored event. Call when any event details may have changed.
     */
    public void refresh() {
        if (eventModel == null) {
            return;
        }

        view.getTitleText().setText(eventModel.getTitle());

        if (eventModel.isInRegistration()) {
            PreLotteryPoolFragment preLotteryFragment = new PreLotteryPoolFragment(eventModel, view.getEntrantCountText());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_list_fragment, preLotteryFragment)
                    .commit();
            controller = new OEventPoolController(this, eventModel, view.getFragmentContainerView(), preLotteryFragment);
        }
        else if (eventModel.isInConfirmation()) {
            selectedForRemoval = new ArrayList<>();
            PostLotteryPoolFragment postLotteryFragment = new PostLotteryPoolFragment(eventModel, selectedForRemoval);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_list_fragment, postLotteryFragment)
                    .commit();
            controller = new OEventPoolController(this, eventModel, view.getFragmentContainerView(), postLotteryFragment);
        }
        else {
            FinalizedPoolFragment finalizedPoolFragment = new FinalizedPoolFragment(eventModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_list_fragment, finalizedPoolFragment)
                    .commit();
            controller = new OEventPoolController(this, eventModel, view.getFragmentContainerView(), finalizedPoolFragment);
        }

        //Only show the lottery/invite button if the event hasn't ended yet
        if (eventModel.isInRegistration() || eventModel.isInConfirmation()) {
            view.getLotteryButton().setVisibility(View.VISIBLE);
            //Turn lottery button to an invite button if the event is private
            if (!eventModel.getIsPrivate()) {
                //Currently draws the lottery automatically upon clicking. More deliberate forms of execution can be done later.
                view.getLotteryButton().setText("Lottery");
                view.getLotteryButton().setOnClickListener(v -> {
                    WaitlistRepository collectForLottery = new WaitlistRepository();
                    collectForLottery.getAllEntries(eventModel.getEventId(), new WaitlistRepository.EntryListCallback() {
                        @Override
                        public void onEntriesLoaded(List<WaitlistEntry> entries) {
                            Lottery lottery = new Lottery();
                            //Draw the lottery
                            boolean lotterySuccess = lottery.drawToCapacity(entries, eventModel.getApplicantCapacity());

                            //Guard statement for if the lottery didn't succeed
                            if (!lotterySuccess) {
                                Toast.makeText(OEventPoolActivity.this, "Lottery Failed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //Update the waitlist with the new state of the list
                            for (WaitlistEntry entrant: entries) {
                                lottery.updateWaitlist(eventModel.getEventId(), entrant, OEventPoolActivity.this);
                            }
                            controller.sendNotifications(OEventPoolActivity.this,
                                    String.format(Locale.getDefault(), "%s Lottery Result", eventModel.getTitle()),
                                    "You've been selected!",
                                    Notification.NotificationTarget.SELECTED);
                            controller.sendNotifications(OEventPoolActivity.this,
                                    String.format(Locale.getDefault(), "%s Lottery Result", eventModel.getTitle()),
                                    "You were not selected",
                                    Notification.NotificationTarget.NOT_SELECTED);
                            refresh();
                        }
                    });
                });
            }
            else {
                view.getLotteryButton().setText("Invite");

                String testEntrantId = DeviceIdManager.getOrCreate(this);
                view.getLotteryButton().setOnClickListener(v -> {
                    controller.sendInvite(this, testEntrantId, "Invitation",
                            String.format(Locale.getDefault(),"You were invited to %s!", eventModel.getTitle()));
                });
            }
        }
        else {
            view.getLotteryButton().setVisibility(View.GONE);
        }
    }
}
