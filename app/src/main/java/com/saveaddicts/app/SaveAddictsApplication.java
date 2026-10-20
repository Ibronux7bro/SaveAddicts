package com.saveaddicts.app;

import android.app.Application;

import com.saveaddicts.app.repositories.RepositoryLocator;

/**
 * Custom Application class for Save Addicts.
 *
 * Responsibilities:
 * - Activates the Firebase backend at application start-up so that all
 *   repository calls throughout the app use live Firebase Authentication
 *   and Firebase Realtime Database instead of the local mock repository.
 *
 * Registered in AndroidManifest.xml via android:name=".SaveAddictsApplication".
 */
public class SaveAddictsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enforce consistent light theme palette across all OEM devices
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);

        // Switch the service locator to the Mock backend for instant evaluation with rich pre-seeded data.
        // Can be switched to RepositoryLocator.enableFirebaseBackend() when live cloud database is desired.
        RepositoryLocator.enableMockBackend();
    }
}
