<!-- 管理端数据统计：用户数、商品、订单、销售额及图表 -->
<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card stat-card--users" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon stat-icon--users">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">总用户数</div>
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card stat-card--products" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon stat-icon--products">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">商品总数</div>
              <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card stat-card--orders" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon stat-icon--orders">
              <el-icon><ShoppingBag /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card stat-card--sales" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon stat-icon--sales">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">总销售额</div>
              <div class="stat-value stat-value--money">¥{{ stats.totalSales || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span class="chart-dot chart-dot--bar" />
              核心指标柱状图
            </div>
          </template>
          <div ref="overviewChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span class="chart-dot chart-dot--pie" />
              指标占比饼图
            </div>
          </template>
          <div ref="ratioChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/** ECharts 展示趋势；仅管理员路由可见 */
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import api from '../../utils/api'
import { User, Goods, ShoppingBag, Money } from '@element-plus/icons-vue'

const stats = ref({})
const overviewChartRef = ref(null)
const ratioChartRef = ref(null)
let overviewChart = null
let ratioChart = null
let echartsModule = null

const getStatSeries = () => ([
  { name: '用户数', value: Number(stats.value.totalUsers || 0) },
  { name: '商品数', value: Number(stats.value.totalProducts || 0) },
  { name: '订单数', value: Number(stats.value.totalOrders || 0) }
])

const ensureEcharts = async () => {
  if (echartsModule) return echartsModule
  const mod = await import('echarts')
  echartsModule = mod.default || mod
  return echartsModule
}

const renderCharts = async () => {
  const data = getStatSeries()
  if (!overviewChartRef.value || !ratioChartRef.value) return
  const echarts = await ensureEcharts()

  if (!overviewChart) {
    overviewChart = echarts.init(overviewChartRef.value)
  }
  if (!ratioChart) {
    ratioChart = echarts.init(ratioChartRef.value)
  }

  const barColor =
    echarts.graphic && echarts.graphic.LinearGradient
      ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#f19e8f' },
          { offset: 1, color: '#bc3823' }
        ])
      : '#bc3823'

  overviewChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 40 },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: data.map(item => item.value),
        itemStyle: { color: barColor },
        barWidth: 32
      }
    ]
  })

  const pieColors = ['#bc3823', '#52c41a', '#faad14']

  ratioChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: '#595959' } },
    color: pieColors,
    series: [
      {
        name: '占比',
        type: 'pie',
        radius: ['35%', '65%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { color: '#595959' },
        data
      }
    ]
  })
}

const handleResize = () => {
  overviewChart?.resize()
  ratioChart?.resize()
}

const loadStats = async () => {
  try {
    const res = await api.get('/admin/statistics/dashboard')
    stats.value = res.data || {}
    await nextTick()
    await renderCharts()
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

onMounted(() => {
  loadStats()
  window.addEventListener('resize', handleResize)
})

watch(stats, () => {
  renderCharts()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  overviewChart?.dispose()
  ratioChart?.dispose()
  overviewChart = null
  ratioChart = null
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-row {
  margin-bottom: 4px;
}

.stat-row :deep(.el-col) {
  margin-bottom: 16px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px 22px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 18px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 26px;
  flex-shrink: 0;
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.12);
}

.stat-icon--users {
  background: linear-gradient(135deg, #5b8cff, #3a6df0);
}

.stat-icon--products {
  background: linear-gradient(135deg, #52c41a, #389e0d);
}

.stat-icon--orders {
  background: linear-gradient(135deg, #faad14, #d48806);
}

.stat-icon--sales {
  background: linear-gradient(135deg, #d45a47, #bc3823);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-value--money {
  font-size: 22px;
  color: var(--primary-color);
}

.chart-row {
  margin-top: 4px;
}

.chart-row :deep(.el-col) {
  margin-bottom: 16px;
}

.chart-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.chart-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.chart-dot--bar {
  background: linear-gradient(180deg, #f19e8f, #bc3823);
}

.chart-dot--pie {
  background: conic-gradient(#bc3823, #52c41a, #faad14, #bc3823);
}

.chart-container {
  height: 320px;
}
</style>

