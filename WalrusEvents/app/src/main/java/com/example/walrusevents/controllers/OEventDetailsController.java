package com.example.walrusevents.controllers;

import android.graphics.Bitmap;

import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.PosterGenerator;
import com.example.walrusevents.util.QRGenerator;

public class OEventDetailsController {
    private Event model;

    public OEventDetailsController(Event model) {
        this.model = model;
    }
    public void setPoster(Bitmap poster) {
        model.setPoster(poster);
    }

    public Bitmap getPoster() {
        return model.getPoster();
    }

    public void setQRCode(Bitmap QRCodeImage) {
        model.setQrCodeImage(QRCodeImage);
    }
    public void generateQRAndPoster() {
        /**
         * This function is called when an organizer creates an event
         * It will generate the QR code and poster for the event
         */
        // create link for poster
        String eventLink = "walrusevents://event/" + model.getEventId();

        // Generate QR code using method in util, set destination as the link for the event
        Bitmap qrCode = QRGenerator.generateQRCode(eventLink);


        // Generate the poster
        // Needs event title & description, and QR code
        Bitmap poster = PosterGenerator.createEventPoster(
                model.getTitle(),
                model.getDescription(),
                qrCode
        );

        // Update the Event
        model.setQrCodeImage(qrCode);
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
            String fileName = "poster_" + model.getEventId();
            imgRepo.storeGeneratedBitmap(model.getPoster(), fileName, listener);
        }
    }
}
