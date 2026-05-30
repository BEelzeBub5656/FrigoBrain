package com.frigobrain.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.iot.IotMqttClient;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * 华为云 IoT 数据同步服务
 *
 * 生命周期: START_STICKY（被杀后自动重启）
 * 定时器: AlarmManager（每30分钟）
 * 支持: 手动Intent触发同步、开机自启
 */
public class IotDataSyncService extends Service {

    private static final String TAG = "IotDataSyncService";
    private AppDatabase db;
    private IotMqttClient mqttClient;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        db = AppDatabase.getInstance(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "IotDataSyncService started");

        // 使用内置设备凭证自动连接
        String deviceId = Constants.IOT_DEVICE_ID;
        String deviceSecret = Constants.IOT_DEVICE_SECRET;

        if (mqttClient == null && !deviceId.isEmpty()) {
            String wsUri = "ssl://" + Constants.IOT_MQTT_HOST + ":" + Constants.IOT_MQTT_PORT;
            mqttClient = new IotMqttClient(wsUri, deviceId, deviceSecret);
            mqttClient.connect();
        }

        // 执行同步
        syncInventoryData();

        // 定时下次同步
        scheduleNextSync();

        return START_STICKY;
    }

    private void syncInventoryData() {
        long userId = FrigoBrainApp.getCurrentUserId();
        long now = System.currentTimeMillis();
        long threeDaysLater = DateUtils.daysFromNow(3);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // 获取食材总计
                var foods = db.foodItemDao().getActiveByUser(userId).getValue();
                int totalItems = foods != null ? foods.size() : 0;

                // 获取即将过期数量
                var expiring = db.foodItemDao().getExpiringFoodsWithRecipes(userId, now, threeDaysLater).getValue();
                int expiringItems = expiring != null ? expiring.size() : 0;

                // 构建食材列表 JSON
                JSONArray itemsArray = new JSONArray();
                if (foods != null) {
                    for (var f : foods) {
                        JSONObject item = new JSONObject();
                        item.put("name", f.getName());
                        item.put("quantity", f.getQuantity());
                        item.put("unit", f.getUnit());
                        item.put("expiry", f.getExpiryDate());
                        itemsArray.put(item);
                    }
                }

                if (mqttClient != null && mqttClient.isConnected()) {
                    mqttClient.reportInventory(totalItems, expiringItems, itemsArray.toString());
                    Log.d(TAG, "Synced: " + totalItems + " items, " + expiringItems + " expiring");
                }
            } catch (Exception e) {
                Log.e(TAG, "Sync error", e);
            }
        });
    }

    private void scheduleNextSync() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, com.frigobrain.broadcast.AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + Constants.SYNC_INTERVAL_MS;
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
