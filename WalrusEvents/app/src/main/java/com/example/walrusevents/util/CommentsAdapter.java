package com.example.walrusevents.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Entrant;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommentsAdapter extends ArrayAdapter<Comment> implements ProfileRepository.ProfileCallback {
    private Context context;
    private ArrayList<Comment> comments;

    public CommentsAdapter(@NonNull Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        }

        Comment comment = comments.get(position);
        TextView likesCounter = view.findViewById(R.id.likes_counter);
        TextView dislikesCounter = view.findViewById(R.id.dislikes_counter);

        likesCounter.setText(comment.getLikes());
        dislikesCounter.setText(comment.getDislikes());

        return view;
    }

    @Override
    public void onEntrantLoaded(Entrant entrant) {
        String entrantName = entrant.getProfile().getName();
    }
}
