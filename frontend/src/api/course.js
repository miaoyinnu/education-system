import request from '@/utils/request'

export function getCourses(params) {
  return request({
    url: '/api/admin/courses',
    method: 'get',
    params
  })
}

export function addCourse(data) {
  return request({
    url: '/api/admin/course',
    method: 'post',
    data
  })
}

export function updateCourse(id, data) {
  return request({
    url: `/api/admin/course/${id}`,
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/api/admin/course/${id}`,
    method: 'delete'
  })
}

export function autoScheduleCourses() {
  return request({
    url: '/api/admin/schedule/auto',
    method: 'post'
  })
} 