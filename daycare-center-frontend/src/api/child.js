import request from './request'

export const getChildPage = (params) => request.get('/child/page', { params })
export const getChildById = (id) => request.get(`/child/${id}`)
export const addChild = (data) => request.post('/child', data)
export const updateChild = (data) => request.put('/child', data)
export const deleteChild = (id) => request.delete(`/child/${id}`)
export const updateChildStatus = (id, status) => request.put(`/child/${id}/status?status=${status}`)
