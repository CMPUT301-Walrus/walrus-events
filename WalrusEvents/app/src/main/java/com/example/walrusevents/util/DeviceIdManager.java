package com.example.walrusevents.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * DeviceIdManager
 * Generates and persists a unique device ID for passwordless authentication.
 *
 * Use this everywhere an entrant/device ID is needed instead of reading
 *
 * The ID is created once on first call and stored in SharedPreferences.
 * It survives app restarts but will be lost on uninstall or data clear
 * which is acceptable for this app's auth model.
 */
public class DeviceIdManager {

    private static final String PREFS_NAME = "walrus_prefs";
    private static final String KEY_DEVICE_ID = "device_id";

    // Cached in memory after first load so repeated calls don't hit disk
    private static String cachedId = null;

    /**
     * Returns the device ID, creating and storing one if it doesn't exist yet.
     * Safe to call from any Activity or class that has a Context.
     *
     * @param context Any context (Activity, Service, etc.)
     * @return A stable UUID string identifying this install
     */
    public static String getOrCreate(Context context) {
        if (cachedId != null) {
            return cachedId;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String id = prefs.getString(KEY_DEVICE_ID, null);

        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_DEVICE_ID, id).apply();
        }

        cachedId = id;
        return cachedId;
    }

    public static String replaceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String id = UUID.randomUUID().toString();
        prefs.edit().putString(KEY_DEVICE_ID, id).apply();
        cachedId = id;
        return getOrCreate(context);
    }

    private DeviceIdManager() {}
}
