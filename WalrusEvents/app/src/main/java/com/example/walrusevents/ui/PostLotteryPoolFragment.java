/**
 * This fragment pops up when the organizer wants to view the finalized waitlist
 * It provides details about the entrants in the waitlist and who was selected
 * It also communicates with the rest of the application to send notifications to the entrants
 * As well as what information should be updated coresponding to the lottery draw
 */

package com.example.walrusevents.ui;

import android.app.Activity;
import android.graphics.Color;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.walrusevents.controllers.OEventPoolController;
import com.example.walrusevents.R;
import com.example.walrusevents.model.WaitlistEntry;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class PostLotteryPoolFragment extends Fragment {
    private final OEventPoolController controller;
    private final ArrayList<String> selectedForRemoval;
    private ArrayList<Entrant> acceptedList;
    private EntrantArrayAdapter acceptedListAdapter;
    private ArrayList<Entrant> chosenList;
    private EntrantArrayAdapter chosenListAdapter;
    private ArrayList<Entrant> canceledList;
    private EntrantArrayAdapter canceledListAdapter;
    private ArrayList<Entrant> notChosenList;
    private EntrantArrayAdapter notChosenListAdapter;

    public PostLotteryPoolFragment(OEventPoolController controller, ArrayList<String> selectedForRemoval) {
        this.selectedForRemoval = selectedForRemoval;
        this.controller = controller;
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
        TextView acceptedText = view.findViewById(R.id.accepted_text);
        TextView chosenText = view.findViewById(R.id.chosen_text);
        TextView canceledText = view.findViewById(R.id.canceled_text);
        TextView notChosenText = view.findViewById(R.id.not_chosen_text);

        acceptedList = new ArrayList<>();
        chosenList = new ArrayList<>();
        canceledList = new ArrayList<>();
        notChosenList = new ArrayList<>();

        acceptedListAdapter = new EntrantArrayAdapter(context, acceptedList);
        registerCountObserver(acceptedListAdapter, () -> {
            acceptedText.setText(String.format(Locale.getDefault(), "Accepted (%d)", acceptedList.size()));
            chosenText.setText(getAwaitingConfirmationLabel());
        });
        setupList(view, acceptedList, acceptedListAdapter, R.id.accepted_list, WaitlistEntry.Status.ACCEPTED);

        chosenListAdapter = new EntrantArrayAdapter(context, chosenList);
        registerCountObserver(chosenListAdapter, () -> chosenText.setText(getAwaitingConfirmationLabel()));
        setupList(view, chosenList, chosenListAdapter, R.id.chosen_list, WaitlistEntry.Status.INVITED);

        canceledListAdapter = new EntrantArrayAdapter(context, canceledList);
        registerCountObserver(canceledListAdapter, () -> {
            canceledText.setText(String.format(Locale.getDefault(),
                    "Canceled (%d)",
                    canceledList.size()));
            chosenText.setText(getAwaitingConfirmationLabel());
        });
        setupList(view, canceledList, canceledListAdapter, R.id.canceled_list, WaitlistEntry.Status.CANCELED);
        controller.fillEntrantListByStatus(canceledList, canceledListAdapter, WaitlistEntry.Status.DECLINED);

        notChosenListAdapter = new EntrantArrayAdapter(context, notChosenList);
        registerCountObserver(notChosenListAdapter, () -> notChosenText.setText(String.format(Locale.getDefault(),
                "Not Chosen (%d)",
                notChosenList.size())));
        setupList(view, notChosenList, notChosenListAdapter, R.id.not_chosen_list, WaitlistEntry.Status.NOT_CHOSEN);

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

        acceptedText.setText(String.format(Locale.getDefault(), "Accepted (%d)", acceptedList.size()));
        chosenText.setText(getAwaitingConfirmationLabel());
        canceledText.setText(String.format(Locale.getDefault(), "Canceled (%d)", canceledList.size()));
        notChosenText.setText(String.format(Locale.getDefault(), "Not Chosen (%d)", notChosenList.size()));
    }

    public int getAcceptedCount() {
        return acceptedList.size();
    }
    public int getChosenList() {
        return chosenList.size();
    }
    public int getNotChosenList() {
        return notChosenList.size();
    }
    public int getCanceledCount() {
        return canceledList.size();
    }

    private void setupList(View view, ArrayList<Entrant> list, EntrantArrayAdapter adapter, int listViewId, WaitlistEntry.Status status) {
        ListView listView = view.findViewById(listViewId);
        listView.setAdapter(adapter);

        controller.fillEntrantListByStatus(list, adapter, status);
    }

    private void registerCountObserver(EntrantArrayAdapter adapter, Runnable onChanged) {
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                onChanged.run();
            }
        });
    }

    private String getAwaitingConfirmationLabel() {
        int respondedCount = acceptedList.size() + canceledList.size();
        return String.format(Locale.getDefault(),
                "Awaiting Confirmation (%d/%d)",
                acceptedList.size(),
                respondedCount);
    }
}
