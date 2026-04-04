package com.example.walrusevents.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.data.ProfileRepository;
import com.example.walrusevents.data.ProfilePermissionsRepository;
import com.example.walrusevents.R;
import com.example.walrusevents.data.WaitlistRepository;
import com.example.walrusevents.controllers.EntrantController;
import com.example.walrusevents.model.AccountRole;
import com.example.walrusevents.model.Entrant;
import com.example.walrusevents.model.Profile;
import com.example.walrusevents.model.ProfilePermissions;
import com.example.walrusevents.util.DeviceIdManager;
import com.example.walrusevents.util.PermissionGatekeeper;

/**
 * Activity that allows a user to view and edit their profile settings.
 * <p>
 * Users can update their name, email, and phone number, toggle notification
 * preferences, or permanently delete their profile. Profile changes can be
 * saved from the dedicated save button and are also persisted when the user
 * navigates away from the screen. If validation fails, the user is kept on the
 * screen so they can correct the errors before leaving.
 * </p>
 * <p>
 * Notification preference changes are saved immediately when toggled, since
 * they do not require validation.
 * </p>
 *
 * @see EntrantController
 * @see ProfileRepository
 */
public class USettingsActivity extends AppCompatActivity {
    public static final String INITIAL_PROFILE_SETUP =
            "com.example.walrusevents.activity.USettingsActivity.INITIAL_PROFILE_SETUP";

    /** Input field for the user's display name. */
    private EditText nameInput;
    /** Input field for the user's email address. */
    private EditText emailInput;
    /** Input field for the user's phone number (optional). */
    private EditText phoneInput;
    /** Checkbox for opting out of notifications. Checked means opted-out. */
    private CheckBox notificationsCheckbox;
    /** Button that saves the current profile edits. */
    private Button saveProfileButton;
    /** Button that triggers the profile deletion flow. */
    private Button deleteProfileButton;

    /** Repository for reading and writing profile data to Firestore. */
    private ProfileRepository profileRepository;
    /** Repository for reading and writing account permissions data to Firestore. */
    private ProfilePermissionsRepository permissionsRepository;
    /** Controller providing business-logic operations on the entrant's profile. */
    private EntrantController entrantController;
    /** The currently loaded entrant whose profile is being edited. */
    private Entrant currentEntrant;
    /** The current account permissions loaded from Firestore. */
    private ProfilePermissions currentPermissions;
    /** Whether the activity is being shown for initial profile setup. */
    private boolean initialProfileSetup;

    /**
     * Initializes the activity, binds UI components, registers the back-navigation
     * callback, and begins loading the user's profile from Firestore.
     *
     * @param savedInstanceState the previously saved instance state, or {@code null}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> {
            currentPermissions = permissions;
            initializeUi();
        });
    }

    private void initializeUi() {
        setContentView(R.layout.general_settings);

        profileRepository = new ProfileRepository();
        permissionsRepository = new ProfilePermissionsRepository();

        initialProfileSetup = getIntent().getBooleanExtra(INITIAL_PROFILE_SETUP, false);
        TextView title = findViewById(R.id.settings_title);
        if(initialProfileSetup){
            title.setText("Profile Setup");
        }

        ImageButton backButton = findViewById(R.id.settings_back_button);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        notificationsCheckbox = findViewById(R.id.notifications_checkbox);
        saveProfileButton = findViewById(R.id.save_profile_button);
        deleteProfileButton = findViewById(R.id.delete_profile_button);
        updateDeleteButtonVisibility();

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        notificationsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            updateNotificationsPreference(isChecked);
        });
        saveProfileButton.setOnClickListener(v -> saveProfileAndFinish());
        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmation());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveProfileAndFinish();
            }
        });

        loadProfile();
    }

    /**
     * Fetches the current user's profile from Firestore using the device ID.
     * While loading, all input fields are disabled. If no profile exists, a new
     * blank {@link Entrant} is created. Once loaded, the profile data is bound
     * to the UI and fields are re-enabled.
     */
    private void loadProfile() {
        String deviceId = DeviceIdManager.getOrCreate(this);
        setInteractive(false);
        profileRepository.getProfile(deviceId, entrant -> runOnUiThread(() -> {
            if (entrant == null) {
                currentEntrant = new Entrant(new Profile(deviceId));
            } else {
                currentEntrant = entrant;
            }

            entrantController = new EntrantController(
                    currentEntrant,
                    new WaitlistRepository(),
                    profileRepository
            );

            bindProfileToViews();
            setInteractive(true);
        }));
    }

    /**
     * Populates the input fields and notification checkbox with the current
     * entrant's profile data. Null field values are displayed as empty strings.
     */
    private void bindProfileToViews() {
        Profile profile = currentEntrant.getProfile();
        nameInput.setText(valueOrEmpty(profile.getName()));
        emailInput.setText(valueOrEmpty(profile.getEmail()));
        phoneInput.setText(valueOrEmpty(profile.getPhone()));
        notificationsCheckbox.setChecked(!profile.isNotificationsEnabled());
    }

