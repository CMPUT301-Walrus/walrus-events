package com.example.walrusevents;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Event {
    private String title;
    private LocalDateTime startRegistrationTime;
    private LocalDateTime endRegistrationTime;
    private LocalDateTime startConfirmationTime;
    private LocalDateTime endConfirmationTIme;
    private int entrantCapacity;
    private Image poster;
    private Image thumbnail;
    private Image qrCodeImage;
    private boolean useGeolocation;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartRegistrationTime() {
        return startRegistrationTime;
    }

    public void setStartRegistrationTime(LocalDateTime startRegistrationTime) {
        this.startRegistrationTime = startRegistrationTime;
    }

    public LocalDateTime getEndRegistrationTime() {
        return endRegistrationTime;
    }

    public void setEndRegistrationTime(LocalDateTime endRegistrationTime) {
        this.endRegistrationTime = endRegistrationTime;
    }

    public LocalDateTime getStartConfirmationTime() {
        return startConfirmationTime;
    }

    public void setStartConfirmationTime(LocalDateTime startConfirmationTime) {
        this.startConfirmationTime = startConfirmationTime;
    }

    public LocalDateTime getEndConfirmationTIme() {
        return endConfirmationTIme;
    }

    public void setEndConfirmationTIme(LocalDateTime endConfirmationTIme) {
        this.endConfirmationTIme = endConfirmationTIme;
    }

    public int getEntrantCapacity() {
        return entrantCapacity;
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
        this.entrantCapacity = entrantCapacity;
        return true;
    }

    public Image getPoster() {
        return poster;
    }

    public void setPoster(Image poster) {
        this.poster = poster;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean getUseGeolocation() {
        return useGeolocation;
    }

    /**
     * Toggles whether or not to use geolocation
     * @return the value of useGeolocation after toggling
     */
    public boolean toggleGeolocation() {
        useGeolocation = !useGeolocation;
        return useGeolocation;
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean inRegistrationPhase() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(startRegistrationTime) && now.isBefore(endRegistrationTime);
    }

    /**
     * Checks if the event is currently in registration phase
     * @return true if in registration phase, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean inConfirmationPhase() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(startConfirmationTime) && now.isBefore(endConfirmationTIme);
    }
}
