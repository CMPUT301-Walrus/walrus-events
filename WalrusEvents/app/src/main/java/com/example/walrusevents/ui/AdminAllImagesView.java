package com.example.walrusevents.ui;

import android.app.Activity;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.walrusevents.R;

public class AdminAllImagesView {
    private ListView imagesListView;

    private ImageView backButton;

    public AdminAllImagesView(Activity activity){
        imagesListView = activity.findViewById(R.id.listView3);
        backButton = activity.findViewById(R.id.back_admin_images);
    }

    public ListView getImagesListView(){
        return imagesListView;
    }

    public ImageView getBackButton(){
        return backButton;
    }

}
