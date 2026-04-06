/**
 * This class manages the comments for an event
 * It retrieves and stores information about the comments to make storing and retrieving comments easier
 */

package com.example.walrusevents.util;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walrusevents.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView nameText;
    TextView likesCounter;
    Button likeButton;
    TextView bodyText;
    Button replyButton;
    RecyclerView repliesView;
    TextView viewRepliesButton;
    TextView hideRepliesButton;
    ImageView contextMenuButton;
    View divider;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        nameText = itemView.findViewById(R.id.comment_name);
        likesCounter = itemView.findViewById(R.id.likes_counter);
        likeButton = itemView.findViewById(R.id.comment_like_button);
        bodyText = itemView.findViewById(R.id.comment_body);
        replyButton = itemView.findViewById(R.id.reply_button);
        repliesView = itemView.findViewById(R.id.replies_view);
        viewRepliesButton = itemView.findViewById(R.id.comment_view_replies_button);
        hideRepliesButton = itemView.findViewById(R.id.comment_hide_replies_button);
        contextMenuButton = itemView.findViewById(R.id.context_menu_button);
        divider = itemView.findViewById(R.id.comment_divider);
    }

    public TextView getNameText() {
        return nameText;
    }

    public TextView getLikesCounter() {
        return likesCounter;
    }

    public Button getLikeButton() {
        return likeButton;
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

    public TextView getViewRepliesButton() {
        return viewRepliesButton;
    }

    public TextView getHideRepliesButton() {
        return hideRepliesButton;
    }

    public ImageView getContextMenuButton() {
        return contextMenuButton;
    }

    public View getDivider() {
        return divider;
    }
}
