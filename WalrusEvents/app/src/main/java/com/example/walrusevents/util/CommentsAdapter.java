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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.ui.AddCommentFragment;

import java.util.ArrayList;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentViewHolder>
        implements ProfileRepository.ProfileCallback {
    private Context context;
    private String eventId;
    private ArrayList<Comment> commentsList;
    private EventRepository eventRepository;
    private AddCommentFragment.AddCommentListener addCommentListener;

    public CommentsAdapter(@NonNull Context context, ArrayList<Comment> comments, String eventId, AddCommentFragment.AddCommentListener addCommentListener) {
        this.eventId = eventId;
        this.commentsList = comments;
        this.context = context;
        this.addCommentListener = addCommentListener;
        eventRepository = new EventRepository();
    }

    @Override
    public void onEntrantLoaded(Entrant entrant) {
        String entrantName = entrant.getProfile().getName();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);

        ArrayList<Comment> replies = new ArrayList<>();
        CommentsAdapter repliesAdapter = new CommentsAdapter(context, replies, eventId, addCommentListener);


        holder.getRepliesView().setAdapter(repliesAdapter);


        EventRepository eventRepository = new EventRepository();

        eventRepository.initiateGetCommentsFromEvent(eventId, comment.getCommentId(), new EventRepository.CommentListCallback() {
            @Override
            public void onCommentsLoaded(ArrayList<Comment> comments) {
                if (comments != null && !comments.isEmpty()) {
                    int prevSize = replies.size();
                    replies.addAll(comments);
                    repliesAdapter.notifyDataSetChanged();
                    eventRepository.getNextCommentBatch(eventId, comment.getCommentId(), this);
                }
                else {
                    holder.getRepliesView().setLayoutManager(new LinearLayoutManager(context));
                }
            }
        });
        holder.getReplyButton().setOnClickListener(v -> {
            addCommentListener.addComment(comment);
        });

        holder.getBodyText().setText(comment.getBody());
        holder.getLikesCounter().setText(String.format(Locale.CANADA, "%d", comment.getLikes()));
        holder.getDislikesCounter().setText(String.format(Locale.CANADA, "%d", comment.getDislikes()));
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}
