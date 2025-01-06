<template>
  <div class="classrooms-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>教室管理</span>
          <el-button type="primary" @click="handleAdd">添加教室</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索教室名称"
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

      <!-- 教室列表 -->
      <el-table :data="classrooms" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="教室ID" width="80" />
        <el-table-column prop="name" label="教室名称" />
        <el-table-column prop="building" label="教学楼" />
        <el-table-column prop="type" label="教室类型" />
        <el-table-column prop="capacity" label="容量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
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

    <!-- 添加/编辑教室对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加教室' : '编辑教室'"
      width="500px"
    >
      <el-form
        ref="classroomFormRef"
        :model="classroomForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="教室名称" prop="name">
          <el-input v-model="classroomForm.name" placeholder="如：201" />
        </el-form-item>
        <el-form-item label="教学楼" prop="building">
          <el-select v-model="classroomForm.building" placeholder="请选择教学楼">
            <el-option label="教学楼A" value="教学楼A" />
            <el-option label="教学楼B" value="教学楼B" />
            <el-option label="实验楼" value="实验楼" />
          </el-select>
        </el-form-item>
        <el-form-item label="教室类型" prop="type">
          <el-select v-model="classroomForm.type" placeholder="请选择教室类型">
            <el-option label="多媒体教室" value="多媒体教室" />
            <el-option label="普通教室" value="普通教室" />
            <el-option label="阶梯教室" value="阶梯教室" />
            <el-option label="计算机实验室" value="计算机实验室" />
            <el-option label="物理实验室" value="物理实验室" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="classroomForm.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="classroomForm.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="维修中" value="维修中" />
            <el-option label="停用" value="停用" />
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

// 数据列表
const classrooms = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const classroomFormRef = ref(null)
const classroomForm = ref({
  name: '',
  building: '',
  type: '',
  capacity: 50,
  status: '正常'
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入教室名称', trigger: 'blur' }],
  building: [{ required: true, message: '请选择教学楼', trigger: 'change' }],
  type: [{ required: true, message: '请选择教室类型', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取教室列表
const fetchClassrooms = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/classrooms', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        search: searchQuery.value
      }
    })
    classrooms.value = res.data
    total.value = res.total
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败')
  } finally {
    loading.value = false
  }
}

// 添加教室
const handleAdd = () => {
  dialogType.value = 'add'
  classroomForm.value = {
    name: '',
    building: '',
    type: '',
    capacity: 50,
    status: '正常'
  }
  dialogVisible.value = true
  // 重置表单验证
  if (classroomFormRef.value) {
    classroomFormRef.value.resetFields()
  }
}

// 编辑教室
const handleEdit = (row) => {
  dialogType.value = 'edit'
  classroomForm.value = {
    ...row,
    building: row.building || '',
    type: row.type || '',
    status: row.status || '正常'
  }
  dialogVisible.value = true
  // 重置表单验证
  if (classroomFormRef.value) {
    classroomFormRef.value.resetFields()
  }
}

// 删除教室
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该教室吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/admin/classrooms/${row.id}`)
    ElMessage.success('删除成功')
    fetchClassrooms()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除教室失败:', error)
      ElMessage.error('删除教室失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!classroomFormRef.value) return

  try {
    await classroomFormRef.value.validate()
    const formData = {
      ...classroomForm.value,
      building: classroomForm.value.building,
      type: classroomForm.value.type,
      status: classroomForm.value.status
    }

    if (dialogType.value === 'add') {
      await request.post('/admin/classrooms', formData)
      ElMessage.success('添加成功')
    } else {
      await request.put(`/admin/classrooms/${formData.id}`, formData)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchClassrooms()
  } catch (error) {
    console.error('保存教室失败:', error)
    ElMessage.error('保存教室失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchClassrooms()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchClassrooms()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchClassrooms()
}

// 初始化
onMounted(() => {
  fetchClassrooms()
})
</script>

<style scoped>
.classrooms-container {
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