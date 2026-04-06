package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.walrusevents.R;

public class AdminAllEventsView {
    private ListView listView;

    private ImageView backButton;

    public AdminAllEventsView(Activity activity){
        listView = activity.findViewById(R.id.listview_admin_events);
        backButton = activity.findViewById(R.id.back_admin_events);
    }

    public ListView getListView(){
        return listView;
    }

    public ImageView getBackButton(){
        return backButton;
    }
}
