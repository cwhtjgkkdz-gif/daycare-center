import request from './request'

export const getNoticePage = (params) => request.get('/notice/page', { params })
export const getNoticeById = (id) => request.get(`/notice/${id}`)
export const addNotice = (data) => request.post('/notice', data)
export const updateNotice = (data) => request.put('/notice', data)
export const deleteNotice = (id) => request.delete(`/notice/${id}`)
