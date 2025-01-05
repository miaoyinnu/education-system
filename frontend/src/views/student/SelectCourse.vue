<template>
  <div class="select-course">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>选课</span>
          <div class="search-box">
            <el-input
              v-model="searchKey"
              placeholder="搜索课程"
              style="width: 200px; margin-right: 10px"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="courses" stripe>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacher" label="任课教师" />
        <el-table-column prop="time" label="上课时间" />
        <el-table-column prop="location" label="上课地点" />
        <el-table-column prop="capacity" label="剩余容量" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              :disabled="scope.row.selected || scope.row.capacity <= 0"
              @click="handleSelect(scope.row)"
            >
              {{ scope.row.selected ? '已选' : '选课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const courses = ref([])
const searchKey = ref('')

const fetchCourses = async () => {
  try {
    const res = await request.get('/api/student/available-courses', {
      params: { search: searchKey.value }
    })
    courses.value = res.data
  } catch (error) {
    console.error('获取可选课程失败:', error)
    ElMessage.error('获取可选课程失败')
  }
}

const handleSearch = () => {
  fetchCourses()
}

const handleSelect = async (course) => {
  try {
    await ElMessageBox.confirm(`确定要选择课程"${course.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.post('/api/student/select-course', {
      courseId: course.id
    })
    
    ElMessage.success('选课成功')
    fetchCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('选课失败:', error)
      ElMessage.error(error.response?.data || '选课失败')
    }
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.select-course {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-box {
  display: flex;
  align-items: center;
}
</style> 