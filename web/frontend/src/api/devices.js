import request from './index'

const mockDevices = [
  {
    id: 1,
    name: 'Main Fridge',
    type: 'Fridge',
    status: 'online',
    temperature: 3.5,
    humidity: 45,
    lastOnline: '2026-05-29 10:30:00',
    shadow: {
      state: {
        reported: {
          temperature: 3.5,
          humidity: 45,
          doorOpen: false,
          compressorRunning: true,
          defrosting: false,
          internalLight: true,
          totalPower: 0.85,
          compartments: {
            fridge: { temperature: 3.5, humidity: 45, targetTemp: 4 },
            freezer: { temperature: -18.2, humidity: 60, targetTemp: -18 },
            crisper: { temperature: 5.0, humidity: 80, targetTemp: 5 }
          },
          sensors: {
            doorSensor: 'closed',
            temperatureSensor: 'normal',
            humiditySensor: 'normal'
          }
        },
        desired: {
          targetTemp: 4,
          freezerTargetTemp: -18,
          ecoMode: false,
          vacationMode: false
        }
      },
      metadata: {
        model: 'FB-5000',
        firmware: 'v2.4.1',
        serialNumber: 'FB2024A0001',
        lastUpdate: '2026-05-29T10:30:00Z',
        uptime: '14d 6h 32m'
      }
    }
  },
  {
    id: 2,
    name: 'Freezer Unit',
    type: 'Freezer',
    status: 'online',
    temperature: -18.2,
    humidity: 60,
    lastOnline: '2026-05-29 10:29:00',
    shadow: {
      state: {
        reported: {
          temperature: -18.2,
          humidity: 60,
          doorOpen: false,
          compressorRunning: true,
          defrosting: false,
          internalLight: false,
          totalPower: 1.2,
          compartments: {
            main: { temperature: -18.2, targetTemp: -18 },
            iceMaker: { temperature: -15.0, targetTemp: -15 }
          },
          sensors: {
            doorSensor: 'closed',
            temperatureSensor: 'normal',
            iceLevel: 'full'
          }
        },
        desired: {
          targetTemp: -18,
          quickFreeze: false,
          ecoMode: true
        }
      },
      metadata: {
        model: 'FB-Freeze-2000',
        firmware: 'v2.3.8',
        serialNumber: 'FB2024F0001',
        lastUpdate: '2026-05-29T10:29:00Z',
        uptime: '30d 12h 15m'
      }
    }
  },
  {
    id: 3,
    name: 'Kitchen Sensor Hub',
    type: 'Sensor',
    status: 'online',
    temperature: 22.0,
    humidity: 42,
    lastOnline: '2026-05-29 10:30:00',
    shadow: {
      state: {
        reported: {
          temperature: 22.0,
          humidity: 42,
          airQuality: 'good',
          motionDetected: false,
          lightLevel: 320,
          batteryLevel: 85,
          sensors: {
            motion: 'inactive',
            smoke: 'clear',
            airQuality: 'good'
          }
        },
        desired: {
          motionSensitivity: 'medium',
          reportingInterval: 60
        }
      },
      metadata: {
        model: 'FB-Sensor-Hub',
        firmware: 'v1.8.2',
        serialNumber: 'FB2024S0001',
        lastUpdate: '2026-05-29T10:30:00Z',
        uptime: '7d 3h 45m'
      }
    }
  },
  {
    id: 4,
    name: 'Wine Cooler',
    type: 'Fridge',
    status: 'offline',
    temperature: 12.5,
    humidity: 55,
    lastOnline: '2026-05-28 18:00:00',
    shadow: {
      state: {
        reported: {
          temperature: 12.5,
          humidity: 55,
          doorOpen: false,
          compressorRunning: false,
          internalLight: true,
          totalPower: 0.3,
          compartments: {
            main: { temperature: 12.5, targetTemp: 12 }
          },
          sensors: {
            doorSensor: 'closed',
            temperatureSensor: 'warning-high'
          }
        },
        desired: {
          targetTemp: 12,
          ecoMode: true
        }
      },
      metadata: {
        model: 'FB-Wine-100',
        firmware: 'v2.1.0',
        serialNumber: 'FB2024W0001',
        lastUpdate: '2026-05-28T18:00:00Z',
        uptime: '0d 0h 0m'
      }
    }
  }
]

export async function getDevices() {
  try {
    const res = await request.get('/devices')
    return res.data || res
  } catch {
    return mockDevices
  }
}

export async function getDeviceShadow(id) {
  try {
    const res = await request.get(`/devices/${id}/shadow`)
    return res.data || res
  } catch {
    const device = mockDevices.find(d => d.id === id)
    return device ? device.shadow : null
  }
}

export async function sendCommand(id, command) {
  try {
    const res = await request.post(`/devices/${id}/commands`, command)
    return res.data || res
  } catch {
    return {
      success: true,
      message: `Command ${command.command} sent to device ${id} (demo mode)`,
      timestamp: new Date().toISOString()
    }
  }
}
