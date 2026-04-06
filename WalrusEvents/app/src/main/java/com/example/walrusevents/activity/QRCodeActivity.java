/**
 * This activity is activated when someone views the QR code for an event
 * It displays it larger so that you can be scanned by an external device
 */

package com.example.walrusevents.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walrusevents.R;
import com.example.walrusevents.util.QRGenerator;
import com.example.walrusevents.util.PermissionGatekeeper;

public class QRCodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionGatekeeper.requireNotBanned(this, false, permissions -> initializeUi());
    }

    private void initializeUi() {
        // Use the XML layout that matches your storyboard design
        setContentView(R.layout.qr_code);

        String eventId = getIntent().getStringExtra("EVENT_ID");
        String eventName = getIntent().getStringExtra("EVENT_NAME");

        TextView nameView = findViewById(R.id.event_name_qr);
        ImageView qrView = findViewById(R.id.qr_code_view);
        ImageView backBtn = findViewById(R.id.back_button_qr);

        nameView.setText(eventName);

        // Generate the code using your utility class
        Bitmap bitmap = QRGenerator.generateQRCode(eventId);
        if (bitmap != null) {
            qrView.setImageBitmap(bitmap);
        }

        backBtn.setOnClickListener(v -> finish());

        // Logic for Copy/Print can be added here later
    }
}
