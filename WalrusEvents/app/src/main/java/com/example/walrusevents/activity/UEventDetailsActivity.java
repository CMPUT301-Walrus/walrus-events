package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import android.location.Location;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Class handles displaying event details for a particular event
 * Handles whether event was clicked on or scanned to go to event page
 */
public class UEventDetailsActivity extends AppCompatActivity
        implements EntrantController.ActionCallback, AcceptInvitationFragment.AcceptInvitationListener,
        WaitlistRepository.EntryCallback {
    private static final String INVITATION_DIALOG_TAG = "invitationDialog";

    private Event eventModel;
    private UEventDetailsView view;
    private WaitlistEntry entry;
    private WaitlistRepository waitlistRepository;
    private EventRepository eventRepository;
    private Entrant me;

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
                        .skipMemoryCache(true)
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
        waitlistRepository.getEntry(eventModel.getEventId(), deviceId, this);

        UserRole role = UserRoleManager.getRole();
        if (role != UserRole.USER || eventModel.getIsPrivate()) {
            view.getJoinButton().setVisibility(View.GONE);
        }
    }

    private void refreshWaitlistEntry() {
        if (eventModel == null || waitlistRepository == null) {
            return;
        }
        waitlistRepository.getEntry(eventModel.getEventId(), DeviceIdManager.getOrCreate(this), this);
    }

    private boolean canManageWaitlist() {
        return eventModel != null
                && UserRoleManager.getRole() == UserRole.USER
                && !eventModel.getIsPrivate();
    }

    private boolean hasActiveWaitlistEntry() {
        return entry != null && entry.getStatus() != WaitlistEntry.Status.CANCELED;
    }

    private boolean shouldHideLeaveButton() {
        if (eventModel.getOwners().contains(DeviceIdManager.getOrCreate(this))) {
            return true;
        }
        if (!hasActiveWaitlistEntry()) {
            return false;
        }
        if (!eventModel.isInRegistration()) {
            return true;
        }

        WaitlistEntry.Status status = entry.getStatus();
        return status == WaitlistEntry.Status.INVITED
                || status == WaitlistEntry.Status.ACCEPTED
                || status == WaitlistEntry.Status.DECLINED;
    }

    private void updateJoinButton(String deviceId) {
        if (!canManageWaitlist()) {
            view.getJoinButton().setVisibility(View.GONE);
            return;
        }

        Entrant entrant = new Entrant(new Profile(deviceId, "User", "email@uab.ca"));
        if (shouldHideLeaveButton()) {
            view.getJoinButton().setVisibility(View.GONE);
            return;
        }
        if (hasActiveWaitlistEntry()) {

            view.getJoinButton().setVisibility(View.VISIBLE);
            view.getJoinButton().setEnabled(true);
            view.getJoinButton().setText("- Leave");
            view.getJoinButton().setOnClickListener(v -> {
                WaitlistRepository waitRep = new WaitlistRepository();
                ProfileRepository pfRep = new ProfileRepository();
                EntrantController entrantController = new EntrantController(entrant, waitRep, pfRep);
                entrantController.leaveWaitlist(eventModel.getEventId(), this);
            });
        }
        else {
            view.getJoinButton().setVisibility(View.VISIBLE);
            view.getJoinButton().setEnabled(true);
            view.getJoinButton().setText("+ Join");
            view.getJoinButton().setOnClickListener(v -> {
                if (!eventModel.isInRegistration()) {
                    Toast.makeText(this, "Registration deadline has passed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eventModel.getUseGeolocation()) {
                    fetchLocationAndJoin();
                } else {
                    performJoin(null, null);
                }
            });
        }
    }
    @Override
    public void onEntryLoaded(WaitlistEntry retrievedEntry) {
        UEventDetailsActivity.this.entry = retrievedEntry;
        if (retrievedEntry != null) {
            checkForInvitation();
        }
        updateJoinButton(DeviceIdManager.getOrCreate(this));
    }
    /**
     * Checks if the user has an active invitation to respond to.
     */
    private void checkForInvitation() {
        if (eventModel == null || entry == null || entry.getStatus() == null) {
            return;
        }

        if (!shouldShowInvitationDialog(entry.getStatus())) {
            return;
        }

        if (getSupportFragmentManager().findFragmentByTag(INVITATION_DIALOG_TAG) != null) {
            return;
        }

        AcceptInvitationFragment inviteFragment =
                AcceptInvitationFragment.newInstance(entry.getStatus(), getInvitationHeaderText());
        inviteFragment.show(getSupportFragmentManager(), INVITATION_DIALOG_TAG);
    }

    private String getInvitationHeaderText() {
        if (eventModel.getIsPrivate()) {
            return eventModel.getTitle();
        }
        return "Lottery Result";
    }

    private boolean shouldShowInvitationDialog(WaitlistEntry.Status status) {
        switch (status) {
            case INVITED:
                return true;
            case NOT_CHOSEN:
            case PENDING:
                return eventModel.isInConfirmation();
            default:
                return false;
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Action Successful", Toast.LENGTH_SHORT).show();
        refreshWaitlistEntry();
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e("UEventDetails", errorMessage);
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
        refreshWaitlistEntry();
    }

    @Override
    public void acceptInvite() {
        if (entry == null || entry.getStatus() != WaitlistEntry.Status.INVITED) return;
        waitlistRepository.updateStatus(entry.getEventId(), entry.getEntrantId(), WaitlistEntry.Status.ACCEPTED,
                new WaitlistRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        entry.setStatus(WaitlistEntry.Status.ACCEPTED);
                        updateJoinButton(DeviceIdManager.getOrCreate(UEventDetailsActivity.this));
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
        if (entry == null || entry.getStatus() != WaitlistEntry.Status.INVITED) return;
        waitlistRepository.updateStatus(entry.getEventId(), entry.getEntrantId(), WaitlistEntry.Status.DECLINED,
                new WaitlistRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        entry.setStatus(WaitlistEntry.Status.DECLINED);
                        updateJoinButton(DeviceIdManager.getOrCreate(UEventDetailsActivity.this));
                        Toast.makeText(UEventDetailsActivity.this, "Declined", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("Invite", error);
                    }
                });
    }

    /**
     * The following two methods were generated by Gemini 3, Google DeepMind
     * Fed in this file and asked it to implement the location services reuirement
     * 02/04/26
     */
    private void fetchLocationAndJoin() {
        if (!eventModel.isInRegistration()) {
            Toast.makeText(this, "Registration deadline has passed.", Toast.LENGTH_SHORT).show();
            return;
        }

        com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient =
                com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);

        // Permission check
        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        // The 'location' inside the parenthesis is the variable name
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Now 'location' is recognized as an android.location.Location object
                    performJoin(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(UEventDetailsActivity.this, "Location unavailable", Toast.LENGTH_SHORT).show();
                    performJoin(null, null);
                }
            }
        });
    }

    private void performJoin(Double lat, Double lon) {
        if (!eventModel.isInRegistration()) {
            Toast.makeText(this, "Registration deadline has passed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Double check 'me' isn't null (safety)
        if (me == null) {
            String deviceId = DeviceIdManager.getOrCreate(this);
            me = new Entrant(new Profile(deviceId, "User", "email@uab.ca"));
        }

        WaitlistRepository waitRep = new WaitlistRepository();
        ProfileRepository pfRep = new ProfileRepository();
        EntrantController entrantController = new EntrantController(me, waitRep, pfRep);

        if (lat != null && lon != null) {
            entrantController.joinWaitlistWithLocation(eventModel.getEventId(), lat, lon, this);
        } else {
            entrantController.joinWaitlist(eventModel.getEventId(), this);
        }
    }
}
