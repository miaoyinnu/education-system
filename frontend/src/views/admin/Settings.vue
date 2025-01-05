<!-- 系统设置页面 -->
<template>
  <div class="settings">
    <el-tabs v-model="activeTab">
      <!-- 基本设置 -->
      <el-tab-pane label="基本设置" name="basic">
        <el-form :model="basicForm" :rules="basicRules" ref="basicForm" label-width="120px">
          <el-form-item label="系统名称" prop="systemName">
            <el-input v-model="basicForm.systemName"></el-input>
          </el-form-item>
          <el-form-item label="选课开始时间" prop="courseSelectionStart">
            <el-date-picker
              v-model="basicForm.courseSelectionStart"
              type="datetime"
              placeholder="选择开始时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="选课结束时间" prop="courseSelectionEnd">
            <el-date-picker
              v-model="basicForm.courseSelectionEnd"
              type="datetime"
              placeholder="选择结束时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="每人最大选课数" prop="maxCourseSelection">
            <el-input-number v-model="basicForm.maxCourseSelection" :min="1"></el-input-number>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 成绩预警设置 -->
      <el-tab-pane label="成绩预警设置" name="alert">
        <el-form :model="alertForm" :rules="alertRules" ref="alertForm" label-width="120px">
          <el-form-item label="预警分数线" prop="alertThreshold">
            <el-input-number 
              v-model="alertForm.alertThreshold" 
              :min="0" 
              :max="100"
              :step="5">
            </el-input-number>
          </el-form-item>
          <el-form-item label="预警通知方式" prop="alertMethods">
            <el-checkbox-group v-model="alertForm.alertMethods">
              <el-checkbox label="email">邮件通知</el-checkbox>
              <el-checkbox label="sms">短信通知</el-checkbox>
              <el-checkbox label="system">系统通知</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveAlertSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 系统日志 -->
      <el-tab-pane label="系统日志" name="log">
        <div class="table-toolbar">
          <el-form :inline="true" :model="logSearch">
            <el-form-item label="操作类型">
              <el-select v-model="logSearch.operation" placeholder="选择操作类型">
                <el-option label="全部" value=""></el-option>
                <el-option label="登录" value="LOGIN"></el-option>
                <el-option label="修改" value="MODIFY"></el-option>
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
          <el-table-column prop="createdAt" label="时间" width="180"></el-table-column>
          <el-table-column prop="operation" label="操作"></el-table-column>
          <el-table-column prop="operator" label="操作人"></el-table-column>
          <el-table-column prop="ipAddress" label="IP地址"></el-table-column>
          <el-table-column prop="description" label="详细信息"></el-table-column>
        </el-table>
        <div class="pagination-container">
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
        <el-card class="box-card">
          <div slot="header">
            <span>数据备份</span>
            <el-button 
              style="float: right; padding: 3px 0" 
              type="text"
              @click="createBackup">
              创建备份
            </el-button>
          </div>
          <el-table :data="backups" style="width: 100%">
            <el-table-column prop="createdAt" label="备份时间" width="180"></el-table-column>
            <el-table-column prop="fileSize" label="文件大小"></el-table-column>
            <el-table-column prop="description" label="备注"></el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button size="mini" @click="restoreBackup(scope.row)">恢复</el-button>
                <el-button 
                  size="mini" 
                  type="danger" 
                  @click="deleteBackup(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import {
  getSettings,
  updateSettings,
  getLogs,
  exportLogs as exportLogsApi,
  getBackups,
  createBackup as createBackupApi,
  restoreBackup as restoreBackupApi,
  deleteBackup as deleteBackupApi
} from '@/api/settings'

export default {
  name: 'Settings',
  data() {
    return {
      activeTab: 'basic',
      // 基本设置
      basicForm: {
        systemName: '',
        courseSelectionStart: '',
        courseSelectionEnd: '',
        maxCourseSelection: 5
      },
      basicRules: {
        systemName: [
          { required: true, message: '请输入系统名称', trigger: 'blur' }
        ],
        courseSelectionStart: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        courseSelectionEnd: [
          { required: true, message: '请选择结束时间', trigger: 'change' }
        ],
        maxCourseSelection: [
          { required: true, message: '请输入最大选课数', trigger: 'blur' }
        ]
      },
      // 预警设置
      alertForm: {
        alertThreshold: 60,
        alertMethods: ['system']
      },
      alertRules: {
        alertThreshold: [
          { required: true, message: '请设置预警分数线', trigger: 'blur' }
        ],
        alertMethods: [
          { required: true, message: '请选择至少一种通知方式', trigger: 'change' }
        ]
      },
      // 日志查询
      logSearch: {
        operation: '',
        timeRange: []
      },
      logs: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 数据备份
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
        const settings = response.data
        this.basicForm = {
          systemName: settings.systemName,
          courseSelectionStart: settings.courseSelectionStart,
          courseSelectionEnd: settings.courseSelectionEnd,
          maxCourseSelection: settings.maxCourseSelection
        }
        this.alertForm = {
          alertThreshold: settings.alertThreshold,
          alertMethods: settings.alertMethods
        }
      } catch (error) {
        this.$message.error('获取设置失败')
      }
    },
    async saveBasicSettings() {
      this.$refs.basicForm.validate(async (valid) => {
        if (valid) {
          try {
            await updateSettings({ ...this.basicForm, type: 'basic' })
            this.$message.success('保存成功')
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    async saveAlertSettings() {
      this.$refs.alertForm.validate(async (valid) => {
        if (valid) {
          try {
            await updateSettings({ ...this.alertForm, type: 'alert' })
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
          page: this.currentPage,
          size: this.pageSize,
          ...this.logSearch
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
        await exportLogsApi(this.logSearch)
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
    async fetchBackups() {
      try {
        const response = await getBackups()
        this.backups = response.data
      } catch (error) {
        this.$message.error('获取备份列表失败')
      }
    },
    async createBackup() {
      try {
        await createBackupApi()
        this.$message.success('创建备份成功')
        this.fetchBackups()
      } catch (error) {
        this.$message.error('创建备份失败')
      }
    },
    async restoreBackup(backup) {
      try {
        await this.$confirm('确定要恢复该备份吗？恢复后当前数据将被覆盖', '提示', {
          type: 'warning'
        })
        await restoreBackupApi(backup.id)
        this.$message.success('恢复成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('恢复失败')
        }
      }
    },
    async deleteBackup(backup) {
      try {
        await this.$confirm('确定要删除该备份吗？', '提示', {
          type: 'warning'
        })
        await deleteBackupApi(backup.id)
        this.$message.success('删除成功')
        this.fetchBackups()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.settings {
  padding: 20px;
}
.table-toolbar {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.el-form {
  max-width: 600px;
}
</style> 