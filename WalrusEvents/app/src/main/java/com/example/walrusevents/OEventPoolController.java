package com.example.walrusevents;

import android.app.Activity;

import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class OEventPoolController implements ProfileRepository.ProfileCallback, WaitlistRepository.EntryListCallback {
    private ArrayList<Entrant> entrantList;
    private EntrantArrayAdapter eventListAdapter;
    private WaitlistRepository waitlistRepository;
    private ProfileRepository profileRepository;

    public OEventPoolController(Activity context, String eventId) {
        entrantList = new ArrayList<>();
        eventListAdapter = new EntrantArrayAdapter(context, entrantList);

        waitlistRepository = new WaitlistRepository();
        profileRepository = new ProfileRepository();
        waitlistRepository.getAllEntries(eventId, this);
    }

    @Override
    public void onEntriesLoaded(List<WaitlistEntry> entries) {
        for (WaitlistEntry entry: entries) {
            profileRepository.getProfile(entry.getEventId(), this);
        }
    }

    @Override
    public void onEntrantLoaded(Entrant entrant) {
        entrantList.add(entrant);
        eventListAdapter.notifyDataSetChanged();
    }
}
