import request from './request'

export const getSummary = () => request.get('/dashboard/summary')
export const getChildrenPerClass = () => request.get('/dashboard/children-per-class')
export const getTeacherByPosition = () => request.get('/dashboard/teacher-by-position')
export const getMonthlyFees = () => request.get('/dashboard/monthly-fees')
export const getGenderRatio = () => request.get('/dashboard/gender-ratio')
