<template>
  <div class="analysis-container">
    <!-- 成绩统计 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>成绩统计分析</span>
        </div>
      </template>

      <!-- 课程成绩统计 -->
      <div class="section">
        <h3>课程成绩统计</h3>
        <el-table :data="courseStats" style="width: 100%" v-loading="loading">
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="totalStudents" label="学生人数" />
          <el-table-column prop="avgScore" label="平均分">
            <template #default="{ row }">
              {{ row.avgScore?.toFixed(2) || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="maxScore" label="最高分" />
          <el-table-column prop="minScore" label="最低分" />
          <el-table-column prop="passRate" label="及格率">
            <template #default="{ row }">
              {{ row.passRate?.toFixed(2) || '-' }}%
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 成绩分布 -->
      <div class="section">
        <h3>成绩分布</h3>
        <div class="chart-container" ref="gradeDistributionChart"></div>
      </div>

      <!-- 成绩异常提醒 -->
      <div class="section">
        <h3>成绩异常提醒</h3>
        <!-- 预警阈值设置 -->
        <div class="threshold-setting">
          <el-form :inline="true">
            <el-form-item label="预警分数线">
              <el-input-number 
                v-model="thresholdForm.threshold" 
                :min="0" 
                :max="100"
                :step="5"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveThreshold">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <el-table :data="gradeAlerts" style="width: 100%" v-loading="loading">
          <el-table-column prop="student_name" label="学生姓名" />
          <el-table-column prop="course_name" label="课程名称" />
          <el-table-column prop="score" label="成绩">
            <template #default="{ row }">
              <span class="warning-score">{{ row.score }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="teacher_name" label="任课教师" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                link 
                @click="handleNotify(row)"
              >
                发送提醒
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 学生成绩统计 -->
      <div class="section">
        <h3>学生成绩统计</h3>
        <el-table :data="studentStats" style="width: 100%" v-loading="loading">
          <el-table-column prop="studentName" label="学生姓名" />
          <el-table-column prop="totalCourses" label="选课数" />
          <el-table-column prop="avgScore" label="平均分">
            <template #default="{ row }">
              {{ row.avgScore?.toFixed(2) || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="passedCourses" label="通过课程数" />
        </el-table>
      </div>
    </el-card>

    <!-- 教室使用统计 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>教室使用统计</span>
        </div>
      </template>

      <!-- 教室使用率 -->
      <div class="section">
        <h3>教室使用率</h3>
        <el-table :data="usageStats" style="width: 100%" v-loading="loading">
          <el-table-column prop="classroomName" label="教室" />
          <el-table-column prop="totalCourses" label="课程数" />
          <el-table-column prop="usedTimeSlots" label="已用时段" />
          <el-table-column prop="usageRate" label="使用率">
            <template #default="{ row }">
              {{ row.usageRate?.toFixed(2) || '-' }}%
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 教室容量利用率 -->
      <div class="section">
        <h3>教室容量利用率</h3>
        <el-table :data="capacityStats" style="width: 100%" v-loading="loading">
          <el-table-column prop="classroomName" label="教室" />
          <el-table-column prop="totalCapacity" label="总容量" />
          <el-table-column prop="avgStudents" label="平均人数">
            <template #default="{ row }">
              {{ row.avgStudents?.toFixed(2) || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="maxStudents" label="最大人数" />
          <el-table-column prop="utilizationRate" label="利用率">
            <template #default="{ row }">
              {{ row.utilizationRate?.toFixed(2) || '-' }}%
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import * as echarts from 'echarts'

const loading = ref(false)
const courseStats = ref([])
const gradeDistribution = ref({})
const studentStats = ref([])
const usageStats = ref([])
const capacityStats = ref([])
const gradeDistributionChart = ref(null)
const gradeAlerts = ref([])
const thresholdForm = ref({
  threshold: 60
})

// 获取当前阈值设置
const getThreshold = async () => {
  try {
    const res = await request.get('/settings/grade-alert/threshold')
    thresholdForm.value.threshold = res.data
  } catch (error) {
    console.error('获取阈值设置失败:', error)
    ElMessage.error('获取阈值设置失败')
  }
}

// 保存阈值设置
const saveThreshold = async () => {
  try {
    await request.put('/settings/grade-alert/threshold', null, {
      params: {
        threshold: thresholdForm.value.threshold
      }
    })
    ElMessage.success('保存成功')
    await fetchGradeAlerts()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 获取成绩统计数据
const fetchGradeStatistics = async () => {
  try {
    loading.value = true
    const response = await request.get('/admin/statistics/grade')
    courseStats.value = response.data.courseStats
    gradeDistribution.value = response.data.gradeDistribution
    studentStats.value = response.data.studentStats
    renderGradeDistributionChart()
  } catch (error) {
    console.error('获取成绩统计失败:', error)
    ElMessage.error('获取成绩统计失败')
  } finally {
    loading.value = false
  }
}

// 获取教室使用统计数据
const fetchClassroomStatistics = async () => {
  try {
    loading.value = true
    const response = await request.get('/admin/statistics/classroom')
    usageStats.value = response.data.usageStats
    capacityStats.value = response.data.capacityStats
  } catch (error) {
    console.error('获取教室使用统计失败:', error)
    ElMessage.error('获取教室使用统计失败')
  } finally {
    loading.value = false
  }
}

// 获取成绩异常列表
const fetchGradeAlerts = async () => {
  try {
    loading.value = true
    const res = await request.get('/admin/grade-alerts')
    gradeAlerts.value = res.data
  } catch (error) {
    console.error('获取成绩异常列表失败:', error)
    ElMessage.error('获取成绩异常列表失败')
  } finally {
    loading.value = false
  }
}

// 发送提醒
const handleNotify = async (row) => {
  try {
    await request.post('/admin/grade-alerts/notify', {
      studentId: row.student_id,
      courseId: row.course_id
    })
    ElMessage.success('提醒已发送')
  } catch (error) {
    ElMessage.error('发送提醒失败')
  }
}

// 渲染成绩分布图表
const renderGradeDistributionChart = () => {
  if (!gradeDistributionChart.value) return

  const chart = echarts.init(gradeDistributionChart.value)
  const option = {
    title: {
      text: '成绩分布'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      data: ['优秀(90-100)', '良好(80-89)', '中等(70-79)', '及格(60-69)', '不及格(<60)']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '学生人数',
        type: 'bar',
        data: [
          gradeDistribution.value.excellent || 0,
          gradeDistribution.value.good || 0,
          gradeDistribution.value.fair || 0,
          gradeDistribution.value.pass || 0,
          gradeDistribution.value.fail || 0
        ]
      }
    ]
  }
  chart.setOption(option)
}

// 初始化
onMounted(() => {
  fetchGradeStatistics()
  fetchClassroomStatistics()
  fetchGradeAlerts()
  getThreshold()
})
</script>

<style scoped>
.analysis-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.section {
  margin-bottom: 30px;
}

.chart-container {
  height: 400px;
  margin: 20px 0;
}

h3 {
  margin-bottom: 15px;
  color: #606266;
}

.warning-score {
  color: #F56C6C;
  font-weight: bold;
}

.threshold-setting {
  margin-bottom: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style> 