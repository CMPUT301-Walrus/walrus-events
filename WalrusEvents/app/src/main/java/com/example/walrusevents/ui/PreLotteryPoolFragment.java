/**
 * This fragment pops up when the organizer wants to view the pre-lottery waitlist
 * It will give the organizer the ability to draw the lottery and progress to the PostLotteryPoolFragment
 */

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

import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;

public class PreLotteryPoolFragment extends Fragment {
    private final Event eventModel;
    private final WaitlistRepository waitlistRepository;
    private final ProfileRepository profileRepository;
    private ArrayList<Entrant> entrantList;
    private EntrantArrayAdapter eventListAdapter;

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

        waitlistRepository.getEntriesByStatus(eventModel.getEventId(), WaitlistEntry.Status.PENDING, entries -> {
            ArrayList<String> deviceIds = new ArrayList<>();
            for (WaitlistEntry entry: entries) {
                deviceIds.add(entry.getEntrantId());
            }

            profileRepository.getProfilesInList(deviceIds, entrant -> {
                if (entrant == null)
                {
                    System.out.println("null entrant");
                }
                else {
                    entrantList.add(entrant);
                    eventListAdapter.notifyDataSetChanged();
                }
            });
        });
    }
}
