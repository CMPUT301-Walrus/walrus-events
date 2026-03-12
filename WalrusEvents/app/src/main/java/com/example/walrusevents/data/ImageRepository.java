package com.example.walrusevents.data;

import android.graphics.Bitmap;
import android.net.Uri;
import java.io.File;

public class ImageRepository {

    private final FirebaseAPIManager firebaseAPIMgr;

    public ImageRepository(FirebaseAPIManager firebaseAPIMgr) {
        this.firebaseAPIMgr = firebaseAPIMgr;
    }

    // Since Firebase is async, we can't return 'boolean' immediately.
    // We pass the listener through so the UI knows when it's done.
    // UPDATE: I don't think this function will be used at any point
    public void storeImage(Uri imageUri, String fileName, FirebaseAPIManager.OnUploadCompleteListener listener) {
        firebaseAPIMgr.uploadImage(imageUri, fileName, listener);
    }

    public void storeGeneratedBitmap(Bitmap bitmap, String fileName, FirebaseAPIManager.OnUploadCompleteListener listener) {
        firebaseAPIMgr.uploadBitmap(bitmap, fileName, listener);
    }

    public void retrieveImage(String imageId, FirebaseAPIManager.OnDownloadCompleteListener listener) {
        firebaseAPIMgr.downloadImage(imageId, listener);
    }

    public void replaceImage(Uri newImageUri, String imageId, FirebaseAPIManager.OnUploadCompleteListener listener) {
        // In Firebase Storage, 'updating' is just uploading a new file with the same name.
        // It automatically overwrites the old one!
        storeImage(newImageUri, imageId, listener);
    }
}