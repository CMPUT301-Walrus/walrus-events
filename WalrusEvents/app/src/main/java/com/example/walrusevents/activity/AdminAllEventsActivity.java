package com.example.walrusevents.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        view.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!eventlist.isEmpty()) {
                    Event event = eventlist.get(position);
                    String eventId = event.getEventId();
                    new AlertDialog.Builder(AdminAllEventsActivity.this)
                            .setTitle("Delete Event")
                            .setMessage("Are you sure you want to delete this event?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                eventRepository.deleteEventAdmin(eventId,
                                        new EventRepository.OnEventDeletedListener() {
                                            @Override
                                            public void onSuccess() {
                                                eventlist.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onFailure(String error) {
                                                Log.e("DELETE_EVENT", error);
                                            }
                                        });

                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }else{
                    Log.d("Events","list is empty");
                }
            }
        });

    }

    public void deleteSelectedEvent(String eventId){
        eventRepository.deleteEvent(eventId);
        adapter.notifyDataSetChanged();
    }
}
