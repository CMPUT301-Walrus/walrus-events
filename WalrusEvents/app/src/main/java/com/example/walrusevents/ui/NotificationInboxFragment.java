package com.example.walrusevents.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.walrusevents.R;
import com.example.walrusevents.controllers.NotificationsController;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.NotificationsAdapter;

import java.util.ArrayList;

public class NotificationInboxFragment extends Fragment {

    private NotificationsController controller;
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private TextView emptyStateText;

    public NotificationInboxFragment() {
        // Required empty public constructor
    }

    public static NotificationInboxFragment newInstance() {
        return new NotificationInboxFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Initialize the controller here
        controller = new NotificationsController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 2. Inflate the layout
        View view = inflater.inflate(R.layout.fragment_notification_inbox, container, false);

        // 3. Bind UI components
        recyclerView = view.findViewById(R.id.notification_inbox);
        emptyStateText = view.findViewById(R.id.empty_inbox);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. Load the data using the deviceId
        loadInbox();

        return view;
    }

    private void loadInbox() {
        String deviceId = DeviceIdManager.getOrCreate(getContext());

        controller.fetchUniversalInbox(deviceId, notifications -> {
            // Safety check: make sure fragment is still visible
            if (isAdded() && notifications != null) {
                if (notifications.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    adapter = new NotificationsAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}