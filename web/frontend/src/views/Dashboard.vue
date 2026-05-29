<template>
  <div class="dashboard">
    <div class="page-header">
      <div>
        <h2 class="page-title">仪表盘</h2>
        <p class="page-desc">智能冰箱概览</p>
      </div>
      <el-button type="primary" @click="refreshData" :icon="Refresh" :loading="loading">
        刷新
      </el-button>
    </div>

    <!-- Stat Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="stat in statCards" :key="stat.label">
        <StatCard
          :icon="stat.icon"
          :value="stat.value"
          :label="stat.label"
          :subtext="stat.subtext"
          :color="stat.color"
        />
      </el-col>
    </el-row>

    <!-- Charts and Expiring -->
    <el-row :gutter="20" class="content-row">
      <!-- Calorie Trend Chart -->
      <el-col :xs="24" :md="14">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>7天热量趋势</span>
              <el-tag size="small" type="success">kcal</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- Expiring Soon -->
      <el-col :xs="24" :md="10">
        <el-card shadow="never" class="expiring-card">
          <template #header>
            <div class="card-header">
              <span>即将过期</span>
              <el-tag size="small" :type="expiringList.length > 3 ? 'warning' : 'info'">
                {{ expiringList.length }} 项
              </el-tag>
            </div>
          </template>
          <div v-if="expiringList.length === 0" class="empty-state">
            <el-empty description="暂无即将过期食材" :image-size="80" />
          </div>
          <el-table
            v-else
            :data="expiringList"
            stripe
            size="small"
            style="width: 100%"
            @row-click="goToInventory"
          >
            <el-table-column prop="name" label="食材" min-width="120" />
            <el-table-column prop="quantity" label="数量" width="60" align="center">
              <template #default="{ row }">
                {{ row.quantity }}{{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <FoodStatusTag :expiry-date="row.expiryDate" />
              </template>
            </el-table-column>
            <el-table-column label="过期时间" width="100" align="center">
              <template #default="{ row }">
                <span class="expiry-date">{{ formatDate(row.expiryDate) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Quick Stats Row -->
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>分类库存</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container chart-sm"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <span>小贴士</span>
              <el-icon color="#E6A23C"><WarningFilled /></el-icon>
            </div>
          </template>
          <div class="tips-list">
            <div v-for="(tip, idx) in tips" :key="idx" class="tip-item">
              <el-icon :color="tip.iconColor" :size="16"><component :is="tip.icon" /></el-icon>
              <span>{{ tip.text }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh, WarningFilled, ColdDrink, Sunny, InfoFilled, Bell } from '@element-plus/icons-vue'
import gsap from 'gsap'
import * as echarts from 'echarts/core'
import StatCard from '@/components/StatCard.vue'
import FoodStatusTag from '@/components/FoodStatusTag.vue'
import { getFoods, getExpiring } from '@/api/foods'
import { getInventorySummary, getTrend } from '@/api/statistics'

const router = useRouter()
const loading = ref(false)
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null

const foods = ref([])
const expiringList = ref([])
const summary = ref({ totalItems: 0, expiringSoon: 0, totalValue: 0, categoryDistribution: [] })
const trendData = ref({ days: [], calories: [] })

const statCards = reactive([
  { icon: 'ColdDrink', value: '0', label: '食材总数', subtext: '冰箱中', color: '#4CAF50' },
  { icon: 'Warning', value: '0', label: '即将过期', subtext: '3天内', color: '#E6A23C' },
  { icon: 'Sunny', value: '0', label: '日均热量', subtext: '本周', color: '#409EFF' },
  { icon: 'Delete', value: '0', label: '本月浪费', subtext: '本月公斤数', color: '#F56C6C' }
])

const tips = [
  { icon: 'ColdDrink', iconColor: '#4CAF50', text: '将冰箱设置为4°C以获得最佳食品保存效果' },
  { icon: 'Bell', iconColor: '#E6A23C', text: '本周有5件食材即将过期 - 请合理规划膳食' },
  { icon: 'InfoFilled', iconColor: '#409EFF', text: '每月清洁冰箱内部一次' },
  { icon: 'Sunny', iconColor: '#909399', text: '将水果和蔬菜分开存放' }
]

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

function goToInventory() {
  router.push('/inventory')
}

async function fetchData() {
  loading.value = true
  try {
    foods.value = await getFoods()
    expiringList.value = await getExpiring(3)
    summary.value = await getInventorySummary()
    trendData.value = await getTrend(7)

    // Update stat cards
    statCards[0].value = summary.value.totalItems || foods.value.length
    statCards[1].value = summary.value.expiringSoon || expiringList.value.length
    statCards[2].value = trendData.value.calories
      ? Math.round(trendData.value.calories.reduce((a, b) => a + b, 0) / trendData.value.calories.length)
      : 0
    statCards[3].value = summary.value.monthlyWaste || '3.2'

    // If we have real food data but no category distribution from backend
    if ((!summary.value.categoryDistribution || summary.value.categoryDistribution.length === 0) && foods.value.length > 0) {
      const cats = {}
      foods.value.forEach(f => {
        cats[f.category] = (cats[f.category] || 0) + 1
      })
      summary.value.categoryDistribution = Object.entries(cats).map(([name, count]) => ({ name, count }))
    }
  } finally {
    loading.value = false
  }
}

function initCharts() {
  nextTick(() => {
    initTrendChart()
    initCategoryChart()
  })
}

function initTrendChart() {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: trendData.value.days,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    series: [
      {
        type: 'line',
        data: trendData.value.calories,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#4CAF50', width: 3 },
        itemStyle: { color: '#4CAF50' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(76, 175, 80, 0.3)' },
            { offset: 1, color: 'rgba(76, 175, 80, 0.02)' }
          ])
        },
        markLine: {
          silent: true,
          data: [{ yAxis: statCards[2].value }],
          label: { show: false },
          lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 }
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
        return `<strong>${p.axisValue}</strong><br/>热量: ${p.value} kcal`
      }
    }
  })
}

