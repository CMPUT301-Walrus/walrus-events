package com.example.walrusevents.ui;

import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.walrusevents.R;
import com.example.walrusevents.model.WaitlistEntry;

public class AcceptInvitationFragment extends DialogFragment {
    public interface AcceptInvitationListener {
        void acceptInvite();
        void declineInvite();
    }

    private AcceptInvitationListener listener;
    private WaitlistEntry.Status status;
    private String headerText;

    private void setListener(AcceptInvitationListener listener) {
        this.listener = listener;
    }
    private void setInviteStatus(WaitlistEntry.Status status) {
        this.status = status;
    }
    private void setHeaderText(String headerText) {
        this.headerText = headerText;
    }
    public static AcceptInvitationFragment newInstance(AcceptInvitationListener listener, WaitlistEntry.Status status, String titleText){
        AcceptInvitationFragment fragment = new AcceptInvitationFragment();
        fragment.setInviteStatus(status);
        fragment.setListener(listener);
        fragment.setHeaderText(titleText);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.accept_invitation_popup, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button laterButton = view.findViewById(R.id.laterInviteButton);
        laterButton.setOnClickListener(v -> {
            dismiss();
        });
        Button acceptButton = view.findViewById(R.id.acceptInviteButton);
        Button declineButton = view.findViewById(R.id.declineInviteButton);
        TextView headerView = view.findViewById(R.id.invitation_header);

        headerView.setText(headerText);

        TextView invitedText = view.findViewById(R.id.invitationText);
        TextView disclaimerText = view.findViewById(R.id.invite_disclaimer);

        switch (status) {
            case INVITED:
                acceptButton.setOnClickListener(v -> {
                    listener.acceptInvite();
                    dismiss();
                });
                declineButton.setOnClickListener(v -> {
                    listener.declineInvite();
                    dismiss();
                });
                break;
            case NOT_CHOSEN:
                invitedText.setText("Not Selected");
                disclaimerText.setText("You may still be chosen if another entrant declines");
                acceptButton.setVisibility(INVISIBLE);
                declineButton.setVisibility(INVISIBLE);
                laterButton.setText("OK");
                break;
            case PENDING:
                invitedText.setText("Awaiting Organizer Action");
                disclaimerText.setText("Organizer still needs to initiate the lottery");
                acceptButton.setVisibility(INVISIBLE);
                declineButton.setVisibility(INVISIBLE);
                laterButton.setText("OK");
                break;
        }
    }
}
