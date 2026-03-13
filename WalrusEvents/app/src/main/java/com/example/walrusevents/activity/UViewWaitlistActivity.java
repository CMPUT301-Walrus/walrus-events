package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;

public class UViewWaitlistActivity extends AppCompatActivity {
    Event event;
    Button backToEvent;
    TextView eventTitle;
    ListView waitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_view_waitlist_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
         * Retrieve Event object that was clicked in MainActivity
         * Exception handling referenced from https://www.geeksforgeeks.org/android/exceptions-in-android-with-example/. March 12, 2026
         */
        try {
            Intent retrieveEvent = this.getIntent();
            Bundle bundle = retrieveEvent.getExtras();
            if (bundle == null) {
                throw new NullPointerException("Bundle containing selected Event not found");
            }
            event = bundle.getSerializable("event", Event.class);
            if (event == null) { /* If event doesn't load, return to main activity */
                throw new NullPointerException("Selected Event not found");
            }
        } catch (NullPointerException e) {
            Log.e("Walrus Events", "Missing Object", e);
        }

        /*
         * Set 'Back to Event' button to return to UEventDetailsActivity
         */
        backToEvent = findViewById(R.id.back_to_event);
        backToEvent.setOnClickListener(v -> {
            Intent goBack = new Intent(UViewWaitlistActivity.this, UEventDetailsActivity.class);

            // packaging serializable object into Intent referenced from Peter Mortensen in Stack Overflow https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable. March 12, 2026
            Bundle bundle = new Bundle();
            bundle.putSerializable("Event", event);
            goBack.putExtras(bundle);
            startActivity(goBack);
        });
    }
}