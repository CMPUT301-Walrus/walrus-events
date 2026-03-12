package com.example.walrusevents.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PosterGenerator {
    /**
     * Create a Poster that has the event description and QR Code
     * @param title
     * @param description: description of event
     * @param qrCode: QR code for event (said code also directs user to this poster)
     * @return: returns a bitmap of poster to be set on screen when needed (when user scans QR code)
     */
    public static Bitmap createEventPoster(String title, String description, Bitmap qrCode) {
        // Set poster specifications
        int width = 2000;
        int height = 3000;
        Bitmap poster = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(poster);
        canvas.drawColor(Color.WHITE); // Solid white background

        // Set Text type
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);

        // Center & bold title
        paint.setTextSize(160f); // Large and bold
        canvas.drawText(title, width / 2f, 300, paint);

        // Center and unbold event description
        paint.setTypeface(android.graphics.Typeface.DEFAULT);
        paint.setTextSize(70f);
        canvas.drawText(description, width / 2f, 550, paint);

        // Generate and input QR code into poster
        if (qrCode != null) {
            Bitmap largeQR = Bitmap.createScaledBitmap(qrCode, 1000, 1000, true);

            canvas.drawBitmap(largeQR, 500, 1200, null);

            // Text below QR code
            paint.setTextSize(60f);
            paint.setColor(Color.GRAY);
            canvas.drawText("SCAN TO VIEW EVENT DETAILS", width / 2f, 2400, paint);
        }

        return poster;
    }
}
