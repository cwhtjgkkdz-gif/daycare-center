<template>
  <div class="header">
    <div class="header-left">
      <el-icon class="collapse-btn" @click="$emit('toggle')" :size="20">
        <Fold v-if="!isCollapse" /><Expand v-else />
      </el-icon>
      <!-- 面包屑 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <!-- 快捷操作 -->
      <el-tooltip content="接口文档" placement="bottom">
        <el-icon class="header-icon" @click="openDoc" :size="18"><Link /></el-icon>
      </el-tooltip>
      <el-tooltip content="全屏" placement="bottom">
        <el-icon class="header-icon" @click="toggleFullscreen" :size="18"><FullScreen /></el-icon>
      </el-tooltip>

      <el-divider direction="vertical" />

      <!-- 用户 -->
      <el-dropdown @command="handleCommand" trigger="click">
        <div class="user-info">
          <el-avatar :size="32" style="background:#409EFF">
            {{ userStore.username?.charAt(0) }}
          </el-avatar>
          <span class="username">{{ userStore.username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon> 个人信息
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../store'
import { logout as apiLogout } from '../../api/auth'

defineProps({ isCollapse: Boolean })
defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const openDoc = () => window.open('http://localhost:8080/doc.html', '_blank')
const toggleFullscreen = () => {
  if (!document.fullscreenElement) document.documentElement.requestFullscreen()
  else document.exitFullscreen()
}

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    await apiLogout()
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.header {
  height: 56px; display: flex; align-items: center; justify-content: space-between;
  padding: 0 20px; background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  z-index: 10; position: relative;
}
.header-left { display: flex; align-items: center; gap: 16px; }
.collapse-btn { cursor: pointer; color: #606266; }
.collapse-btn:hover { color: #409EFF; }
.header-right { display: flex; align-items: center; gap: 16px; }
.header-icon { cursor: pointer; color: #909399; }
.header-icon:hover { color: #409EFF; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.username { font-size: 14px; color: #303133; max-width: 100px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
