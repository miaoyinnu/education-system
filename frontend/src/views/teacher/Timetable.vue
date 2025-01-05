<template>
  <div class="timetable">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的课表</span>
              <div class="header-right">
                <el-select v-model="currentSemester" placeholder="选择学期" clearable @change="fetchCourses" style="margin-right: 15px;">
                  <el-option
                    v-for="semester in semesters"
                    :key="semester"
                    :label="semester"
                    :value="semester"
                  />
                </el-select>
                <el-radio-group v-model="viewType" size="small">
                  <el-radio-button label="calendar">日历视图</el-radio-button>
                  <el-radio-button label="list">列表视图</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>

          <!-- 日历视图 -->
          <el-calendar v-if="viewType === 'calendar'" class="calendar-view">
            <template #dateCell="{ data }">
              <div class="calendar-cell">
                <p class="date">{{ data.day.split('-').slice(1).join('-') }}</p>
                <div v-for="course in getDayCourses(data.day)" :key="course.id" class="course-item">
                  <el-tooltip :content="getCourseTooltip(course)" placement="top">
                    <div class="course-content">
                      <span class="course-name">{{ course.name }}</span>
                      <el-tag size="small" type="info">{{ course.location }}</el-tag>
                    </div>
                  </el-tooltip>
                </div>
              </div>
            </template>
          </el-calendar>

          <!-- 列表视图 -->
          <el-table v-else :data="courses" stripe>
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="semester" label="学期" />
            <el-table-column prop="time" label="上课时间" />
            <el-table-column prop="location" label="上课地点" />
            <el-table-column prop="currentStudents" label="已选人数" />
            <el-table-column prop="maxStudents" label="最大人数" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="showCourseDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="课程详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="课程名称">{{ currentCourse?.name }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ currentCourse?.semester }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ currentCourse?.time }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ currentCourse?.location }}</el-descriptions-item>
        <el-descriptions-item label="已选人数">{{ currentCourse?.currentStudents }}</el-descriptions-item>
        <el-descriptions-item label="最大人数">{{ currentCourse?.maxStudents }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const currentSemester = ref('')
const semesters = ref([])
const courses = ref([])
const viewType = ref('calendar')
const detailDialogVisible = ref(false)
const currentCourse = ref(null)

const fetchSemesters = async () => {
  try {
    const res = await request.get('/api/teacher/semesters')
    semesters.value = res.data
  } catch (error) {
    console.error('获取学期列表失败:', error)
    ElMessage.error('获取学期列表失败')
  }
}

const fetchCourses = async () => {
  try {
    const res = await request.get('/api/teacher/timetable', {
      params: { semester: currentSemester.value }
    })
    courses.value = res.data
  } catch (error) {
    console.error('获取课表失败:', error)
    ElMessage.error('获取课表失败')
  }
}

const getDayCourses = (day) => {
  return courses.value.filter(course => {
    const courseDay = course.time.split(' ')[0] // 假设时间格式为 "周一 1-2节"
    return courseDay === getWeekDay(day)
  })
}

const getWeekDay = (day) => {
  const date = new Date(day)
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weekDays[date.getDay()]
}

const getCourseTooltip = (course) => {
  return `${course.name}\n${course.time}\n${course.location}\n已选人数: ${course.currentStudents}/${course.maxStudents}`
}

const showCourseDetail = (course) => {
  currentCourse.value = course
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchSemesters()
  fetchCourses()
})
</script>

<style scoped>
.timetable {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.calendar-view {
  margin-top: 20px;
}

.calendar-cell {
  height: 100%;
  padding: 4px;
}

.date {
  margin: 0 0 4px;
  font-size: 12px;
  color: #999;
}

.course-item {
  margin-bottom: 4px;
}

.course-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
}

.course-name {
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style> 