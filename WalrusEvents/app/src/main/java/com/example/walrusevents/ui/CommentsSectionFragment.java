package com.example.walrusevents.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.CommentsAdapter;
import com.example.walrusevents.util.DeviceIdManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class CommentsSectionFragment extends BottomSheetDialogFragment
        implements AddCommentFragment.AddCommentListener, EventRepository.CommentListCallback {
    private Event eventModel;
    private ArrayList<Comment> commentsList;
    private CommentsAdapter commentsAdapter;
    private FragmentManager fragmentManager;
    private EventRepository eventRepository;
    private String parentId;

    public static CommentsSectionFragment newInstance(Activity context, Event eventModel, String parentId, FragmentManager fragmentManager){
        CommentsSectionFragment fragment = new CommentsSectionFragment();
        fragment.setEventModel(eventModel);
        fragment.setFragmentManager(fragmentManager);
        fragment.setParentId(parentId);
        return fragment;
    }

    private void setParentId(String parentId) {
        this.parentId = parentId;
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
        ListView commentsView = view.findViewById(R.id.comments_section_view);
        Button addCommentButton = view.findViewById(R.id.add_comment_button);
        ImageView closeButton = view.findViewById(R.id.comments_close_button);
        eventRepository = new EventRepository();

        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(getActivity(), commentsList, eventModel.getEventId(), this);
        commentsView.setAdapter(commentsAdapter);

        eventRepository.initiateGetCommentsFromEvent(eventModel.getEventId(), parentId, this);

        addCommentButton.setOnClickListener(v -> {
            addComment(null);
        });

        closeButton.setOnClickListener(v -> {
            dismiss();
        });
    }

    /**
     * Opens up the popup for the user to write down their comment
     */
    @Override
    public void addComment(Comment parent) {
        AddCommentFragment addCommentFragment = AddCommentFragment.newInstance(this, parent);
        addCommentFragment.show(fragmentManager, "Add Comment");
    }

    /**
     * Update the event in the database to add the user's comment
     * @param bodyText
     * The text that the user wrote as a comment
     */
    @Override
    public void postComment(Comment parent, String bodyText) {
        if (parent != null) {
            Comment comment = new Comment(parent.getCommentId(), DeviceIdManager.getOrCreate(getActivity()), bodyText, 0, 0);
            eventRepository.addComment(eventModel.getEventId(), comment);
            commentsList.add(comment);
        }
        else {
            Comment comment = new Comment(null, DeviceIdManager.getOrCreate(getActivity()), bodyText, 0, 0);
            eventRepository.addComment(eventModel.getEventId(), comment);
        }

        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCommentsLoaded(ArrayList<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        commentsList.addAll(comments);
        eventRepository.getNextCommentBatch(eventModel.getEventId(), parentId, this);
        commentsAdapter.notifyDataSetChanged();
    }
}
