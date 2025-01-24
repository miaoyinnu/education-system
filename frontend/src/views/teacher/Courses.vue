<template>
  <div class="courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
        </div>
      </template>

      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="classroomName" label="上课地点" />
        <el-table-column prop="currentStudents" label="已选人数" />
        <el-table-column prop="maxStudents" label="最大人数" />
        <el-table-column prop="averageScore" label="平均成绩">
          <template #default="{ row }">
            {{ row.averageScore ? row.averageScore.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleGrades(row)">成绩管理</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无课程数据" />
        </template>
      </el-table>
    </el-card>

    <!-- 成绩管理对话框 -->
    <el-dialog v-model="gradeDialogVisible" title="成绩管理" width="900px">
      <div class="dialog-header">
        <div class="course-info">
          <h3>{{ currentCourse?.name }}</h3>
          <p>{{ currentCourse?.semester }} | {{ currentCourse?.courseTime }} | {{ currentCourse?.classroomName }}</p>
        </div>
      </div>

      <el-table :data="students" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="成绩" width="150">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.score" 
              :min="0" 
              :max="100" 
              :precision="1"
              :step="0.5"
              :controls="false"
              style="width: 120px"
              placeholder="请输入成绩"
              @change="(value) => handleScoreChange(row, value)"
            />
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="gradeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGrades">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const courses = ref([])
const students = ref([])
const gradeDialogVisible = ref(false)
const currentCourse = ref(null)
const loading = ref(false)

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 处理成绩变化
const handleScoreChange = (row, value) => {
  if (value === null || value === undefined) {
    row.score = null
    return
  }
  // 确保成绩在0-100之间，保留一位小数
  row.score = Math.min(100, Math.max(0, Math.round(value * 10) / 10))
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await request.get('/teacher/courses')
    console.log('获取到的课程数据:', res)
    if (Array.isArray(res)) {
      courses.value = res
    } else if (res.data && Array.isArray(res.data)) {
      courses.value = res.data
    } else {
      courses.value = []
      console.error('课程数据格式不正确:', res)
    }
    console.log('courses.value:', courses.value)
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleGrades = async (course) => {
  currentCourse.value = course
  try {
    const res = await request.get(`/teacher/course/${course.id}/grades`)
    console.log('成绩列表:', res)
    // 确保所有学生的成绩都是数字或null
    students.value = Array.isArray(res) ? res.map(student => ({
      ...student,
      score: student.score != null ? Number(student.score) : null
    })) : []
    gradeDialogVisible.value = true
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败')
  }
}

const saveGrades = async () => {
  try {
    // 过滤掉无效的成绩
    const validGrades = students.value.filter(student => 
      student.score !== null && !isNaN(student.score)
    )
    await request.post(`/teacher/course/${currentCourse.value.id}/grades`, validGrades)
    ElMessage.success('保存成功')
    gradeDialogVisible.value = false
    // 重新加载成绩列表
    if (currentCourse.value) {
      handleGrades(currentCourse.value)
    }
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存成绩失败')
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.courses {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-header {
  margin-bottom: 20px;
}

.course-info h3 {
  margin: 0 0 8px;
}

.course-info p {
  margin: 0;
  color: #666;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 