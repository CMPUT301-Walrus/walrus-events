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
    private String entrantId;
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
        entrantId = DeviceIdManager.getOrCreate(context);
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

        comment.initializeLiked(context);

        ArrayList<Comment> replies = new ArrayList<>();
        CommentsAdapter repliesAdapter = new CommentsAdapter(context, replies, eventId, addCommentListener);

        holder.getRepliesView().setAdapter(repliesAdapter);

        EventRepository eventRepository = new EventRepository();

        eventRepository.initiateGetCommentsFromEvent(eventId, comment.getCommentId(), new EventRepository.CommentListCallback() {
            @Override
            public void onCommentsLoaded(ArrayList<Comment> comments) {
                if (comments != null && !comments.isEmpty()) {
                    holder.getViewRepliesButton().setVisibility(View.VISIBLE);
                    holder.getDivider().setVisibility(View.VISIBLE);

                    int prevSize = replies.size();
                    replies.addAll(comments);
                    repliesAdapter.notifyItemRangeInserted(prevSize, replies.size());
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

        holder.getLikeButton().setOnClickListener(v -> {
            comment.toggleLike(DeviceIdManager.getOrCreate(context));
            holder.getLikesCounter().setText(String.format(Locale.CANADA, "%d", comment.getTotalLikes()));
            addCommentListener.updateComment(comment);
        });

        holder.getViewRepliesButton().setOnClickListener(v -> {
            holder.getRepliesView().setVisibility(View.VISIBLE);
            holder.getHideRepliesButton().setVisibility(View.VISIBLE);
            holder.getViewRepliesButton().setVisibility(View.GONE);
        });

        holder.getHideRepliesButton().setOnClickListener(v -> {
            holder.getRepliesView().setVisibility(View.GONE);
            holder.getHideRepliesButton().setVisibility(View.GONE);
            holder.getViewRepliesButton().setVisibility(View.VISIBLE);
        });
        holder.getBodyText().setText(comment.getBody());
        holder.getLikesCounter().setText(String.format(Locale.CANADA, "%d", comment.getTotalLikes()));
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}
