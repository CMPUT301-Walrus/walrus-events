package com.example.walrusevents;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class EventController {
    private final Event model;

    public EventController(Event model) {
        //Initialize EventController
        this.model = model;
    }

    public void setTitle(String title) {
        model.setTitle(title);
    }

    public void setStartRegistrationTime(LocalDateTime startRegistrationTime) {
        model.setStartRegistrationTime(startRegistrationTime);
    }

    public void setEndRegistrationTime(LocalDateTime endRegistrationTime) {
        model.setEndRegistrationTime(endRegistrationTime);
    }

    public void setStartConfirmationTime(LocalDateTime startConfirmationTime) {
        model.setStartConfirmationTime(startConfirmationTime);
    }

    public void setEndConfirmationTIme(LocalDateTime endConfirmationTIme) {
        model.setEndConfirmationTIme(endConfirmationTIme);
    }
    
    public void setEntrantCapacity(int entrantCapacity) {
        model.setEntrantCapacity(entrantCapacity);
    }

    public void setPoster(Image poster) {
        model.setPoster(poster);
    }

    public void setThumbnail(Image thumbnail) {
        model.setThumbnail(thumbnail);
    }

    /**
     * Toggles whether or not to use geolocation
     * @return the value of useGeolocation after toggling
     */
    public boolean toggleGeolocation() {
        model.setUseGeolocation(!model.getUseGeolocation());
        return model.getUseGeolocation();
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean inRegistrationPhase() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(model.getStartRegistrationTime()) && now.isBefore(model.getEndRegistrationTime());
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean inConfirmationPhase() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(model.getStartConfirmationTime()) && now.isBefore(model.getEndConfirmationTIme());
    }
}
