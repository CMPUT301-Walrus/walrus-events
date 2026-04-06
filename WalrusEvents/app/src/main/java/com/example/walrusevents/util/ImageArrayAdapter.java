package com.example.walrusevents.util;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.walrusevents.R;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Uri> {
    private Context context;
    private ArrayList<Uri> imageUris;

    public ImageArrayAdapter(Context context, ArrayList<Uri> imageUris) {
        super(context, 0, imageUris);
        this.context = context;
        this.imageUris = imageUris;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_image, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);

        Uri uri = imageUris.get(position);

        // Use Glide (recommended)
        Glide.with(context)
                .load(uri)
                //.skipMemoryCache(true)
                .placeholder(R.drawable.rounded_light_blue_square)
                .into(imageView);

        return convertView;
    }
}
