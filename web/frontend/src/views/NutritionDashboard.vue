<template>
  <div class="nutrition">
    <div class="page-header">
      <div>
        <h2 class="page-title">Nutrition Dashboard</h2>
        <p class="page-desc">Weekly nutrition intake and analysis</p>
      </div>
      <el-date-picker
        v-model="weekRange"
        type="week"
        format="Week WW, YYYY"
        placeholder="Select week"
        size="default"
        @change="fetchData"
      />
    </div>

    <!-- Summary Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="stat in nutritionStats" :key="stat.label">
        <StatCard
          :icon="stat.icon"
          :value="stat.value"
          :label="stat.label"
          :subtext="stat.subtext"
          :color="stat.color"
        />
      </el-col>
    </el-row>

    <!-- Charts Row -->
    <el-row :gutter="20" class="content-row">
      <!-- Radar Chart -->
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>Nutrition vs Recommended</span>
              <el-tag size="small" type="success">Daily target</el-tag>
            </div>
          </template>
          <div ref="radarChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- Calorie Line Chart -->
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>7-Day Calorie Trend</span>
              <el-tag size="small" type="warning">kcal</el-tag>
            </div>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Macro Breakdown -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>Daily Macro Breakdown</span>
              <el-tag size="small" type="info">grams</el-tag>
            </div>
          </template>
          <div ref="macroChartRef" class="chart-container chart-macro"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Daily Table -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>Daily Nutrition Log</span>
        </div>
      </template>
      <el-table :data="dailyTable" stripe size="small" style="width: 100%">
        <el-table-column prop="date" label="Day" width="120" />
        <el-table-column prop="calories" label="Calories (kcal)" width="140" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.calories > 2200 ? '#F56C6C' : '#67C23A', fontWeight: 600 }">
              {{ row.calories }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="protein" label="Protein (g)" width="120" align="center" />
        <el-table-column prop="fat" label="Fat (g)" width="120" align="center" />
        <el-table-column prop="carbs" label="Carbs (g)" width="120" align="center" />
        <el-table-column label="Status" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.calories > 2200 ? 'warning' : 'success'"
              size="small"
              effect="plain"
            >
              {{ row.calories > 2200 ? 'Over target' : 'Within target' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts/core'
import StatCard from '@/components/StatCard.vue'
import { getNutritionDaily, getRecommendedIntake } from '@/api/statistics'

const weekRange = ref('')
const radarChartRef = ref(null)
const lineChartRef = ref(null)
const macroChartRef = ref(null)
let radarChart = null
let lineChart = null
let macroChart = null

const nutritionData = ref({ dates: [], calories: [], protein: [], fat: [], carbs: [] })
const recommended = ref({})

const nutritionStats = reactive([
  { icon: 'Sunny', value: '0', label: 'Avg Calories', subtext: 'kcal / day', color: '#4CAF50' },
  { icon: 'Goblet', value: '0', label: 'Avg Protein', subtext: 'g / day', color: '#409EFF' },
  { icon: 'Coin', value: '0', label: 'Avg Fat', subtext: 'g / day', color: '#E6A23C' },
  { icon: 'Grape', value: '0', label: 'Avg Carbs', subtext: 'g / day', color: '#F56C6C' }
])

const dailyTable = computed(() => {
  const d = nutritionData.value
  return d.dates.map((date, i) => ({
    date,
    calories: d.calories[i] || 0,
    protein: d.protein[i] || 0,
    fat: d.fat[i] || 0,
    carbs: d.carbs[i] || 0
  }))
})

async function fetchData() {
  try {
    nutritionData.value = await getNutritionDaily()
    recommended.value = getRecommendedIntake()

    const nd = nutritionData.value
    const avgCal = nd.calories.length ? Math.round(nd.calories.reduce((a, b) => a + b, 0) / nd.calories.length) : 0
    const avgPro = nd.protein.length ? Math.round(nd.protein.reduce((a, b) => a + b, 0) / nd.protein.length) : 0
    const avgFat = nd.fat.length ? Math.round(nd.fat.reduce((a, b) => a + b, 0) / nd.fat.length) : 0
    const avgCarb = nd.carbs.length ? Math.round(nd.carbs.reduce((a, b) => a + b, 0) / nd.carbs.length) : 0

    nutritionStats[0].value = avgCal
    nutritionStats[1].value = avgPro
    nutritionStats[2].value = avgFat
    nutritionStats[3].value = avgCarb

    nutritionStats[0].subtext = `Target: ${recommended.value.calories || 2200} kcal`
    nutritionStats[1].subtext = `Target: ${recommended.value.protein || 80} g`
    nutritionStats[2].subtext = `Target: ${recommended.value.fat || 65} g`
    nutritionStats[3].subtext = `Target: ${recommended.value.carbs || 260} g`
  } catch {
    // mock data handled in api
  }
}

function initCharts() {
  nextTick(() => {
    initRadarChart()
    initLineChart()
    initMacroChart()
  })
}

function initRadarChart() {
  if (!radarChartRef.value) return
  if (radarChart) radarChart.dispose()
  radarChart = echarts.init(radarChartRef.value)

  const rec = recommended.value
  const nd = nutritionData.value
  const avgCal = nd.calories.length ? Math.round(nd.calories.reduce((a, b) => a + b, 0) / nd.calories.length) : 0
  const avgPro = nd.protein.length ? Math.round(nd.protein.reduce((a, b) => a + b, 0) / nd.protein.length) : 0
  const avgFat = nd.fat.length ? Math.round(nd.fat.reduce((a, b) => a + b, 0) / nd.fat.length) : 0
  const avgCarb = nd.carbs.length ? Math.round(nd.carbs.reduce((a, b) => a + b, 0) / nd.carbs.length) : 0

  radarChart.setOption({
    radar: {
      indicator: [
        { name: 'Calories', max: (rec.calories || 2200) * 1.5 },
        { name: 'Protein', max: (rec.protein || 80) * 1.5 },
        { name: 'Fat', max: (rec.fat || 65) * 1.5 },
        { name: 'Carbs', max: (rec.carbs || 260) * 1.5 },
        { name: 'Fiber', max: (rec.fiber || 30) * 1.5 },
        { name: 'Sugar', max: (rec.sugar || 36) * 1.5 }
      ],
      shape: 'circle',
      splitNumber: 4,
      axisName: {
        color: '#606266',
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        lineStyle: { color: 'rgba(76, 175, 80, 0.1)' }
      },
      splitArea: {
        areaStyle: { color: ['rgba(76, 175, 80, 0.02)', 'rgba(76, 175, 80, 0.05)'] }
      },
      axisLine: {
        lineStyle: { color: 'rgba(76, 175, 80, 0.2)' }
      }
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: [avgCal, avgPro, avgFat, avgCarb],
            name: 'Actual Average',
            areaStyle: { color: 'rgba(76, 175, 80, 0.3)' },
            lineStyle: { color: '#4CAF50', width: 2 },
            itemStyle: { color: '#4CAF50' }
          },
          {
            value: [rec.calories || 2200, rec.protein || 80, rec.fat || 65, rec.carbs || 260, rec.fiber || 30, rec.sugar || 36],
            name: 'Recommended',
            areaStyle: { color: 'rgba(64, 158, 255, 0.2)' },
            lineStyle: { color: '#409EFF', width: 2, type: 'dashed' },
            itemStyle: { color: '#409EFF' }
          }
        ],
        symbol: 'circle',
        symbolSize: 6,
        emphasis: { lineStyle: { width: 3 } }
      }
    ],
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1
    },
    legend: {
      bottom: 0,
      data: ['Actual Average', 'Recommended'],
      textStyle: { color: '#909399', fontSize: 12 }
    }
  })
}

