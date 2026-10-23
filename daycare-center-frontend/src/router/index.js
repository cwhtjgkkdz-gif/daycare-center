import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store'
import { getUserInfo } from '../api/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/',
    component: () => import('../components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/dashboard/index.vue'), meta: { title: '首页' } },
      { path: 'class', name: 'Class', component: () => import('../views/class/index.vue'), meta: { title: '班级管理' } },
      { path: 'child', name: 'Child', component: () => import('../views/child/index.vue'), meta: { title: '儿童档案' } },
      { path: 'teacher', name: 'Teacher', component: () => import('../views/teacher/index.vue'), meta: { title: '教职工管理' } },
      { path: 'activity', name: 'Activity', component: () => import('../views/activity/index.vue'), meta: { title: '园区活动' } },
      { path: 'notice', name: 'Notice', component: () => import('../views/notice/index.vue'), meta: { title: '通知公告' } },
      { path: 'finance', name: 'Finance', component: () => import('../views/finance/index.vue'), meta: { title: '财务管理' } },
      { path: 'attendance', name: 'Attendance', component: () => import('../views/attendance/index.vue'), meta: { title: '考勤管理' } },
      { path: 'system/user', name: 'SystemUser', component: () => import('../views/system/user/index.vue'), meta: { title: '用户管理' } },
      { path: 'system/role', name: 'SystemRole', component: () => import('../views/system/role/index.vue'), meta: { title: '角色管理' } },
      { path: 'system/menu', name: 'SystemMenu', component: () => import('../views/system/menu/index.vue'), meta: { title: '菜单管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录守卫
router.beforeEach(async (to, from, next) => {
  const store = useUserStore()

  if (to.meta.noAuth) {
    next()
    return
  }

  // 已登录直接放行
  if (store.isLoggedIn) {
    next()
    return
  }

  // 未登录，尝试从后端获取用户信息（刷新页面时Session还在）
  try {
    const res = await getUserInfo()
    store.setUser(res.data.userInfo)
    store.setMenus(res.data.menus)
    next()
  } catch {
    // Session过期，跳登录
    next('/login')
  }
})

export default router
