package com.example.walrusevents.util;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walrusevents.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView likesCounter = itemView.findViewById(R.id.likes_counter);
    TextView dislikesCounter = itemView.findViewById(R.id.dislikes_counter);
    TextView bodyText = itemView.findViewById(R.id.comment_body);
    Button replyButton = itemView.findViewById(R.id.reply_button);
    RecyclerView repliesView = itemView.findViewById(R.id.replies_view);
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        likesCounter = itemView.findViewById(R.id.likes_counter);
        dislikesCounter = itemView.findViewById(R.id.dislikes_counter);
        bodyText = itemView.findViewById(R.id.comment_body);
        replyButton = itemView.findViewById(R.id.reply_button);
        repliesView = itemView.findViewById(R.id.replies_view);
    }

    public TextView getLikesCounter() {
        return likesCounter;
    }

    public TextView getDislikesCounter() {
        return dislikesCounter;
    }

    public TextView getBodyText() {
        return bodyText;
    }

    public Button getReplyButton() {
        return replyButton;
    }

    public RecyclerView getRepliesView() {
        return repliesView;
    }
}
