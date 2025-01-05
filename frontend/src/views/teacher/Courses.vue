<template>
  <div class="courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
        </div>
      </template>

      <el-table :data="courses" stripe>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="classroomName" label="上课地点" />
        <el-table-column prop="currentStudents" label="已选人数" />
        <el-table-column prop="maxStudents" label="最大人数" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleGrades(row)">成绩管理</el-button>
          </template>
        </el-table-column>
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
        <el-table-column label="成绩" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="100" :precision="1" />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
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

const fetchCourses = async () => {
  try {
    const res = await request.get('/api/teacher/courses')
    courses.value = res.data
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

const handleGrades = async (course) => {
  currentCourse.value = course
  try {
    const res = await request.get(`/api/teacher/course/${course.id}/grades`)
    students.value = res.data
    gradeDialogVisible.value = true
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败')
  }
}

const saveGrades = async () => {
  try {
    await request.post(`/api/teacher/course/${currentCourse.value.id}/grades`, students.value)
    ElMessage.success('保存成功')
    gradeDialogVisible.value = false
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