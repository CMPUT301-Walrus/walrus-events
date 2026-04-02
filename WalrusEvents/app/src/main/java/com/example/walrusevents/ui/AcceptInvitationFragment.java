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

public class AcceptInvitationFragment extends DialogFragment {
    public interface AcceptInvitationListener {
        void acceptInvite();
        void declineInvite();
    }

    private AcceptInvitationListener listener;
    private boolean invited;

    private void setListener(AcceptInvitationListener listener) {
        this.listener = listener;
    }

    private void setInvited(boolean invited) {
        this.invited = invited;
    }
    public static AcceptInvitationFragment newInstance(AcceptInvitationListener listener, boolean invited){
        AcceptInvitationFragment fragment = new AcceptInvitationFragment();
        fragment.setInvited(invited);
        fragment.setListener(listener);
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

        if (invited) {
            acceptButton.setOnClickListener(v -> {
                listener.acceptInvite();
                dismiss();
            });
            declineButton.setOnClickListener(v -> {
                listener.declineInvite();
                dismiss();
            });
        }
        else {
            TextView invitedText = view.findViewById(R.id.invitationText);
            invitedText.setText("Not Selected");
            acceptButton.setVisibility(INVISIBLE);
            declineButton.setVisibility(INVISIBLE);
            laterButton.setText("OK");
        }
    }
}
