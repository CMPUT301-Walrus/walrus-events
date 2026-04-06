/**
 * This fragment pops up when the organizer wants to configure the lottery
 */

package com.example.walrusevents.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.walrusevents.R;
import com.example.walrusevents.activity.OEventPoolActivity;
import com.example.walrusevents.model.Event;
import com.example.walrusevents.ui.ConfigureNotificationsFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ConfigureLotteryFragment extends DialogFragment {
    private FragmentManager fragmentManager;
    private OEventPoolActivity activity;
    private Event eventModel;
    public ConfigureLotteryFragment(Event eventModel, OEventPoolActivity activity) {
        this.eventModel = eventModel;
        this.activity = activity;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_lottery, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputEditText applicantCapText = view.findViewById(R.id.number_of_applicants_textbox);
        Button confirmButton = view.findViewById(R.id.confirm_lottery_config_button);
        Button cancelButton = view.findViewById(R.id.cancel_lottery_config_button);

        if (eventModel.getApplicantCapacity() > 0) {
            applicantCapText.setText(String.format(Locale.getDefault(), "%d", eventModel.getApplicantCapacity()));
        }

        cancelButton.setOnClickListener(v -> dismiss());

        confirmButton.setOnClickListener(v -> {
            int configuredCapacity = 0;
            if (applicantCapText.getText() != null && !applicantCapText.getText().isEmpty()) {
                configuredCapacity = Integer.parseInt(applicantCapText.getText().toString());
            }

            if (configuredCapacity <= 0) {
                Toast.makeText(activity, "Set the applicant capacity before drawing selection", Toast.LENGTH_SHORT).show();
                return;
            }

            eventModel.setApplicantCapacity(configuredCapacity);

            if (activity.getSupportFragmentManager().findFragmentByTag(OEventPoolActivity.CONFIG_NOTIFICATIONS_TAG) == null) {
                ConfigureNotificationsFragment configureNotificationsFragment = new ConfigureNotificationsFragment(eventModel, activity);
                configureNotificationsFragment.show(activity.getSupportFragmentManager(), OEventPoolActivity.CONFIG_NOTIFICATIONS_TAG);
            }

            dismiss();
        });
    }
}
