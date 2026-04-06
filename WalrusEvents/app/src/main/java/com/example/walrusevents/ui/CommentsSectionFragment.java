/**
 * This fragment pops up to hold the comments related to a partiuclar event
 * It shows all comments and allows users to add/edit comments
 */

package com.example.walrusevents.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walrusevents.activity.OEventEditActivity;
import com.example.walrusevents.activity.QRCodeActivity;
import com.example.walrusevents.activity.UEventDetailsActivity;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.AddCommentFragment;
import com.example.walrusevents.util.CommentsAdapter;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.UserRole;
import com.example.walrusevents.util.UserRoleManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class CommentsSectionFragment extends BottomSheetDialogFragment
        implements AddCommentFragment.AddCommentListener, EventRepository.CommentListCallback {
    private Event eventModel;
    private ArrayList<Comment> commentsList;
    private CommentsAdapter commentsAdapter;
    private FragmentManager fragmentManager;
    private EventRepository eventRepository;
    private RecyclerView.LayoutManager layoutManager;

    public static CommentsSectionFragment newInstance(Event eventModel, FragmentManager fragmentManager){
        CommentsSectionFragment fragment = new CommentsSectionFragment();
        fragment.setEventModel(eventModel);
        fragment.setFragmentManager(fragmentManager);
        return fragment;
    }

    private void setEventModel(Event eventModel) {
        this.eventModel = eventModel;
    }

    private void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comments_section, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView commentsView = view.findViewById(R.id.comments_section_view);
        Button addCommentButton = view.findViewById(R.id.add_comment_button);
        ImageView closeButton = view.findViewById(R.id.comments_close_button);
        ImageView contextMenuButton = view.findViewById(R.id.context_menu_button);
        eventRepository = new EventRepository();

        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(getActivity(),
                commentsList,
                eventModel.getEventId(),
                this,
                eventModel.getOwners());
        commentsView.setAdapter(commentsAdapter);
        commentsView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventRepository.initiateGetCommentsFromEvent(eventModel.getEventId(), null, this);

        addCommentButton.setOnClickListener(v -> {
            addComment(null, commentsAdapter);
        });

        closeButton.setOnClickListener(v -> {
            dismiss();
        });

    }

    /**
     * Opens up the popup for the user to write down their comment
     */
    @Override
    public void addComment(String parentId, CommentsAdapter adapter) {
        AddCommentFragment addCommentFragment = AddCommentFragment.newInstance(parentId, this, adapter);
        addCommentFragment.show(fragmentManager, "Add Comment");
    }

    /**
     * Update the event in the database to add the user's comment
     * @param bodyText
     * The text that the user wrote as a comment
     */
    @Override
    public void postComment(String parentId, CommentsAdapter adapter, String bodyText) {
        if (parentId != null) {
            Comment comment = new Comment(parentId, DeviceIdManager.getOrCreate(getActivity()), bodyText, new ArrayList<>());
            eventRepository.addComment(eventModel.getEventId(), comment);
            adapter.notifyDataSetChanged();;
        }
        else {
            Comment comment = new Comment(null, DeviceIdManager.getOrCreate(getActivity()), bodyText, new ArrayList<>());
            eventRepository.addComment(eventModel.getEventId(), comment);
            commentsList.add(comment);
            commentsAdapter.notifyItemInserted(commentsList.size() - 1);
        }


    }

    @Override
    public void updateComment(Comment comment) {
        eventRepository.setComment(eventModel.getEventId(), comment);
    }

    @Override
    public void onCommentsLoaded(ArrayList<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        commentsList.addAll(comments);
        eventRepository.getNextCommentBatch(eventModel.getEventId(), null, this);
        commentsAdapter.notifyDataSetChanged();
    }
}
