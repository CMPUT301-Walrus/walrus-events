package com.example.walrusevents;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class EventController {
    private Event model;

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

    /**
     * Sets the maximum amount of entrants that can sign up for the event. Must be greater than 0.
     * @param entrantCapacity
     * The new capacity that will be set
     * @return true if successfully set to the specified capacity, false if not
     */
    public boolean setEntrantCapacity(int entrantCapacity) {
        if (entrantCapacity < 0) {
            return false;
        }
        model.setEntrantCapacity(entrantCapacity);
        return true;
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
