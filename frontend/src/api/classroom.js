import request from '@/utils/request'

export function getClassrooms(params) {
  return request({
    url: '/api/admin/classrooms',
    method: 'get',
    params
  })
}

export function addClassroom(data) {
  return request({
    url: '/api/admin/classroom',
    method: 'post',
    data
  })
}

export function updateClassroom(id, data) {
  return request({
    url: `/api/admin/classroom/${id}`,
    method: 'put',
    data
  })
}

export function deleteClassroom(id) {
  return request({
    url: `/api/admin/classroom/${id}`,
    method: 'delete'
  })
}

export function getClassroomUsage(id) {
  return request({
    url: `/api/admin/classroom/${id}/usage`,
    method: 'get'
  })
} 