package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_info")
public class DeviceInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "device_id")
    private long deviceId;

    @NonNull
    @ColumnInfo(name = "device_name")
    private String deviceName;

    @NonNull
    @ColumnInfo(name = "iot_device_id")
    private String iotDeviceId;

    @NonNull
    @ColumnInfo(name = "device_type", defaultValue = "FRIDGE")
    private String deviceType;

    @NonNull
    @ColumnInfo(name = "status", defaultValue = "OFFLINE")
    private String status;

    @ColumnInfo(name = "temperature")
    private double temperature;

    @ColumnInfo(name = "humidity")
    private double humidity;

    @ColumnInfo(name = "last_online_time")
    private long lastOnlineTime;

    @ColumnInfo(name = "fw_version")
    private String fwVersion;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public DeviceInfo(@NonNull String deviceName, @NonNull String iotDeviceId) {
        this.deviceName = deviceName;
        this.iotDeviceId = iotDeviceId;
        this.deviceType = "FRIDGE";
        this.status = "OFFLINE";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getDeviceId() { return deviceId; }
    @NonNull public String getDeviceName() { return deviceName; }
    @NonNull public String getIotDeviceId() { return iotDeviceId; }
    @NonNull public String getDeviceType() { return deviceType; }
    @NonNull public String getStatus() { return status; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public long getLastOnlineTime() { return lastOnlineTime; }
    public String getFwVersion() { return fwVersion; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }
    public void setDeviceName(@NonNull String deviceName) { this.deviceName = deviceName; }
    public void setIotDeviceId(@NonNull String iotDeviceId) { this.iotDeviceId = iotDeviceId; }
    public void setDeviceType(@NonNull String deviceType) { this.deviceType = deviceType; }
    public void setStatus(@NonNull String status) { this.status = status; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setLastOnlineTime(long lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
    public void setFwVersion(String fwVersion) { this.fwVersion = fwVersion; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
