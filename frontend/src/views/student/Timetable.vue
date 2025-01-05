<template>
  <div class="timetable">
    <el-calendar>
      <template #dateCell="{ data }">
        <div>
          {{ data.day.split('-').slice(1).join('-') }}
          <div v-for="course in getDayCourses(data.day)" :key="course.id">
            {{ course.name }}
          </div>
        </div>
      </template>
    </el-calendar>
  </div>
</template>

<script>
export default {
  name: 'StudentTimetable',
  data() {
    return {
      courses: []
    }
  },
  methods: {
    getDayCourses(day) {
      return this.courses.filter(course => course.courseTime === day)
    },
    async fetchCourses() {
      try {
        const response = await this.$axios.get('/api/student/timetable')
        this.courses = response.data
      } catch (error) {
        this.$message.error('获取课表失败')
      }
    }
  },
  mounted() {
    this.fetchCourses()
  }
}
</script> 