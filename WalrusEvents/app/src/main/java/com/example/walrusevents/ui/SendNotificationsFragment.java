package com.example.walrusevents.ui;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.walrusevents.R;
import com.example.walrusevents.activity.OEventPoolActivity;
import com.example.walrusevents.controllers.NotificationsController;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.model.Notification;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class SendNotificationsFragment extends DialogFragment {
    private Notification.NotificationTarget targetGroup;
    private Event eventModel;
    private Context context;
    private TextView targetText;
    private TextView targetDescription;

    public SendNotificationsFragment(Event eventModel, Context context) {
        this.eventModel = eventModel;
        this.context = context;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_notifs_p2, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button confirmButton = view.findViewById(R.id.confirm_send_notif_button);
        ImageView cancelButton = view.findViewById(R.id.back_send_notif_p2);
        targetText = view.findViewById(R.id.send_notif_category);
        targetDescription = view.findViewById(R.id.send_notif_description);
        TextInputEditText messageEditText = view.findViewById(R.id.send_notifs_edit_text);

        targetGroup = Notification.NotificationTarget.CANCELED; //Default target group set to canceled

        cancelButton.setOnClickListener(v -> dismiss());

        targetText.setOnClickListener(v -> nextTargetGroup());

        confirmButton.setOnClickListener(v -> {

            if (messageEditText.getText() == null || messageEditText.getText().toString().isBlank()) {
                Toast.makeText(context, "No message set, please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            String message = messageEditText.getText().toString();
            String title = String.format(Locale.getDefault(), "%s Update", eventModel.getTitle());

            NotificationsController notificationsController = new NotificationsController();
            notificationsController.sendNotifications(context, eventModel.getEventId(), title, message, targetGroup);

            dismiss();
        });
    }

    private void nextTargetGroup() {
        switch (targetGroup) {
            case ALL:
                targetText.setText("Selected Entrants");
                targetDescription.setText("(Entrants sampled to be invited to the event)");
                targetGroup = Notification.NotificationTarget.SELECTED;
                break;
            case SELECTED:
                targetText.setText("Non-selected Entrants");
                targetDescription.setText("(Entrants not sampled to be invited to the event)");
                targetGroup = Notification.NotificationTarget.NOT_SELECTED;
                break;
            case NOT_SELECTED:
                targetText.setText("Canceled Entrants");
                targetDescription.setText("(Entrants that you remove or cancel their invitation)");
                targetGroup = Notification.NotificationTarget.CANCELED;
                break;
            case CANCELED:
                targetText.setText("All Entrants");
                targetDescription.setText("(Send to all entrants that took interest in the event)");
                targetGroup = Notification.NotificationTarget.ALL;
                break;
        }
    }
}
