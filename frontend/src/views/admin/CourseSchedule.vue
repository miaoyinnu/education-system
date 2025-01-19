<template>
  <div class="course-schedule">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <span>课程排课</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAutoSchedule">自动排课</el-button>
            <el-button type="success" @click="handleManualSchedule">手动排课</el-button>
          </div>
        </div>
      </template>

      <!-- 未排课程列表 -->
      <div class="section">
        <h3>未排课程列表</h3>
        <el-table :data="unscheduledCourses" stripe v-loading="loading">
          <el-table-column prop="name" label="课程名称" />
          <el-table-column prop="teacherName" label="任课教师" />
          <el-table-column prop="maxStudents" label="最大人数" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                link
                @click="openScheduleDialog(row)"
              >
                排课
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 已排课程列表 -->
      <div class="section">
        <h3>已排课程列表</h3>
        <el-table :data="scheduledCourses" stripe>
          <el-table-column prop="name" label="课程名称" />
          <el-table-column prop="teacherName" label="任课教师" />
          <el-table-column prop="classroomName" label="教室" />
          <el-table-column prop="time" label="上课时间" />
          <el-table-column prop="maxStudents" label="最大人数" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button 
                type="danger" 
                link
                @click="handleCancelSchedule(row)"
              >
                取消排课
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 手动排课对话框 -->
    <el-dialog 
      v-model="scheduleDialog.visible" 
      title="课程排课"
      width="500px"
    >
      <el-form 
        :model="scheduleForm" 
        :rules="rules"
        ref="scheduleFormRef"
        label-width="100px"
      >
        <el-form-item label="课程名称">
          <span>{{ scheduleForm.name }}</span>
        </el-form-item>
        <el-form-item label="任课教师">
          <span>{{ scheduleForm.teacherName }}</span>
        </el-form-item>
        <el-form-item label="教室" prop="classroomId">
          <el-select 
            v-model="scheduleForm.classroomId" 
            placeholder="请选择教室"
            filterable
          >
            <el-option
              v-for="classroom in availableClassrooms"
              :key="classroom.id"
              :label="classroom.name + ' (容量: ' + classroom.capacity + ')'"
              :value="classroom.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="timeSlot">
          <el-select 
            v-model="scheduleForm.timeSlot" 
            placeholder="请选择上课时间"
          >
            <el-option
              v-for="slot in timeSlots"
              :key="slot"
              :label="slot"
              :value="slot"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitSchedule">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 自动排课结果对话框 -->
    <el-dialog 
      v-model="autoScheduleDialog.visible" 
      title="自动排课结果"
      width="70%"
    >
      <div v-if="autoScheduleDialog.loading" class="auto-schedule-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在进行自动排课，请稍候...</p>
      </div>
      <div v-else>
        <el-alert
          v-if="autoScheduleDialog.success"
          title="自动排课完成"
          type="success"
          :description="'成功排课 ' + autoScheduleDialog.successCount + ' 门课程'"
          show-icon
        />
        <el-alert
          v-else
          title="自动排课失败"
          type="error"
          :description="autoScheduleDialog.errorMessage"
          show-icon
        />
        <el-table 
          :data="autoScheduleDialog.results" 
          stripe
          style="margin-top: 20px;"
        >
          <el-table-column prop="name" label="课程名称" />
          <el-table-column prop="teacherName" label="任课教师" />
          <el-table-column prop="classroomName" label="教室" />
          <el-table-column prop="time" label="上课时间" />
        </el-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="autoScheduleDialog.visible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="confirmAutoSchedule"
            :disabled="!autoScheduleDialog.success"
          >
            确认应用
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const unscheduledCourses = ref([])
const scheduledCourses = ref([])
const availableClassrooms = ref([])

// 时间槽配置
const timeSlots = [
  '周一-1', '周一-2', '周一-3', '周一-4',
  '周二-1', '周二-2', '周二-3', '周二-4',
  '周三-1', '周三-2', '周三-3', '周三-4',
  '周四-1', '周四-2', '周四-3', '周四-4',
  '周五-1', '周五-2', '周五-3', '周五-4'
]

// 手动排课对话框
const scheduleDialog = reactive({
  visible: false,
  currentCourse: null
})

// 排课表单
const scheduleFormRef = ref(null)
const scheduleForm = reactive({
  courseId: null,
  name: '',
  teacherName: '',
  classroomId: '',
  timeSlot: ''
})

