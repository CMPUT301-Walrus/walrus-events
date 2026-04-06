/**
 * This fragment pops up when an organizer wants to send notifications to other users about their event
 */

package com.example.walrusevents.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.walrusevents.R;
import com.example.walrusevents.activity.OEventPoolActivity;
import com.example.walrusevents.controllers.OEventPoolController;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class SendNotificationsFragmentCont extends Fragment {
    private Button sendButton;
    private TextInputEditText canceledEditText;
    private Context context;
    private OEventPoolController controller;
    private Event eventModel;
    private OEventPoolActivity activity;
    public SendNotificationsFragmentCont(Context context, OEventPoolController controller, Event eventModel, OEventPoolActivity activity) {
        this.context = context;
        this.controller = controller;
        this.eventModel = eventModel;
        this.activity = activity;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_notifs_p1, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sendButton = view.findViewById(R.id.confirm_send_notif_button);
        canceledEditText = view.findViewById(R.id.canceled_notifs_edit_text);

        canceledEditText.setText(String.format(Locale.getDefault(), "You've been removed from %s", eventModel.getTitle()));

        sendButton.setOnClickListener(v -> {
            WaitlistRepository collectForLottery = new WaitlistRepository();
            collectForLottery.getAllEntries(eventModel.getEventId(), entries -> {
                activity.doLottery(entries);
            });
        });
    }
}
