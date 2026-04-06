/**
 * This is where organizers can view the pool of entrants for their specified event
 * It is also where can choose to invite winner from the lottery
 * This activity is responsible for managing the entrants that join the lottery
 * It is also responsible to initaiting the lottery and sending notifications
 */

package com.example.walrusevents.activity;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import com.example.walrusevents.model.Entrant;
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
import com.example.walrusevents.ui.SearchEntrantsPrivateEventFragment;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.PermissionGatekeeper;
import com.example.walrusevents.util.SearchPrivateEntrantsController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OEventPoolActivity extends AppCompatActivity {
    public static final String CONFIG_LOTTERY_TAG = "configLottery";
    public static final String CONFIG_NOTIFICATIONS_TAG = "configNotifications";

    private enum PoolPhase {
        PRE_LOTTERY,
        POST_LOTTERY,
        FINALIZED
    }

    private Event eventModel;
    private OEventPoolView view;
    private OEventPoolController controller;
    private final WaitlistRepository waitlistRepository = new WaitlistRepository();
    private ArrayList<String> selectedForRemoval;
    private ArrayList<Entrant> finalList;
    private String selectedMessage;
    private String nonSelectedMessage;

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

    ActivityResultLauncher<Uri> writeCSVLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocumentTree(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        getContentResolver().takePersistableUriPermission(uri, FLAG_GRANT_WRITE_URI_PERMISSION);
                        controller.writeCSV(OEventPoolActivity.this, uri, getContentResolver(), finalList);
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

        view.getBackButton().setOnClickListener(v -> {
            finish();
        });

        view.getMapButton().setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("eventId", eventModel.getEventId());
            startActivity(intent);
        });

        updateMapButtonVisibility();
    }

    private void updateMapButtonVisibility() {
        if (eventModel.getUseGeolocation()) {
            view.getMapButton().setVisibility(View.VISIBLE);
        }
        else {
            view.getMapButton().setVisibility(View.GONE);
        }

        //Invite Button visibility based on isPrivate req
        if(eventModel.getIsPrivate()){
            view.getInviteButton().setVisibility(View.VISIBLE);
        }else{
            view.getInviteButton().setVisibility(View.GONE);
        }

        view.getInviteButton().setOnClickListener(v -> {
            SearchEntrantsPrivateEventFragment fragment = new SearchEntrantsPrivateEventFragment();
            fragment.show(getSupportFragmentManager(),"search");
        });
    }

    /**
     * Updates the activity based on the stored event. Call when any event details may have changed.
     */
    public void refresh() {
        if (eventModel == null) {
            return;
        }
        controller = new OEventPoolController(eventModel);
        view.getTitleText().setText(eventModel.getTitle());
        updateMapButtonVisibility();

        waitlistRepository.getAllEntries(eventModel.getEventId(), entries -> runOnUiThread(() -> renderPool(entries)));
    }

    private void renderPool(List<WaitlistEntry> entries) {
        PoolPhase poolPhase = determinePoolPhase(entries);
        updateEntrantCount(entries, poolPhase);

        switch (poolPhase) {
            case PRE_LOTTERY:
                showPreLotteryPool();
                break;
            case POST_LOTTERY:
                showPostLotteryPool();
                break;
            case FINALIZED:
                showFinalizedPool();
                break;
        }
    }

    private PoolPhase determinePoolPhase(List<WaitlistEntry> entries) {
        if (hasLotteryResults(entries)) {
            if (hasConfirmationEnded()) {
                return PoolPhase.FINALIZED;
            }
            return PoolPhase.POST_LOTTERY;
        }

        if (hasConfirmationEnded()) {
            return PoolPhase.FINALIZED;
        }
        return PoolPhase.PRE_LOTTERY;
    }

    private boolean hasLotteryResults(List<WaitlistEntry> entries) {
        boolean hasCanceledAfterRegistration = false;

        for (WaitlistEntry entry : entries) {
            WaitlistEntry.Status status = entry.getStatus();
            if (status == WaitlistEntry.Status.INVITED
                    || status == WaitlistEntry.Status.ACCEPTED
                    || status == WaitlistEntry.Status.NOT_CHOSEN
                    || status == WaitlistEntry.Status.DECLINED) {
                return true;
            }
            if (status == WaitlistEntry.Status.CANCELED && !eventModel.isInRegistration()) {
                hasCanceledAfterRegistration = true;
            }
        }

        return hasCanceledAfterRegistration;
    }

    private boolean hasConfirmationEnded() {
        String endConfirmationTime = eventModel.getEndConfirmationTime();
        if (endConfirmationTime == null || endConfirmationTime.isBlank()) {
            return false;
        }

        try {
            return LocalDateTime.now().isAfter(LocalDateTime.parse(endConfirmationTime));
        }
        catch (DateTimeParseException ignored) {
            return false;
        }
    }

    private void updateEntrantCount(List<WaitlistEntry> entries, PoolPhase poolPhase) {
        int displayedEntrants;

        switch (poolPhase) {
            case PRE_LOTTERY:
                displayedEntrants = countEntries(entries, WaitlistEntry.Status.PENDING);
                break;
            case POST_LOTTERY:
                displayedEntrants = countEntries(entries,
                        WaitlistEntry.Status.ACCEPTED,
                        WaitlistEntry.Status.INVITED,
                        WaitlistEntry.Status.CANCELED,
                        WaitlistEntry.Status.DECLINED,
                        WaitlistEntry.Status.NOT_CHOSEN);
                break;
            case FINALIZED:
            default:
                displayedEntrants = countEntries(entries, WaitlistEntry.Status.ACCEPTED);
                break;
        }

        if (eventModel.getEntrantCapacity() <= 0) {
            view.getEntrantCountText().setText(String.format(Locale.getDefault(), "(%d)", displayedEntrants));
            return;
        }

        view.getEntrantCountText().setText(String.format(Locale.getDefault(),
                "(%d/%d)",
                displayedEntrants,
                eventModel.getEntrantCapacity()));
    }

    private int countEntries(List<WaitlistEntry> entries, WaitlistEntry.Status... statuses) {
        int count = 0;
        for (WaitlistEntry entry : entries) {
            for (WaitlistEntry.Status status : statuses) {
                if (entry.getStatus() == status) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private void showPreLotteryPool() {
        selectedForRemoval = null;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_list_fragment, new PreLotteryPoolFragment(eventModel))
                .commit();

        view.getSendNotificationsButton().setVisibility(View.GONE);
        view.getRemoveButton().setVisibility(View.GONE);

        if (eventModel.getIsPrivate()) {
            setLotteryButton(1);
        }
        else {
            setLotteryButton(4);
        }
    }

    private void showPostLotteryPool() {
        selectedForRemoval = new ArrayList<>();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_list_fragment, new PostLotteryPoolFragment(controller, selectedForRemoval))
                .commit();

        view.getSendNotificationsButton().setVisibility(View.VISIBLE);
        //view.getSendNotificationsButton().setOnClickListener(v -> PLACEHOLDER);

        view.getRemoveButton().setVisibility(View.VISIBLE);
        view.getRemoveButton().setText("Remove");
        view.getRemoveButton().setOnClickListener(v -> removeSelectedEntrants());

        if (eventModel.getIsPrivate()) {
            setLotteryButton(1);
        }
        else {
            setLotteryButton(0);
        }
    }

    private void showFinalizedPool() {
        selectedForRemoval = null;
        finalList = new ArrayList<>();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_list_fragment, new FinalizedPoolFragment(eventModel, controller, finalList))
                .commit();

        view.getSendNotificationsButton().setVisibility(View.GONE);
        view.getRemoveButton().setVisibility(View.GONE);
        setLotteryButton(2);
    }

    private void removeSelectedEntrants() {
        if (selectedForRemoval == null || selectedForRemoval.isEmpty()) {
            Toast.makeText(this, "Select at least one entrant to remove", Toast.LENGTH_SHORT).show();
            return;
        }
//Not Working
//        ArrayList<String> entrantsToRemove = new ArrayList<>(selectedForRemoval);
//        AtomicInteger remainingUpdates = new AtomicInteger(entrantsToRemove.size());
//        AtomicBoolean updateFailed = new AtomicBoolean(false);
//
//        for (String entrantId : entrantsToRemove) {
//            waitlistRepository.updateStatus(eventModel.getEventId(), entrantId, WaitlistEntry.Status.CANCELED, new WaitlistRepository.SaveCallback() {
//                @Override
//                public void onSuccess() {
//                    if (remainingUpdates.decrementAndGet() == 0 && !updateFailed.get()) {
//                        refresh();
//                    }
//                }
//
//                @Override
//                public void onFailure(String error) {
//                    if (updateFailed.compareAndSet(false, true)) {
//                        Toast.makeText(OEventPoolActivity.this, "Failed to remove entrants", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
    }

    /**
     * Sets the lottery button to the primary action for the current pool phase.
     * @param mode 0 -> replace canceled, 1 -> invite, 2 -> export list, 3 -> gone, 4 -> confirm selection
     */
    private void setLotteryButton(int mode) {
        switch (mode) {
            case 0:
                view.getLotteryButton().setVisibility(View.VISIBLE);
                view.getLotteryButton().setText("Replace Canceled");
                view.getLotteryButton().setOnClickListener(v -> {
                    runLotteryWithoutNotifications();
                });
                break;
            case 1:
                view.getLotteryButton().setVisibility(View.VISIBLE);
                view.getLotteryButton().setText("Invite");

                //TODO: replace testEntrantId when entrant search is implemented
                String testEntrantId = DeviceIdManager.getOrCreate(this);
                view.getLotteryButton().setOnClickListener(v -> {
                    controller.sendInvite(this, testEntrantId, "Invitation",
                            String.format(Locale.getDefault(),"You were invited to %s!", eventModel.getTitle()));
                });
                break;
            case 2:
                view.getLotteryButton().setVisibility(View.VISIBLE);
                view.getLotteryButton().setText("Export List");
                view.getLotteryButton().setOnClickListener(v -> {
                    if (finalList != null && !finalList.isEmpty()) {
                        writeCSVLauncher.launch(null);
                    }
                    else {
                        Toast.makeText(this, "There are no entrants", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 3:
                view.getLotteryButton().setVisibility(View.GONE);
                break;
            case 4:
                view.getLotteryButton().setVisibility(View.VISIBLE);
                view.getLotteryButton().setText("Confirm Selection");
                view.getLotteryButton().setOnClickListener(v -> {
                    if (eventModel.getApplicantCapacity() <= 0) {
                        Toast.makeText(this, "Set the applicant capacity before confirming the selection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    runLotteryWithoutNotifications();
                });
                break;
        }
    }

    public void setNotificationMessages(String selectedMessage, String nonSelectedMessage) {
        this.selectedMessage = selectedMessage;
        this.nonSelectedMessage = nonSelectedMessage;
    }

    private void runLotteryWithoutNotifications() {
        waitlistRepository.getAllEntries(eventModel.getEventId(), entries -> doLottery(entries, false));
    }

    public void doLottery(List<WaitlistEntry> entries) {
        doLottery(entries, true);
    }

    public void doLottery(List<WaitlistEntry> entries, boolean sendNotifications) {
        Lottery lottery = new Lottery();
        //Draw the lottery
        boolean lotterySuccess = lottery.drawToCapacity(entries, eventModel.getApplicantCapacity());

        //Guard statement for if the lottery didn't succeed
        if (!lotterySuccess) {
            Toast.makeText(OEventPoolActivity.this, "Lottery Failed", Toast.LENGTH_SHORT).show();
            return;
        }

        AtomicInteger remainingUpdates = new AtomicInteger(entries.size());
        AtomicBoolean updateFailed = new AtomicBoolean(false);

        //Update the waitlist with the new state of the list
        for (WaitlistEntry entrant: entries) {
            waitlistRepository.updateStatus(eventModel.getEventId(), entrant.getEntrantId(), entrant.getStatus(), new WaitlistRepository.SaveCallback() {
                @Override
                public void onSuccess() {
                    if (remainingUpdates.decrementAndGet() == 0 && !updateFailed.get()) {
                        finishLottery(sendNotifications);
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (updateFailed.compareAndSet(false, true)) {
                        Toast.makeText(OEventPoolActivity.this, "Lottery Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void finishLottery(boolean sendNotifications) {
        if (sendNotifications) {
            String selectedNotification = selectedMessage;
            if (selectedNotification == null || selectedNotification.isBlank()) {
                selectedNotification = "You've been selected!";
            }

            String nonSelectedNotification = nonSelectedMessage;
            if (nonSelectedNotification == null || nonSelectedNotification.isBlank()) {
                nonSelectedNotification = "You were not selected";
            }

            controller.sendNotifications(OEventPoolActivity.this,
                    String.format(Locale.getDefault(), "%s Lottery Result", eventModel.getTitle()),
                    selectedNotification,
                    Notification.NotificationTarget.SELECTED);
            controller.sendNotifications(OEventPoolActivity.this,
                    String.format(Locale.getDefault(), "%s Lottery Result", eventModel.getTitle()),
                    nonSelectedNotification,
                    Notification.NotificationTarget.NOT_SELECTED);
        }
        refresh();
    }
}
