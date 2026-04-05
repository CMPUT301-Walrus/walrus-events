package com.example.walrusevents.model;

import android.media.Image;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import android.graphics.Bitmap;

import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.util.QRGenerator;

/**
 * Model class that holds information about an event
 */
public class Event implements Serializable {
    private String eventId;
    private String title;
    private String description;
    private String startRegistrationTime;
    private String endRegistrationTime;
    private String startConfirmationTime;
    private String endConfirmationTime;
    private int entrantCapacity;    //Limits number of entrants. Set to 0 for no limit
    private int applicantCapacity;  //Number of applicants chosen by lottery. If 0, lottery should be disabled
    private Bitmap poster;
    private Image thumbnail;
    private Bitmap qrCodeImage;
    private boolean useGeolocation;
    private ArrayList<String> owners;
    private boolean isPrivate;
    private int numParticipants;

    private WaitlistRepository waitlistRepository;

    /**
     * Constructor for no args
     */
    public Event() {

    }

    /**
     * Constructor with args
     * @param title title of event set by organizer
     * @param eventId unique id for event
     */
    public Event(String title, String eventId, String ownerId) {
        this.eventId = eventId;
        this.title = title;
        owners = new ArrayList<>();
        owners.add(ownerId);
        description = "";
        entrantCapacity = 0;
        applicantCapacity = 0;
        numParticipants=1; //TEMP num Participants
    }

    /**
     * Gets unique id for event
     * @return eventId
     */
    public String getEventId() { return eventId; }

    /**
     * Sets id for event
     * @param eventId unique identifier for event
     */
    public void setEventId(String eventId) { this.eventId = eventId; }

    /**
     * Gets title for event set by organizer
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Used to edit title of event
     * @param title event title set by organizer
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description for event set by organizer
     * @return description
     */
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

    public String getEndConfirmationTime() {
        return endConfirmationTime;
    }

    public void setEndConfirmationTime(String endConfirmationTime) {
        this.endConfirmationTime = endConfirmationTime;
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
        if (entrantCapacity != 0 && applicantCapacity > entrantCapacity) {
            applicantCapacity = entrantCapacity;
        }
        return true;
    }

    /**
     * Gets the amount of applicants that can be chosen by the lottery.
     * @return applicantCapacity the current capacity of applicants
     */
    public int getApplicantCapacity() {
        return applicantCapacity;
    }

    /**
     * Method for turning limit capacity for applicants for an event on/off
     * @param applicantCapacity number of applicants allowed
     * @return success ? true : false
     */
    public boolean setApplicantCapacity(int applicantCapacity) {
        if (applicantCapacity < 0) {
            return false;
        }
        if (entrantCapacity != 0 && applicantCapacity > entrantCapacity) {
            return false;
        }
        this.applicantCapacity = applicantCapacity;
        return true;
    }

    public void setNumParticipants(int numOpenSeats){

        this.numParticipants=numOpenSeats;
    }

    public int getNumParticipants(){
        return numParticipants;
    }

    public boolean hasOpenSeats(){
        if(entrantCapacity!=0){
            return numParticipants<entrantCapacity;
        }else{
            //waitlist has no limit
            return true;
        }
    }

    /**
     * Gets poster for event, used to retrieve poster
     * @return poster (Bitmap format)
     */
    public Bitmap getPoster() {
        return poster;
    }

    /**
     * Sets poster for event, used for initialization and editing the poster
     * @param poster image poster for event
     */
    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public ArrayList<String> getOwners() {
        return owners;
    }

    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }

    public void addOwner(String ownerId) {
        if (owners == null) {
            owners = new ArrayList<>();
        }
        if (owners.contains(ownerId)) {
            return;
        }
        owners.add(ownerId);
    }

    public boolean isOwner(String ownerId) {
        return ownerId != null && owners != null && owners.contains(ownerId);
    }

    public boolean isCoOrganizer(String ownerId) {
        return isOwner(ownerId) && owners.size() > 1 && !ownerId.equals(owners.get(0));
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * Calls QR Code generators to make a unique QR code for event
     */


    public void generateEventQRCode() {
        this.qrCodeImage = QRGenerator.generateQRCode("walrusevents://event/" + this.eventId);
    }

    /**
     * Retrieves QR code image
     * @return qrCodeImage (Bitmap format)
     */
    public Bitmap getQrCodeImage() { return qrCodeImage; }

    /**
     * Sets QR code, used to initialize and edit code
     * @param qrCodeImage image of QR code
     */
    public void setQrCodeImage(Bitmap qrCodeImage) { this.qrCodeImage = qrCodeImage; }

    /**
     * Gets promotional thumbnail for event
     * @return thumbnail
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets promotional thumbnail for event
     * @param thumbnail promotional event-related image
     */
    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Returns whether geolocation for the event is on or off
     * @return whether geolocation is on or off
     */
    public boolean getUseGeolocation() {
        return useGeolocation;
    }

    /**
     * Toggles geolocation for the event on/off
     * @param useGeolocation On ? true : false
     */
    public void setUseGeolocation(boolean useGeolocation) {
        this.useGeolocation = useGeolocation;
    }

    public boolean isInRegistration() {
        if (isPrivate) {
            return false;
        }
        if (endRegistrationTime == null || startRegistrationTime == null
                || endRegistrationTime.isBlank() || startRegistrationTime.isBlank()) {
            //No valid registration period set, so assume always in registration
            return true;
        }
        return LocalDateTime.now().isBefore(LocalDateTime.parse(endRegistrationTime))
                && LocalDateTime.now().isAfter(LocalDateTime.parse(startRegistrationTime));
    }

    public boolean isInConfirmation() {
        if (isInRegistration() && !isPrivate) {
            return false;
        }
        if (startConfirmationTime == null || startConfirmationTime.isBlank()) {
            //No valid confirmation period start time set, so assume always out of confirmation
            return false;
        }
        if (endConfirmationTime == null || endConfirmationTime.isBlank()) {
            //No valid confirmation period end time set, so assume always in confirmation
            return true;
        }
        return LocalDateTime.now().isBefore(LocalDateTime.parse(endConfirmationTime))
                && LocalDateTime.now().isAfter(LocalDateTime.parse(startConfirmationTime));
    }
}
