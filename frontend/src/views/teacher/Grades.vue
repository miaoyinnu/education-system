<template>
  <div class="grades">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
          <div class="header-actions">
            <el-upload
              action=""
              :http-request="handleUpload"
              :show-file-list="false"
              accept=".xlsx,.xls"
            >
              <el-button type="primary">导入成绩</el-button>
            </el-upload>
            <el-button type="success" @click="handlePublish">发布成绩</el-button>
          </div>
        </div>
      </template>

      <el-table :data="students" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="成绩" width="200">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.score"
              :min="0"
              :max="100"
              :precision="1"
              :controls="false"
              style="width: 120px"
              @change="handleScoreChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后更新时间" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleSave(scope.row)"
            >
              保存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const courseId = route.params.courseId

const students = ref([])

const fetchStudents = async () => {
  try {
    const res = await request.get(`/api/teacher/courses/${courseId}/students`)
    students.value = res.data
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败')
  }
}

const handleScoreChange = (student) => {
  if (student.score < 0) student.score = 0
  if (student.score > 100) student.score = 100
}

const handleSave = async (student) => {
  try {
    await request.put(`/api/teacher/courses/${courseId}/grades`, {
      studentId: student.studentId,
      score: student.score
    })
    ElMessage.success('保存成功')
    await fetchStudents()
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存成绩失败')
  }
}

const handleUpload = async (params) => {
  const formData = new FormData()
  formData.append('file', params.file)
  
  try {
    await request.post(`/api/teacher/courses/${courseId}/grades/import`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    ElMessage.success('导入成功')
    await fetchStudents()
  } catch (error) {
    console.error('导入成绩失败:', error)
    ElMessage.error('导入成绩失败')
  }
}

const handlePublish = async () => {
  try {
    await request.post(`/api/teacher/courses/${courseId}/grades/publish`)
    ElMessage.success('发布成功')
  } catch (error) {
    console.error('发布成绩失败:', error)
    ElMessage.error('发布成绩失败')
  }
}

onMounted(() => {
  fetchStudents()
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

.header-actions {
  display: flex;
  gap: 10px;
}
</style> 