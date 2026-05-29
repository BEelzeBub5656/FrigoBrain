package com.frigobrain;

import android.app.Application;

import com.frigobrain.data.db.AppDatabase;

public class FrigoBrainApp extends Application {

    private static AppDatabase database;
    private static long currentUserId = 1; // Default user

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    public static long getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(long userId) {
        currentUserId = userId;
    }
}
