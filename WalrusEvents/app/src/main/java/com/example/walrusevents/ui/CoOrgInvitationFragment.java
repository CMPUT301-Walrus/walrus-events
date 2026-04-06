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

public class CoOrgInvitationFragment extends DialogFragment {
    private static final String ARG_STATUS = "status";
    private static final String ARG_HEADER_TEXT = "header_text";

    public interface AcceptInvitationListener {
        void acceptInvite();
        void declineInvite();
    }

    private CoOrgInvitationFragment.AcceptInvitationListener listener;

    public static CoOrgInvitationFragment newInstance(CoOrgInvitationFragment.AcceptInvitationListener listener) {
        CoOrgInvitationFragment fragment = new CoOrgInvitationFragment();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(CoOrgInvitationFragment.AcceptInvitationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.co_org_invitation_popup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button laterButton = view.findViewById(R.id.co_org_later_button);
        laterButton.setOnClickListener(v -> {
            dismiss();
        });
        Button acceptButton = view.findViewById(R.id.co_org_accept_invite_button);
        Button declineButton = view.findViewById(R.id.co_org_decline_invite_button);

        acceptButton.setOnClickListener(v -> {
            listener.acceptInvite();
            dismiss();
        });
        declineButton.setOnClickListener(v -> {
            listener.declineInvite();
            dismiss();
        });
    }
}
