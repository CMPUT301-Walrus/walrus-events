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

import com.example.walrusevents.R;
import com.example.walrusevents.controllers.OEventPoolController;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class FinalizedPoolFragment extends Fragment {
    private Event eventModel;
    private ArrayList<Entrant> entrantList;
    private EntrantArrayAdapter eventListAdapter;
    private WaitlistRepository waitlistRepository;
    private ProfileRepository profileRepository;
    private OEventPoolController controller;

    public FinalizedPoolFragment(Event eventModel, OEventPoolController controller, @NonNull ArrayList<Entrant> entrantList) {
        this.eventModel = eventModel;
        this.controller = controller;
        this.entrantList = entrantList;
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

        entrantList.clear();
        eventListAdapter = new EntrantArrayAdapter(context, entrantList);
        ListView entrantListView = view.findViewById(R.id.org_entrant_list_view);

        entrantListView.setAdapter(eventListAdapter);

        controller.fillEntrantListByStatus(entrantList, eventListAdapter, WaitlistEntry.Status.ACCEPTED);
    }
}
