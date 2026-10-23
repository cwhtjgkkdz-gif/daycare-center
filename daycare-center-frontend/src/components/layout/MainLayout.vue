<template>
  <div class="layout">
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <Sidebar :is-collapse="isCollapse" />
    </aside>
    <div class="main">
      <Header :is-collapse="isCollapse" @toggle="isCollapse = !isCollapse" />
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const isCollapse = ref(false)
</script>

<style scoped>
.layout { display: flex; height: 100vh; overflow: hidden; }
.sidebar { width: 232px; flex-shrink: 0; transition: width 0.3s ease; }
.sidebar.collapsed { width: 64px; }
.main { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-width: 0; }
.content { flex: 1; padding: 16px 20px; background: #f0f2f5; overflow-y: auto; }
</style>
