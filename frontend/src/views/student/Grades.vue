<template>
  <div class="grades">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
          <el-select v-model="currentSemester" placeholder="选择学期" clearable @change="handleSemesterChange">
            <el-option
              v-for="semester in semesters"
              :key="semester"
              :label="semester"
              :value="semester"
            />
          </el-select>
        </div>
      </template>

      <el-table :data="grades" stripe>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="credit" label="学分" />
        <el-table-column prop="score" label="成绩">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.score < 60 }">
              {{ scope.row.score }}
            </span>
          </template>
        </el-table-column>
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
const grades = ref([])

const fetchSemesters = async () => {
  try {
    const res = await request.get('/api/student/semesters')
    semesters.value = res.data
  } catch (error) {
    console.error('获取学期列表失败:', error)
    ElMessage.error('获取学期列表失败')
  }
}

const fetchGrades = async (semester = '') => {
  try {
    const res = await request.get('/api/student/grades', {
      params: { semester }
    })
    grades.value = res.data
  } catch (error) {
    console.error('获取成绩失败:', error)
    ElMessage.error('获取成绩失败')
  }
}

const handleSemesterChange = () => {
  fetchGrades(currentSemester.value)
}

onMounted(() => {
  fetchSemesters()
  fetchGrades()
})
</script>

<style scoped>
.grades {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-danger {
  color: #F56C6C;
}
</style> 