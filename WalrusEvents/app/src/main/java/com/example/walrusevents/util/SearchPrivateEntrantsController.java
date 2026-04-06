/**
 * This controller is repsonsible for searching through entrants for private events
 * It queries and filters for specific information
 */

package com.example.walrusevents.util;

import android.content.Context;

import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.util.EntrantArrayAdapter;

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
        filteredList = new ArrayList<>();
        this.profileRepository=repo;
        this.listAdapter=new EntrantArrayAdapter(context,filteredList);
        listview.setAdapter(listAdapter);
    }

    public void loadAllEntrants(){
        entrants.clear();
        listAdapter.clear();
        profileRepository.resetPagination();
        profileRepository.initiateGetAllProfiles(2,this);
    }

    @Override
    public void onEntrantsLoaded(ArrayList<Entrant> entrants) {
        if (entrants == null) return;

        updateData(entrants);
        //listAdapter.notifyDataSetChanged();
        if (!entrants.isEmpty())
        {
            profileRepository.getNextProfilesBatch(2, this);
        }

        System.out.printf("%d event(s) loaded", entrants.size());
    }

    /*
    * Currently for some reason doesn't filter
     */
    public void applyFilter(){
        filteredList.clear();
        if(query!=null && !query.isEmpty()){
            for (Entrant entrant : entrants) {

                boolean matchesKeyword = true;

                String name = entrant.getProfile().getName();
                String email = entrant.getProfile().getEmail();
                String phone = entrant.getProfile().getPhone();
                boolean matchName = name != null && name.toLowerCase().contains(query.toLowerCase());
                boolean matchEmail = email != null && email.toLowerCase().contains(query.toLowerCase());
                boolean matchPhone = phone != null && phone.toLowerCase().contains(query.toLowerCase());
                matchesKeyword = matchName || matchEmail || matchPhone;

                if (matchesKeyword) {
                    filteredList.add(entrant);
                }
        }
        }else
        {
            filteredList.addAll(entrants);
        }

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
