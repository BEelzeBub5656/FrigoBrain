package com.frigobrain.web.service;

import com.frigobrain.web.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IotDeviceService {

    private final FoodRepository foodRepository;

    public IotDeviceService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<Map<String, Object>> listDevices() {
        List<Map<String, Object>> devices = new ArrayList<>();

        Map<String, Object> device1 = new LinkedHashMap<>();
        device1.put("deviceId", "fridge-001");
        device1.put("deviceName", "FrigoBrain Smart Fridge - Kitchen");
        device1.put("status", "online");
        device1.put("type", "fridge");
        device1.put("model", "FB-2000");
        device1.put("lastOnline", LocalDateTime.now().minusMinutes(3)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        devices.add(device1);

        Map<String, Object> device2 = new LinkedHashMap<>();
        device2.put("deviceId", "fridge-002");
        device2.put("deviceName", "FrigoBrain Smart Fridge - Garage");
        device2.put("status", "offline");
        device2.put("type", "fridge");
        device2.put("model", "FB-1000");
        device2.put("lastOnline", LocalDateTime.now().minusDays(2)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        devices.add(device2);

        Map<String, Object> device3 = new LinkedHashMap<>();
        device3.put("deviceId", "sensor-001");
        device3.put("deviceName", "Temperature/Humidity Sensor");
        device3.put("status", "online");
        device3.put("type", "sensor");
        device3.put("model", "FB-SENSOR-X1");
        device3.put("lastOnline", LocalDateTime.now().minusMinutes(1)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        devices.add(device3);

        return devices;
    }

    public Map<String, Object> getDeviceShadow(String deviceId) {
        long totalItems = foodRepository.countByIsConsumedFalse();
        List<Object[]> categoryCounts = foodRepository.countByCategory();
        long expiringCount = foodRepository.findExpiringFoodsList(
                java.time.LocalDate.now().plusDays(7)).size();

        Map<String, Object> shadow = new LinkedHashMap<>();
        shadow.put("deviceId", deviceId);
        shadow.put("reported", getReportedState(totalItems, categoryCounts));
        shadow.put("desired", getDesiredState());
        shadow.put("metadata", getMetadata());
        shadow.put("version", 5);
        shadow.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return shadow;
    }

    private Map<String, Object> getReportedState(long totalItems, List<Object[]> categoryCounts) {
        Map<String, Object> reported = new LinkedHashMap<>();

        Map<String, Object> environment = new LinkedHashMap<>();
        environment.put("temperature", 4.5);
        environment.put("humidity", 65.0);
        environment.put("temperatureUnit", "celsius");
        reported.put("environment", environment);

        Map<String, Object> inventory = new LinkedHashMap<>();
        inventory.put("totalItems", totalItems);
        inventory.put("categoryBreakdown", getCategoryMap(categoryCounts));
        inventory.put("lastUpdated", LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        reported.put("inventory", inventory);

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("doorState", "closed");
        status.put("powerConsumption", 1.2);
        status.put("powerUnit", "kWh");
        status.put("compressorStatus", "running");
        status.put("defrosting", false);
        status.put("errorCodes", Collections.emptyList());
        reported.put("status", status);

        return reported;
    }

    private Map<String, Object> getDesiredState() {
        Map<String, Object> desired = new LinkedHashMap<>();
        desired.put("targetTemperature", 4.0);
        desired.put("targetHumidity", 60.0);
        desired.put("ecoMode", true);
        desired.put("vacationMode", false);
        return desired;
    }

    private Map<String, Object> getMetadata() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("manufacturer", "FrigoBrain Inc.");
        metadata.put("firmwareVersion", "2.4.1");
        metadata.put("hardwareVersion", "Rev C");
        metadata.put("serialNumber", "FB-2000-2024-00042");
        metadata.put("warrantyUntil", "2027-06-30");
        return metadata;
    }

    private Map<String, Long> getCategoryMap(List<Object[]> categoryCounts) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : categoryCounts) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }

    public Map<String, Object> sendCommand(String deviceId, String commandType,
                                           Map<String, Object> payload) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("deviceId", deviceId);
        response.put("command", commandType);
        response.put("status", "success");
        response.put("message", "Command '" + commandType + "' sent to " + deviceId + " successfully");
        response.put("payload", payload != null ? payload : Collections.emptyMap());
        response.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("estimatedExecutionTime", "5s");

        return response;
    }
}
