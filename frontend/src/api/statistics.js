import request from '@/utils/request'

export function getGradeStats() {
  return request({
    url: '/api/admin/stats/grades',
    method: 'get'
  })
}

export function getClassroomStats() {
  return request({
    url: '/api/admin/stats/classroom-usage',
    method: 'get'
  })
}

export function getGradeAlerts() {
  return request({
    url: '/api/admin/grade-alert/list',
    method: 'get'
  })
}

export function setGradeAlertThreshold(data) {
  return request({
    url: '/api/admin/grade-alert/settings',
    method: 'post',
    data
  })
}

export function handleGradeAlert(id) {
  return request({
    url: `/api/admin/grade-alert/${id}/process`,
    method: 'put'
  })
} 