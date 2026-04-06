package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.walrusevents.R;

public class AdminAllNotificationsView{
    private final ListView logListView;
    private final ImageView backButton;

    public AdminAllNotificationsView(Activity activity) {
        logListView = activity.findViewById(R.id.admin_log_list);
        backButton = activity.findViewById(R.id.admin_log_back_button);    }

    public ListView getLogListView() { return logListView; }
    public ImageView getBackButton() { return backButton; }
}