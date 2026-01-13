<template>
  <div id="app">
    <router-view v-slot="{ Component, route }">
      <keep-alive :include="keepAliveNames">
        <component :is="Component" :key="route.name ?? route.path" />
      </keep-alive>
    </router-view>
  </div>
</template>

<script setup>
import { computed, onErrorCaptured, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const keepAliveNames = computed(() => {
  const routes = router.getRoutes()
  return routes
    .filter(r => r?.meta?.keepAlive && r.name)
    .map(r => String(r.name))
})

// 错误处理
onErrorCaptured((err, instance, info) => {
  console.error('Error:', err)
  console.error('Component:', instance)
  console.error('Info:', info)
  return false
})

// 预加载关键组件
onMounted(() => {
  // 预加载 HomeFirst 组件
  const HomeFirst = () => import('./views/HomeFirst3.vue')
  HomeFirst()
})
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

#app {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
  overflow-x: hidden;
}

/* 页面过渡时的背景色 */
body.page-transitioning {
  background-color: #fff;
}

/* 修改过渡效果 */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s ease-out;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 优化页面层级 */
.page-enter-active {
  z-index: 1;
}

.page-leave-active {
  z-index: 0;
}

/* 添加过渡期间的样式 */
.page-enter-from,
.page-leave-to {
  opacity: 0;
}

.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s ease;
}
</style>
