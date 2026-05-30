<!-- 根组件：仅承载路由出口，全局基础样式在此定义 -->
<template>
  <router-view />
</template>

<script setup>
// 无业务逻辑，布局与页面由各 layout / view 负责
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>

