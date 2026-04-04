package com.example.walrusevents.ui;

import androidx.core.util.Pair;

import com.example.walrusevents.util.MainSEventListController;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class showMaterialRangeDatePicker {

    private MainSEventListController eventListController;
    MaterialDatePicker<Pair<Long, Long>> picker =
            MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select date range")
                    .build();

    //picker.show(getSupportFragmentManager(), "DATE_PICKER");

    /*
    picker.addOnPositiveButtonClickListener(selection -> {

        Long startMillis = selection.first;
        Long endMillis = selection.second;

        if (startMillis != null && endMillis != null) {

            LocalDateTime start = Instant.ofEpochMilli(startMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime end = Instant.ofEpochMilli(endMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            eventListController.setDateRange(start, end);
        }
    });

     */
}
