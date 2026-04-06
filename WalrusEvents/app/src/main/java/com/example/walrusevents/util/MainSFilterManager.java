package com.example.walrusevents.util;

import android.util.Log;

import com.example.walrusevents.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

/*
* Manages the filters for the Listview in the MainActivity
* keyword search filter, capacity filter, availability filter
*
* Currently : Availability is based on
 */
public class MainSFilterManager {
    private ArrayList<Event> originalList;
    private ArrayList<Event> filteredList;
    private LocalDateTime selectedStartTime=null;
    private LocalDateTime selectedEndTime=null;
    private String keyword="";
    private boolean onlyOpenSeats=false;

    private MainSEventArrayAdapter arrayAdapter;

    public MainSFilterManager(ArrayList<Event> eventList, MainSEventArrayAdapter mainScreenAdapter){
        this.originalList=new ArrayList<>(eventList);
        this.filteredList=new ArrayList<Event>();
        this.arrayAdapter=mainScreenAdapter;
    }

    public ArrayList<Event> getFilteredList(){
        return filteredList;
    }

    public ArrayList<Event> getOriginalList(){return originalList;}

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        applyFilters();
    }

    public void setOnlyOpenSeats(boolean onlyOpenSeats) {
        this.onlyOpenSeats = onlyOpenSeats;
        applyFilters();
    }

    public void updateData(ArrayList<Event> newEvents) {
        originalList.clear();
        originalList.addAll(newEvents);
        applyFilters();
    }

    public void setSelectedRange(LocalDateTime start, LocalDateTime end){
        this.selectedStartTime=start;
        this.selectedEndTime=end;
        applyFilters();
    }

    public void applyFilters() {
        ArrayList<Event> nextFilteredList = new ArrayList<>();
        if (isWithoutFilters()) {
            nextFilteredList.addAll(originalList);
        } else {
            for (Event event : originalList) {
                // Keyword filter
                boolean matchesKeyword = true;
                boolean matchesSeats = true;
                boolean matchesAvailability = true;

                if (keyword != null && !keyword.isEmpty()) {
                    matchesKeyword =
                            event.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                    event.getDescription().toLowerCase().contains(keyword.toLowerCase());
                }

                // Capacity filter
                if (onlyOpenSeats) {
                    matchesSeats = event.hasOpenSeats();
                }

                //Availability Filter
                if (selectedStartTime != null && selectedEndTime != null) {
                    if (event.getStartRegistrationTime() != null && event.getEndRegistrationTime() != null) {
                        LocalDateTime eventStartTime = LocalDateTime.parse(event.getStartRegistrationTime());
                        LocalDateTime eventEndTime = LocalDateTime.parse(event.getEndRegistrationTime());

                        matchesAvailability =
                                !eventEndTime.isBefore(selectedStartTime) &&
                                        !eventStartTime.isAfter(selectedEndTime);
                    } else {
                        matchesAvailability = false;
                    }
                }

                if (matchesKeyword && matchesSeats && matchesAvailability) {
                    nextFilteredList.add(event);
                }
            }
        }

        filteredList = nextFilteredList;
        arrayAdapter.clear();
        arrayAdapter.addAll(filteredList);
        arrayAdapter.notifyDataSetChanged();
        Log.d("FilteredList", filteredList.toString());
    }

    public boolean isWithoutFilters(){

        return (keyword == null || keyword.isEmpty()) && selectedStartTime == null && selectedEndTime == null && !onlyOpenSeats;
    }

    public void resetFilters(){
        this.keyword="";
        this.selectedStartTime=null;
        this.selectedEndTime=null;
        this.onlyOpenSeats=false;
    }
}
