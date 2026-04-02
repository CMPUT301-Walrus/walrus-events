package com.example.walrusevents.controllers;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.WaitlistRepository;

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
