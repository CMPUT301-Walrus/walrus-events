package com.example.walrusevents;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.util.QRGenerator;
import com.example.walrusevents.util.PosterGenerator;

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

    public void setPoster(Bitmap poster) {
        model.setPoster(poster);
    }

    public Bitmap getPoster() {
        return model.getPoster();
    }

    public void setQRCode(Bitmap QRCodeImage) {
        model.setQRCodeImage(QRCodeImage);
    }

    public void setThumbnail(Image thumbnail) {
        model.setThumbnail(thumbnail);
    }

    public void generateQRAndPoster() {
        /**
         * This function is called when an organizer creates an event
         * It will generate the QR code and poster for the event
         */
        // create link for poster
        String eventLink = "walrusevents://event/" + model.getId();

        // Generate QR code using method in util, set destination as the link for the event
        Bitmap qrCode = QRGenerator.generateQRCode(eventLink);

        // Generate description for event
        String eventDescription = model.generateDefaultDescription();

        // Generate the poster
        // Needs event title & description, and QR code
        Bitmap poster = PosterGenerator.createEventPoster(
                model.getTitle(),
                eventDescription,
                qrCode
        );

        // Update the Event
        model.setQRCodeImage(qrCode);
        model.setPoster(poster);
    }

    /**
     * This method stores event posters to Firebase
     * @param imgRepo
     * @param listener
     */
    public void saveEventPoster(ImageRepository imgRepo, FirebaseAPIManager.OnUploadCompleteListener listener) {
        if (model.getPoster() != null) {
            // Use the event ID as the filename so it's always unique
            String fileName = "poster_" + model.getId();
            imgRepo.storeGeneratedBitmap(model.getPoster(), fileName, listener);
        }
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
