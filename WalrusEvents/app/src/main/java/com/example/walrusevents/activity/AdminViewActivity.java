package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;

public class AdminViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);
        //For some reason not going back
        // stack doesn't connect MainActivity??

        TextView wipText = findViewById(R.id.adminViewWIPText);
        wipText.setOnClickListener(v -> {
            finish();
        });






    }
}
