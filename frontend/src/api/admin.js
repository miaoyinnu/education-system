import request from '@/utils/request'

// 获取仪表盘统计数据
export function getStatistics() {
  return request({
    url: '/api/admin/dashboard/statistics',
    method: 'get'
  })
}

// 获取图表数据
export function getChartData() {
  return request({
    url: '/api/admin/dashboard/charts',
    method: 'get'
  })
}

// 获取最新预警
export function getLatestAlerts() {
  return request({
    url: '/api/admin/dashboard/alerts',
    method: 'get'
  })
}

// 获取最新日志
export function getLatestLogs() {
  return request({
    url: '/api/admin/dashboard/logs',
    method: 'get'
  })
}

// 获取系统设置
export function getSettings() {
  return request({
    url: '/api/admin/settings',
    method: 'get'
  })
}

// 更新系统设置
export function updateSettings(data) {
  return request({
    url: '/api/admin/settings',
    method: 'post',
    data
  })
}

// 获取系统日志
export function getLogs(params) {
  return request({
    url: '/api/admin/settings/logs',
    method: 'get',
    params
  })
}

// 导出系统日志
export function exportLogs(params) {
  return request({
    url: '/api/admin/settings/logs/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 创建备份
export function createBackup() {
  return request({
    url: '/api/admin/settings/backup',
    method: 'post'
  })
}

// 获取备份列表
export function getBackups() {
  return request({
    url: '/api/admin/settings/backup',
    method: 'get'
  })
}

// 还原备份
export function restoreBackup(id) {
  return request({
    url: `/api/admin/settings/backup/${id}/restore`,
    method: 'post'
  })
}

// 删除备份
export function deleteBackup(id) {
  return request({
    url: `/api/admin/settings/backup/${id}`,
    method: 'delete'
  })
} 