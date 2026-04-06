/**
 * This controller is in charge of managing an events details when an organizer edits them
 * Primarily composed of retrieving and storing information from Firebase
 */

package com.example.walrusevents.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.media.Image;
import android.widget.DatePicker;

import com.example.walrusevents.data.EventRepository;
import com.example.walrusevents.data.FirebaseAPIManager;
import com.example.walrusevents.data.ImageRepository;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.util.PosterGenerator;
import com.example.walrusevents.util.QRGenerator;

import java.time.LocalDateTime;

public class OEventEditController {
    public interface EventEditListener {
        public void updateRegistrationStart(LocalDateTime dateTime);
        public void updateRegistrationEnd(LocalDateTime dateTime);
        public void updateConfirmationStart(LocalDateTime dateTime);
        public void updateConfirmationEnd(LocalDateTime dateTime);
        public void updateEntrantCapacity(int capacity);
        public void updateApplicantCapacity(int capacity);
    }

    private Event model;
    private EventEditListener listener;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime confirmationStart;
    private LocalDateTime confirmationEnd;

    /**
     * Constructor for the organizer event edit controller
     * @param model
     */
    public OEventEditController(Event model, EventEditListener listener) {
        this.model = model;
        this.listener = listener;

        if (model.getStartRegistrationTime() != null)
        {
            registrationStart = LocalDateTime.parse(model.getStartRegistrationTime());
            listener.updateRegistrationStart(registrationStart);
        }

        if (model.getEndRegistrationTime() != null) {
            registrationEnd = LocalDateTime.parse(model.getEndRegistrationTime());
            listener.updateRegistrationEnd(registrationEnd);
        }

        if (model.getStartConfirmationTime() != null) {
            confirmationStart = LocalDateTime.parse(model.getStartConfirmationTime());
            listener.updateConfirmationStart(confirmationStart);
        }

        if (model.getEndConfirmationTime() != null) {
            confirmationEnd = LocalDateTime.parse(model.getEndConfirmationTime());
            listener.updateConfirmationEnd(confirmationEnd);
            System.out.println(confirmationEnd);
        }

        listener.updateEntrantCapacity(model.getEntrantCapacity());
        listener.updateApplicantCapacity(model.getApplicantCapacity());
    }

    public void setTitle(String title) {
        model.setTitle(title);
    }

    public void setIsPrivate(boolean isPrivate) {
        model.setIsPrivate(isPrivate);
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
        model.setEndConfirmationTime(endConfirmationTIme);
    }

    public boolean setEntrantCapacity(String entrantCapacity) {
        return model.setEntrantCapacity(parseCapacity(entrantCapacity));
    }

    public boolean setApplicantCapacity(String applicantCapacity) {
        return model.setApplicantCapacity(parseCapacity(applicantCapacity));
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

    public void openStartRegistrationDialog(Activity context) {
        DatePickerDialog dialog = new DatePickerDialog(context, 0);
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                registrationStart = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0);
                listener.updateRegistrationStart(registrationStart);
            }
        });

        dialog.show();
    }

    public void openEndRegistrationDialog(Activity context) {
        DatePickerDialog dialog = new DatePickerDialog(context, 0);
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                registrationEnd = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0);
                listener.updateRegistrationEnd(registrationEnd);
            }
        });

        dialog.show();
    }

    public void openStartConfirmationDialog(Activity context) {
        DatePickerDialog dialog = new DatePickerDialog(context, 0);
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                confirmationStart = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0);
                listener.updateConfirmationStart(confirmationStart);
            }
        });

        dialog.show();
    }
    public void openEndConfirmationDialog(Activity context) {
        DatePickerDialog dialog = new DatePickerDialog(context, 0);
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                confirmationEnd = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0);
                listener.updateConfirmationEnd(confirmationEnd);
            }
        });

        dialog.show();
    }

    private int parseCapacity(String capacityText) {
        if (capacityText == null) {
            return 0;
        }

        String trimmed = capacityText.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(trimmed);
    }

    public void saveModel() {
        EventRepository eventRepository = new EventRepository();

        if (registrationStart != null) {
            model.setStartRegistrationTime(registrationStart.toString());
        }

        if (registrationEnd != null) {
            model.setEndRegistrationTime(registrationEnd.toString());
        }

        if (confirmationStart != null) {
            model.setStartConfirmationTime(confirmationStart.toString());
        }

        if (confirmationEnd != null) {
            model.setEndConfirmationTime(confirmationEnd.toString());
        }

        eventRepository.setEvent(model);
    }
}
