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
    private String ownerId;     //ID that references the organizer of this event
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
    private ArrayList<Comment> comments;
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
    public Event(String title, String eventId) {
        this.eventId = eventId;
        this.title = title;
        this.ownerId = "ABCDEF";  //TODO: refactor when profile functionality is added.
        description = "";
        entrantCapacity = 0;
        applicantCapacity = 0;
        comments = new ArrayList<>();
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
     * Gets id of owner/organizer of the event
     * @return ownerId
     */
    public String getOwnerId() { return ownerId; }

    /**
     * Sets owner id, could be used for switching organizers for an event
     * @param ownerId unique identifier for event
     */
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

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
        if (applicantCapacity < 0 || applicantCapacity > entrantCapacity) {
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
        return numParticipants<entrantCapacity;
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

    public ArrayList<Comment> getComments() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment parent, Comment comment) {
        if (parent == null) {
            comments.add(comment);
        }
        else {
            parent.addReply(comment);
        }
    }
    public boolean isInRegistration() {
        if (endRegistrationTime == null || startRegistrationTime == null
                || endRegistrationTime.isBlank() || startRegistrationTime.isBlank()) {
            //No valid registration period set, so assume always in registration
            return true;
        }
        return LocalDateTime.now().isBefore(LocalDateTime.parse(endRegistrationTime))
                && LocalDateTime.now().isAfter(LocalDateTime.parse(startRegistrationTime));
    }

    public boolean isInConfirmation() {
        if (isInRegistration()) {
            return false;
        }
        if (endConfirmationTime == null || startConfirmationTime == null
                || endConfirmationTime.isBlank() || startConfirmationTime.isBlank()) {
            //No valid confirmation period set, so assume always in registration
            return true;
        }
        return LocalDateTime.now().isBefore(LocalDateTime.parse(endConfirmationTime))
                && LocalDateTime.now().isAfter(LocalDateTime.parse(startConfirmationTime));
    }
}
