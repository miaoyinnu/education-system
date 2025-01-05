<template>
  <div class="analysis-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总课程数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.totalCourses }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总教室数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.totalClassrooms }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>教室使用率</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.classroomUsage }}%</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>选课率</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.courseSelectionRate }}%</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>课程成绩分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="gradeDistributionChart" style="width: 100%; height: 300px"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>教室使用情况</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="classroomUsageChart" style="width: 100%; height: 300px"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩预警 -->
    <el-card shadow="hover" class="alerts">
      <template #header>
        <div class="card-header">
          <span>成绩预警</span>
        </div>
      </template>
      <el-table :data="alerts" style="width: 100%" v-loading="loading">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="score" label="成绩" width="100" />
        <el-table-column prop="warningLevel" label="预警等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getWarningType(row.warningLevel)">
              {{ row.warningLevel }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

// 数据
const statistics = ref({
  totalCourses: 0,
  totalClassrooms: 0,
  classroomUsage: 0,
  courseSelectionRate: 0
})
const alerts = ref([])
const loading = ref(false)

// echarts 实例
let gradeDistributionChart = null
let classroomUsageChart = null

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await request.get('/admin/statistics')
    statistics.value = res
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取成绩预警数据
const fetchAlerts = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/grade-alerts')
    alerts.value = res
  } catch (error) {
    console.error('获取成绩预警数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化成绩分布图表
const initGradeDistributionChart = async () => {
  try {
    const res = await request.get('/admin/grade-distribution')
    const chart = echarts.init(document.querySelector('#gradeDistributionChart'))
    chart.setOption({
      title: {
        text: '课程成绩分布'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['90-100', '80-89', '70-79', '60-69', '0-59']
      },
      yAxis: {
        type: 'value',
        name: '学生数'
      },
      series: [{
        data: Object.values(res),
        type: 'bar'
      }]
    })
    gradeDistributionChart = chart
  } catch (error) {
    console.error('获取成绩分布数据失败:', error)
  }
}

// 初始化教室使用情况图表
const initClassroomUsageChart = async () => {
  try {
    const res = await request.get('/admin/classroom-usage')
    const chart = echarts.init(document.querySelector('#classroomUsageChart'))
    chart.setOption({
      title: {
        text: '教室使用情况'
      },
      tooltip: {
        trigger: 'item'
      },
      series: [{
        type: 'pie',
        radius: '50%',
        data: [
          { value: res.inUse, name: '使用中' },
          { value: res.available, name: '空闲' }
        ]
      }]
    })
    classroomUsageChart = chart
  } catch (error) {
    console.error('获取教室使用情况数据失败:', error)
  }
}

// 获取预警等级对应的标签类型
const getWarningType = (level) => {
  switch (level) {
    case '严重':
      return 'danger'
    case '中等':
      return 'warning'
    case '轻微':
      return 'info'
    default:
      return ''
  }
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  if (gradeDistributionChart) {
    gradeDistributionChart.resize()
  }
  if (classroomUsageChart) {
    classroomUsageChart.resize()
  }
}

onMounted(() => {
  fetchStatistics()
  fetchAlerts()
  initGradeDistributionChart()
  initClassroomUsageChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (gradeDistributionChart) {
    gradeDistributionChart.dispose()
  }
  if (classroomUsageChart) {
    classroomUsageChart.dispose()
  }
})
</script>

<style scoped>
.analysis-container {
  padding: 20px;
}

.statistics {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
}

.card-content h2 {
  margin: 0;
  font-size: 24px;
  color: #409EFF;
}

.charts {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.alerts {
  margin-bottom: 20px;
}
</style> 