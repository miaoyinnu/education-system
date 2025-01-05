<template>
  <div class="course-selection">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>可选课程</span>
        </div>
      </template>
      
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" />
        <el-table-column prop="classroom" label="教室" />
        <el-table-column prop="courseTime" label="上课时间" />
        <el-table-column prop="currentStudents" label="已选人数">
          <template #default="scope">
            {{ scope.row.currentStudents }}/{{ scope.row.maxStudents }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleSelect(scope.row)"
              :disabled="scope.row.currentStudents >= scope.row.maxStudents"
            >
              选课
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      courses: []
    }
  },
  methods: {
    async fetchCourses() {
      try {
        const res = await this.$axios.get('/api/student/available-courses')
        this.courses = res.data
      } catch (error) {
        this.$message.error('获取课程列表失败')
      }
    },
    async handleSelect(course) {
      try {
        await this.$axios.post(`/api/student/select-course/${course.id}`)
        this.$message.success('选课成功')
        this.fetchCourses()
      } catch (error) {
        this.$message.error(error.response?.data?.message || '选课失败')
      }
    }
  },
  created() {
    this.fetchCourses()
  }
}
</script> 