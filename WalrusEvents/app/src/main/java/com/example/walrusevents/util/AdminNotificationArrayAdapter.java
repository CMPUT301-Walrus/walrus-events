package com.example.walrusevents.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walrusevents.R;
import com.example.walrusevents.model.Notification;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminNotificationArrayAdapter extends ArrayAdapter<Notification> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault());

    public AdminNotificationArrayAdapter(@NonNull Context context, @NonNull List<Notification> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification, parent, false);
        }

        Notification notification = getItem(position);

        TextView title = convertView.findViewById(R.id.notif_title);
        TextView message = convertView.findViewById(R.id.notif_message);
        TextView timestamp = convertView.findViewById(R.id.notif_timestamp);

        if (notification != null) {
            title.setText(notification.getTitle());
            message.setText(notification.getMessage());
            if (notification.getTimestamp() != null) {
                timestamp.setText(dateFormat.format(notification.getTimestamp()));
            } else {
                timestamp.setText("");
            }
        }

        return convertView;
    }
}
