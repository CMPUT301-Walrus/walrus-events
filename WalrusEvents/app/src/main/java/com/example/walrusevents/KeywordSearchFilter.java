package com.example.walrusevents;

import android.widget.Filter;

import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.MainSEventArrayAdapter;

import java.util.ArrayList;

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
                if(originalList.get(i).getTitle().toLowerCase().contains(constraint)){
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
