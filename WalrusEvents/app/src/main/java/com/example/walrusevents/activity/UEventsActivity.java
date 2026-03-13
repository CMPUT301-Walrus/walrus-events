package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.R;

/*
* Shows the History of Events of all the registered events from the User.
* WIP: Base
 */

public class UEventsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_history_events);


        Button backButton = findViewById(R.id.historyEventsBackButton);
        backButton.setOnClickListener(v -> {
            finish();
        });


    }
}
