import request from './request'

export const getUserPage = (params) => request.get('/user/page', { params })
export const getUserById = (id) => request.get(`/user/${id}`)
export const addUser = (data) => request.post('/user', data)
export const updateUser = (data) => request.put('/user', data)
export const deleteUser = (id) => request.delete(`/user/${id}`)
export const updateUserStatus = (id, status) => request.put(`/user/${id}/status?status=${status}`)
export const resetPassword = (id) => request.put(`/user/${id}/password`)
