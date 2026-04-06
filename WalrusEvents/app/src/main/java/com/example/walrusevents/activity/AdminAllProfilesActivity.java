package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.ui.AdminAllProfilesView;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;

public class AdminAllProfilesActivity extends AppCompatActivity {
    private ArrayList<Entrant> entrants;
    private AdminAllProfilesView view;
    private EntrantArrayAdapter entrantsAdapter;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_profiles_admin);
        view = new AdminAllProfilesView(this);
        loadProfiles();

        view.getBackButton().setOnClickListener(v -> finish());
    }

    private void loadProfiles() {
        entrants = new ArrayList<>();
        entrantsAdapter = new EntrantArrayAdapter(this, entrants);
        view.getProfilesListView().setAdapter(entrantsAdapter);
        profileRepository = new ProfileRepository();
        profileRepository.initiateGetAllProfiles(3, this::onEntrantBatchLoaded);

        view.getProfilesListView().setOnItemClickListener((parent, view, position, id) -> {
            Intent goToInspectProfile = new Intent(AdminAllProfilesActivity.this, AdminInspectProfileActivity.class);
            goToInspectProfile.putExtra("Device ID", entrants.get(position).getDeviceId());
            startActivity(goToInspectProfile);
        });
    }

    public void onEntrantBatchLoaded(ArrayList<Entrant> entrantBatch) {
        if (entrantBatch != null && !entrantBatch.isEmpty()) {
            profileRepository.getNextProfilesBatch(3, this::onEntrantBatchLoaded);
            entrants.addAll(entrantBatch);
            entrantsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadProfiles();
    }
}
