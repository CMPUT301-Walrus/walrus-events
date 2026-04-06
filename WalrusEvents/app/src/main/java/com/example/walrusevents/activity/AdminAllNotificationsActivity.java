/**
 * This activity is made for displaying all notifications for the Admin
 */
package com.example.walrusevents.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.NotificationRepository;
import com.example.walrusevents.model.Notification;
import com.example.walrusevents.ui.AdminAllNotificationsView;
import java.util.List;

public class AdminAllNotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_notification_log);
        AdminAllNotificationsView view = new AdminAllNotificationsView(this);
        NotificationRepository repo = new NotificationRepository();

        view.getBackButton().setOnClickListener(v -> finish());

        repo.getAllNotificationLogs(querySnapshot -> {
            List<Notification> logs = querySnapshot.toObjects(Notification.class);

            // Create a simple list display (Title: Message)
            ArrayAdapter<Notification> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, logs);
            view.getLogListView().setAdapter(adapter);
        });
    }
}