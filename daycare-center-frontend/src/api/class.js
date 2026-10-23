import request from './request'

export const getClassPage = (params) => request.get('/class/page', { params })
export const getClassList = () => request.get('/class/list')
export const getClassById = (id) => request.get(`/class/${id}`)
export const addClass = (data) => request.post('/class', data)
export const updateClass = (data) => request.put('/class', data)
export const deleteClass = (id) => request.delete(`/class/${id}`)
