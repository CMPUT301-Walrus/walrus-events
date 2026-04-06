package com.example.walrusevents.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.AdminAllEventsView;
import com.example.walrusevents.util.MainSEventArrayAdapter;
import com.example.walrusevents.util.MainSEventListController;

import java.util.ArrayList;

public class AdminAllEventsActivity extends AppCompatActivity {
    private AdminAllEventsView view;

    private EventRepository eventRepository;

    private MainSEventListController controller;

    private MainSEventArrayAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events_admin);
        view = new AdminAllEventsView(this);
        ArrayList<Event> eventlist =new ArrayList<>();
        adapter=new MainSEventArrayAdapter(this,eventlist);
        eventRepository=new EventRepository();
        controller=new MainSEventListController(this,eventRepository,view.getListView());
        controller.loadEvents();

        view.getBackButton().setOnClickListener(v -> {finish();});

    }
}
