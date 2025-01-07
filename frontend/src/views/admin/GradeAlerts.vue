<template>
  <div class="grade-alerts-container">
    <el-card class="threshold-card">
      <template #header>
        <div class="card-header">
          <span>成绩预警阈值设置</span>
          <el-button type="primary" @click="saveThreshold">保存设置</el-button>
        </div>
      </template>
      <el-form :model="thresholdForm" label-width="120px">
        <el-form-item label="预警分数线">
          <el-input-number 
            v-model="thresholdForm.threshold" 
            :min="0" 
            :max="100"
            :step="5"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="alert-list-card">
      <template #header>
        <div class="card-header">
          <span>成绩异常学生名单</span>
          <el-button type="primary" @click="refreshList">刷新列表</el-button>
        </div>
      </template>
      <el-table :data="alertList" style="width: 100%">
        <el-table-column prop="student_name" label="学生姓名" />
        <el-table-column prop="course_name" label="课程名称" />
        <el-table-column prop="score" label="成绩">
          <template #default="{ row }">
            <span :class="{ 'warning-score': row.score < thresholdForm.threshold }">
              {{ row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="teacher_name" label="任课教师" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link 
              @click="handleNotify(row)"
            >
              发送提醒
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const thresholdForm = ref({
  threshold: 60
})

const alertList = ref([])

// 获取当前阈值设置
const getThreshold = async () => {
  try {
    const res = await request.get('/settings/grade-alert/threshold');
    thresholdForm.value.threshold = res.data;
  } catch (error) {
    ElMessage.error('获取阈值设置失败');
  }
}

// 保存阈值设置
const saveThreshold = async () => {
  try {
    await request.put('/settings/grade-alert/threshold', null, {
      params: {
        threshold: thresholdForm.value.threshold
      }
    });
    ElMessage.success('保存成功');
    await refreshList();
  } catch (error) {
    ElMessage.error('保存失败');
  }
}

// 获取异常成绩列表
const refreshList = async () => {
  try {
    const res = await request.get('/admin/grade-alerts');
    alertList.value = res.data;
  } catch (error) {
    ElMessage.error('获取异常成绩列表失败');
  }
}

// 发送提醒
const handleNotify = async (row) => {
  try {
    await request.post('/admin/grade-alerts/notify', {
      studentId: row.student_id,
      courseId: row.course_id
    });
    ElMessage.success('提醒已发送');
  } catch (error) {
    ElMessage.error('发送提醒失败');
  }
}

onMounted(() => {
  getThreshold()
  refreshList()
})
</script>

<style scoped>
.grade-alerts-container {
  padding: 20px;
}

.threshold-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.warning-score {
  color: #F56C6C;
  font-weight: bold;
}

.alert-list-card {
  margin-top: 20px;
}
</style> 