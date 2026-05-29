<template>
  <div class="device-manage">
    <div class="page-header">
      <div>
        <h2 class="page-title">Device Management</h2>
        <p class="page-desc">Monitor and manage your smart fridge devices</p>
      </div>
      <el-button type="primary" :icon="Refresh" @click="fetchData" :loading="loading">
        Refresh
      </el-button>
    </div>

    <!-- Device Stats Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="6">
        <StatCard icon="Monitor" :value="devices.length" label="Total Devices" subtext="Registered devices" color="#4CAF50" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatCard icon="CircleCheck" :value="onlineCount" label="Online" subtext="Active devices" color="#67C23A" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatCard icon="CircleClose" :value="offlineCount" label="Offline" subtext="Inactive devices" color="#F56C6C" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatCard icon="ColdDrink" :value="avgTemp" label="Avg Temperature" subtext="Across all devices" color="#409EFF" />
      </el-col>
    </el-row>

    <!-- Device Table -->
    <el-card shadow="never" class="table-card">
      <el-table
        :data="devices"
        stripe
        style="width: 100%"
        v-loading="loading"
        @row-click="showShadow"
        highlight-current-row
      >
        <el-table-column prop="name" label="Device Name" min-width="160">
          <template #default="{ row }">
            <div class="device-name-cell">
              <el-icon :size="22" :color="row.type === 'Freezer' ? '#409EFF' : row.type === 'Sensor' ? '#9C27B0' : '#4CAF50'">
                <ColdDrink v-if="row.type === 'Fridge'" />
                <ColdDrink v-else-if="row.type === 'Freezer'" />
                <Monitor v-else />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="Type" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 'Freezer' ? 'primary' : row.type === 'Sensor' ? 'warning' : 'success'" size="small" effect="plain">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Status" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'online' ? 'success' : 'danger'" size="small" effect="dark" round>
              <el-icon style="margin-right: 4px;" :size="10">
                <CircleCheck v-if="row.status === 'online'" />
                <CircleClose v-else />
              </el-icon>
              {{ row.status === 'online' ? 'Online' : 'Offline' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="temperature" label="Temperature" width="130" align="center">
          <template #default="{ row }">
            <span :style="{ color: tempColor(row.temperature, row.type), fontWeight: 600 }">
              {{ row.temperature }}°C
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="humidity" label="Humidity" width="110" align="center">
          <template #default="{ row }">
            {{ row.humidity }}%
          </template>
        </el-table-column>
        <el-table-column prop="lastOnline" label="Last Online" min-width="170" align="center">
          <template #default="{ row }">
            <span class="last-online">{{ row.lastOnline }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Action" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click.stop="showShadow(row)">
              <el-icon><Search /></el-icon> View Shadow
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Device Shadow Dialog -->
    <el-dialog
      v-model="shadowDialogVisible"
      :title="`Device Shadow: ${selectedDevice?.name}`"
      width="720px"
      top="5vh"
      destroy-on-close
    >
      <template v-if="selectedDevice?.shadow">
        <el-tabs v-model="activeShadowTab" type="border-card">
          <el-tab-pane label="Reported State" name="reported">
            <pre class="shadow-json">{{ formatJSON(selectedDevice.shadow.state.reported) }}</pre>
          </el-tab-pane>
          <el-tab-pane label="Desired State" name="desired">
            <pre class="shadow-json">{{ formatJSON(selectedDevice.shadow.state.desired) }}</pre>
          </el-tab-pane>
          <el-tab-pane label="Metadata" name="metadata">
            <pre class="shadow-json">{{ formatJSON(selectedDevice.shadow.metadata) }}</pre>
          </el-tab-pane>
          <el-tab-pane label="Full Shadow" name="full">
            <pre class="shadow-json">{{ formatJSON(selectedDevice.shadow) }}</pre>
          </el-tab-pane>
        </el-tabs>

        <div class="command-section">
          <h4>Send Command</h4>
          <div class="command-bar">
            <el-select v-model="selectedCommand" placeholder="Select command" style="width: 200px;">
              <el-option
                v-for="cmd in availableCommands"
                :key="cmd.value"
                :label="cmd.label"
                :value="cmd.value"
              />
            </el-select>
            <el-button type="primary" :loading="sendingCommand" @click="sendCommandToDevice">
              Send
            </el-button>
            <el-tag v-if="commandResult" :type="commandResult.success ? 'success' : 'danger'" effect="plain">
              {{ commandResult.message }}
            </el-tag>
          </div>
        </div>
      </template>
      <div v-else class="empty-shadow">
        <el-empty description="No shadow data available" :image-size="80" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh, Monitor, ColdDrink, CircleCheck, CircleClose,
  Search
} from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import { getDevices, sendCommand } from '@/api/devices'

const loading = ref(false)
const devices = ref([])
const shadowDialogVisible = ref(false)
const selectedDevice = ref(null)
const activeShadowTab = ref('reported')
const selectedCommand = ref('')
const sendingCommand = ref(false)
const commandResult = ref(null)

const onlineCount = computed(() => devices.value.filter(d => d.status === 'online').length)
const offlineCount = computed(() => devices.value.filter(d => d.status === 'offline').length)

const avgTemp = computed(() => {
  const online = devices.value.filter(d => d.status === 'online')
  if (online.length === 0) return '--'
  const total = online.reduce((sum, d) => sum + d.temperature, 0)
  return (total / online.length).toFixed(1)
})

const availableCommands = computed(() => {
  if (!selectedDevice.value) return []
  const type = selectedDevice.value.type
  const commands = [
    { label: 'Sync Shadow', value: 'syncShadow' },
    { label: 'Reboot Device', value: 'reboot' },
  ]
  if (type === 'Fridge' || type === 'Freezer') {
    commands.push(
      { label: 'Set Temperature', value: 'setTemperature' },
      { label: 'Toggle Eco Mode', value: 'toggleEcoMode' },
      { label: 'Start Defrost', value: 'startDefrost' }
    )
  }
  if (type === 'Sensor') {
    commands.push(
      { label: 'Calibrate Sensors', value: 'calibrate' },
      { label: 'Update Interval', value: 'updateInterval' }
    )
  }
  return commands
})

function tempColor(temp, type) {
  if (type === 'Sensor') return temp > 30 ? '#F56C6C' : '#67C23A'
  if (type === 'Freezer') return temp > -15 ? '#F56C6C' : '#409EFF'
  return temp > 8 ? '#F56C6C' : temp > 6 ? '#E6A23C' : '#67C23A'
}

function formatJSON(obj) {
  return JSON.stringify(obj, null, 2)
}

async function fetchData() {
  loading.value = true
  try {
    devices.value = await getDevices()
  } finally {
    loading.value = false
  }
}

function showShadow(device) {
  selectedDevice.value = device
  activeShadowTab.value = 'reported'
  commandResult.value = null
  selectedCommand.value = ''
  shadowDialogVisible.value = true
}

async function sendCommandToDevice() {
  if (!selectedCommand.value || !selectedDevice.value) {
    ElMessage.warning('Please select a command')
    return
  }
  sendingCommand.value = true
  commandResult.value = null
  try {
    const result = await sendCommand(selectedDevice.value.id, {
      command: selectedCommand.value,
      timestamp: new Date().toISOString()
    })
    commandResult.value = result
    ElMessage.success(`Command sent: ${selectedCommand.value}`)
  } catch {
    commandResult.value = { success: false, message: 'Failed to send command' }
    ElMessage.error('Failed to send command')
  } finally {
    sendingCommand.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.device-manage {
  max-width: 1400px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-desc {
  font-size: 13px;
  color: #909399;
  margin: 4px 0 0;
}

.stat-row {
  margin-bottom: 20px;
}

.table-card {
  border-radius: var(--fb-radius);
}

.table-card :deep(.el-table th.el-table__cell) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

.table-card :deep(.el-table__row) {
  cursor: pointer;
}

.table-card :deep(.el-table__row:hover) {
  background-color: #f0f9f0 !important;
}

.device-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.last-online {
  font-size: 12px;
  color: #909399;
}

/* Shadow Dialog */
.shadow-json {
  background: #1a1a2e;
  color: #a0a0b8;
  padding: 16px 20px;
  border-radius: 8px;
  font-size: 12px;
  line-height: 1.6;
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  white-space: pre;
}

.shadow-json::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.shadow-json::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.command-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.command-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.command-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.empty-shadow {
  padding: 40px 0;
}

:deep(.el-tabs--border-card) {
  box-shadow: none;
  border: 1px solid #e8e8e8;
}

:deep(.el-tabs--border-card > .el-tabs__header) {
  background-color: #f8f9fa;
}
</style>
