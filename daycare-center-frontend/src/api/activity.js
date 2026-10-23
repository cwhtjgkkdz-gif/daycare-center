import request from './request'

export const getActivityPage = (params) => request.get('/activity/page', { params })
export const getActivityById = (id) => request.get(`/activity/${id}`)
export const addActivity = (data) => request.post('/activity', data)
export const updateActivity = (data) => request.put('/activity', data)
export const deleteActivity = (id) => request.delete(`/activity/${id}`)
