package com.example.walrusevents.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.R;

import java.util.ArrayList;

public class EntrantArrayAdapter extends ArrayAdapter<Entrant> {
    private ArrayList<Entrant> entrants;
    private Context context;
    private Entrant entrant;
    /**
     * Constructor for organizer event array adapter
     * @param context
     * @param entrants
     */
    public EntrantArrayAdapter(@NonNull Context context, ArrayList<Entrant> entrants) {
        super(context, 0, entrants);
        this.entrants = entrants;
        this.context = context;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.waitlist_entry, parent, false);
        }

        Entrant entrant = entrants.get(position);
        ImageView profilePicture = view.findViewById(R.id.profilePicture);
        TextView profileName = view.findViewById(R.id.profileName);

        //TODO: display the corresponding profile picture
        profileName.setText(entrant.getProfile().getName());

        return view;
    }
}
