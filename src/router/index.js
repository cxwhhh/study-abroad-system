import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Preparation from '../views/steps/Preparation.vue'
import Materials from '../views/steps/Materials.vue'
import Submission from '../views/steps/Submission.vue'
import Interview from '../views/steps/Interview.vue'
import Visa from '../views/steps/Visa.vue'
import Assistant from '../views/steps/Assistant.vue'
import Rebate from '../views/steps/Rebate.vue'
import HomeFirst from '../views/HomeFirst.vue'
import ApplicationSteps from '../views/steps/ApplicationSteps.vue'
import Login from '../components/Login.vue'
import FileUpload from '../views/FileUpload.vue'
import ImageUpload from '../views/ImageUpload.vue'
import R from '../views/steps/r.vue'
import Country from '../views/Country.vue'
import Schools from '../views/schools.vue'
import RebateStart from '../views/steps/rebateStart.vue'
// AiSelect 已移动到 ai-school-selection 文件夹
import HelpCenter from '../views/HelpCenter.vue'
import User from '@/components/admin/user/User.vue'
import Privacy from '../views/Privacy.vue'
import Terms from '../views/Terms.vue'
import Sitemap from '../views/Sitemap.vue'
import sessionManager from '../utils/sessionManager'




/**
 * 路由配置
 * 定义应用的页面路由规则
 */
