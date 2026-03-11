package com.example.walrusevents;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.walrusevents.data.FirebaseAPIManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This test was written by Gemini 3, Google DeepMind
 * Fed in the FirebaseAPIMgr & ImageRepo and asked it for a way to test the connection
 * 11/03/26
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseImageStorageTest {

    @Test
    public void testEventPosterUpload() throws InterruptedException {
        // 1. Setup
        Event testEvent = new Event("Integration Test Party", "test-999");
        EventController controller = new EventController(testEvent);
        CountDownLatch latch = new CountDownLatch(1); // Used to wait for async Firebase

        // 2. Execution
        controller.generateQRAndPoster();
        Bitmap poster = controller.getPoster();

        FirebaseAPIManager apiMgr = new FirebaseAPIManager();

        // 3. Validation
        apiMgr.uploadBitmap(poster, "integration_test_poster", new FirebaseAPIManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess() {
                // If we hit this, the test passed!
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                // If we hit this, the test failed
                Assert.fail("Upload failed: " + error);
                latch.countDown();
            }
        });

        // Wait up to 10 seconds for the network to finish
        boolean success = latch.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("The upload timed out", success);
    }
}