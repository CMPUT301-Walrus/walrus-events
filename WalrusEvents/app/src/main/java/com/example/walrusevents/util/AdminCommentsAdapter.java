/**
 * This adapter is responsible for changing the comments in the admin view
 * It handles the logic for adjusting the collectino of comments as an admin makes changes
 */

package com.example.walrusevents.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walrusevents.R;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.ui.AddCommentFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class AdminCommentsAdapter extends ArrayAdapter<Comment> {
    private Context context;
    private ArrayList<Comment> commentsList;
    private ArrayList<String> linkedEvents;

    public AdminCommentsAdapter(@NonNull Context context, ArrayList<Comment> comments, ArrayList<String> linkedEvents) {
        super(context, 0, comments);
        this.commentsList = comments;
        this.linkedEvents = linkedEvents;
        this.context = context;
    }

    public void setCommentName(Entrant entrant, TextView nameTextView) {
        String displayedName;
        if (entrant == null) {
            displayedName = "Deleted User";
        }
        else {
            displayedName = entrant.getProfile().getName();
        }

        nameTextView.setText(displayedName);
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        }
        Comment comment = commentsList.get(position);
        TextView nameText = view.findViewById(R.id.comment_name);
        ImageView contextMenuButton = view.findViewById(R.id.context_menu_button);
        TextView bodyText = view.findViewById(R.id.comment_body);
        TextView likesCounter = view.findViewById(R.id.likes_counter);

        ProfileRepository profileRepository = new ProfileRepository();
        profileRepository.getProfile(comment.getEntrantId(), entrant ->
                setCommentName(entrant, nameText));

        EventRepository eventRepository = new EventRepository();

        contextMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, contextMenuButton);

            popupMenu.getMenuInflater().inflate(R.menu.comment_context_popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.comment_context_delete) {
                    eventRepository.deleteComment(linkedEvents.get(position), comment.getCommentId());
                    commentsList.remove(position);
                    linkedEvents.remove(position);
                    notifyDataSetChanged();
                }
                return true;
            });

            popupMenu.show();
        });

        bodyText.setText(comment.getBody());
        likesCounter.setText(String.format(Locale.CANADA, "%d", comment.getTotalLikes()));
        return view;
    }
}
