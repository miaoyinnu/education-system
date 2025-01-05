<!-- 系统设置页面 -->
<template>
  <div class="system-settings">
    <el-tabs v-model="activeTab">
      <!-- 基本设置 -->
      <el-tab-pane label="基本设置" name="basic">
        <el-form :model="basicSettings" :rules="basicRules" ref="basicForm" label-width="120px">
          <el-form-item label="系统名称" prop="systemName">
            <el-input v-model="basicSettings.systemName"></el-input>
          </el-form-item>
          <el-form-item label="选课开始时间" prop="courseSelectionStart">
            <el-date-picker
              v-model="basicSettings.courseSelectionStart"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="选课结束时间" prop="courseSelectionEnd">
            <el-date-picker
              v-model="basicSettings.courseSelectionEnd"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="成绩预警阈值" prop="gradeAlertThreshold">
            <el-input-number
              v-model="basicSettings.gradeAlertThreshold"
              :min="0"
              :max="100">
            </el-input-number>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 系统日志 -->
      <el-tab-pane label="系统日志" name="logs">
        <div class="operation-bar">
          <el-form :inline="true" :model="logSearch">
            <el-form-item label="操作类型">
              <el-select v-model="logSearch.operation" placeholder="选择操作类型">
                <el-option label="全部" value=""></el-option>
                <el-option label="登录" value="LOGIN"></el-option>
                <el-option label="修改" value="UPDATE"></el-option>
                <el-option label="删除" value="DELETE"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="logSearch.timeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期">
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchLogs">查询</el-button>
              <el-button @click="exportLogs">导出日志</el-button>
            </el-form-item>
          </el-form>
        </div>
        <el-table :data="logs" style="width: 100%">
          <el-table-column prop="operation" label="操作类型"></el-table-column>
          <el-table-column prop="operator" label="操作人"></el-table-column>
          <el-table-column prop="description" label="操作描述"></el-table-column>
          <el-table-column prop="ip" label="IP地址"></el-table-column>
          <el-table-column prop="time" label="操作时间"></el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </el-tab-pane>

      <!-- 数据备份 -->
      <el-tab-pane label="数据备份" name="backup">
        <div class="operation-bar">
          <el-button type="primary" @click="createBackup">创建备份</el-button>
        </div>
        <el-table :data="backups" style="width: 100%">
          <el-table-column prop="fileName" label="备份文件"></el-table-column>
          <el-table-column prop="fileSize" label="文件大小">
            <template slot-scope="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="备份描述"></el-table-column>
          <el-table-column prop="createdAt" label="创建时间"></el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="success"
                @click="restoreBackup(scope.row)">还原</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="deleteBackup(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import {
  getSettings,
  updateSettings,
  getLogs,
  exportLogs,
  createBackup,
  getBackups,
  restoreBackup,
  deleteBackup
} from '@/api/admin'

export default {
  name: 'SystemSettings',
  data() {
    return {
      activeTab: 'basic',
      basicSettings: {
        systemName: '',
        courseSelectionStart: '',
        courseSelectionEnd: '',
        gradeAlertThreshold: 60
      },
      basicRules: {
        systemName: [
          { required: true, message: '请输入系统名称', trigger: 'blur' }
        ],
        courseSelectionStart: [
          { required: true, message: '请选择选课开始时间', trigger: 'change' }
        ],
        courseSelectionEnd: [
          { required: true, message: '请选择选课结束时间', trigger: 'change' }
        ],
        gradeAlertThreshold: [
          { required: true, message: '请设置成绩预警阈值', trigger: 'blur' }
        ]
      },
      logSearch: {
        operation: '',
        timeRange: []
      },
      logs: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      backups: []
    }
  },
  created() {
    this.fetchSettings()
    this.fetchLogs()
    this.fetchBackups()
  },
  methods: {
    async fetchSettings() {
      try {
        const response = await getSettings()
        this.basicSettings = response.data
      } catch (error) {
        this.$message.error('获取系统设置失败')
      }
    },
    async saveBasicSettings() {
      this.$refs.basicForm.validate(async (valid) => {
        if (valid) {
          try {
            await updateSettings(this.basicSettings)
            this.$message.success('保存成功')
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    async fetchLogs() {
      try {
        const response = await getLogs({
          operation: this.logSearch.operation,
          startTime: this.logSearch.timeRange[0],
          endTime: this.logSearch.timeRange[1],
          page: this.currentPage,
          size: this.pageSize
        })
        this.logs = response.data.records
        this.total = response.data.total
      } catch (error) {
        this.$message.error('获取日志失败')
      }
    },
    searchLogs() {
      this.currentPage = 1
      this.fetchLogs()
    },
    async exportLogs() {
      try {
        await exportLogs({
          operation: this.logSearch.operation,
          startTime: this.logSearch.timeRange[0],
          endTime: this.logSearch.timeRange[1]
        })
        this.$message.success('导出成功')
      } catch (error) {
        this.$message.error('导出失败')
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchLogs()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchLogs()
    },
    async createBackup() {
      try {
        await createBackup()
        this.$message.success('备份创建成功')
        this.fetchBackups()
      } catch (error) {
        this.$message.error('备份创建失败')
      }
    },
    async fetchBackups() {
      try {
        const response = await getBackups()
        this.backups = response.data
      } catch (error) {
        this.$message.error('获取备份列表失败')
      }
    },
    async restoreBackup(backup) {
      try {
        await this.$confirm('确定要还原该备份吗？还原后将覆盖当前数据', '提示', {
          type: 'warning'
        })
        await restoreBackup(backup.id)
        this.$message.success('还原成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('还原失败')
        }
      }
    },
    async deleteBackup(backup) {
      try {
        await this.$confirm('确定要删除该备份吗？', '提示', {
          type: 'warning'
        })
        await deleteBackup(backup.id)
        this.$message.success('删除成功')
        this.fetchBackups()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    formatFileSize(size) {
      const units = ['B', 'KB', 'MB', 'GB']
      let index = 0
      while (size >= 1024 && index < units.length - 1) {
        size /= 1024
        index++
      }
      return size.toFixed(2) + ' ' + units[index]
    }
  }
}
</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.el-form {
  max-width: 600px;
}
</style> 