package com.example.walrusevents.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.walrusevents.R;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.util.SearchPrivateEntrantsController;

/*
* WIP
* Search for an entrants to invite to a private event
* can find by email name phone number.
* Fragment will show on top of an Activity
* should be able to give info of the chosen entrant (output from fragment)
 */
public class SearchEntrantsPrivateEventFragment extends DialogFragment {
    private ProfileRepository profileRepo;
    private SearchPrivateEntrantsController controller;
    private SearchView searchBar;
    private Button confirmButton;
    private ListView listView;
    private ImageView backButton;

    private String currentQuery="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchBar=view.findViewById(R.id.searchView);
        listView=view.findViewById(R.id.listView2);
        confirmButton=view.findViewById(R.id.confirm_search_entrants_button);
        backButton=view.findViewById(R.id.back_fragment_invite);

        //Scrolling Listview
        profileRepo=new ProfileRepository();
        controller=new SearchPrivateEntrantsController(requireContext(),profileRepo,listView);
        controller.loadAllEntrants();
        //controller of the list to load events by filter (profile repo by info)

        //Search Bar that takes query on submit and filters the listView - use ProfileRepo get profiles by info
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                controller.setQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        /*
        * Choosing item of entrant to invite
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //the chosen person to invite
                Entrant chosenEntrant = (Entrant) parent.getItemAtPosition(position);
                controller.setChosenEntrant(chosenEntrant);
                //TODO: Get the chosen entrant to be invited
            }
        });

        confirmButton.setOnClickListener(v -> {
            dismiss();
        });
    }

}