function initLineChart() {
  if (!lineChartRef.value) return
  if (lineChart) lineChart.dispose()
  lineChart = echarts.init(lineChartRef.value)

  const nd = nutritionData.value

  lineChart.setOption({
    grid: { left: 50, right: 20, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: nd.dates,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#909399', fontSize: 11, formatter: '{value} kcal' }
    },
    series: [
      {
        type: 'line',
        data: nd.calories,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#E6A23C', width: 3 },
        itemStyle: { color: '#E6A23C' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0.02)' }
          ])
        },
        markLine: {
          silent: true,
          data: [{ yAxis: recommended.value.calories || 2200 }],
          label: {
            formatter: 'Target: {c} kcal',
            color: '#909399',
            fontSize: 11,
            position: 'end'
          },
          lineStyle: { color: '#4CAF50', type: 'dashed', width: 1.5 }
        }
      }
    ],
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      formatter: (params) => {
        const p = params[0]
        return `<strong>${p.axisValue}</strong><br/>Calories: ${p.value} kcal`
      }
    }
  })
}

function initMacroChart() {
  if (!macroChartRef.value) return
  if (macroChart) macroChart.dispose()
  macroChart = echarts.init(macroChartRef.value)

  const nd = nutritionData.value

  macroChart.setOption({
    grid: { left: 50, right: 20, top: 10, bottom: 30 },
    xAxis: {
      type: 'category',
      data: nd.dates,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#909399', fontSize: 11, formatter: '{value} g' }
    },
    series: [
      {
        name: 'Protein',
        type: 'bar',
        stack: 'macro',
        data: nd.protein,
        itemStyle: { color: '#409EFF', borderRadius: 0 },
        barWidth: 40
      },
      {
        name: 'Fat',
        type: 'bar',
        stack: 'macro',
        data: nd.fat,
        itemStyle: { color: '#E6A23C', borderRadius: 0 }
      },
      {
        name: 'Carbs',
        type: 'bar',
        stack: 'macro',
        data: nd.carbs,
        itemStyle: { color: '#4CAF50', borderRadius: [4, 4, 0, 0] }
      }
    ],
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      formatter: (params) => {
        let html = `<strong>${params[0].axisValue}</strong><br/>`
        let total = 0
        params.forEach(p => {
          html += `${p.marker} ${p.seriesName}: ${p.value}g<br/>`
          total += p.value
        })
        html += `<hr style="margin:4px 0"/><span>Total: ${total}g</span>`
        return html
      }
    },
    legend: {
      bottom: 0,
      data: ['Protein', 'Fat', 'Carbs'],
      textStyle: { color: '#909399', fontSize: 12 }
    }
  })
}

function handleResize() {
  radarChart?.resize()
  lineChart?.resize()
  macroChart?.resize()
}

onMounted(async () => {
  await fetchData()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  radarChart?.dispose()
  lineChart?.dispose()
  macroChart?.dispose()
})
</script>

<style scoped>
.nutrition {
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

.content-row {
  margin-bottom: 20px;
}

.chart-card, .table-card {
  border-radius: var(--fb-radius);
  margin-bottom: 0;
}

.chart-card :deep(.el-card__header),
.table-card :deep(.el-card__header) {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.chart-macro {
  height: 300px;
}

.table-card :deep(.el-table th.el-table__cell) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}
</style>
