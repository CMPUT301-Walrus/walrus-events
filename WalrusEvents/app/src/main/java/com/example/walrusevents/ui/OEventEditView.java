package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.walrusevents.R;
import com.example.walrusevents.model.Event;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class OEventEditView {
    private TextView titleView;
    private ImageView editPosterImage;
    private ImageView editThumbnail;
    private TextInputEditText editDescription;
    private EditText editRegistrationStart;
    private EditText editRegistrationEnd;
    private EditText editConfirmationStart;
    private EditText editConfirmationEnd;
    private EditText editEntrantCapacity;
    private EditText editApplicantCapacity;
    private Button doneButton;

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

    public EditText getEditRegistrationStart() {
        return editRegistrationStart;
    }

    public EditText getEditRegistrationEnd() {
        return editRegistrationEnd;
    }

    public EditText getEditConfirmationStart() {
        return editConfirmationStart;
    }

    public EditText getEditConfirmationEnd() {
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
}
