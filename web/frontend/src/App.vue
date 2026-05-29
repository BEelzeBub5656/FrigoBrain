<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

:root {
  --fb-primary: #4CAF50;
  --fb-primary-dark: #388E3C;
  --fb-primary-light: #C8E6C9;
  --fb-bg: #f0f2f5;
  --fb-sidebar-bg: #1a1a2e;
  --fb-sidebar-text: #a0a0b8;
  --fb-sidebar-active: #4CAF50;
  --fb-card-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  --fb-radius: 12px;
}

.el-button--primary {
  --el-button-bg-color: var(--fb-primary);
  --el-button-border-color: var(--fb-primary);
  --el-button-hover-bg-color: var(--fb-primary-dark);
  --el-button-hover-border-color: var(--fb-primary-dark);
}
</style>
