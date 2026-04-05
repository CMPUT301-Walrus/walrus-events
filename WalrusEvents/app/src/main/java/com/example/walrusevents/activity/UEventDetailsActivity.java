package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.controllers.EntrantController;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.AcceptInvitationFragment;
import com.example.walrusevents.ui.CommentsSectionFragment;
import com.example.walrusevents.ui.EventPosterFragment;
import com.example.walrusevents.ui.UEventDetailsView;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;
import com.example.walrusevents.util.PermissionGatekeeper;

/**
 * Class handles displaying event details for a particular event
 * Handles whether event was clicked on or scanned to go to event page
 */
public class UEventDetailsActivity extends AppCompatActivity
        implements EntrantController.ActionCallback, AcceptInvitationFragment.AcceptInvitationListener {

    private Event eventModel;
    private UEventDetailsView view;
    private WaitlistEntry entry;
    private WaitlistRepository waitlistRepository;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> initializeUi());
    }

    private void initializeUi() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        waitlistRepository = new WaitlistRepository();
        eventRepository = new EventRepository();

        // Get scann id (will be null if user clicked event)
        String scannedId = getIntent().getStringExtra("EVENT_ID");

        // Initialize UI depending on whether user clicked event or scanned qr code
        if (scannedId != null) {
            // Scanned QR code
            fetchEventFromFirestore(scannedId);
        } else {
            // Clicked event
            try {
                eventModel = getIntent().getSerializableExtra("Event", Event.class);
                if (eventModel != null) {
                    setupUI();
                } else {
                    Log.e("UEventDetails", "Event object is null");
                    finish();
                }
            } catch (Exception e) {
                Log.e("UEventDetails", "Error loading serialized event", e);
                finish();
            }
        }
    }

    /**
     * Retrieves event details from the database
     * Used for when a user scans a QR code
     */
    private void fetchEventFromFirestore(String eventId) {
        eventRepository.getEvent(eventId, new EventRepository.EventCallback() {
            @Override
            public void onEventLoaded(Event loadedEvent) {
                if (loadedEvent != null) {
                    eventModel = loadedEvent;
                    setupUI();
                } else {
                    Toast.makeText(UEventDetailsActivity.this, "Invalid QR Code or Event not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    /**
     * Initialize UI for the event details
     */
    private void setupUI() {
        if (eventModel == null) return;

        UserRole role = UserRoleManager.getRole();

        // Initialize the View wrapper
        view = new UEventDetailsView(this, eventModel);

        // Load Poster Image via ImageRepository and Glide
        FirebaseAPIManager apiMgr = new FirebaseAPIManager();
        ImageRepository imageRepo = new ImageRepository(apiMgr);

        imageRepo.retrieveImage(eventModel.getEventId(), new FirebaseAPIManager.OnDownloadCompleteListener() {
            @Override
            public void onSuccess(String imageUrl) {
                Glide.with(UEventDetailsActivity.this)
                        .load(imageUrl)
                        .placeholder(R.drawable.rounded_light_blue_square)
                        .error(R.drawable.image_not_found_placeholder)
                        .into(view.getEventPoster());
            }

            @Override
            public void onFailure(String error) {
                Log.e("POSTER_LOAD", "Could not load poster: " + error);
                view.getEventPoster().setImageResource(R.drawable.image_not_found_placeholder);
            }
        });

        // Setup Listeners
        view.getEventPoster().setOnClickListener(v -> {
            EventPosterFragment posterFragment = new EventPosterFragment();
            Bundle args = new Bundle();
            args.putString("event_id", eventModel.getEventId());
            posterFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, posterFragment)
                    .addToBackStack(null)
                    .commit();
        });

        String desc = eventModel.getDescription();
        if (desc != null && !desc.isEmpty()) {
            view.getDescription().setText(desc);
        }

        CommentsSectionFragment commentsSectionFragment =
                CommentsSectionFragment.newInstance(eventModel, getSupportFragmentManager());

        view.getViewCommentsButton().setOnClickListener(v -> {
            commentsSectionFragment.show(getSupportFragmentManager(), "View Comments");
        });

        view.getBackButton().setOnClickListener(v -> finish());

        view.getSeePoolButton().setOnClickListener(v -> {
            Intent seePool = new Intent(UEventDetailsActivity.this, UViewWaitlistActivity.class);
            seePool.putExtra("Event", eventModel);
            startActivity(seePool);
        });

        // Waitlist Join Logic
        String deviceId = DeviceIdManager.getOrCreate(this);
        Entrant me = new Entrant(new Profile(deviceId, "User", "email@uab.ca"));

        if (role == UserRole.USER && !eventModel.getIsPrivate()) {
            view.getJoinButton().setOnClickListener(v -> {
                WaitlistRepository waitRep = new WaitlistRepository();
                ProfileRepository pfRep = new ProfileRepository();
                EntrantController entrantController = new EntrantController(me, waitRep, pfRep);
                entrantController.joinWaitlist(eventModel.getEventId(), this);
            });
        }
        else {
            view.getJoinButton().setVisibility(View.GONE);
        }

        checkForInvitation();
    }

    /**
     * Checks if the user has an active invitation to respond to.
     */
    private void checkForInvitation() {
        if (eventModel == null) {
            return;
        }

        if (!eventModel.isInConfirmation()) {
            return;
        }

        String deviceId = DeviceIdManager.getOrCreate(this);
        String eventId = eventModel.getEventId();

        waitlistRepository.getEntry(eventId, deviceId, new WaitlistRepository.EntryCallback() {
            @Override
            public void onEntryLoaded(WaitlistEntry retrievedEntry) {
                if (retrievedEntry != null) {
                    UEventDetailsActivity.this.entry = retrievedEntry;
                    AcceptInvitationFragment inviteFragment;

                    String headerText;
                    if (eventModel.getIsPrivate()) {
                        headerText = eventModel.getTitle();
                    }
                    else {
                        headerText = "Lottery Result";
                    }

                    switch (retrievedEntry.getStatus()) {
                        case INVITED:
                            inviteFragment = AcceptInvitationFragment.newInstance(UEventDetailsActivity.this, WaitlistEntry.Status.INVITED, headerText);
                            inviteFragment.show(getSupportFragmentManager(), "Invited");
                            break;
                        case NOT_CHOSEN:
                            inviteFragment = AcceptInvitationFragment.newInstance(UEventDetailsActivity.this, WaitlistEntry.Status.NOT_CHOSEN, headerText);
                            inviteFragment.show(getSupportFragmentManager(), "Not Invited");
                            break;
                        case PENDING:
                            inviteFragment = AcceptInvitationFragment.newInstance(UEventDetailsActivity.this, WaitlistEntry.Status.PENDING, headerText);
                            inviteFragment.show(getSupportFragmentManager(), "Pending");
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Action Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e("UEventDetails", errorMessage);
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void acceptInvite() {
        if (entry == null) return;
        entry.setStatus(WaitlistEntry.Status.ACCEPTED);
        waitlistRepository.updateStatus(entry.getEventId(), entry.getEntrantId(), WaitlistEntry.Status.ACCEPTED,
                new WaitlistRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UEventDetailsActivity.this, "Accepted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("Invite", error);
                    }
                });
    }

    @Override
    public void declineInvite() {
        if (entry == null) return;
        entry.setStatus(WaitlistEntry.Status.DECLINED);
        waitlistRepository.updateStatus(entry.getEventId(), entry.getEntrantId(), WaitlistEntry.Status.DECLINED,
                new WaitlistRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UEventDetailsActivity.this, "Declined", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("Invite", error);
                    }
                });
    }
}