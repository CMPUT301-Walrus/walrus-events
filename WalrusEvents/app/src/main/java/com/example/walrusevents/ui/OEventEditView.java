package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.walrusevents.OEventEditController;
import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.util.Locale;

public class OEventEditView implements OEventEditController.EventEditListener {
    private TextView titleView;
    private ImageView editPosterImage;
    private ImageView editThumbnail;
    private TextInputEditText editDescription;
    private TextView editRegistrationStart;
    private TextView editRegistrationEnd;
    private TextView editConfirmationStart;
    private TextView editConfirmationEnd;
    private EditText editEntrantCapacity;
    private EditText editApplicantCapacity;
    private Button doneButton;
    private ImageView backButton;
    public OEventEditView(Activity activity, Event model) {
        titleView = activity.findViewById(R.id.eventName);
        titleView.setText(model.getTitle());

        editPosterImage = activity.findViewById(R.id.editPoster);
        editThumbnail = activity.findViewById(R.id.editThumbnail);

        editDescription = activity.findViewById(R.id.editDescription);
        editDescription.setText(model.getDescription());

        editRegistrationStart = activity.findViewById(R.id.startRegistration);
        editRegistrationEnd = activity.findViewById(R.id.endRegistration);
        editConfirmationStart = activity.findViewById(R.id.startConfirmation);
        editConfirmationEnd = activity.findViewById(R.id.endConfirmation);

        editEntrantCapacity = activity.findViewById(R.id.maxEntrantsText);

        editApplicantCapacity = activity.findViewById(R.id.maxApplicantsText);

        doneButton = activity.findViewById(R.id.doneButton);
        backButton = activity.findViewById(R.id.backButton_organizer_to_main);
    }

    public TextView getTitleView() {
        return titleView;
    }

    public ImageView getEditPosterImage() {
        return editPosterImage;
    }

    public ImageView getEditThumbnail() {
        return editThumbnail;
    }

    public TextInputEditText getEditDescription() {
        return editDescription;
    }

    public TextView getEditRegistrationStart() {
        return editRegistrationStart;
    }

    public TextView getEditRegistrationEnd() {
        return editRegistrationEnd;
    }

    public TextView getEditConfirmationStart() {
        return editConfirmationStart;
    }

    public TextView getEditConfirmationEnd() {
        return editConfirmationEnd;
    }

    public EditText getEditEntrantCapacity() {
        return editEntrantCapacity;
    }

    public EditText getEditApplicantCapacity() {
        return editApplicantCapacity;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public ImageView getBackButton() {
        return backButton;
    }
    public void updateEntrantCapacity(int capacity) {
        if (capacity > 0) {
            editEntrantCapacity.setText(String.format(Locale.CANADA, "%d", capacity));
        }
        else {
            editEntrantCapacity.setText("");
        }
    }

    public void updateApplicantCapacity(int capacity) {
        if (capacity > 0) {
            editApplicantCapacity.setText(String.format(Locale.CANADA, "%d", capacity));
        }
        else {
            editApplicantCapacity.setText("");
        }
    }
    @Override
    public void updateRegistrationStart(LocalDateTime dateTime) {
        if (dateTime == null) {
            editRegistrationStart.setText("START DATE");
        }
        else {
            String displayText = dateTime.getMonth() + " " + dateTime.getDayOfMonth() + ", " + dateTime.getYear();
            editRegistrationStart.setText(displayText);
        }
    }

    @Override
    public void updateRegistrationEnd(LocalDateTime dateTime) {
        if (dateTime == null) {
            editRegistrationEnd.setText("END DATE");
        }
        else {
            String displayText = dateTime.getMonth() + " " + dateTime.getDayOfMonth() + ", " + dateTime.getYear();
            editRegistrationEnd.setText(displayText);
        }
    }

    @Override
    public void updateConfirmationStart(LocalDateTime dateTime) {
        if (dateTime == null) {
            editConfirmationStart.setText("START DATE");
        }
        else {
            String displayText = dateTime.getMonth() + " " + dateTime.getDayOfMonth() + ", " + dateTime.getYear();
            editConfirmationStart.setText(displayText);
        }
    }

    @Override
    public void updateConfirmationEnd(LocalDateTime dateTime) {
        if (dateTime == null) {
            editConfirmationEnd.setText("END DATE");
        }
        else {
            String displayText = dateTime.getMonth() + " " + dateTime.getDayOfMonth() + ", " + dateTime.getYear();
            editConfirmationEnd.setText(displayText);
        }
    }
}
