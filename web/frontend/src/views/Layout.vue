<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapsed ? '64px' : '240px'" class="layout-sidebar">
      <div class="sidebar-header" @click="isCollapsed = !isCollapsed">
        <div class="sidebar-logo">
          <el-icon :size="28" color="#4CAF50"><ColdDrink /></el-icon>
          <span v-show="!isCollapsed" class="sidebar-brand">FrigoBrain</span>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#1a1a2e"
        text-color="#a0a0b8"
        active-text-color="#4CAF50"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <el-icon><Goods /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/nutrition">
          <el-icon><DataLine /></el-icon>
          <span>营养仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/recipes">
          <el-icon><Notebook /></el-icon>
          <span>菜谱库</span>
        </el-menu-item>
        <el-menu-item index="/waste">
          <el-icon><TrendCharts /></el-icon>
          <span>浪费报告</span>
        </el-menu-item>
        <el-menu-item index="/devices">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer" v-show="!isCollapsed">
        <div class="sidebar-footer-text">FrigoBrain v1.0</div>
      </div>
    </el-aside>

    <el-container class="layout-main">
      <el-header class="layout-topbar">
        <div class="topbar-left">
          <el-icon class="collapse-btn" @click="isCollapsed = !isCollapsed" :size="20">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <h2 class="topbar-title">{{ pageTitle }}</h2>
        </div>
        <div class="topbar-right">
          <el-tooltip content="通知" placement="bottom">
            <el-badge :value="3" class="notification-badge">
              <el-icon :size="20" class="topbar-icon"><Bell /></el-icon>
            </el-badge>
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-profile">
              <el-avatar :size="36" class="user-avatar">
                {{ userInitial }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon> 设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import gsap from 'gsap'
import {
  ColdDrink, Odometer, Goods, DataLine, Notebook,
  TrendCharts, Monitor, Fold, Expand, Bell,
  User, Setting, SwitchButton, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => route.meta?.title || '仪表盘')

const userStr = localStorage.getItem('fb_user')
const userData = userStr ? JSON.parse(userStr) : { username: 'Admin' }
const userName = computed(() => userData.username || 'Admin')
const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

function handleCommand(command) {
  if (command === 'logout') {
    localStorage.removeItem('fb_token')
    localStorage.removeItem('fb_user')
    router.push('/login')
  }
}

// GSAP page transition on route change
watch(() => route.path, () => {
  const main = document.querySelector('.layout-content')
  if (main) {
    gsap.fromTo(main, { opacity: 0, y: 20 }, { opacity: 1, y: 0, duration: 0.4, ease: 'power2.out' })
  }
})

// Initial sidebar entrance
onMounted(() => {
  gsap.from('.layout-sidebar', { x: -40, opacity: 0, duration: 0.6, ease: 'power3.out' })
  gsap.from('.layout-topbar', { y: -20, opacity: 0, duration: 0.4, delay: 0.2, ease: 'power2.out' })
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-sidebar {
  background-color: var(--fb-sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  overflow: hidden;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.sidebar-header {
  padding: 20px 16px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sidebar-brand {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  padding: 8px 0;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

.sidebar-menu.el-menu--collapse {
  width: 64px;
}

.sidebar-menu .el-menu-item {
  margin: 2px 8px;
  border-radius: 8px;
  font-size: 14px;
  height: 44px;
  line-height: 44px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(76, 175, 80, 0.1);
}

.sidebar-menu .el-menu-item.is-active {
  background-color: rgba(76, 175, 80, 0.15);
}

.sidebar-footer {
  padding: 12px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  text-align: center;
}

.sidebar-footer-text {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
}

.layout-main {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-topbar {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  flex-shrink: 0;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
  padding: 4px;
  border-radius: 6px;
}

.collapse-btn:hover {
  background-color: #f0f2f5;
}

.topbar-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.topbar-icon {
  color: #606266;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
}

.topbar-icon:hover {
  background-color: #f0f2f5;
}

.notification-badge :deep(.el-badge__content) {
  border: none;
  font-size: 10px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
}

.user-profile:hover {
  background-color: #f0f2f5;
}

.user-avatar {
  background-color: var(--fb-primary);
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.layout-content {
  flex: 1;
  overflow-y: auto;
  background-color: var(--fb-bg);
  padding: 24px;
}
</style>
