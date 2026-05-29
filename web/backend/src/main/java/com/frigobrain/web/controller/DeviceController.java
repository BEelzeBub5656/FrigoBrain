package com.frigobrain.web.controller;

import com.frigobrain.web.service.IotDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final IotDeviceService iotDeviceService;

    public DeviceController(IotDeviceService iotDeviceService) {
        this.iotDeviceService = iotDeviceService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listDevices() {
        List<Map<String, Object>> devices = iotDeviceService.listDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}/shadow")
    public ResponseEntity<Map<String, Object>> getDeviceShadow(@PathVariable String id) {
        Map<String, Object> shadow = iotDeviceService.getDeviceShadow(id);
        return ResponseEntity.ok(shadow);
    }

    @PostMapping("/{id}/commands")
    public ResponseEntity<Map<String, Object>> sendCommand(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> body) {
        String commandType = "custom";
        Map<String, Object> payload = null;

        if (body != null) {
            commandType = (String) body.getOrDefault("command", "custom");
            payload = (Map<String, Object>) body.get("payload");
        }

        Map<String, Object> response = iotDeviceService.sendCommand(id, commandType, payload);
        return ResponseEntity.ok(response);
    }
}
