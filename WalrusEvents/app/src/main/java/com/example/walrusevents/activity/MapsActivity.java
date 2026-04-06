package com.example.walrusevents.activity;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.walrusevents.R;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.WaitlistEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.walrusevents.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String eventId;  // Store eventId that is passed from previous activity/fragment
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get eventId from previous activity/fragment
        eventId = getIntent().getStringExtra("eventId");

        // Back button listener
        binding.backButton.setOnClickListener(v -> finish());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get coordinates for each entrant from waitlist
        WaitlistRepository waitlistRepo = new WaitlistRepository();
        waitlistRepo.getAllEntries(eventId, entries -> {
            for (WaitlistEntry entry : entries) {
                // Loop through entrants and get location if stored
                if (entry.hasLocation()) {
                    // Create location object with specified latitude and longitude
                    LatLng location = new LatLng(entry.getLatitude(), entry.getLongitude());
                    // Place marker on map titled with entrant's ID
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(entry.getEntrantId()));
                    // Move camera to last added entrant
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                }
            }
        });
    }
}