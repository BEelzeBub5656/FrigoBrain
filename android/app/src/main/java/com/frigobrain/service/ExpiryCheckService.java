package com.frigobrain.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.MainActivity;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;

import java.util.Calendar;
import java.util.concurrent.Executors;

/**
 * 过期检查服务
 * 每天8:00检查即将过期食材，通过通知栏提醒用户
 */
public class ExpiryCheckService extends Service {

    private AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = AppDatabase.getInstance(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkExpiringFoods();
        scheduleNextCheck();

        // START_STICKY: 被系统杀死后自动重启
        return START_STICKY;
    }

    private void checkExpiringFoods() {
        long userId = FrigoBrainApp.getCurrentUserId();
        long now = System.currentTimeMillis();
        long threeDaysLater = DateUtils.daysFromNow(3);

        Executors.newSingleThreadExecutor().execute(() -> {
            var items = db.foodItemDao().getExpiringFoodsWithRecipes(userId, now, threeDaysLater);
            db.foodItemDao().getExpiringFoodsWithRecipes(userId, now, threeDaysLater)
                .observeForever(foods -> {
                    if (foods != null && !foods.isEmpty()) {
                        String title = foods.size() + " 种食材即将过期";
                        String msg = "建议尽快使用，点击查看推荐菜谱";
                        sendNotification(title, msg);
                    }
                });
        });
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_EXPIRY)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(Constants.NOTIFY_EXPIRY_ID, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_EXPIRY,
                    "食材过期提醒",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("冰箱食材即将过期时发送提醒通知");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void scheduleNextCheck() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, com.frigobrain.broadcast.AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
