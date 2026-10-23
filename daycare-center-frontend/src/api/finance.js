import request from './request'

export const getFinancePage = (params) => request.get('/finance/page', { params })
export const getFinanceById = (id) => request.get(`/finance/${id}`)
export const addFinance = (data) => request.post('/finance', data)
export const updateFinance = (data) => request.put('/finance', data)
export const deleteFinance = (id) => request.delete(`/finance/${id}`)
