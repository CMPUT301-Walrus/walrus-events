package com.example.walrusevents.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.ui.AddCommentFragment;

import java.util.ArrayList;
import java.util.Locale;

public class CommentsAdapter extends ArrayAdapter<Comment>
        implements ProfileRepository.ProfileCallback {
    private Context context;
    private String eventId;
    private ArrayList<Comment> commentsList;
    private EventRepository eventRepository;
    private AddCommentFragment.AddCommentListener addCommentListener;

    public CommentsAdapter(@NonNull Context context, ArrayList<Comment> comments, String eventId, AddCommentFragment.AddCommentListener addCommentListener) {
        super(context, 0, comments);
        this.eventId = eventId;
        this.commentsList = comments;
        this.context = context;
        this.addCommentListener = addCommentListener;
        eventRepository = new EventRepository();
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        }

        Comment comment = commentsList.get(position);
        TextView likesCounter = view.findViewById(R.id.likes_counter);
        TextView dislikesCounter = view.findViewById(R.id.dislikes_counter);
        TextView bodyText = view.findViewById(R.id.comment_body);
        Button replyButton = view.findViewById(R.id.reply_button);
        ListView repliesView = view.findViewById(R.id.replies_view);

        ArrayList<Comment> replies = new ArrayList<>();
        CommentsAdapter repliesAdapter = new CommentsAdapter(context, replies, eventId, addCommentListener);
        repliesView.setAdapter(repliesAdapter);

        EventRepository eventRepository = new EventRepository();

        eventRepository.initiateGetCommentsFromEvent(eventId, comment.getCommentId(), new EventRepository.CommentListCallback() {
            @Override
            public void onCommentsLoaded(ArrayList<Comment> comments) {
                if (comments != null && !comments.isEmpty()) {
                    replies.addAll(comments);
                    eventRepository.getNextCommentBatch(eventId, comment.getCommentId(), this);
                }
                else {
                    repliesAdapter.notifyDataSetChanged();
                }
            }
        });

        replyButton.setOnClickListener(v -> {
            addCommentListener.addComment(comment);
            repliesAdapter.notifyDataSetChanged();
        });

        bodyText.setText(comment.getBody());
        likesCounter.setText(String.format(Locale.CANADA, "%d", comment.getLikes()));
        dislikesCounter.setText(String.format(Locale.CANADA, "%d", comment.getDislikes()));

        return view;
    }

    @Override
    public void onEntrantLoaded(Entrant entrant) {
        String entrantName = entrant.getProfile().getName();
    }
}
