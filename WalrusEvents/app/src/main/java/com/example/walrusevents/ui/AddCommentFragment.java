package com.example.walrusevents.ui;

import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.walrusevents.R;
import com.example.walrusevents.model.Comment;
import com.google.android.material.textfield.TextInputEditText;

public class AddCommentFragment extends DialogFragment {

    public interface AddCommentListener {
        void addComment(Comment parent);
        void postComment(Comment parent, String bodyText);
    }

    private Comment parent;
    private AddCommentListener listener;

    public void setListener(AddCommentListener listener) {
        this.listener = listener;
    }

    public static AddCommentFragment newInstance(AddCommentListener listener, Comment parent){
        AddCommentFragment fragment = new AddCommentFragment();
        fragment.setParent(parent);
        fragment.setListener(listener);
        return fragment;
    }

    private void setParent(Comment parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.write_comment_popup, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button postButton = view.findViewById(R.id.comment_post_button);
        Button cancelButton = view.findViewById(R.id.comment_cancel_button);
        TextInputEditText commentBody = view.findViewById(R.id.edit_comment_body);

        postButton.setEnabled(false);
        postButton.setOnClickListener(v -> {
            listener.postComment(parent, commentBody.getText().toString());
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        commentBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String bodyText = s.toString();
                if (!bodyText.isBlank()) {
                    postButton.setEnabled(true);
                }
                else {
                    postButton.setEnabled(false);
                }
            }
        });
    }
}
