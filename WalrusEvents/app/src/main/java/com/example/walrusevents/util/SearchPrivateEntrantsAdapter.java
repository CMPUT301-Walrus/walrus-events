/**
 * This adapter manages the collection of all entants for a partifulcar event
 * In particular it is repsonsible for managing entrants for private events
 */

package com.example.walrusevents.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.walrusevents.model.Entrant;

import java.util.ArrayList;

public class SearchPrivateEntrantsAdapter extends ArrayAdapter<Entrant> {
    private ArrayList<Entrant> entrants;
    private Context context;
    private Entrant entrant;
    /**
     * Constructor for organizer event array adapter
     * @param context
     * @param entrants
     */
    public SearchPrivateEntrantsAdapter(@NonNull Context context, ArrayList<Entrant> entrants) {
        super(context, 0, entrants);
        this.entrants = entrants;
        this.context = context;
    }


}
