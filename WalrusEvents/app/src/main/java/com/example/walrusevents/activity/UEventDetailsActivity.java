package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;


public class UEventDetailsActivity extends AppCompatActivity {
    private Button backButton;
    private Button joinButton;
    private Button viewPoolButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent back = new Intent(UEventDetailsActivity.this, MainActivity.class);
            startActivity(back);
        });
    }

    
}