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

      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="classroomName" label="上课地点" />
        <el-table-column prop="currentStudents" label="已选人数" />
        <el-table-column prop="maxStudents" label="最大人数" />
        <template #empty>
          <el-empty description="暂无课表数据" />
        </template>
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
const loading = ref(false)

const fetchSemesters = async () => {
  loading.value = true
  try {
    const res = await request.get('/teacher/semesters')
    console.log('获取到的学期数据:', res)
    if (Array.isArray(res)) {
      semesters.value = res
    } else if (res.data && Array.isArray(res.data)) {
      semesters.value = res.data
    } else {
      semesters.value = []
      console.error('学期数据格式不正确:', res)
    }
    
    if (semesters.value.length > 0) {
      currentSemester.value = semesters.value[0]
      fetchCourses()
    }
  } catch (error) {
    console.error('获取学期列表失败:', error)
    ElMessage.error('获取学期列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCourses = async () => {
  if (!currentSemester.value) return
  
  loading.value = true
  try {
    const res = await request.get('/teacher/timetable', {
      params: { semester: currentSemester.value }
    })
    console.log('获取到的课表数据:', res)
    if (Array.isArray(res)) {
      courses.value = res
    } else if (res.data && Array.isArray(res.data)) {
      courses.value = res.data
    } else {
      courses.value = []
      console.error('课表数据格式不正确:', res)
    }
  } catch (error) {
    console.error('获取课表失败:', error)
    ElMessage.error('获取课表失败')
  } finally {
    loading.value = false
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