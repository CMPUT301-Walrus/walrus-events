package com.example.walrusevents.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.Event;
import com.example.walrusevents.EventRepository;
import com.example.walrusevents.OEventListController;
import com.example.walrusevents.R;
import com.example.walrusevents.ui.OEventListView;

public class OEventPoolActivity extends AppCompatActivity {
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        eventRepository = new EventRepository();
    }
}
