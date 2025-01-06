<template>
  <div class="teachers-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
          <el-button type="primary" @click="handleAdd">添加教师</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item>
            <el-input
              v-model="searchQuery"
              placeholder="搜索教师姓名"
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
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAdd">添加教师</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 教师列表 -->
      <el-table :data="teachers" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="教师ID" width="100" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
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
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑教师对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加教师' : '编辑教师'"
      width="500px"
      @closed="handleDialogClosed"
    >
      <el-form
        ref="teacherFormRef"
        :model="teacherForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="teacherForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="teacherForm.userId" placeholder="请输入用户ID" />
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
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据列表
const teachers = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const teacherFormRef = ref(null)
const teacherForm = ref({
  name: '',
  userId: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { type: 'number', message: '用户ID必须为数字', trigger: 'blur', transform: (value) => Number(value) }
  ]
}

// 获取教师列表
const fetchTeachers = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/teachers', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        search: searchQuery.value
      }
    })
    teachers.value = res.data
    total.value = res.total
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

// 添加教师
const handleAdd = () => {
  dialogType.value = 'add'
  teacherForm.value = {
    name: '',
    userId: ''
  }
  dialogVisible.value = true
  // 重置表单验证
  if (teacherFormRef.value) {
    teacherFormRef.value.resetFields()
  }
}

// 编辑教师
const handleEdit = (row) => {
  dialogType.value = 'edit'
  teacherForm.value = {
    id: row.id,
    name: row.name,
    userId: row.userId
  }
  dialogVisible.value = true
  // 重置表单验证
  if (teacherFormRef.value) {
    teacherFormRef.value.resetFields()
  }
}

// 删除教师
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该教师吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await request.delete(`/admin/teachers/${row.id}`)
    ElMessage.success('删除成功')
    if (teachers.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    fetchTeachers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除教师失败:', error)
      ElMessage.error('删除教师失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!teacherFormRef.value) return

  try {
    await teacherFormRef.value.validate()
    const formData = { ...teacherForm.value }

    if (dialogType.value === 'add') {
      await request.post('/admin/teachers', formData)
      ElMessage.success('添加成功')
    } else {
      await request.put(`/admin/teachers/${formData.id}`, formData)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchTeachers()
  } catch (error) {
    console.error('保存教师失败:', error)
    ElMessage.error(error.response?.data?.message || '保存教师失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchTeachers()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchTeachers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchTeachers()
}

// 对话框关闭时的处理
const handleDialogClosed = () => {
  teacherFormRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  fetchTeachers()
})
</script>

<style scoped>
.teachers-container {
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