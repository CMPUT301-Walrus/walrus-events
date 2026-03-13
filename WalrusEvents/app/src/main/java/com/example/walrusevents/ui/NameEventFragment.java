package com.example.walrusevents.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.walrusevents.R;

public class NameEventFragment extends DialogFragment {
    public interface NameEventListener {
        void addEvent(String title);
    }

    private  NameEventListener listener;

    public void setListener(NameEventListener listener) {
        this.listener = listener;
    }
    public static NameEventFragment newInstance(NameEventListener listener){
        NameEventFragment fragment = new NameEventFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event_popup, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText editName = view.findViewById(R.id.editName);
        Button confirmButton = view.findViewById(R.id.editNameConfirm);
        confirmButton.setOnClickListener(v -> {
            listener.addEvent(editName.getText().toString());
            dismiss();
        });

        Button cancelButton = view.findViewById(R.id.editNameCancel);
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
    }
}
