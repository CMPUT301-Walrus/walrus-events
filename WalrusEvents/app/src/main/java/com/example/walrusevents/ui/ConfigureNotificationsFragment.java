package com.example.walrusevents.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.walrusevents.R;
import com.example.walrusevents.activity.OEventPoolActivity;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.google.android.material.textfield.TextInputEditText;

public class ConfigureNotificationsFragment extends DialogFragment {
    private Event eventModel;
    private OEventPoolActivity activity;

    public ConfigureNotificationsFragment(Event eventModel, OEventPoolActivity activity) {
        this.eventModel = eventModel;
        this.activity = activity;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_notifs_p1, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button nextButton = view.findViewById(R.id.next_notif_button);
        Button cancelButton = view.findViewById(R.id.cancel_notif_button1);
        TextInputEditText selectedEditText = view.findViewById(R.id.selected_notif_edit_text);
        TextInputEditText nonSelectedEditText = view.findViewById(R.id.non_select_notif_edit_text);

        selectedEditText.setText("You've been selected!");
        nonSelectedEditText.setText("You were not selected");

        cancelButton.setOnClickListener(v -> dismiss());

        nextButton.setOnClickListener(v -> {
            activity.setNotificationMessages(selectedEditText.getText().toString(), nonSelectedEditText.getText().toString());
            WaitlistRepository collectForLottery = new WaitlistRepository();
            collectForLottery.getAllEntries(eventModel.getEventId(), entries -> {
                activity.doLottery(entries);
            });
            dismiss();
        });
    }
}
