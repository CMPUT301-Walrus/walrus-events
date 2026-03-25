package com.example.walrusevents.controllers;

import android.app.Activity;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.walrusevents.ProfileRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.WaitlistEntry;
import com.example.walrusevents.WaitlistRepository;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.util.EntrantArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class OEventPoolController {
    private WaitlistRepository waitlistRepository;
    private ProfileRepository profileRepository;
    private boolean inConfirmationPhase;
    private String eventId;

    public OEventPoolController(Activity context, String eventId, boolean inConfirmationPhase, FragmentContainerView fragmentContainerView, @NonNull Fragment fragment) {
        this.inConfirmationPhase = inConfirmationPhase;
        this.eventId = eventId;

        waitlistRepository = new WaitlistRepository();
        profileRepository = new ProfileRepository();
    }
}
