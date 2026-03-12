package com.example.walrusevents.model;

import android.media.Image;

import java.io.Serializable;
import java.time.LocalDateTime;

import android.graphics.Bitmap;
import com.example.walrusevents.util.QRGenerator;

/**
 * Model class that holds information about an event
 */
public class Event implements Serializable {
    private String eventId;
    private String ownerId;     //ID that references the organizer of this event
    private String title;
    private String description;
    private String startRegistrationTime;
    private String endRegistrationTime;
    private String startConfirmationTime;
    private String endConfirmationTIme;
    private int entrantCapacity;    //Limits number of entrants. Set to 0 for no limit
    private int applicantCapacity;  //Number of applicants chosen by lottery. If 0, lottery should be disabled
    private Bitmap poster;
    private Image thumbnail;
    private Bitmap qrCodeImage;
    private boolean useGeolocation;

    //No-argument constructor for database queries
    public Event() {
        this.eventId = "";
        this.title = "";
        this.ownerId = "";
    }

    public Event(String title, String eventId) {
        this.eventId = eventId;
        this.title = title;
        this.ownerId = "ABCDEF";  //TODO: refactor when profile functionality is added.
        description = "";
        entrantCapacity = 0;
        applicantCapacity = 0;
        setStartConfirmationTime(LocalDateTime.now().toString());
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String eventId) { this.ownerId = ownerId; }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartRegistrationTime() {
        return startRegistrationTime;
    }

    public void setStartRegistrationTime(String startRegistrationTime) {
        this.startRegistrationTime = startRegistrationTime;
    }

    public String getEndRegistrationTime() {
        return endRegistrationTime;
    }

    public void setEndRegistrationTime(String endRegistrationTime) {
        this.endRegistrationTime = endRegistrationTime;
    }

    public String getStartConfirmationTime() {
        return startConfirmationTime;
    }

    public void setStartConfirmationTime(String startConfirmationTime) {
        this.startConfirmationTime = startConfirmationTime;
    }

    public String getEndConfirmationTIme() {
        return endConfirmationTIme;
    }

    public void setEndConfirmationTIme(String endConfirmationTIme) {
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
        if (entrantCapacity < 0) {
            return false;
        }
        this.entrantCapacity = entrantCapacity;
        return true;
    }

    /**
     * Sets the amount of applicants that can be chosen by the lottery. Must be greater than
     * 0 and less than entrant capacity.
     * @param applicantCapacity
     * The new capacity that will be set
     */
    public boolean setApplicantCapacity(int applicantCapacity) {
        if (applicantCapacity < 0 || applicantCapacity > entrantCapacity) {
            return false;
        }
        this.applicantCapacity = applicantCapacity;
        return true;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public void generateEventQRCode() {
        this.qrCodeImage = QRGenerator.generateQRCode("walrusevents://event/" + this.eventId);
    }

    public Bitmap getQrCode() { return qrCodeImage; }

    public void setQRCodeImage(Bitmap qrCodeImage) { this.qrCodeImage = qrCodeImage; }

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
}
