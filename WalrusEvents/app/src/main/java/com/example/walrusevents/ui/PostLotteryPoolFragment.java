package com.example.walrusevents.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostLotteryPoolFragment extends Fragment {
    private Event eventModel;
    WaitlistRepository waitlistRepository;
    ProfileRepository profileRepository;
    private ArrayList<Entrant> acceptedList;
    private EntrantArrayAdapter acceptedListAdapter;
    private ArrayList<Entrant> chosenList;
    private EntrantArrayAdapter chosenListAdapter;
    private ArrayList<Entrant> canceledList;
    private EntrantArrayAdapter canceledListAdapter;
    private ArrayList<Entrant> pendingList;
    private ArrayList<Entrant> notChosenList;
    private EntrantArrayAdapter notChosenListAdapter;
    private ArrayList<String> selectedForRemoval;

    public PostLotteryPoolFragment(Event eventModel, ArrayList<String> selectedForRemoval) {
        this.eventModel = eventModel;
        this.selectedForRemoval = selectedForRemoval;
        waitlistRepository = new WaitlistRepository();
        profileRepository = new ProfileRepository();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.post_lottery_waitlist_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createLists(view);
    }
    private void createLists(View view) {
        Activity context = getActivity();
        acceptedList = new ArrayList<>();
        acceptedListAdapter = new EntrantArrayAdapter(context, acceptedList);
        setupList(view, acceptedList, acceptedListAdapter, R.id.accepted_list, WaitlistEntry.Status.ACCEPTED);

        chosenList = new ArrayList<>();
        chosenListAdapter = new EntrantArrayAdapter(context, chosenList);
        setupList(view, chosenList, chosenListAdapter, R.id.chosen_list, WaitlistEntry.Status.INVITED);

        canceledList = new ArrayList<>();
        canceledListAdapter = new EntrantArrayAdapter(context, canceledList);
        setupList(view, canceledList, canceledListAdapter, R.id.canceled_list, WaitlistEntry.Status.CANCELLED);

        notChosenList = new ArrayList<>();
        notChosenListAdapter = new EntrantArrayAdapter(context, notChosenList);
        setupList(view, notChosenList, notChosenListAdapter, R.id.not_chosen_list, WaitlistEntry.Status.NOT_CHOSEN);

        pendingList = new ArrayList<>();
        setupList(view, notChosenList, notChosenListAdapter, R.id.not_chosen_list, WaitlistEntry.Status.PENDING);

        ListView chosenListView = view.findViewById(R.id.chosen_list);
        chosenListView.setOnItemClickListener((parent, view1, position, id) -> {
            String entrantId = chosenList.get(position).getDeviceId();

            if (selectedForRemoval.contains(entrantId)){
                selectedForRemoval.remove(entrantId);

                TextView nameText = view1.findViewById(R.id.profileName);
                nameText.setTextColor(Color.parseColor("#242424"));
                view1.findViewById(R.id.waitlist_entry_background_selected).setVisibility(View.GONE);
            }
            else {
                selectedForRemoval.add(entrantId);

                TextView nameText = view1.findViewById(R.id.profileName);
                nameText.setTextColor(Color.WHITE);
                view1.findViewById(R.id.waitlist_entry_background_selected).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupList(View view, ArrayList<Entrant> list, EntrantArrayAdapter adapter, int listViewId, WaitlistEntry.Status status) {

        ListView listView = view.findViewById(listViewId);

        listView.setAdapter(adapter);

        waitlistRepository.getEntriesByStatus(eventModel.getEventId(), status, new WaitlistRepository.EntryListCallback() {
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
                            list.add(entrant);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }
}
