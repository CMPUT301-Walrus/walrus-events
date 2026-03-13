package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
/*
 Admin View where the admin will redirect to all admin functionalities
 wip: base view w/o admin functionalities yet
 */

public class AdminViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        TextView wipText = findViewById(R.id.adminViewWIPText);

        wipText.setOnClickListener(v -> {
            finish();
        });

        //Future Buttons: Events, Images, Profiles, Notif Log






    }
}