// 表单验证规则
const rules = {
  classroomId: [
    { required: true, message: '请选择教室', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择上课时间', trigger: 'change' }
  ]
}

// 自动排课对话框
const autoScheduleDialog = reactive({
  visible: false,
  loading: false,
  success: false,
  successCount: 0,
  errorMessage: '',
  results: []
})

// 获取未排课程列表
const fetchUnscheduledCourses = async () => {
  try {
    loading.value = true
    const res = await request.get('/admin/courses/unscheduled')
    console.log('未排课程数据详情:', res.data)
    if (res.data && Array.isArray(res.data)) {
      console.log('第一条未排课程数据:', res.data[0])
    }
    unscheduledCourses.value = res.data
  } catch (error) {
    console.error('获取未排课程失败:', error)
    ElMessage.error('获取未排课程失败')
  } finally {
    loading.value = false
  }
}

// 获取已排课程列表
const fetchScheduledCourses = async () => {
  try {
    loading.value = true
    const res = await request.get('/admin/courses/scheduled')
    console.log('已排课程数据详情:', res.data)
    if (res.data && Array.isArray(res.data)) {
      console.log('第一条已排课程数据:', res.data[0])
    }
    scheduledCourses.value = res.data
  } catch (error) {
    console.error('获取已排课程失败:', error)
    ElMessage.error('获取已排课程失败')
  } finally {
    loading.value = false
  }
}

// 获取可用教室列表
const fetchAvailableClassrooms = async () => {
  try {
    const res = await request.get('/admin/classrooms')
    availableClassrooms.value = res.data
  } catch (error) {
    ElMessage.error('获取教室列表失败')
  }
}

// 打开手动排课对话框
const openScheduleDialog = (course) => {
  scheduleDialog.currentCourse = course
  scheduleForm.courseId = course.id
  scheduleForm.name = course.name
  scheduleForm.teacherName = course.teacherName
  scheduleForm.classroomId = ''
  scheduleForm.timeSlot = ''
  scheduleDialog.visible = true
}

// 提交手动排课
const submitSchedule = async () => {
  if (!scheduleFormRef.value) return
  
  try {
    await scheduleFormRef.value.validate()
    
    await request.post('/admin/courses/schedule', {
      courseId: scheduleForm.courseId,
      classroomId: scheduleForm.classroomId,
      timeSlot: scheduleForm.timeSlot
    })
    
    ElMessage.success('排课成功')
    scheduleDialog.visible = false
    await Promise.all([
      fetchUnscheduledCourses(),
      fetchScheduledCourses()
    ])
  } catch (error) {
    ElMessage.error(error.message || '排课失败')
  }
}

// 取消排课
const handleCancelSchedule = async (course) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该课程的排课吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await request.post(`/admin/courses/${course.id}/cancel-schedule`)
    ElMessage.success('取消排课成功')
    await Promise.all([
      fetchUnscheduledCourses(),
      fetchScheduledCourses()
    ])
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消排课失败')
    }
  }
}

// 自动排课
const handleAutoSchedule = async () => {
  autoScheduleDialog.visible = true
  autoScheduleDialog.loading = true
  autoScheduleDialog.success = false
  autoScheduleDialog.results = []
  
  try {
    const res = await request.post('/admin/schedule/auto')
    autoScheduleDialog.results = res.data
    autoScheduleDialog.successCount = res.data.length
    autoScheduleDialog.success = true
  } catch (error) {
    autoScheduleDialog.success = false
    autoScheduleDialog.errorMessage = error.message || '自动排课失败'
  } finally {
    autoScheduleDialog.loading = false
  }
}

// 确认应用自动排课结果
const confirmAutoSchedule = async () => {
  try {
    await request.post('/admin/schedule/auto/confirm')
    ElMessage.success('应用排课结果成功')
    autoScheduleDialog.visible = false
    await Promise.all([
      fetchUnscheduledCourses(),
      fetchScheduledCourses()
    ])
  } catch (error) {
    ElMessage.error('应用排课结果失败')
  }
}

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchUnscheduledCourses(),
    fetchScheduledCourses(),
    fetchAvailableClassrooms()
  ])
})
</script>

<style scoped>
.course-schedule {
  padding: 20px;
}

.schedule-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.section {
  margin-bottom: 30px;
}

h3 {
  margin-bottom: 15px;
  color: #606266;
}

.auto-schedule-loading {
  text-align: center;
  padding: 40px 0;
}

.auto-schedule-loading .el-icon {
  font-size: 30px;
  margin-bottom: 10px;
}
</style> 