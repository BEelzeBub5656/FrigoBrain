package com.frigobrain;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.frigobrain.data.db.AppDatabase;
import java.util.concurrent.Executors;

public class FrigoBrainApp extends Application {

    private static AppDatabase database;
    private static long currentUserId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        if (!p.getBoolean("db_seeded", false)) {
            Executors.newSingleThreadExecutor().execute(() -> {
                database.seedDatabase();
                p.edit().putBoolean("db_seeded", true).apply();
            });
        }
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
