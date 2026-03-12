package com.example.walrusevents.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.EventRepository;

public class OEventPoolActivity extends AppCompatActivity {
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        eventRepository = new EventRepository();
    }
}
