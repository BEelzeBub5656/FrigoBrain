package com.frigobrain.iot;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

/**
 * 华为云 IoTDA MQTT 客户端封装
 *
 * 认证方式: 设备密钥 (DeviceSecret)
 * 连接端口: 8883 (TLS)
 *
 * Topic:
 *   属性上报: $oc/devices/{device_id}/sys/properties/report
 *   命令响应: $oc/devices/{device_id}/sys/commands/response/{request_id}
 *   订阅命令: $oc/devices/{device_id}/sys/commands/request/#
 */
public class IotMqttClient {

    private static final String TAG = "IotMqttClient";
    private String deviceId;
    private String deviceSecret;
    private String iotServer;
    private boolean isConnected = false;

    public IotMqttClient(String iotServer, String deviceId, String deviceSecret) {
        this.iotServer = iotServer;
        this.deviceId = deviceId;
        this.deviceSecret = deviceSecret;
    }

    /**
     * 模拟连接华为云 IoT
     * 实际项目中应使用 Eclipse Paho MQTT 客户端进行 TLS MQTT 连接
     * 此处使用 HTTP 模拟属性上报，便于课程演示和快速验证
     */
    public boolean connect() {
        Log.d(TAG, "Connecting to Huawei Cloud IoT: " + iotServer);
        // 实际实现:
        // MqttConnectOptions options = new MqttConnectOptions();
        // options.setSocketFactory(SSLSocketFactory.getDefault());
        // options.setUserName(deviceId);
        // options.setPassword(deviceSecret.toCharArray());
        // client.connect(options);

        isConnected = true;
        return true;
    }

    /**
     * 上报属性到华为云 IoTDA 平台
     * 使用 HTTP POST 模拟 MQTT 属性上报
     *
     * @param serviceId 服务ID (如: "inventorySync")
     * @param properties 属性 JSON 对象
     */
    public void reportProperties(String serviceId, JSONObject properties) {
        try {
            JSONObject payload = new JSONObject();
            JSONObject service = new JSONObject();
            service.put("service_id", serviceId);
            service.put("properties", properties);
            service.put("event_time", new java.text.SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
                    .format(new java.util.Date()));
            payload.put("services", new org.json.JSONArray().put(service));

            Log.d(TAG, "Reporting: " + payload.toString());

            // 实际通过 MQTT publish 发送
            // client.publish("$oc/devices/" + deviceId + "/sys/properties/report", payload);

        } catch (Exception e) {
            Log.e(TAG, "Report failed", e);
        }
    }

    /**
     * 上报冰箱库存数据到华为云
     */
    public void reportInventory(int totalItems, int expiringItems, String itemsJson) {
        try {
            JSONObject props = new JSONObject();
            props.put("totalItems", totalItems);
            props.put("expiringItems", expiringItems);
            props.put("items", itemsJson);
            props.put("lastSyncTime", System.currentTimeMillis());
            reportProperties("inventorySync", props);
        } catch (Exception e) {
            Log.e(TAG, "Inventory sync failed", e);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disconnect() {
        isConnected = false;
    }
}
