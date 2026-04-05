package com.example.walrusevents.ui;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.walrusevents.R;

import java.time.LocalDateTime;

/*
* Fragment for the availability filter on the main screen
* Popup for user to select availability
 */
public class CalendarAvailabilityFragment extends DialogFragment {

    private LocalDateTime selectedStartDate;
    private LocalDateTime selectedEndDate;

    public CalendarAvailabilityFragment(){

        super(R.layout.fragment_calendar);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
