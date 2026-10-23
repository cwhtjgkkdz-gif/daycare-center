import { defineStore } from 'pinia'

/**
 * 用户状态管理 — 全局共享的登录态
 *
 * 数据来源：登录接口返回 { userInfo, menus }
 * 消费方：
 *   - router.beforeEach 读取 isLoggedIn 判断是否需要跳转登录
 *   - Sidebar.vue 读取 menus 动态渲染菜单树
 *   - Header.vue 读取 username 显示当前用户
 *
 * 注意：刷新页面后 store 会重置，但路由守卫会调用 /api/auth/info
 * 重新获取用户信息（后端 Session 仍然有效）
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null, // { id, username, realName, roleId, roleName, ... }
    menus: []       // 后端返回的菜单树 [{ id, menuName, path, icon, children }]
  }),
  getters: {
    isLoggedIn: (state) => !!state.userInfo,
    username: (state) => state.userInfo?.realName || state.userInfo?.username || ''
  },
  actions: {
    setUser(userInfo) { this.userInfo = userInfo },
    setMenus(menus) { this.menus = menus },
    logout() { this.userInfo = null; this.menus = [] }
  }
})
