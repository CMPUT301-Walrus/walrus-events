/**
 * This class allows the user to search for events by keyword
 * It handles the logic for querying the database for events that contain the keyword
 */

package com.example.walrusevents.util;

import android.widget.Filter;

import com.example.walrusevents.model.Event;

import java.util.ArrayList;

/*
* Class that adds keword search filter functionality
*
 */
public class KeywordSearchFilter extends Filter {
    ArrayList<Event> originalList;
    MainSEventArrayAdapter adapter;

    public KeywordSearchFilter(ArrayList<Event> filteredList, MainSEventArrayAdapter adapter) {
        this.originalList = filteredList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint=constraint.toString().toLowerCase();

            ArrayList<Event> filteredList = new ArrayList<>();
            for(int i=0;i<originalList.size();i++){
                if(originalList.get(i).getTitle().toLowerCase().contains(constraint)||originalList.get(i).getDescription().toLowerCase().contains(constraint)){
                    filteredList.add(originalList.get(i));
                }
            }
            filterResults.count=filteredList.size();
            filterResults.values=filteredList;

        }else{
            filterResults.count=originalList.size();
            filterResults.values=originalList;
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.clear();
        adapter.addAll((ArrayList<Event>) results.values);
        adapter.notifyDataSetChanged();
    }
}
