package com.example.walrusevents.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;

/*
* Shows the "Settings" Tab for the User.
* Will have 'Personal Info' editing functionality and general settings for the user
* uses general_settings layout
 */

public class USettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_settings);

        TextView settingsText =findViewById(R.id.generalSettingsText);
        settingsText.setOnClickListener(v -> {
            finish();
        });

    }
}
