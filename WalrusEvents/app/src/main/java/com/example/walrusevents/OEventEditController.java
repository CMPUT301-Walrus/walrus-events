package com.example.walrusevents;

import android.graphics.Bitmap;
import android.media.Image;

import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.PosterGenerator;
import com.example.walrusevents.util.QRGenerator;

public class OEventEditController {
    private Event model;

    /**
     * Constructor for the organizer event edit controller
     * @param model
     */
    public OEventEditController(Event model) {
        this.model = model;
    }

    public void setTitle(String title) {
        model.setTitle(title);
    }

    public void setDescription(String description) {
        model.setDescription(description);
    }

    public void setStartRegistrationTime(String startRegistrationTime) {
        model.setStartRegistrationTime(startRegistrationTime);
    }

    public void setEndRegistrationTime(String endRegistrationTime) {
        model.setEndRegistrationTime(endRegistrationTime);
    }

    public void setStartConfirmationTime(String startConfirmationTime) {
        model.setStartConfirmationTime(startConfirmationTime);
    }

    public void setEndConfirmationTIme(String endConfirmationTIme) {
        model.setEndConfirmationTIme(endConfirmationTIme);
    }

    public void setEntrantCapacity(int entrantCapacity) {
        model.setEntrantCapacity(entrantCapacity);
    }

    public void setThumbnail(Image thumbnail) {
        model.setThumbnail(thumbnail);
    }

    /**
     * Toggles whether or not to use geolocation
     * @return the value of useGeolocation after toggling
     */
    public boolean toggleGeolocation(int index) {
        model.setUseGeolocation(!model.getUseGeolocation());
        return model.getUseGeolocation();
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

    /**
     * This method stores event posters to Firebase
     * @param imgRepo
     * @param listener
     */
    public void saveEventPoster(ImageRepository imgRepo, FirebaseAPIManager.OnUploadCompleteListener listener) {
        if (model.getPoster() != null) {
            // Use the event ID as the filename so it's always unique
            String fileName = "poster_" + model.getEventId();
            imgRepo.storeGeneratedBitmap(model.getPoster(), fileName, listener);
        }
    }

    public void saveModel() {
        EventRepository eventRepository = new EventRepository();
        eventRepository.setEvent(model);
    }
}
