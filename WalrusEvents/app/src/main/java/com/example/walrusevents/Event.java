package com.example.walrusevents;

import android.media.Image;

import java.time.LocalDateTime;

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

    public void setEntrantCapacity(int entrantCapacity) {
        this.entrantCapacity = entrantCapacity;
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

    public void setUseGeolocation(boolean useGeolocation) {
        this.useGeolocation = useGeolocation;
    }

    /**
     * Save all edited data to database
     */
    public void saveToDatabase() {
        //TODO: Update database
    }
}
