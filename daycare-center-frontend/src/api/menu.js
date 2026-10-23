import request from './request'

export const getMenuTree = () => request.get('/menu/tree')
export const getRoleMenuTree = (roleId) => request.get(`/menu/role-tree/${roleId}`)
export const getMenuById = (id) => request.get(`/menu/${id}`)
export const addMenu = (data) => request.post('/menu', data)
export const updateMenu = (data) => request.put('/menu', data)
export const deleteMenu = (id) => request.delete(`/menu/${id}`)
