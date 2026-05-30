package com.frigobrain.iot;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * 华为云 IoTDA MQTT 客户端（Eclipse Paho 真实实现）
 *
 * 端点: 21158429fd.st1.iotda-device.cn-north-4.myhuaweicloud.com
 * 端口: 443 (MQTT over WebSocket)
 * 认证: 设备密钥 (DeviceSecret)
 *
 * Topic:
 *   属性上报: $oc/devices/{device_id}/sys/properties/report
 *   命令订阅: $oc/devices/{device_id}/sys/commands/request/#
 *   命令响应: $oc/devices/{device_id}/sys/commands/response/{request_id}
 */
public class IotMqttClient {

    private static final String TAG = "IotMqttClient";
    private final String deviceId;
    private final String deviceSecret;
    private final String serverUri;

    private MqttClient mqttClient;
    private boolean isConnected = false;

    /**
     * @param serverUri    MQTT Broker 地址 (格式: wss://host:port/mqtt)
     * @param deviceId     华为云设备ID
     * @param deviceSecret 华为云设备密钥
     */
    public IotMqttClient(String serverUri, String deviceId, String deviceSecret) {
        this.serverUri = serverUri;
        this.deviceId = deviceId;
        this.deviceSecret = deviceSecret;
    }

    /**
     * 连接到华为云 IoTDA（真实 MQTT over WebSocket TLS）
     */
    public boolean connect() {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHH", java.util.Locale.US);
            fmt.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String ts = fmt.format(new Date());
            String clientId = deviceId + "_0_0_" + ts;
            String pwd = hmacHex(deviceSecret, ts);
            Log.i(TAG, "Connecting: uri=" + serverUri + " clientId=" + clientId + " user=" + deviceId);
            Log.i(TAG, "Password(hidden)=" + pwd.substring(0,8) + "… len=" + pwd.length() + " ts=" + ts);
            mqttClient = new MqttClient(serverUri, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(deviceId);
            options.setPassword(pwd.toCharArray());
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(120);
            options.setAutomaticReconnect(true);

            // Paho ssl:// 协议原生处理 TLS，无需手动 SSL

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.w(TAG, "MQTT connection lost: " + cause.getMessage());
                    isConnected = false;
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.d(TAG, "Message arrived: " + topic + " -> " + new String(message.getPayload()));
                    // Handle commands from cloud
                    if (topic.contains("/sys/commands/request/")) {
                        handleCommand(topic, message);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery complete: " + token.getMessageId());
                }
            });

            IMqttToken token = mqttClient.connectWithResult(options);
            token.waitForCompletion(10000);

            if (mqttClient.isConnected()) {
                isConnected = true;
                Log.i(TAG, "Connected to Huawei Cloud IoTDA");

                // Subscribe to commands
                String cmdTopic = "$oc/devices/" + deviceId + "/sys/commands/request/#";
                mqttClient.subscribe(cmdTopic, 1);
                Log.d(TAG, "Subscribed to: " + cmdTopic);

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "MQTT connect failed: " + e.getMessage(), e);
            isConnected = false;
        }
        return false;
    }

    /**
     * 上报属性到华为云 IoTDA
     */
    public void reportProperties(String serviceId, JSONObject properties) {
        if (!isConnected || mqttClient == null) {
            Log.w(TAG, "Not connected, cannot report");
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            JSONObject service = new JSONObject();
            service.put("service_id", serviceId);
            service.put("properties", properties);
            service.put("event_time",
                    new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'").format(new Date()));
            payload.put("services", new JSONArray().put(service));

            String topic = "$oc/devices/" + deviceId + "/sys/properties/report";
            MqttMessage msg = new MqttMessage(payload.toString().getBytes("UTF-8"));
            msg.setQos(1);
            mqttClient.publish(topic, msg);

            Log.d(TAG, "Reported to " + topic + ": " + payload.toString());
        } catch (Exception e) {
            Log.e(TAG, "Report properties failed", e);
        }
    }

    /**
     * 上报冰箱库存数据
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

    /**
     * 上传设备状态
     */
    public void reportDeviceStatus(String status, double temperature, double humidity) {
        try {
            JSONObject props = new JSONObject();
            props.put("status", status);
            props.put("temperature", temperature);
            props.put("humidity", humidity);
            props.put("fwVersion", "1.0.0");
            reportProperties("fridgeStatus", props);
        } catch (Exception e) {
            Log.e(TAG, "Device status sync failed", e);
        }
    }

    /**
     * 处理云端下发的命令
     */
    private void handleCommand(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            JSONObject cmd = new JSONObject(payload);
            String commandName = cmd.optString("command_name", "");
            String requestId = cmd.optString("request_id", "");

            Log.i(TAG, "Received command: " + commandName + " requestId: " + requestId);

            // Respond to command
            JSONObject response = new JSONObject();
            response.put("result_code", 0);
            response.put("response", new JSONObject().put("result", "success"));

            String responseTopic = "$oc/devices/" + deviceId
                    + "/sys/commands/response/" + requestId;
            MqttMessage respMsg = new MqttMessage(response.toString().getBytes("UTF-8"));
            respMsg.setQos(1);
            mqttClient.publish(responseTopic, respMsg);
        } catch (Exception e) {
            Log.e(TAG, "Handle command failed", e);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    /** hex(hmac_sha256(secret, timestamp)) — 匹配华为云 MQTTS 密码格式 */
    private String hmacHex(String secret, String ts) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(ts.getBytes("UTF-8"), "HmacSHA256"));
            byte[] raw = mac.doFinal(secret.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return secret; }
    }

    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (Exception e) {
            Log.e(TAG, "Disconnect failed", e);
        }
        isConnected = false;
    }
}
