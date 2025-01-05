<template>
  <div class="courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>可选课程</span>
          <el-input
            v-model="searchText"
            placeholder="搜索课程"
            style="width: 200px"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>

      <el-table :data="courses" stripe>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacher" label="任课教师" />
        <el-table-column prop="time" label="上课时间" />
        <el-table-column prop="location" label="上课地点" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="capacity" label="剩余名额" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              :disabled="scope.row.selected"
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
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const searchText = ref('')
const courses = ref([])

const fetchCourses = async (search = '') => {
  try {
    const res = await request.get('/student/available-courses', {
      params: { search }
    })
    console.log('课程列表:', res)
    courses.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

const handleSearch = () => {
  fetchCourses(searchText.value)
}

const handleSelect = async (course) => {
  try {
    await request.post('/student/select-course', {
      courseId: course.id
    })
    ElMessage.success('选课成功')
    await fetchCourses(searchText.value)
  } catch (error) {
    console.error('选课失败:', error)
    ElMessage.error(error.response?.data?.message || '选课失败')
  }
}

// 初始加载
fetchCourses()
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
</style> 