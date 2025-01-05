<template>
  <div class="grade-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
          <div class="header-operations">
            <el-button type="primary" @click="handleSave" :loading="saving">
              保存修改
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="students" style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column label="成绩">
          <template #default="scope">
            <el-input-number 
              v-model="scope.row.score" 
              :min="0" 
              :max="100"
              :precision="1"
              controls-position="right"
              @change="handleGradeChange(scope.row)"
            />
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
      students: [],
      modifiedGrades: new Set(),
      saving: false
    }
  },
  methods: {
    async fetchStudents() {
      try {
        const courseId = this.$route.params.courseId
        const res = await this.$axios.get(`/api/teacher/course/${courseId}/students`)
        this.students = res.data
      } catch (error) {
        this.$message.error('获取学生列表失败')
      }
    },
    handleGradeChange(student) {
      this.modifiedGrades.add(student)
    },
    async handleSave() {
      if (this.modifiedGrades.size === 0) {
        return
      }
      
      try {
        this.saving = true
        await this.$axios.post('/api/teacher/grades/batch', 
          Array.from(this.modifiedGrades))
        this.$message.success('成绩保存成功')
        this.modifiedGrades.clear()
      } catch (error) {
        this.$message.error(error.response?.data?.message || '成绩保存失败')
      } finally {
        this.saving = false
      }
    }
  },
  created() {
    this.fetchStudents()
  }
}
</script>

<style scoped>
.header-operations {
  float: right;
}
</style> 