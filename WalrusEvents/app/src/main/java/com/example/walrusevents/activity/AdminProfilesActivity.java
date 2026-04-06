package com.example.walrusevents.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.ui.AdminProfilesView;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;

public class AdminProfilesActivity extends AppCompatActivity {
    private ArrayList<Entrant> entrants;
    private AdminProfilesView view;
    private EntrantArrayAdapter entrantsAdapter;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_profiles_admin);
        view = new AdminProfilesView(this);
        loadProfiles();
    }

    private void loadProfiles() {
        entrants = new ArrayList<>();
        entrantsAdapter = new EntrantArrayAdapter(this, entrants);
        view.getProfilesListView().setAdapter(entrantsAdapter);
        profileRepository = new ProfileRepository();
        profileRepository.initiateGetAllProfiles(3, this::onEntrantBatchLoaded);
    }

    public void onEntrantBatchLoaded(ArrayList<Entrant> entrantBatch) {
        if (entrantBatch != null && !entrantBatch.isEmpty()) {
            profileRepository.getNextProfilesBatch(3, this::onEntrantBatchLoaded);
            entrants.addAll(entrantBatch);
            entrantsAdapter.notifyDataSetChanged();
        }
    }
}
