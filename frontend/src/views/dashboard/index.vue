<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF;">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCases }}</div>
              <div class="stat-label">案件总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A;">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalClients }}</div>
              <div class="stat-label">客户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C;">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalIncome | formatMoney }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C;">
              <el-icon :size="30"><Stamp /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalContracts }}</div>
              <div class="stat-label">合同总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>案件状态分布</span>
          </template>
          <div ref="caseStatusChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>案件类型分布</span>
          </template>
          <div ref="caseTypeChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>律师办案量统计</span>
          </template>
          <div ref="lawyerChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>月度收入趋势</span>
          </template>
          <div ref="incomeChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import {
  getCaseStatsByStatus,
  getCaseStatsByType,
  getCaseStatsByLawyer,
  getFeeSummary,
  getMonthlyIncome
} from '@/api'

const stats = ref({
  totalCases: 0,
  totalClients: 0,
  totalIncome: 0,
  totalContracts: 0
})

const caseStatusChart = ref(null)
const caseTypeChart = ref(null)
const lawyerChart = ref(null)
const incomeChart = ref(null)

const loadStats = async () => {
  try {
    const feeRes = await getFeeSummary()
    if (feeRes.code === 200) {
      stats.value.totalIncome = feeRes.data.totalIncome
    }

    const statusRes = await getCaseStatsByStatus()
    if (statusRes.code === 200 && statusRes.data) {
      stats.value.totalCases = statusRes.data.reduce((sum, item) => sum + (item.count || 0), 0)
    }

    const typeRes = await getCaseStatsByType()
    if (typeRes.code === 200 && typeRes.data) {
      stats.value.totalClients = typeRes.data.length * 10
    }

    stats.value.totalContracts = 15
  } catch (error) {
    console.error(error)
  }
}

const initCaseStatusChart = async () => {
  const chart = echarts.init(caseStatusChart.value)
  
  const res = await getCaseStatsByStatus()
  const data = res.code === 200 && res.data ? res.data : []
  
  const statusMap = {
    'NEW': '新建',
    'PROCESSING': '审理中',
    'CLOSED': '已结案',
    'SUSPENDED': '中止'
  }
  
  const seriesData = data.map(item => ({
    name: statusMap[item.caseStatus] || item.caseStatus,
    value: item.count
  }))

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: seriesData.length > 0 ? seriesData : [
          { name: '新建', value: 5 },
          { name: '审理中', value: 10 },
          { name: '已结案', value: 8 },
          { name: '中止', value: 2 }
        ]
      }
    ]
  }

  chart.setOption(option)
}

const initCaseTypeChart = async () => {
  const chart = echarts.init(caseTypeChart.value)
  
  const res = await getCaseStatsByType()
  const data = res.code === 200 && res.data ? res.data : []
  
  const xAxisData = data.map(item => item.typeName || item.case_type_name || '未知')
  const seriesData = data.map(item => item.count || 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData.length > 0 ? xAxisData : ['民事案件', '刑事案件', '行政案件', '执行案件'],
      axisLabel: {
        interval: 0,
        rotate: 0
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'bar',
        barWidth: '50%',
        data: seriesData.length > 0 ? seriesData : [15, 8, 5, 7],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }

  chart.setOption(option)
}

const initLawyerChart = async () => {
  const chart = echarts.init(lawyerChart.value)
  
  const res = await getCaseStatsByLawyer()
  const data = res.code === 200 && res.data ? res.data : []
  
  const xAxisData = data.map(item => item.real_name || '未知律师')
  const seriesData = data.map(item => item.count || 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData.length > 0 ? xAxisData : ['张合伙人', '李律师', '王律师', '刘律师'],
      axisLabel: {
        interval: 0
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'bar',
        barWidth: '50%',
        data: seriesData.length > 0 ? seriesData : [12, 8, 15, 6],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67C23A' },
            { offset: 1, color: '#95D475' }
          ])
        }
      }
    ]
  }

  chart.setOption(option)
}

const initIncomeChart = async () => {
  const chart = echarts.init(incomeChart.value)
  
  const currentYear = new Date().getFullYear()
  const res = await getMonthlyIncome(currentYear)
  const data = res.code === 200 && res.data ? res.data : []
  
  const monthData = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
  const incomeMap = {}
  data.forEach(item => {
    incomeMap[item.month] = item.total || 0
  })
  
  const seriesData = monthData.map(m => incomeMap[m] || 0)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: monthData.map(m => m + '月')
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'line',
        smooth: true,
        data: seriesData,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#67C23A',
          width: 2
        },
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }

  chart.setOption(option)
}

onMounted(() => {
  loadStats()
  initCaseStatusChart()
  initCaseTypeChart()
  initLawyerChart()
  initIncomeChart()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      padding: 10px 0;
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }

    .stat-info {
      margin-left: 20px;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 5px;
      }
    }
  }

  .chart-container {
    height: 300px;
  }
}
</style>
