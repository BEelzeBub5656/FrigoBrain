package com.frigobrain.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.R;
import com.frigobrain.service.IotDataSyncService;
import com.frigobrain.util.Constants;

/**
 * 系统设置 - 华为云绑定、同步频率、通知开关
 */
public class SettingsActivity extends AppCompatActivity {

    private EditText etIotDeviceId, etIotDeviceSecret;
    private TextView tvIotStatus;
    private Button btnIotConnect, btnSave;
    private Spinner spinnerSyncFrequency;
    private Switch switchNotification;
    private SharedPreferences prefs;

    private static final String[] SYNC_FREQ_LABELS = {"15分钟", "30分钟", "1小时"};
    private static final long[] SYNC_FREQ_VALUES = {15 * 60 * 1000L, 30 * 60 * 1000L, 60 * 60 * 1000L};

    private static final String KEY_IOT_DEVICE_ID = "iot_device_id";
    private static final String KEY_IOT_DEVICE_SECRET = "iot_device_secret";
    private static final String KEY_SYNC_FREQ = "sync_frequency";
    private static final String KEY_NOTIFICATION = "notification_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        etIotDeviceId = findViewById(R.id.et_iot_device_id);
        etIotDeviceSecret = findViewById(R.id.et_iot_device_secret);
        tvIotStatus = findViewById(R.id.tv_iot_status);
        btnIotConnect = findViewById(R.id.btn_iot_connect);
        spinnerSyncFrequency = findViewById(R.id.spinner_sync_frequency);
        switchNotification = findViewById(R.id.switch_notification);
        btnSave = findViewById(R.id.btn_save_settings);

        // Setup sync frequency spinner
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, SYNC_FREQ_LABELS);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSyncFrequency.setAdapter(freqAdapter);

        // Load saved settings
        loadSettings();

        // IoT Connect button -> saves credentials and starts sync service
        btnIotConnect.setOnClickListener(v -> connectToIot());

        // Save all settings
        btnSave.setOnClickListener(v -> saveSettings());
    }

    /**
     * Load previously saved settings from SharedPreferences
     */
    private void loadSettings() {
        String deviceId = prefs.getString(KEY_IOT_DEVICE_ID, "");
        String deviceSecret = prefs.getString(KEY_IOT_DEVICE_SECRET, "");
        long savedFreq = prefs.getLong(KEY_SYNC_FREQ, Constants.SYNC_INTERVAL_MS);
        boolean notification = prefs.getBoolean(KEY_NOTIFICATION, true);

        etIotDeviceId.setText(deviceId);
        etIotDeviceSecret.setText(deviceSecret);
        switchNotification.setChecked(notification);

        boolean iotEnabled = prefs.getBoolean(Constants.KEY_IOT_ENABLED, false);
        tvIotStatus.setText(iotEnabled ? R.string.iot_connected : R.string.iot_disconnected);
        tvIotStatus.setTextColor(iotEnabled
                ? getColor(android.R.color.holo_green_dark)
                : getColor(android.R.color.darker_gray));

        // Set spinner selection based on saved frequency
        for (int i = 0; i < SYNC_FREQ_VALUES.length; i++) {
            if (SYNC_FREQ_VALUES[i] == savedFreq) {
                spinnerSyncFrequency.setSelection(i);
                break;
            }
        }
    }

    /**
     * Save IoT credentials and start the IoT data sync service
     */
    private void connectToIot() {
        String deviceId = etIotDeviceId.getText().toString().trim();
        String deviceSecret = etIotDeviceSecret.getText().toString().trim();

        if (deviceId.isEmpty() || deviceSecret.isEmpty()) {
            Toast.makeText(this, "请输入设备ID和密钥", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save IoT credentials
        prefs.edit()
                .putString(KEY_IOT_DEVICE_ID, deviceId)
                .putString(KEY_IOT_DEVICE_SECRET, deviceSecret)
                .putBoolean(Constants.KEY_IOT_ENABLED, true)
                .apply();

        tvIotStatus.setText(R.string.iot_connected);
        tvIotStatus.setTextColor(getColor(android.R.color.holo_green_dark));

        // Start the IoT data sync service
        Intent intent = new Intent(this, IotDataSyncService.class);
        startService(intent);

        Toast.makeText(this, "华为云IoT已连接，数据同步已启动", Toast.LENGTH_SHORT).show();
    }

    /**
     * Save all settings to SharedPreferences
     */
    private void saveSettings() {
        // Sync frequency
        int freqIndex = spinnerSyncFrequency.getSelectedItemPosition();
        long syncFreq = SYNC_FREQ_VALUES[Math.max(0, Math.min(freqIndex, SYNC_FREQ_VALUES.length - 1))];

        // Notification toggle
        boolean notificationEnabled = switchNotification.isChecked();

        // Save to SharedPreferences
        prefs.edit()
                .putLong(KEY_SYNC_FREQ, syncFreq)
                .putBoolean(KEY_NOTIFICATION, notificationEnabled)
                .apply();

        // Also save IoT fields if filled
        String deviceId = etIotDeviceId.getText().toString().trim();
        String deviceSecret = etIotDeviceSecret.getText().toString().trim();
        if (!deviceId.isEmpty() && !deviceSecret.isEmpty()) {
            prefs.edit()
                    .putString(KEY_IOT_DEVICE_ID, deviceId)
                    .putString(KEY_IOT_DEVICE_SECRET, deviceSecret)
                    .apply();
        }

        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }
}
