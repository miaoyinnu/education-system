<template>
  <div class="timetable">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课表管理</span>
          <el-select v-model="currentSemester" placeholder="选择学期" @change="fetchCourses">
            <el-option
              v-for="semester in semesters"
              :key="semester"
              :label="semester"
              :value="semester"
            />
          </el-select>
        </div>
      </template>

      <el-table :data="courses" stripe>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="classroomName" label="上课地点" />
        <el-table-column prop="currentStudents" label="已选人数" />
        <el-table-column prop="maxStudents" label="最大人数" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const currentSemester = ref('')
const semesters = ref([])
const courses = ref([])

const fetchSemesters = async () => {
  try {
    const res = await request.get('/api/teacher/semesters')
    semesters.value = res.data
    if (semesters.value.length > 0) {
      currentSemester.value = semesters.value[0]
      fetchCourses()
    }
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

onMounted(() => {
  fetchSemesters()
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
</style> 