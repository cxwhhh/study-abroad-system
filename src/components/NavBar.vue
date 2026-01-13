<template>
  <div class="nav-container" :class="{ 'nav-scrolled': isScrolled }">
    <div class="nav-content">
      <!-- Logo区域 -->
      <div class="nav-logo" @click="goHome">
        <div class="logo-circle">X</div>
        <div class="logo-text">
          <span class="logo-main">XINYINGMEI</span>
          <span class="logo-year">2026</span>
        </div>
      </div>

      <!-- 菜单链接 -->
      <div class="nav-links">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/country" class="nav-link">院校库</router-link>
        <router-link to="/schools/major-index" class="nav-link" @mouseenter="prefetchMajorIndex"
          @focus="prefetchMajorIndex" @mousedown="prefetchMajorIndex">专业库</router-link>
        <router-link to="/cases" class="nav-link">成功案例</router-link>
        <router-link to="/news" class="nav-link">留学资讯</router-link>
        <router-link to="/about" class="nav-link">关于我们</router-link>

        <!-- Search Icon -->
        <div class="nav-icon" @click="toggleSearch">
          <el-icon>
            <Search />
          </el-icon>
        </div>

        <!-- User Icon / Login -->
        <div class="nav-icon">
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleCommand">
              <el-avatar :size="28" class="user-avatar" :src="avatarSrc" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <span class="login-text" @click="goToLogin">登录</span>
          </template>
        </div>
      </div>

      <!-- 移动端菜单按钮 -->
      <div class="mobile-menu-btn" @click="toggleMobileMenu">
        <el-icon>
          <Menu />
        </el-icon>
      </div>
    </div>
  </div>

  <!-- 搜索弹窗 (保持原样) -->
  <Transition name="fade">
    <div class="search-overlay" v-show="showSearch" @click="handleOverlayClick">
      <div class="search-dialog" :class="{ 'search-dialog-show': showSearch }">
        <div class="search-container">
          <div class="search-input-wrapper">
            <el-input v-model="searchQuery" placeholder="搜索..." class="search-input" :prefix-icon="Search"
              ref="searchInputRef" @keyup.enter="handleSearch" @keyup.esc="showSearch = false" clearable />
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Menu, ArrowRight } from '@element-plus/icons-vue'
import sessionManager from '../utils/sessionManager'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const isScrolled = ref(false)
const showSearch = ref(false)
const searchQuery = ref('')
const searchInputRef = ref(null)
const isLoggedIn = ref(false)
const currentUser = ref(null)
let majorIndexPrefetched = false

const goHome = () => router.push('/')

const getRandomAvatar = (userId) => {
  const avatars = [
    '/images/avatar1.jpg',
    '/images/avatar2.jpg',
    '/images/avatar3.jpg',
    '/images/1.jpg',
    '/images/2.jpg',
    '/images/3.jpg',
    '/images/4.jpg',
    '/images/5.jpg',
    '/images/6.jpg',
    '/images/7.jpg',
    '/images/8.jpg'
  ]
  const seed = Number(userId) || 1
  const index = seed % avatars.length
  return avatars[index]
}

const avatarSrc = computed(() => {
  const user = currentUser.value
  if (!user) return ''
  return user.avatar || getRandomAvatar(user.id)
})

const prefetchMajorIndex = async () => {
  if (majorIndexPrefetched) return
  majorIndexPrefetched = true

  const tasks = [
    import('../views/schools/MajorIndex.vue'),
    import('../api/schoolService').then((m) => m.default?.getAllSchoolsCached?.().catch(() => { })),
    import('../api/majorService').then((m) => m.default?.getAllMajorsCached?.().catch(() => { }))
  ]
  await Promise.allSettled(tasks)
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

const toggleSearch = () => {
  showSearch.value = !showSearch.value
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push(`/search?q=${encodeURIComponent(searchQuery.value.trim())}`)
    showSearch.value = false
  }
}

const handleOverlayClick = (e) => {
  if (e.target.classList.contains('search-overlay')) {
    showSearch.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

const handleCommand = (command) => {
  if (command === 'profile') router.push('/profile')
  if (command === 'logout') handleLogout()
}

const handleLogout = () => {
  sessionManager.clearSession()
  isLoggedIn.value = false
  ElMessage.success('已退出登录')
}

const checkLoginStatus = () => {
  isLoggedIn.value = sessionManager.isLoggedIn()
  currentUser.value = isLoggedIn.value ? sessionManager.getUserInfo() : null
}

// Watchers and Lifecycle
watch(showSearch, async (newVal) => {
  if (newVal) {
    await nextTick()
    searchInputRef.value?.focus()
  } else {
    searchQuery.value = ''
  }
})

onMounted(() => {
  checkLoginStatus()
  if (sessionManager.isLoggedIn()) {
    sessionManager.startSessionCheck()
    sessionManager.checkSession().finally(checkLoginStatus)
  }
  window.addEventListener('session-changed', checkLoginStatus)
  window.addEventListener('scroll', handleScroll)
})

watch(() => route.fullPath, () => {
  if (!sessionManager.isLoggedIn()) {
    checkLoginStatus()
    return
  }
  sessionManager.checkSession().finally(checkLoginStatus)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('session-changed', checkLoginStatus)
})
</script>

<style scoped>
.nav-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 24px 40px;
  transition: all 0.3s ease;
  background: transparent;
  font-family: 'Times New Roman', serif;
}

.nav-scrolled {
  background: rgba(249, 248, 244, 0.95);
  backdrop-filter: blur(10px);
  padding: 16px 40px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-circle {
  width: 32px;
  height: 32px;
  background-color: #C5A059;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
  font-family: serif;
}

.logo-text {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.logo-main {
  font-weight: bold;
  font-size: 18px;
  color: #1C1917;
  letter-spacing: 0.05em;
}

.logo-year {
  color: #78716C;
  font-size: 14px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-link {
  text-decoration: none;
  color: #57534E;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  font-family: sans-serif;
  transition: color 0.2s;
  position: relative;
}

.nav-link:hover {
  color: #C5A059;
}

.nav-icon {
  color: #1C1917;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.nav-icon:hover {
  color: #C5A059;
}

.login-text {
  font-size: 13px;
  font-weight: 600;
  font-family: sans-serif;
  text-transform: uppercase;
}

.mobile-menu-btn {
  display: none;
  font-size: 24px;
  cursor: pointer;
  color: #1C1917;
}

/* 搜索弹窗样式复用 */
.search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  z-index: 2000;
  display: flex;
  justify-content: center;
  padding-top: 100px;
}

.search-container {
  width: 100%;
  max-width: 600px;
  padding: 0 20px;
}

.search-input :deep(.el-input__wrapper) {
  background: transparent;
  box-shadow: none;
  border-bottom: 2px solid #1C1917;
  border-radius: 0;
  padding: 10px 0;
}

.search-input :deep(.el-input__inner) {
  font-size: 24px;
  color: #1C1917;
  font-family: serif;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }

  .nav-container {
    padding: 16px 20px;
  }
}
</style>
