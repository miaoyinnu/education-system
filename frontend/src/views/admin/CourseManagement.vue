<!-- 课程管理页面 -->
<template>
  <div class="course-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>课程管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleAdd">添加课程</el-button>
      </div>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="课程名称"></el-input>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="searchForm.teacherId" placeholder="选择教师">
            <el-option
              v-for="item in teachers"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 课程列表 -->
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="name" label="课程名称"></el-table-column>
        <el-table-column prop="teacherName" label="任课教师"></el-table-column>
        <el-table-column prop="courseTime" label="上课时间"></el-table-column>
        <el-table-column prop="classroomName" label="教室"></el-table-column>
        <el-table-column prop="maxStudents" label="最大人数"></el-table-column>
        <el-table-column prop="currentStudents" label="当前人数"></el-table-column>
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

    <!-- 添加/编辑课程对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
      <el-form :model="courseForm" :rules="rules" ref="courseForm" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name"></el-input>
        </el-form-item>
        <el-form-item label="任课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="选择教师">
            <el-option
              v-for="item in teachers"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教室" prop="classroomId">
          <el-select v-model="courseForm.classroomId" placeholder="选择教室">
            <el-option
              v-for="item in classrooms"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="courseTime">
          <el-select v-model="courseForm.courseTime" placeholder="选择时间">
            <el-option
              v-for="item in timeSlots"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="courseForm.maxStudents" :min="1"></el-input-number>
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
import { getCourses, addCourse, updateCourse, deleteCourse } from '@/api/course'
import { getTeachers } from '@/api/teacher'
import { getClassrooms } from '@/api/classroom'

export default {
  name: 'CourseManagement',
  data() {
    return {
      searchForm: {
        name: '',
        teacherId: ''
      },
      courses: [],
      teachers: [],
      classrooms: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      courseForm: {
        name: '',
        teacherId: '',
        classroomId: '',
        courseTime: '',
        maxStudents: 50
      },
      timeSlots: [
        '周一-1', '周一-2', '周一-3', '周一-4',
        '周二-1', '周二-2', '周二-3', '周二-4',
        '周三-1', '周三-2', '周三-3', '周三-4',
        '周四-1', '周四-2', '周四-3', '周四-4',
        '周五-1', '周五-2', '周五-3', '周五-4'
      ],
      rules: {
        name: [
          { required: true, message: '请输入课程名称', trigger: 'blur' }
        ],
        teacherId: [
          { required: true, message: '请选择任课教师', trigger: 'change' }
        ],
        classroomId: [
          { required: true, message: '请选择教室', trigger: 'change' }
        ],
        courseTime: [
          { required: true, message: '请选择上课时间', trigger: 'change' }
        ],
        maxStudents: [
          { required: true, message: '请输入最大人数', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchData()
    this.fetchTeachers()
    this.fetchClassrooms()
  },
  methods: {
    async fetchData() {
      try {
        const response = await getCourses({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.courses = response.data.records
        this.total = response.data.total
      } catch (error) {
        this.$message.error('获取课程列表失败')
      }
    },
    async fetchTeachers() {
      try {
        const response = await getTeachers()
        this.teachers = response.data
      } catch (error) {
        this.$message.error('获取教师列表失败')
      }
    },
    async fetchClassrooms() {
      try {
        const response = await getClassrooms()
        this.classrooms = response.data
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
      this.dialogTitle = '添加课程'
      this.courseForm = {
        name: '',
        teacherId: '',
        classroomId: '',
        courseTime: '',
        maxStudents: 50
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑课程'
      this.courseForm = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该课程?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteCourse(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    async submitForm() {
      this.$refs.courseForm.validate(async (valid) => {
        if (valid) {
          try {
            if (this.courseForm.id) {
              await updateCourse(this.courseForm.id, this.courseForm)
              this.$message.success('更新成功')
            } else {
              await addCourse(this.courseForm)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            this.$message.error(error.message || '操作失败')
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.course-management {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 