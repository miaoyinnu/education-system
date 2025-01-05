<!-- 教室管理页面 -->
<template>
  <div class="classroom-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>教室管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleAdd">添加教室</el-button>
      </div>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="教室名称">
          <el-input v-model="searchForm.name" placeholder="教室名称"></el-input>
        </el-form-item>
        <el-form-item label="教学楼">
          <el-select v-model="searchForm.building" placeholder="选择教学楼">
            <el-option
              v-for="item in buildings"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 教室列表 -->
      <el-table :data="classrooms" style="width: 100%">
        <el-table-column prop="name" label="教室名称"></el-table-column>
        <el-table-column prop="building" label="教学楼"></el-table-column>
        <el-table-column prop="capacity" label="容量"></el-table-column>
        <el-table-column prop="type" label="教室类型">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="使用率">
          <template slot-scope="scope">
            <el-progress :percentage="scope.row.usageRate || 0"></el-progress>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 添加/编辑教室对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
      <el-form :model="classroomForm" :rules="rules" ref="classroomForm" label-width="100px">
        <el-form-item label="教室名称" prop="name">
          <el-input v-model="classroomForm.name"></el-input>
        </el-form-item>
        <el-form-item label="教学楼" prop="building">
          <el-select v-model="classroomForm.building" placeholder="选择教学楼">
            <el-option
              v-for="item in buildings"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="classroomForm.capacity" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="教室类型" prop="type">
          <el-select v-model="classroomForm.type" placeholder="选择类型">
            <el-option
              v-for="item in roomTypes"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="classroomForm.status" placeholder="选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getClassrooms, addClassroom, updateClassroom, deleteClassroom } from '@/api/classroom'

export default {
  name: 'ClassroomManagement',
  data() {
    return {
      searchForm: {
        name: '',
        building: ''
      },
      classrooms: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      classroomForm: {
        name: '',
        building: '',
        capacity: 50,
        type: '',
        status: '正常'
      },
      buildings: ['第一教学楼', '第二教学楼', '第三教学楼', '实验楼'],
      roomTypes: ['普通教室', '多媒体教室', '实验室', '阶梯教室'],
      statusOptions: ['正常', '维修中', '已预约'],
      rules: {
        name: [
          { required: true, message: '请输入教室名称', trigger: 'blur' }
        ],
        building: [
          { required: true, message: '请选择教学楼', trigger: 'change' }
        ],
        capacity: [
          { required: true, message: '请输入容量', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择教室类型', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const response = await getClassrooms({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.classrooms = response.data.records
        this.total = response.data.total
      } catch (error) {
        this.$message.error('获取教室列表失败')
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '添加教室'
      this.classroomForm = {
        name: '',
        building: '',
        capacity: 50,
        type: '',
        status: '正常'
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑教室'
      this.classroomForm = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该教室?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteClassroom(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    async submitForm() {
      this.$refs.classroomForm.validate(async (valid) => {
        if (valid) {
          try {
            if (this.classroomForm.id) {
              await updateClassroom(this.classroomForm.id, this.classroomForm)
              this.$message.success('更新成功')
            } else {
              await addClassroom(this.classroomForm)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            this.$message.error(error.message || '操作失败')
          }
        }
      })
    },
    getTypeTag(type) {
      const tags = {
        '普通教室': '',
        '多媒体教室': 'success',
        '实验室': 'warning',
        '阶梯教室': 'info'
      }
      return tags[type] || ''
    },
    getStatusTag(status) {
      const tags = {
        '正常': 'success',
        '维修中': 'danger',
        '已预约': 'warning'
      }
      return tags[status] || ''
    }
  }
}
</script>

<style scoped>
.classroom-management {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 