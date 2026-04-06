package com.example.walrusevents.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.ui.AdminAllImagesView;
import com.example.walrusevents.util.ImageArrayAdapter;

import java.util.ArrayList;

public class AdminAllImagesActivity extends AppCompatActivity {

    private AdminAllImagesView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_images_admin);
        view = new AdminAllImagesView(this);

        ArrayList<Uri> imageList=new ArrayList<>();
        ImageArrayAdapter imageArrayAdapter=new ImageArrayAdapter(this,imageList);
        view.getImagesListView().setAdapter(imageArrayAdapter);

        FirebaseAPIManager api = new FirebaseAPIManager();
        api.getAllImages(new FirebaseAPIManager.OnImagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Uri> imageUris) {
                imageList.clear();
                imageList.addAll(imageUris);
                Log.d("URI",imageUris.get(0).toString());
                imageArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Log.e("UI", "Failed: " + error);
            }
        });

        view.getBackButton().setOnClickListener(v -> {finish();});
    }
}
