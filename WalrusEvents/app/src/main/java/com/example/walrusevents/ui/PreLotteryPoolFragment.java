package com.example.walrusevents.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.walrusevents.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class PreLotteryPoolFragment extends Fragment {
    private Event eventModel;
    private ArrayList<Entrant> entrantList;
    private EntrantArrayAdapter eventListAdapter;
    WaitlistRepository waitlistRepository;
    ProfileRepository profileRepository;

    public PreLotteryPoolFragment(Event eventModel) {
        this.eventModel = eventModel;
        waitlistRepository = new WaitlistRepository();
        profileRepository = new ProfileRepository();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pre_lottery_waitlist_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        registrationPhasePool(view);
    }
    private void registrationPhasePool(View view) {
        Activity context = getActivity();

        entrantList = new ArrayList<>();
        eventListAdapter = new EntrantArrayAdapter(context, entrantList);
        ListView entrantListView = view.findViewById(R.id.org_entrant_list_view);

        entrantListView.setAdapter(eventListAdapter);

        waitlistRepository.getAllEntries(eventModel.getEventId(), new WaitlistRepository.EntryListCallback() {
            @Override
            public void onEntriesLoaded(List<WaitlistEntry> entries) {
                ArrayList<String> deviceIds = new ArrayList<>();
                for (WaitlistEntry entry: entries) {
                    deviceIds.add(entry.getEntrantId());
                }
                profileRepository.getProfilesInList(deviceIds, new ProfileRepository.ProfileCallback() {
                    @Override
                    public void onEntrantLoaded(Entrant entrant) {
                        if (entrant == null)
                        {
                            System.out.println("null entrant");
                        }
                        else {
                            entrantList.add(entrant);
                            eventListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }
}
