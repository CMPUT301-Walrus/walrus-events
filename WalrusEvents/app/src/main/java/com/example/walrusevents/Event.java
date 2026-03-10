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
    private int applicantCapacity;
    private Image poster;
    private Image thumbnail;
    private Image qrCodeImage;
    private boolean useGeolocation;

    public Event() {
        //TODO: Construct model from database object


    }

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
     */
    public boolean setEntrantCapacity(int entrantCapacity) {
        if (entrantCapacity <= 0) {
            return false;
        }
        this.entrantCapacity = entrantCapacity;
        return true;
    }

    /**
     * Sets the maximum amount of applicants that can be chosen by the lottery. Must be greater than
     * 0 and less than entrant capacity.
     * @param applicantCapacity
     * The new capacity that will be set
     */
    public boolean setApplicantCapacity(int applicantCapacity) {
        if (applicantCapacity <= 0 || applicantCapacity > entrantCapacity) {
            return false;
        }
        this.applicantCapacity = applicantCapacity;
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
