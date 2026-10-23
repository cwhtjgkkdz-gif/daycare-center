import request from './request'

export const getAttendancePage = (params) => request.get('/attendance/page', { params })
export const getAttendanceById = (id) => request.get(`/attendance/${id}`)
export const checkIn = (childId, classId) => request.post(`/attendance/check-in?childId=${childId}&classId=${classId}`)
export const checkOut = (id) => request.put(`/attendance/check-out/${id}`)
export const updateAttendance = (data) => request.put('/attendance', data)
export const deleteAttendance = (id) => request.delete(`/attendance/${id}`)
export const getStatistics = (params) => request.get('/attendance/statistics', { params })
