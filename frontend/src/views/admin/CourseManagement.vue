<template>
  <div class="course-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd">添加课程</el-button>
        </div>
      </template>
      
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" />
        <el-table-column prop="classroom" label="教室" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="maxStudents" label="最大人数" />
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" @click="handleDelete(scope.row)" class="delete-button">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog 
      :title="dialogType === 'add' ? '添加课程' : '编辑课程'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="courseForm" :rules="rules" ref="courseForm" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" />
        </el-form-item>
        <el-form-item label="任课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择教师">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教室" prop="classroomId">
          <el-select v-model="courseForm.classroomId" placeholder="请选择教室">
            <el-option
              v-for="classroom in classrooms"
              :key="classroom.id"
              :label="classroom.name"
              :value="classroom.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="courseTime">
          <el-input v-model="courseForm.courseTime" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="courseForm.maxStudents" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      courses: [],
      teachers: [],
      classrooms: [],
      dialogVisible: false,
      dialogType: 'add', // 'add' 或 'edit'
      submitting: false,
      courseForm: {
        name: '',
        teacherId: '',
        classroomId: '',
        courseTime: '',
        maxStudents: 1
      },
      rules: {
        name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        teacherId: [{ required: true, message: '请选择任课教师', trigger: 'change' }],
        classroomId: [{ required: true, message: '请选择教室', trigger: 'change' }],
        courseTime: [{ required: true, message: '请输入上课时间', trigger: 'blur' }],
        maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async fetchCourses() {
      try {
        const res = await this.$axios.get('/api/admin/courses')
        this.courses = res.data
      } catch (error) {
        this.$message.error('获取课程列表失败')
      }
    },
    async fetchTeachers() {
      try {
        const res = await this.$axios.get('/api/admin/teachers')
        this.teachers = res.data
      } catch (error) {
        this.$message.error('获取教师列表失败')
      }
    },
    async fetchClassrooms() {
      try {
        const res = await this.$axios.get('/api/admin/classrooms')
        this.classrooms = res.data
      } catch (error) {
        this.$message.error('获取教室列表失败')
      }
    },
    handleAdd() {
      this.dialogType = 'add'
      this.courseForm = {
        name: '',
        teacherId: '',
        classroomId: '',
        courseTime: '',
        maxStudents: 1
      }
      this.dialogVisible = true
    },
    handleEdit(course) {
      this.dialogType = 'edit'
      this.courseForm = { ...course }
      this.dialogVisible = true
    },
    async handleDelete(course) {
      try {
        await this.$confirm('确认删除该课程?', '提示', {
          type: 'warning'
        })
        await this.$axios.delete(`/api/admin/course/${course.id}`)
        this.$message.success('删除成功')
        this.fetchCourses()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.response?.data?.message || '删除失败')
        }
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.courseForm.validate()
        this.submitting = true
        
        if (this.dialogType === 'add') {
          await this.$axios.post('/api/admin/course', this.courseForm)
          this.$message.success('添加成功')
        } else {
          await this.$axios.put(`/api/admin/course/${this.courseForm.id}`, this.courseForm)
          this.$message.success('更新成功')
        }
        
        this.dialogVisible = false
        this.fetchCourses()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.response?.data?.message || '操作失败')
        }
      } finally {
        this.submitting = false
      }
    }
  },
  created() {
    this.fetchCourses()
    this.fetchTeachers()
    this.fetchClassrooms()
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.delete-button {
  color: #F56C6C;
}
</style> 