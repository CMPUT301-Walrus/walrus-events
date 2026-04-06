package com.example.walrusevents.util;

import android.content.Context;

import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.Entrant;

import java.util.ArrayList;
import android.widget.ListView;

public class SearchPrivateEntrantsController implements ProfileRepository.ProfileListCallback {
    private ProfileRepository profileRepository;
    private ArrayList<Entrant> entrants;

    private EntrantArrayAdapter listAdapter;

    private ArrayList<Entrant> filteredList;

    private String query="";

    private Entrant chosenEntrant=null;


    public SearchPrivateEntrantsController(Context context, ProfileRepository repo,ListView listview){
        this.entrants=new ArrayList<>();
        this.profileRepository=repo;
        this.listAdapter=new EntrantArrayAdapter(context,entrants);
        listview.setAdapter(listAdapter);
    }

    public void loadAllEntrants(){
        listAdapter.clear();
        profileRepository.resetPagination();
        profileRepository.initiateGetAllProfiles(15,this);
    }

    @Override
    public void onEntrantsLoaded(ArrayList<Entrant> entrants) {
        if (entrants == null) return;

        updateData(entrants);
        //listAdapter.notifyDataSetChanged();
        if (entrants.size() == 15){
            profileRepository.getNextProfilesBatch(10, this);
        }

        System.out.printf("%d event(s) loaded", entrants.size());
    }

    /*
    * Currently for some reason doesn't filter
     */
    public void applyFilter(){
        ArrayList<Entrant> filteredEntrants = new ArrayList<>();
        if(!query.isEmpty()&&query==null){
            for (Entrant entrant : entrants) {

                boolean matchesKeyword = true;

                String name = entrant.getProfile().getName();
                String email = entrant.getProfile().getEmail();
                String phone = entrant.getProfile().getPhone();
                boolean matchName = name.toLowerCase().contains(query.toLowerCase());
                boolean matchEmail = email.toLowerCase().contains(query.toLowerCase());
                boolean matchPhone = phone.toLowerCase().contains(query.toLowerCase());
                matchesKeyword = matchName || matchEmail || matchPhone;

                if (matchesKeyword) {
                    filteredEntrants.add(entrant);
                }
        }
        }else
        {
            filteredEntrants.addAll(entrants);
        }

        filteredList = filteredEntrants;
        listAdapter.clear();
        listAdapter.addAll(filteredList);
        listAdapter.notifyDataSetChanged();

    }
    public void setQuery(String s){
        this.query=s;
        applyFilter();
    }

    public String getQuery(){
        return query;
    }

    public void updateData(ArrayList<Entrant> newEntrants){
        entrants.clear();
        entrants.addAll(newEntrants);
        applyFilter();
    }

    public void setChosenEntrant(Entrant entrant){
        this.chosenEntrant=entrant;
    }

    public Entrant getChosenEntrant(){
        return chosenEntrant;
    }
}
