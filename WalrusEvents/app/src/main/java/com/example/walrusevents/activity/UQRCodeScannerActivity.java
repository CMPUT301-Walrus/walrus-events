package com.example.walrusevents.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Designed to pop up when user wants to scan a QR code
 * Scans and links in app to the poster stored in Firebase
 */
public class UQRCodeScannerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Immediately start the ZXing scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a Walrus Event QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
                finish(); // Close this activity and go back
            } else {
                handleResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Processes the scanned string.
     * Since we are avoiding deep links, we treat the scan as a raw event ID.
     */
    private void handleResult(String scannedData) {
        if (scannedData != null && !scannedData.isEmpty()) {
            // Debugging message
            Log.d("SCAN_SUCCESS", "Scanned Event ID: " + scannedData);

            // Navigate directly to User Event Details
            Intent intent = new Intent(this, UEventDetailsActivity.class);
            intent.putExtra("EVENT_ID", scannedData.trim());
            startActivity(intent);

            finish();
        } else {
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}