import request from './request'

export const getRolePage = (params) => request.get('/role/page', { params })
export const getRoleList = () => request.get('/role/list')
export const getRoleById = (id) => request.get(`/role/${id}`)
export const addRole = (data) => request.post('/role', data)
export const updateRole = (data) => request.put('/role', data)
export const deleteRole = (id) => request.delete(`/role/${id}`)
export const assignMenus = (id, menuIds) => request.put(`/role/${id}/menus`, { menuIds })
export const getRoleMenuIds = (id) => request.get(`/role/${id}/menuIds`)
