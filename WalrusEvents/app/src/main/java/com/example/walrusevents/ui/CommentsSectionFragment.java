package com.example.walrusevents.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.walrusevents.EventRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.CommentsAdapter;
import com.example.walrusevents.util.DeviceIdManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class CommentsSectionFragment extends BottomSheetDialogFragment implements AddCommentFragment.AddCommentListener {
    private Event eventModel;
    private ArrayList<Comment> commentsList;
    private CommentsAdapter commentsAdapter;
    private FragmentManager fragmentManager;


    public static CommentsSectionFragment newInstance(Activity context, Event eventModel, FragmentManager fragmentManager){
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
        ListView commentsView = view.findViewById(R.id.comments_section_view);
        Button addCommentButton = view.findViewById(R.id.add_comment_button);
        ImageView closeButton = view.findViewById(R.id.comments_close_button);


        commentsList = eventModel.getComments();

        commentsAdapter = new CommentsAdapter(getActivity(), commentsList, this);
        commentsView.setAdapter(commentsAdapter);

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
        Comment comment = new Comment(DeviceIdManager.getOrCreate(getActivity()), bodyText, 0, 0);
        EventRepository eventRepository = new EventRepository();
        eventModel.addComment(parent, comment);
        eventRepository.setEvent(eventModel);
        commentsAdapter.notifyDataSetChanged();
    }
}