    /**
     * Validates and saves the profile before leaving the screen.
     * <p>
     * Performs the following steps:
     * <ol>
     *   <li>If the controller is not yet initialized, finishes immediately.</li>
     *   <li>Validates that name and email are non-empty and email is well-formed.
     *       If validation fails, focuses the offending field and blocks navigation.</li>
     *   <li>Compares current field values against the loaded profile. If nothing
     *       changed, finishes without a network call.</li>
     *   <li>Disables all fields and persists the update via
     *       {@link EntrantController#updateProfile}. On success, shows a toast and
     *       finishes. On failure, re-enables the fields so the user can retry.</li>
     * </ol>
     * </p>
     */
    private void saveProfileAndFinish() {
        if (entrantController == null || currentEntrant == null) {
            finish();
            return;
        }

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.settings_name_required));
            nameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.settings_email_required));
            emailInput.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(getString(R.string.settings_email_invalid));
            emailInput.requestFocus();
            return;
        }

        // Check if anything actually changed
        Profile profile = currentEntrant.getProfile();
        boolean nameChanged = !name.equals(valueOrEmpty(profile.getName()));
        boolean emailChanged = !email.equals(valueOrEmpty(profile.getEmail()));
        boolean phoneChanged = !phone.equals(valueOrEmpty(profile.getPhone()));

        if (!nameChanged && !emailChanged && !phoneChanged) {
            finish();
            return;
        }

        setInteractive(false);
        entrantController.updateProfile(name, email, emptyToNull(phone), new EntrantController.ActionCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(USettingsActivity.this, R.string.settings_save_success, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    setInteractive(true);
                    Toast.makeText(
                            USettingsActivity.this,
                            errorMessage != null ? errorMessage : getString(R.string.settings_save_error),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    /**
     * Persists the user's notification opt-out preference immediately.
     * The checkbox is disabled while the request is in flight to prevent
     * duplicate toggles. On failure, the checkbox is reverted to its
     * previous state and an error toast is shown.
     *
     * @param optOutChecked {@code true} if the user wants to opt out of
     *                      notifications, {@code false} to opt back in
     */
    private void updateNotificationsPreference(boolean optOutChecked) {
        if (entrantController == null) {
            notificationsCheckbox.setChecked(false);
            return;
        }

        notificationsCheckbox.setEnabled(false);
        EntrantController.ActionCallback callback = new EntrantController.ActionCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> notificationsCheckbox.setEnabled(true));
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    notificationsCheckbox.setEnabled(true);
                    notificationsCheckbox.setChecked(!optOutChecked);
                    Toast.makeText(
                            USettingsActivity.this,
                            errorMessage != null ? errorMessage : getString(R.string.settings_notifications_error),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        };

        if (optOutChecked) {
            entrantController.disableNotifications(callback);
        } else {
            entrantController.enableNotifications(callback);
        }
    }

    /**
     * Displays a confirmation dialog before permanently deleting the user's
     * profile. If the user confirms, {@link #deleteProfile()} is invoked.
     */
    private void showDeleteConfirmation() {
        if (entrantController == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.settings_delete_confirm_title)
                .setMessage(R.string.settings_delete_confirm_message)
                .setNegativeButton(R.string.settings_delete_cancel_action, null)
                .setPositiveButton(R.string.settings_delete_confirm_action, (dialog, which) -> deleteProfile())
                .show();
    }

    /**
     * Permanently deletes the user's profile from Firestore. On success, a new
     * device ID is generated via {@link DeviceIdManager#replaceId} and the user
     * is redirected to {@link MainActivity} with the back stack cleared. On
     * failure, fields are re-enabled and an error toast is shown.
     */
    private void deleteProfile() {
        setInteractive(false);
        entrantController.deleteProfile(new EntrantController.ActionCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(USettingsActivity.this, R.string.settings_delete_success, Toast.LENGTH_SHORT).show();
                    DeviceIdManager.replaceId(USettingsActivity.this);
                    Intent intent = new Intent(USettingsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    setInteractive(true);
                    Toast.makeText(
                            USettingsActivity.this,
                            errorMessage != null ? errorMessage : getString(R.string.settings_delete_error),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private void updateDeleteButtonVisibility() {
        boolean visible = !initialProfileSetup && currentPermissions != null;
        deleteProfileButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Enables or disables all interactive elements on the screen. Used to
     * prevent user interaction during asynchronous operations (loading, saving,
     * deleting).
     *
     * @param enabled {@code true} to enable all fields and buttons,
     *                {@code false} to disable them
     */
    private void setInteractive(boolean enabled) {
        setFieldEnabled(nameInput, enabled);
        setFieldEnabled(emailInput, enabled);
        setFieldEnabled(phoneInput, enabled);
        notificationsCheckbox.setEnabled(enabled);
        saveProfileButton.setEnabled(enabled);
        deleteProfileButton.setEnabled(enabled);
    }

    /**
     * Toggles the enabled, focusable, clickable, and long-clickable states of
     * an {@link EditText}. When disabling, the field's focus is also cleared.
     *
     * @param editText the field to update
     * @param enabled  {@code true} to enable, {@code false} to disable
     */
    private void setFieldEnabled(EditText editText, boolean enabled) {
        editText.setEnabled(enabled);
        editText.setFocusable(enabled);
        editText.setFocusableInTouchMode(enabled);
        editText.setClickable(enabled);
        editText.setLongClickable(enabled);
        if (!enabled) {
            editText.clearFocus();
        }
    }

    /**
     * Converts an empty or blank string to {@code null}.
     *
     * @param value the string to check
     * @return {@code null} if the value is empty or {@code null}, otherwise the
     *         original value
     */
    private String emptyToNull(String value) {
        return TextUtils.isEmpty(value) ? null : value;
    }

    /**
     * Returns the given string, or an empty string if it is {@code null}.
     *
     * @param value the string to check
     * @return the original value, or {@code ""} if {@code null}
     */
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