const routes = [
  {
    path: '/home',
    name: 'HomeFirst',
    component: () => import('../views/HomeFirst.vue'),
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/home2',
    name: 'HomeFirst2',
    component: () => import('../views/HomeFirst2.vue'),
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/',
    name: 'HomeFirst3',
    component: () => import('../views/HomeFirst3.vue'),
    meta: {
      keepAlive: true,
      title: '综合主页'
    }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/steps/preparation',
    name: 'Preparation',
    component: Preparation,
    beforeEnter: (to, from, next) => {
      next()
    }
  },
  {
    path: '/steps/materials',
    name: 'Materials',
    component: Materials,
    beforeEnter: (to, from, next) => {
      next()
    }
  },
  {
    path: '/steps/submission',
    name: 'Submission',
    component: Submission
  },
  {
    path: '/steps/interview',
    name: 'Interview',
    component: Interview
  },
  {
    path: '/steps/visa',
    name: 'Visa',
    component: Visa
  },
  {
    path: '/steps/assistant',
    name: 'Assistant',
    component: Assistant
  },
  {
    path: '/aiselectNew',
    name: 'AiSelectNew',
    component: () => import('../views/ai-school-selection/AiSelectNew.vue'),
    meta: { title: 'AI智能选校' }
  },
  {
    path: '/matching',
    name: 'SchoolMatching',
    component: () => import('../views/ai-school-selection/matching/SchoolMatching.vue'),
    meta: { title: '智能择校匹配' }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/adminlogin.vue')
  },
  {
    path: '/admin1',
    name: 'Admin1',
    component: () => import('../views/admin/admin1.vue'),
  },
  {
    path: '/steps/rebate',
    name: 'Rebate',
    component: Rebate
  },
  {
    path: '/r',
    name: 'R',
    component: R
  },
  {
    path: '/rebateStart',
    name: 'RebateStart',
    component: RebateStart
  },
  {
    path: '/country',
    name: 'Country',
    component: Country,
    meta: {
      keepAlive: true,
      title: '院校库'
    }
  },
  {
    path: '/customer-service',
    name: 'CompanyAbout',
    component: () => import('../views/CompanyAbout.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/CompanyAbout.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/schools/:id',
    name: 'Schools',
    component: Schools,
    meta: {
      title: '学校详情'
    }
  },
  {
    path: '/schools/major-index',
    name: 'MajorIndex',
    component: () => import('../views/schools/MajorIndex.vue')
  },
  {
    path: '/schools/:schoolId/major/:majorId',
    name: 'MajorDetail',
    component: () => import('../views/schools/MajorDetail.vue')
  },
  {
    path: '/self-more',
    name: 'SelfMore',
    component: () => import('../views/steps/self-more.vue')
  },
  {
    path: '/study',
    name: 'Study',
    component: () => import('../views/application/study.vue')
  },
  {
    path: '/visa-service',
    name: 'VisaService',
    component: () => import('../views/application/VisaService.vue'),
    meta: { title: '签证服务' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/application-steps',
    name: 'ApplicationSteps',
    component: () => import('../views/steps/ApplicationSteps.vue'),
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },

  {
    path: '/file-upload',
    name: 'FileUpload',
    component: FileUpload,
    meta: { requiresAuth: true }
  },
  {
    path: '/image-upload',
    name: 'ImageUpload',
    component: ImageUpload,
    meta: { requiresAuth: false }
  },
  {
    path: '/help',
    name: 'HelpCenter',
    component: HelpCenter,
    meta: { requiresAuth: false, title: '帮助中心' }
  },
  {
    path: '/admin/teachers',
    name: 'AdminTeachers',
    component: User,
    meta: {
      requiresAuth: true,
      title: '教师管理'
    }
  },
  {
    path: '/privacy',
    name: 'Privacy',
    component: Privacy,
    meta: { requiresAuth: false }
  },
  {
    path: '/terms',
    name: 'Terms',
    component: Terms,
    meta: { requiresAuth: false }
  },
  {
    path: '/sitemap',
    name: 'Sitemap',
    component: Sitemap,
    meta: { requiresAuth: false }
  },
  {
    path: '/teacher-consultation',
    name: 'TeacherConsultation',
    component: () => import('../views/TeacherConsultation.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/cases',
    name: 'Cases',
    component: () => import('../views/Cases.vue'),
    meta: { requiresAuth: false, title: '成功案例' }
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('../views/News.vue'),
    meta: { requiresAuth: false, title: '留学资讯' }
  },
  {
    path: '/news/:id',
    name: 'NewsDetail',
    component: () => import('../views/News.vue'),
    meta: { requiresAuth: false, title: '资讯详情' }
  },
  {
    path: '/test/progress',
    name: 'ProgressTest',
    component: () => import('../views/test/ProgressTest.vue'),
    meta: { requiresAuth: false, title: '申请进度测试' }
  },
  {
    path: '/test/materials-progress',
    name: 'MaterialsProgressTest',
    component: () => import('../views/test/MaterialsProgressTest.vue'),
    meta: { requiresAuth: false, title: '材料进度测试' }
  }
]

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
  try {
    const isLoggedIn = sessionManager.isLoggedIn()
    const userInfo = sessionManager.getUserInfo()

    // 如果需要登录但未登录
    if (to.matched.some(record => record.meta.requiresAuth)) {
      if (!isLoggedIn || !userInfo) {
        // 清除可能存在的无效状态
        sessionManager.clearSession()

        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
        return
      }

      // 验证后端会话是否有效（但在刚登录后跳过验证）
      try {
        await sessionManager.checkSession()
      } catch (sessionError) {
        console.warn('后端会话验证失败，清除本地登录状态:', sessionError.message)
        // 后端会话无效，清除本地状态并重定向到登录页
        sessionManager.clearSession()
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
        return
      }
    }

    // 如果已登录且访问登录页，验证登录状态的有效性
    if (to.path === '/login' && isLoggedIn && userInfo) {
      // 只有当userInfo确实有效且包含必要信息时才重定向
      if (userInfo && (userInfo.id || userInfo.username)) {
        next('/')
        return
      } else {
        // userInfo无效，清除登录状态，允许访问登录页
        sessionManager.clearSession()
      }
    }

    next()
  } catch (error) {
    console.error('路由守卫错误:', error)
    next('/homefirst')
  }
})

router.afterEach(() => {
  // 页面切换完成后立即执行清理
  requestAnimationFrame(() => {
    document.body.classList.remove('page-transitioning')
  })
})

export default router
