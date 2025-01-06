<template>
  <div class="courses-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd">添加课程</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索课程名称"
          class="search-input"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 课程列表 -->
      <el-table :data="courses" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="课程ID" width="80" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="classroomName" label="教室" />
        <el-table-column prop="maxStudents" label="最大人数" width="100" />
        <el-table-column prop="currentStudents" label="已选人数" width="100" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加课程' : '编辑课程'"
      width="500px"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" />
        </el-form-item>
        <el-form-item label="任课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择教师" clearable>
            <el-option
              v-for="teacher in teachers"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="time">
          <el-input v-model="courseForm.time" placeholder="如：周一 1-2节" />
        </el-form-item>
        <el-form-item label="教室" prop="classroomId">
          <el-select v-model="courseForm.classroomId" placeholder="请选择教室" clearable>
            <el-option
              v-for="classroom in classrooms"
              :key="classroom.id"
              :label="classroom.name"
              :value="classroom.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="courseForm.semester" placeholder="如：2024春季" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="courseForm.maxStudents" :min="1" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="courseForm.credit" :min="0" :precision="1" :step="0.5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据列表
const courses = ref([])
const teachers = ref([])
const classrooms = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const courseFormRef = ref(null)
const courseForm = ref({
  name: '',
  teacherId: '',
  courseTime: '',
  classroomId: '',
  semester: '',
  maxStudents: 50,
  credit: 2
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  teacherId: [{ required: true, message: '请选择任课教师', trigger: 'change' }],
  time: [{ required: true, message: '请输入上课时间', trigger: 'blur' }],
  classroomId: [{ required: true, message: '请选择教室', trigger: 'change' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }]
}

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/courses', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        search: searchQuery.value
      }
    })
    if (res.code === 200 && res.data) {
      courses.value = res.data.data || []
      total.value = res.data.total || 0
    } else {
      courses.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 获取教师列表
const fetchTeachers = async () => {
  try {
    const res = await request.get('/admin/teachers')
    if (res.code === 200) {
      teachers.value = res.data || []
    } else {
      teachers.value = []
      ElMessage.error('获取教师列表失败')
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  }
}

// 获取教室列表
const fetchClassrooms = async () => {
  try {
    const res = await request.get('/admin/classrooms')
    if (res.code === 200) {
      classrooms.value = res.data || []
    } else {
      classrooms.value = []
      ElMessage.error('获取教室列表失败')
    }
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败')
  }
}

// 添加课程
const handleAdd = () => {
  dialogType.value = 'add'
  courseForm.value = {
    name: '',
    teacherId: null,
    time: '',
    classroomId: null,
    semester: '',
    maxStudents: 50,
    credit: 2
  }
  dialogVisible.value = true
  // 等待下一个 tick，确保表单已经渲染
  nextTick(() => {
    courseFormRef.value?.resetFields()
  })
}

// 编辑课程
const handleEdit = (row) => {
  dialogType.value = 'edit'
  courseForm.value = {
    id: row.id,
    name: row.name,
    teacherId: row.teacherId,
    time: row.courseTime,
    classroomId: row.classroomId,
    semester: row.semester,
    maxStudents: row.maxStudents,
    credit: row.credit
  }
  dialogVisible.value = true
  // 等待下一个 tick，确保表单已经渲染
  nextTick(() => {
    courseFormRef.value?.clearValidate()
  })
}

// 删除课程
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？删除后无法恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await request.delete(`/admin/courses/${row.id}`)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除课程失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!courseFormRef.value) return

  try {
    await courseFormRef.value.validate()
    
    if (dialogType.value === 'add') {
      await request.post('/admin/courses', courseForm.value)
      ElMessage.success('添加成功')
    } else {
      await request.put(`/admin/courses/${courseForm.value.id}`, courseForm.value)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchCourses()
  } catch (error) {
    console.error('保存课程失败:', error)
    ElMessage.error('保存课程失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchCourses()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourses()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCourses()
}

// 初始化
onMounted(() => {
  fetchCourses()
  fetchTeachers()
  fetchClassrooms()
})
</script>

<style scoped>
.courses-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 