function initCategoryChart() {
  if (!categoryChartRef.value) return
  if (categoryChart) categoryChart.dispose()
  categoryChart = echarts.init(categoryChartRef.value)

  const dist = summary.value.categoryDistribution || []
  const colors = ['#4CAF50', '#FF9800', '#F56C6C', '#409EFF', '#9C27B0']

  categoryChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c}',
          fontSize: 11,
          color: '#606266'
        },
        labelLine: {
          length: 10,
          length2: 15
        },
        data: dist.map((item, idx) => ({
          name: item.name,
          value: item.count,
          itemStyle: { color: colors[idx % colors.length] }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.15)'
          }
        }
      }
    ]
  })
}

function handleResize() {
  trendChart?.resize()
  categoryChart?.resize()
}

onMounted(async () => {
  await fetchData()
  initCharts()
  window.addEventListener('resize', handleResize)
  // GSAP staggered entrance animations
  nextTick(() => {
    gsap.from('.page-header', { y: -30, opacity: 0, duration: 0.5, ease: 'power3.out' })
    gsap.from('.stat-row .el-col', { y: 40, opacity: 0, duration: 0.5, stagger: 0.1, delay: 0.15, ease: 'power2.out' })
    gsap.from('.content-row .el-col', { y: 30, opacity: 0, duration: 0.5, stagger: 0.15, delay: 0.3, ease: 'power2.out' })
    gsap.from('.chart-container', { scale: 0.95, opacity: 0, duration: 0.5, delay: 0.5, ease: 'power2.out' })
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})

async function refreshData() {
  await fetchData()
  initCharts()
}
</script>

<style scoped>
.dashboard {
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

.chart-card, .expiring-card, .info-card {
  border-radius: var(--fb-radius);
  margin-bottom: 0;
}

.chart-card :deep(.el-card__header),
.expiring-card :deep(.el-card__header),
.info-card :deep(.el-card__header) {
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
  height: 260px;
}

.chart-sm {
  height: 220px;
}

.expiry-date {
  font-size: 12px;
  color: #909399;
}

.empty-state {
  padding: 20px 0;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 8px 0;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}
</style>
