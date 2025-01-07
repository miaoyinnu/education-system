<template>
  <div class="users-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item>
            <el-input
              v-model="searchQuery"
              placeholder="搜索用户名"
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
        </el-form>
      </div>

      <!-- 用户列表 -->
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClosed"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
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

const loading = ref(false)
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')
const userFormRef = ref(null)

const userForm = ref({
  username: '',
  password: '',
  role: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取用户列表
const fetchUsers = async () => {
  try {
    loading.value = true
    const offset = (currentPage.value - 1) * pageSize.value
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      search: searchQuery.value
    }
    const response = await request.get('/admin/users', { params })
    users.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  userForm.value = {
    username: '',
    password: '',
    role: ''
  }
  dialogVisible.value = true
  // 重置表单验证
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  userForm.value = {
    id: row.id,
    username: row.username,
    role: row.role
  }
  dialogVisible.value = true
  // 重置表单验证
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await request.delete(`/admin/users/${row.id}`)
    ElMessage.success('删除成功')
    if (users.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return

  try {
    await userFormRef.value.validate()
    const formData = { ...userForm.value }

    if (dialogType.value === 'add') {
      await request.post('/admin/users', formData)
      ElMessage.success('添加成功')
    } else {
      await request.put(`/admin/users/${formData.id}`, formData)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchUsers()
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error(error.response?.data?.message || '保存用户失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUsers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUsers()
}

// 获取角色标签类型
const getRoleType = (role) => {
  const types = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return types[role] || 'info'
}

// 获取角色标签文本
const getRoleLabel = (role) => {
  const labels = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return labels[role] || role
}

// 对话框关闭时的处理
const handleDialogClosed = () => {
  userFormRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.users-container {
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
</style> 