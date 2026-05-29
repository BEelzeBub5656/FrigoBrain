<template>
  <div class="waste-report">
    <div class="page-header">
      <div>
        <h2 class="page-title">浪费报告</h2>
        <p class="page-desc">追踪和分析食物浪费情况</p>
      </div>
      <el-button type="primary" :icon="Refresh" @click="fetchData" :loading="loading">
        刷新
      </el-button>
    </div>

    <!-- Charts Row -->
    <el-row :gutter="20" class="content-row">
      <!-- Monthly Waste Bar Chart -->
      <el-col :xs="24" :md="14">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>月度浪费分类（公斤）</span>
              <el-tag size="small" type="danger">kg</el-tag>
            </div>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- Waste Reasons Pie Chart -->
      <el-col :xs="24" :md="10">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>浪费原因</span>
              <el-tag size="small" type="info">分布</el-tag>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Summary Table -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>浪费分类汇总</span>
          <div>
            <el-tag size="small" type="warning" class="total-waste-tag">
              总浪费: {{ totalWaste }} 公斤
            </el-tag>
            <el-tag size="small" type="danger" style="margin-left: 8px;">
              总成本: ${{ totalCost }}
            </el-tag>
          </div>
        </div>
      </template>
      <el-table :data="wasteSummary" stripe style="width: 100%" v-if="wasteSummary.length > 0">
        <el-table-column prop="category" label="分类" min-width="140">
          <template #default="{ row }">
            <div class="cat-cell">
              <el-icon :size="18" :color="categoryColor(row.category)">
                <component :is="categoryIcon(row.category)" />
              </el-icon>
              <span>{{ row.category }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="waste" label="总浪费（公斤）" width="160" align="center" sortable>
          <template #default="{ row }">
            <span class="waste-value">{{ row.waste }} kg</span>
          </template>
        </el-table-column>
        <el-table-column prop="cost" label="成本（$）" width="140" align="center" sortable>
          <template #default="{ row }">
            <span class="cost-value">${{ row.cost.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="浪费占比" width="160" align="center">
          <template #default="{ row }">
            <div class="progress-cell">
              <el-progress
                :percentage="Math.round((row.waste / totalWaste) * 100)"
                :color="categoryColor(row.category)"
                :stroke-width="12"
                :text-inside="true"
              />
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无浪费数据" :image-size="80" />
    </el-card>

    <!-- Waste Reduction Tips -->
    <el-card shadow="never" class="tips-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>减少浪费小贴士</span>
          <el-icon color="#4CAF50"><ColdDrink /></el-icon>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="tip in wasteTips" :key="tip.title">
          <div class="tip-card-item">
            <el-icon :size="24" :color="tip.color"><component :is="tip.icon" /></el-icon>
            <h4>{{ tip.title }}</h4>
            <p>{{ tip.desc }}</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import {
  Refresh, ColdDrink, Cherry, Goblet, ShoppingTrolley, Coin,
  Delete as DeleteIcon, Timer as TimerIcon, Sort as SortIcon
} from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { getWasteMonthly } from '@/api/statistics'

const loading = ref(false)
const barChartRef = ref(null)
const pieChartRef = ref(null)
let barChart = null
let pieChart = null

const wasteData = ref({
  categories: [],
  months: [],
  data: [],
  reasons: [],
  summary: []
})

const wasteTips = [
  { icon: DeleteIcon, color: '#4CAF50', title: '规划膳食', desc: '制定每周膳食计划，只买所需食材。' },
  { icon: TimerIcon, color: '#409EFF', title: '先进先出', desc: '优先使用较早购买的食材。存放时检查过期日期。' },
  { icon: SortIcon, color: '#E6A23C', title: '正确储存', desc: '水果和蔬菜分开存放。使用密封容器。' }
]

const wasteSummary = computed(() => wasteData.value.summary || [])

const totalWaste = computed(() => {
  const s = wasteData.value.summary
  return s.length ? s.reduce((a, b) => a + b.waste, 0).toFixed(1) : '0'
})

const totalCost = computed(() => {
  const s = wasteData.value.summary
  return s.length ? s.reduce((a, b) => a + b.cost, 0).toFixed(2) : '0'
})

function categoryColor(cat) {
  const colors = {
    Vegetable: '#4CAF50',
    Fruit: '#FF9800',
    Meat: '#F56C6C',
    Dairy: '#409EFF',
    Other: '#9C27B0'
  }
  return colors[cat] || '#909399'
}

function categoryIcon(cat) {
  const icons = {
    Vegetable: ColdDrink,
    Fruit: Cherry,
    Meat: Goblet,
    Dairy: ShoppingTrolley,
    Other: Coin
  }
  return icons[cat] || Coin
}

async function fetchData() {
  loading.value = true
  try {
    wasteData.value = await getWasteMonthly()
  } finally {
    loading.value = false
  }
  nextTick(() => initCharts())
}

function initCharts() {
  initBarChart()
  initPieChart()
}

function initBarChart() {
  if (!barChartRef.value) return
  if (barChart) barChart.dispose()
  barChart = echarts.init(barChartRef.value)

  const wd = wasteData.value
  const colors = ['#4CAF50', '#FF9800', '#F56C6C', '#409EFF', '#9C27B0']

  barChart.setOption({
    grid: { left: 50, right: 30, top: 10, bottom: 30 },
    legend: {
      data: wd.categories,
      bottom: 0,
      textStyle: { color: '#909399', fontSize: 12 }
    },
    xAxis: {
      type: 'category',
      data: wd.months,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      name: '公斤',
      nameTextStyle: { color: '#909399', fontSize: 11 },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    series: wd.categories.map((cat, idx) => ({
      name: cat,
      type: 'bar',
      stack: 'total',
      barWidth: 30,
      data: wd.data[idx] || [],
      itemStyle: {
        color: colors[idx % colors.length],
        borderRadius: idx === wd.categories.length - 1 ? [2, 2, 0, 0] : 0
      }
    })),
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      formatter: (params) => {
        let html = `<strong>${params[0].axisValue}</strong><br/>`
        let total = 0
        params.forEach(p => {
          html += `${p.marker} ${p.seriesName}: ${p.value} kg<br/>`
          total += p.value
        })
        html += `<hr style="margin:4px 0"/><span>总计: ${total.toFixed(1)} 公斤</span>`
        return html
      }
    }
  })
}

function initPieChart() {
  if (!pieChartRef.value) return
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieChartRef.value)

  const reasons = wasteData.value.reasons || []
  const colors = ['#F56C6C', '#E6A23C', '#409EFF', '#909399']

  pieChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}% ({d}%)'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%',
          fontSize: 12,
          color: '#606266',
          lineHeight: 18
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.15)'
          }
        },
        data: reasons.map((r, idx) => ({
          name: r.name,
          value: r.value,
          itemStyle: { color: colors[idx % colors.length] }
        }))
      }
    ]
  })
}

function handleResize() {
  barChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.waste-report {
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

.content-row {
  margin-bottom: 20px;
}

.chart-card, .table-card, .tips-card {
  border-radius: var(--fb-radius);
}

.chart-card :deep(.el-card__header),
.table-card :deep(.el-card__header),
.tips-card :deep(.el-card__header) {
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
  height: 340px;
}

.table-card :deep(.el-table th.el-table__cell) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

.cat-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.waste-value {
  font-weight: 600;
  color: #F56C6C;
}

.cost-value {
  font-weight: 600;
  color: #E6A23C;
}

.progress-cell {
  padding: 0 12px;
}

.total-waste-tag {
  border: none;
}

.tip-card-item {
  text-align: center;
  padding: 20px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: transform 0.2s;
}

.tip-card-item:hover {
  transform: translateY(-2px);
}

.tip-card-item h4 {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 12px 0 8px;
}

.tip-card-item p {
  font-size: 13px;
  color: #909399;
  margin: 0;
  line-height: 1.5;
}
</style>
