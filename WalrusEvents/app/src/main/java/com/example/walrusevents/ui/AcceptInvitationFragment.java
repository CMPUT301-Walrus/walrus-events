/**
 * This fragment is responsible for popping up when an entrant gets prompted to accept an invite
 * It allows the entrant to accept or decline
 * Handles logic for communcating to the rest of the app about said decision
 */

package com.example.walrusevents.ui;

import static android.view.View.INVISIBLE;

import android.content.Context;
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
    private static final String ARG_STATUS = "status";
    private static final String ARG_HEADER_TEXT = "header_text";

    public interface AcceptInvitationListener {
        void acceptInvite();
        void declineInvite();
    }

    private AcceptInvitationListener listener;

    public static AcceptInvitationFragment newInstance(WaitlistEntry.Status status, String titleText) {
        AcceptInvitationFragment fragment = new AcceptInvitationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status.name());
        args.putString(ARG_HEADER_TEXT, titleText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof AcceptInvitationListener)) {
            throw new IllegalStateException("Host must implement AcceptInvitationListener");
        }
        listener = (AcceptInvitationListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.accept_invitation_popup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = requireArguments();
        WaitlistEntry.Status status = WaitlistEntry.Status.valueOf(args.getString(ARG_STATUS));
        String headerText = args.getString(ARG_HEADER_TEXT, "Lottery Result");

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
