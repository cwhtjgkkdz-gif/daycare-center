<template>
  <div class="sidebar-wrap">
    <!-- Logo区域 -->
    <div class="logo-area">
      <div class="logo-icon">
        <svg viewBox="0 0 40 40" width="36" height="36">
          <circle cx="20" cy="14" r="10" fill="#FFB74D" />
          <rect x="6" y="24" width="28" height="14" rx="4" fill="#66BB6A" />
          <circle cx="20" cy="14" r="6" fill="#FFF9C4" />
        </svg>
      </div>
      <transition name="fade">
        <div v-if="!isCollapse" class="logo-text">
          <div class="logo-title">托育中心</div>
          <div class="logo-sub">管理系统</div>
        </div>
      </transition>
    </div>

    <!-- 菜单 -->
    <el-scrollbar style="height:calc(100% - 180px)">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="transparent"
        text-color="rgba(255,255,255,0.7)"
        active-text-color="#fff"
        router
        class="side-menu"
      >
        <template v-for="menu in userStore.menus" :key="menu.id">
          <!-- 子菜单 -->
          <el-sub-menu v-if="menu.children?.length" :index="String(menu.id)">
            <template #title>
              <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children" :key="child.id"
              :index="child.path"
            >
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 单菜单 -->
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>

    <!-- 底部信息 -->
    <div class="sidebar-footer" v-if="!isCollapse">
      <span>v1.0</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../store'

defineProps({ isCollapse: Boolean })
const userStore = useUserStore()
const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-wrap {
  height: 100%;
  background: linear-gradient(180deg, #1a1f36 0%, #26304e 100%);
  display: flex; flex-direction: column;
}

.logo-area {
  display: flex; align-items: center; gap: 10px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.logo-icon { flex-shrink: 0; }
.logo-text { overflow: hidden; white-space: nowrap; }
.logo-title { font-size: 16px; font-weight: 700; color: #fff; letter-spacing: 1px; }
.logo-sub { font-size: 11px; color: rgba(255,255,255,0.45); margin-top: 2px; }

/* 菜单覆盖 */
.side-menu { border-right: none !important; padding: 8px 0; }
.side-menu :deep(.el-menu-item),
.side-menu :deep(.el-sub-menu__title) {
  height: 48px; line-height: 48px; margin: 2px 8px; border-radius: 8px;
  font-size: 14px;
}
.side-menu :deep(.el-menu-item:hover),
.side-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255,255,255,0.08) !important;
}
.side-menu :deep(.el-menu-item.is-active) {
  background: rgba(64,158,255,0.25) !important;
  border-right: none;
}
.side-menu :deep(.el-sub-menu .el-menu) { padding: 0 0 0 8px; }
.side-menu :deep(.el-sub-menu .el-menu-item) { padding-left: 52px !important; height: 40px; line-height: 40px; font-size: 13px; }

.sidebar-footer {
  padding: 12px 20px; border-top: 1px solid rgba(255,255,255,0.06);
  color: rgba(255,255,255,0.25); font-size: 12px; text-align: center;
}
</style>
