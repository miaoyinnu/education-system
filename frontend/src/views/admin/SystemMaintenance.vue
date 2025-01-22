<template>
  <div class="system-maintenance">
    <el-tabs v-model="activeTab">
      <!-- 系统设置 -->
      <el-tab-pane label="系统设置" name="settings">
        <el-table :data="settings" border>
          <el-table-column prop="key" label="配置项" />
          <el-table-column prop="value" label="配置值">
            <template #default="scope">
              <el-input v-model="scope.row.value" @blur="updateSetting(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" />
        </el-table>
      </el-tab-pane>

      <!-- 系统日志 -->
      <el-tab-pane label="系统日志" name="logs">
        <div class="operation-bar">
          <el-button type="danger" @click="clearLogs">清空日志</el-button>
        </div>
        <el-table :data="logs" border>
          <el-table-column prop="operation" label="操作类型" />
          <el-table-column prop="description" label="操作描述" />
          <el-table-column prop="operator" label="操作人" />
          <el-table-column prop="createTime" label="操作时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
                {{ scope.row.status === 0 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          :total="total"
          :current-page="currentPage"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </el-tab-pane>

      <!-- 数据备份 -->
      <el-tab-pane label="数据备份" name="backup">
        <el-card>
          <div class="backup-actions">
            <el-button type="primary" @click="backupDatabase">
              备份数据库
            </el-button>
            <el-upload
              action="/api/admin/restore-database"
              :show-file-list="false"
              :on-success="handleRestoreSuccess"
              :on-error="handleRestoreError"
            >
              <el-button type="warning">恢复数据库</el-button>
            </el-upload>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'SystemMaintenance',
  setup() {
    const activeTab = ref('settings')
    const settings = ref([])
    const logs = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)

    // 获取系统设置
    const getSettings = async () => {
      try {
        const res = await axios.get('/api/admin/settings')
        settings.value = res.data
      } catch (error) {
        ElMessage.error('获取系统设置失败')
      }
    }

    // 更新系统设置
    const updateSetting = async (setting) => {
      try {
        await axios.put('/api/admin/settings', setting)
        ElMessage.success('更新成功')
      } catch (error) {
        ElMessage.error('更新失败')
      }
    }

    // 获取系统日志
    const getLogs = async () => {
      try {
        const res = await axios.get('/api/admin/logs', {
          params: {
            page: currentPage.value,
            size: pageSize.value
          }
        })
        logs.value = res.data.records
        total.value = res.data.total
      } catch (error) {
        ElMessage.error('获取系统日志失败')
      }
    }

    // 清空日志
    const clearLogs = async () => {
      try {
        await axios.delete('/api/admin/logs')
        ElMessage.success('清空成功')
        getLogs()
      } catch (error) {
        ElMessage.error('清空失败')
      }
    }

    // 备份数据库
    const backupDatabase = async () => {
      try {
        await axios.post('/api/admin/backup-database')
        ElMessage.success('备份成功')
      } catch (error) {
        ElMessage.error('备份失败')
      }
    }

    // 处理数据库恢复成功
    const handleRestoreSuccess = () => {
      ElMessage.success('恢复成功')
    }

    // 处理数据库恢复失败
    const handleRestoreError = () => {
      ElMessage.error('恢复失败')
    }

    // 处理页码变化
    const handlePageChange = (page) => {
      currentPage.value = page
      getLogs()
    }

    onMounted(() => {
      getSettings()
      getLogs()
    })

    return {
      activeTab,
      settings,
      logs,
      total,
      currentPage,
      pageSize,
      updateSetting,
      clearLogs,
      backupDatabase,
      handleRestoreSuccess,
      handleRestoreError,
      handlePageChange
    }
  }
}
</script>

<style scoped>
.system-maintenance {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
}

.backup-actions {
  display: flex;
  gap: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 