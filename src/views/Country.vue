<template>
  <div class="country-container">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>

    <!-- 国家选择横幅 -->
    <div class="country-hero">
      <div class="country-hero-content">
        <div class="section-label">GLOBAL INSTITUTIONS</div>
        <h1 class="country-title">院校库</h1>
        <div class="gold-separator"></div>
        <p class="country-subtitle">选择您心仪的留学国家，探索世界顶尖学府</p>
      </div>
    </div>

    <!-- 国家选择区域 -->
    <div class="country-selector-section">
      <div class="country-selector-container">
        <div class="country-tabs">
          <div v-for="(country, index) in countries" :key="index" class="country-tab"
            :class="{ 'active': selectedCountry === country.code }" @click="selectCountry(country.code)">
            <span class="country-name">{{ country.name }}</span>
          </div>
        </div>
      </div>
    </div>



    <!-- 学校列表 -->
    <div class="schools-section">
      <div class="schools-container">
        <div class="search-container">
          <!-- 添加返回按钮 -->
          <button class="back-button" @click="goBack">
            <el-icon>
              <ArrowLeft />
            </el-icon>
            <span>返回上一级</span>
          </button>
          <el-input v-model="searchKeyword" placeholder="搜索学校名称..." :prefix-icon="Search" clearable size="large"
            class="search-input" />
        </div>

        <!-- 筛选器 - 更新为水平布局 -->
        <div class="filter-container">
          <div class="filter-row">
            <div class="filter-group">
              <span class="filter-label">排序方式:</span>
              <el-select v-model="sortBy" placeholder="排序方式" size="large">
                <el-option label="综合排名" value="ranking"></el-option>
                <el-option label="返点比例" value="rebate"></el-option>
                <el-option label="学费" value="tuition"></el-option>
              </el-select>
            </div>

            <div class="filter-group">
              <span class="filter-label">学位类型:</span>
              <el-select v-model="degreeFilter" placeholder="学位类型" size="large">
                <el-option label="全部" value="all"></el-option>
                <el-option label="本科" value="bachelor"></el-option>
                <el-option label="硕士" value="master"></el-option>
                <el-option label="博士" value="phd"></el-option>
              </el-select>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="3" animated>
            <template #template>
              <div style="padding: 14px;">
                <div style="display: flex; justify-content: space-between;">
                  <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
                </div>
                <div style="margin-top: 16px; display: flex; justify-content: space-between; align-items: center;">
                  <el-skeleton-item variant="text" style="width: 30%;" />
                </div>
                <div style="margin-top: 16px;">
                  <el-skeleton-item variant="text" style="width: 50%;" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>

        <!-- 错误提示 -->
        <div v-else-if="error" class="error-state">
          <el-alert :title="error" type="error" show-icon :closable="false" description="请尝试刷新页面或稍后再试" />
        </div>

        <!-- 无数据提示 -->
        <div v-else-if="filteredSchools.length === 0" class="empty-state">
          <el-empty description="暂无符合条件的学校" />
        </div>

        <!-- 学校卡片网格 - 仿照图片样式 -->
        <transition-group v-else name="school-grid" tag="div" class="schools-grid">
          <div v-for="school in filteredSchools" :key="school.id" class="school-card" @click="goToSchool(school.id)">
            <div class="school-card-content">
              <!-- 左侧：学校logo -->
              <div class="school-logo">
                <img :src="getSchoolImage(school)" :alt="school.name" @error="onSchoolImageError($event, school)">
              </div>

              <!-- 中间：学校信息 -->
              <div class="school-details">
                <h3 class="school-name">{{ school.name }}</h3>
                <p class="school-name-en">{{ school.nameEn || 'The University' }}</p>
                <p class="school-location">{{ school.location }}</p>
              </div>

              <!-- 右侧：排名和数据 -->
              <div class="school-stats">
                <div class="ranking-section">
                  <div class="ranking-item qs-ranking">
                    <span class="ranking-number">{{ school.qsRanking || school.ranking }}</span>
                    <span class="ranking-label">QS排名</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </transition-group>

        <!-- 加载更多按钮 -->
        <div v-if="showLoadMore && !loading" class="load-more-container">
          <el-button size="large" @click="loadMoreSchools" class="load-more-btn">
            加载更多学校
          </el-button>
        </div>

        <!-- 已加载完所有学校的提示 -->
        <div v-if="!showLoadMore && filteredSchools.length > 0" class="all-loaded-tip">
          <p>已显示所有学校</p>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick, onActivated, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { Search, ArrowLeft } from '@element-plus/icons-vue'
import gsap from 'gsap'
import schoolService from '@/api/schoolService'

const router = useRouter()
const route = useRoute()

const COUNTRY_VIEW_STATE_KEY = 'countryViewState:v1'

// 国家数据
const countries = [
  {
    name: '所有学校',
    code: 'all',
    icon: '🌏',
    description: '汇集全球顶尖学府，为您提供全面的留学选择。无论您的目标是哪个国家，这里都有适合您的优质教育资源。',
    stats: {
      universities: '5000+',
      students: '100万+',
      tuition: '¥50,000-500,000',
      rebate: '5%-25%'
    }
  },
  {
    name: '美国',
    code: 'us',
    icon: '🇺🇸', // 使用国旗 emoji
    description: '美国拥有世界上最完善的高等教育体系，包括常春藤盟校在内的众多顶尖大学。美国大学注重学生的全面发展，提供灵活的课程设置和丰富的校园生活。美国学位在全球范围内广受认可，为学生未来的职业发展提供了广阔的平台。',
    stats: {
      universities: '4,000+',
      students: '37万+',
      tuition: '¥200,000-450,000',
      rebate: '10%-18%'
    }
  },
  {
    name: '英国',
    code: 'uk',
    icon: '🇬🇧', // 使用国旗 emoji
    description: '英国拥有悠久的教育传统和世界一流的大学，如牛津、剑桥等。英国本科通常为3年制，硕士为1年制，学制较短，性价比高。英国教育注重批判性思维和独立研究能力的培养，学位含金量高，全球认可度高。',
    stats: {
      universities: '150+',
      students: '22万+',
      tuition: '¥180,000-350,000',
      rebate: '12%-20%'
    }
  },
  {
    name: '澳大利亚',
    code: 'au',
    icon: '🇦🇺', // 使用国旗 emoji
    description: '澳大利亚拥有世界一流的教育体系，八大名校在全球排名靠前。澳洲气候宜人，社会安全稳定，多元文化环境为国际学生提供了良好的学习和生活环境。澳洲毕业生就业率高，部分专业毕业后可获得较长的工作签证。',
    stats: {
      universities: '43+',
      students: '16万+',
      tuition: '¥150,000-300,000',
      rebate: '15%-25%'
    }
  },
  {
    name: '加拿大',
    code: 'ca',
    icon: '🇨🇦', // 使用国旗 emoji
    description: '加拿大教育质量高，学费相对美国更为经济实惠。加拿大社会安全、包容，对国际学生友好。毕业后工作签证政策优厚，移民通道畅通，是留学与移民结合的理想选择。多伦多大学、麦吉尔大学等名校在全球享有盛誉。',
    stats: {
      universities: '100+',
      students: '14万+',
      tuition: '¥120,000-280,000',
      rebate: '8%-15%'
    }
  },
  {
    name: '新加坡',
    code: 'sg',
    icon: '🇸🇬', // 使用国旗 emoji
    description: '新加坡是亚洲教育中心，拥有新加坡国立大学、南洋理工大学等世界一流学府。新加坡地理位置优越，华人文化环境浓厚，语言适应较容易。新加坡学历国际认可度高，就业机会多，是亚洲留学的热门选择。',
    stats: {
      universities: '34+',
      students: '2.5万+',
      tuition: '¥100,000-250,000',
      rebate: '10%-18%'
    }
  },
  {
    name: '日本',
    code: 'jp',
    icon: '🇯🇵', // 使用国旗 emoji
    description: '日本拥有东京大学、京都大学等世界知名学府，教育质量高。日本留学费用相对欧美国家更为经济，同时提供多种奖学金机会。日本科技发达，在工程、科技等领域处于世界领先地位，为相关专业学生提供了良好的学习环境。',
    stats: {
      universities: '780+',
      students: '12万+',
      tuition: '¥60,000-150,000',
      rebate: '5%-12%'
    }
  }
]

// 学校数据
const schoolsData = ref({})
const loading = ref(false)
const error = ref(null)

// 获取学校数据的方法
const fetchSchoolData = async () => {
  const cached = schoolService.peekAllSchools()
  try {
    loading.value = !Array.isArray(cached)
    error.value = null

    const response = await schoolService.getAllSchoolsCached()

    // 检查返回的数据
    if (!response || !response.data) {
      throw new Error('获取学校数据失败: 返回数据格式不正确')
    }

    // 确保数据是数组
    if (!Array.isArray(response.data)) {
      throw new Error('获取学校数据失败: 返回数据不是数组')
    }

    if (response.status === 200 && response.data) {
      // 将学校数据按国家分组
      const groupedSchools = {}

      // 遍历API返回的学校数据
      response.data.forEach(school => {
        // 处理国家代码，确保与前端匹配
        const countryCode = school.countryCode?.toLowerCase() ||
          mapCountryCode(school.country)

        // 如果这个国家代码不存在，创建一个空数组
        if (!groupedSchools[countryCode]) {
          groupedSchools[countryCode] = []
        }

        // 处理标签和学位信息
        const tags = []
        if (school.type) tags.push(school.type)
        if (school.ownership) tags.push(school.ownership)

        // 从 tags 字段拆分标签
        if (school.tags) {
          school.tags.split(',').forEach(tag => {
            if (tag.trim()) {
              tags.push(tag.trim())
            }
          })
        }

        // 处理学位信息
        const degrees = []
        if (school.hasUndergraduate) degrees.push('bachelor')
        if (school.hasGraduate) degrees.push('master')
        if (school.hasPhd) degrees.push('phd')

        // 处理地址信息
        let location = school.city || ''
        if (school.state && school.state !== school.city) {
          location = school.state + (location ? ' ' + location : '')
        }

        // 处理返点和学费
        const rebate = school.rebatePercentage ?
          `${school.rebatePercentage}%` : '待定'

        let tuition = '待定'
        if (school.tuitionFee) {
          tuition = `¥${school.tuitionFee}/年`
        }

        // 使用世界排名或国家排名
        const ranking = school.worldRanking ||
          school.nationalRanking ||
          school.qsRanking ||
          school.timesRanking ||
          999

        // 将学校添加到对应国家的数组中
        groupedSchools[countryCode].push({
          id: school.id.toString(),
          name: school.name,
          nameEn: school.nameEn || school.englishName || 'The University',
          location: location || '未知',
          ranking: ranking,
          qsRanking: school.qsRanking || school.ranking || 999,
          rebate: rebate,
          tuition: tuition,
          tags: tags.length > 0 ? tags : ['综合'],
          degrees: degrees.length > 0 ? degrees : ['bachelor', 'master', 'phd'],
          logo: school.logoUrl || school.logo_url || null,
          image: school.imageUrl || school.image_url || null,
          facultyCount: null,
          majorCount: null,
          countsLoading: false
        })
      })

      // 更新学校数据
      schoolsData.value = groupedSchools

      // 确保所有国家都有数据
      countries.forEach(country => {
        if (!schoolsData.value[country.code]) {
          schoolsData.value[country.code] = []
        }
      })

      // 检查是否所有国家都没有学校数据
      const hasAnySchools = Object.values(schoolsData.value).some(schools => schools.length > 0)
      if (!hasAnySchools) {
        throw new Error('暂无学校数据')
      }
    } else {
      throw new Error('获取学校数据失败')
    }
  } catch (err) {
    error.value = err?.message || '获取学校数据失败'
  } finally {
    loading.value = false
  }
}

// 国家代码映射函数
// 加载更多学校
const loadMoreSchools = () => {
  currentPage.value++
}

// 返回上一级
const goBack = () => {
  router.push('/')
}

// 重置分页
const resetPagination = () => {
  currentPage.value = 1
  showLoadMore.value = true
}
const mapCountryCode = (countryName) => {
  const countryMap = {
    '美国': 'us',
    '英国': 'uk',
    '澳大利亚': 'au',
    '加拿大': 'ca',
    '新加坡': 'sg',
    '日本': 'jp',
    // 可根据需要添加更多映射
  }

  return countryMap[countryName] || 'us'
}

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

const defaultSchoolImage = resolvePublicUrl('images/schools/default-school.jpg')

// 获取学校图片的方法
const getSchoolImage = (school) => {
  if (!school) return defaultSchoolImage

  const candidates = [
    school.logo,
    school.logoUrl,
    school.logo_url,
    school.image,
    school.imageUrl,
    school.image_url
  ]

  for (const candidate of candidates) {
    if (!candidate || typeof candidate !== 'string') continue
    if (candidate.includes('null') || candidate.includes('undefined')) continue

    const clean = candidate.trim().replace(/\n/g, '').replace(/\r/g, '').replace(/\\/g, '/')
    if (!clean) continue

    if (clean.startsWith('http') || clean.startsWith('data:') || clean.startsWith('blob:')) {
      return encodeURI(clean)
    }

    if (clean.startsWith('/')) {
      return encodeURI(resolvePublicUrl(clean))
    }

    if (!clean.includes('/')) {
      return encodeURI(resolvePublicUrl(`images/schools/${clean}`))
    }

    return encodeURI(resolvePublicUrl(clean))
  }

  // 如果没有图片，使用默认图片
  return defaultSchoolImage
}

const onSchoolImageError = (event) => {
  const img = event?.target
  if (!img) return

  if (img.dataset.fallbackDone === '1') return

  const attempt = Number(img.dataset.logoAttempt || '0')
  if (attempt >= 2) {
    img.dataset.fallbackDone = '1'
    img.onerror = null
    img.src = defaultSchoolImage
    return
  }
  img.dataset.logoAttempt = String(attempt + 1)

  let pathname = ''
  try {
    pathname = new URL(img.src).pathname
  } catch {
    pathname = ''
  }

  const filename = pathname.split('/').pop()
  if (attempt === 0 && filename) {
    const lower = filename.toLowerCase()
    if (lower !== filename) {
      img.src = encodeURI(resolvePublicUrl(`images/schools/${lower}`))
      return
    }
  }

  img.dataset.fallbackDone = '1'
  img.onerror = null
  img.src = defaultSchoolImage
}

// 状态变量
const selectedCountry = ref("all") // 默认选择所有学校
const searchKeyword = ref("")
const sortBy = ref("ranking")
const degreeFilter = ref("all")
const isAnimating = ref(false)

// 分页相关状态
const pageSize = ref(12) // 每页显示12所学校
const currentPage = ref(1)
const showLoadMore = ref(true)

const persistCountryViewState = () => {
  try {
    const state = {
      selectedCountry: selectedCountry.value,
      searchKeyword: searchKeyword.value,
      sortBy: sortBy.value,
      degreeFilter: degreeFilter.value,
      currentPage: currentPage.value
    }
    sessionStorage.setItem(COUNTRY_VIEW_STATE_KEY, JSON.stringify(state))
  } catch { }
}

const restoreCountryViewState = () => {
  try {
    const raw = sessionStorage.getItem(COUNTRY_VIEW_STATE_KEY)
    if (!raw) return

    const state = JSON.parse(raw)
    if (!state || typeof state !== 'object') return

    if (typeof state.selectedCountry === 'string' && countries.some(c => c.code === state.selectedCountry)) {
      selectedCountry.value = state.selectedCountry
    }
    if (typeof state.searchKeyword === 'string') searchKeyword.value = state.searchKeyword
    if (typeof state.sortBy === 'string') sortBy.value = state.sortBy
    if (typeof state.degreeFilter === 'string') degreeFilter.value = state.degreeFilter
    if (Number.isFinite(state.currentPage) && state.currentPage >= 1) currentPage.value = state.currentPage
  } catch { }
}

watch([selectedCountry, searchKeyword, sortBy, degreeFilter, currentPage], () => {
  persistCountryViewState()
})
// 计算当前选中的国家信息
const currentCountry = computed(() => {
  return countries.find(country => country.code === selectedCountry.value)
})

// 根据筛选条件过滤和排序学校
const filteredSchools = computed(() => {
  let schools = []

  if (selectedCountry.value === 'all') {
    // 如果选择所有学校，合并所有国家的数据
    Object.values(schoolsData.value).forEach(countrySchools => {
      if (Array.isArray(countrySchools)) {
        schools = schools.concat(countrySchools)
      }
    })
  } else {
    schools = schoolsData.value[selectedCountry.value] || []
  }

  // 应用学位筛选
  if (degreeFilter.value !== "all") {
    schools = schools.filter(school =>
      school.degrees && school.degrees.includes(degreeFilter.value)
    )
  }

  // 应用关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase().trim()
    schools = schools.filter(school =>
      school.name.toLowerCase().includes(keyword) ||
      (school.nameEn && school.nameEn.toLowerCase().includes(keyword))
    )
  }

  // 应用排序
  if (sortBy.value === "ranking") {
    schools = [...schools].sort((a, b) => a.ranking - b.ranking)
  } else if (sortBy.value === "rebate") {
    schools = [...schools].sort((a, b) => {
      const rebateA = parseInt(a.rebate?.replace("%", "")) || 0
      const rebateB = parseInt(b.rebate?.replace("%", "")) || 0
      return rebateB - rebateA
    })
  } else if (sortBy.value === "tuition") {
    schools = [...schools].sort((a, b) => {
      const tuitionA = parseInt(a.tuition?.replace(/[^0-9]/g, "")) || 0
      const tuitionB = parseInt(b.tuition?.replace(/[^0-9]/g, "")) || 0
      return tuitionA - tuitionB
    })
  }

  // 应用分页逻辑
  const totalSchools = schools.length
  const displayCount = currentPage.value * pageSize.value
  const result = schools.slice(0, displayCount)

  // 更新是否显示加载更多按钮
  showLoadMore.value = displayCount < totalSchools

  return result
})

const fetchCountsForSchool = async (school) => {
  if (!school || school.countsLoading) return
  if (school.facultyCount !== null && school.majorCount !== null) return

  school.countsLoading = true
  try {
    const counts = await schoolService.getSchoolCounts(school.id)
    school.facultyCount = Number.isFinite(counts?.facultyCount) ? counts.facultyCount : 0
    school.majorCount = Number.isFinite(counts?.majorCount) ? counts.majorCount : 0
  } catch (e) {
    school.facultyCount = school.facultyCount ?? 0
    school.majorCount = school.majorCount ?? 0
  } finally {
    school.countsLoading = false
  }
}

const loadCountsForSchools = async (schools, concurrency = 4) => {
  if (!Array.isArray(schools) || schools.length === 0) return

  const targets = schools.filter(s => s && (s.facultyCount === null || s.majorCount === null) && !s.countsLoading)
  if (targets.length === 0) return

  let index = 0
  const workers = Array.from({ length: Math.min(concurrency, targets.length) }, async () => {
    while (index < targets.length) {
      const current = targets[index++]
      await fetchCountsForSchool(current)
    }
  })
  await Promise.all(workers)
}

watch(filteredSchools, (schools) => {
  loadCountsForSchools(schools)
}, { immediate: true })

// 选择国家
const selectCountry = (countryCode) => {
  if (selectedCountry.value === countryCode) return

  isAnimating.value = true

  // 先淡出当前学校卡片
  const cards = document.querySelectorAll('.school-card')
  const timeline = gsap.timeline({
    onComplete: () => {
      selectedCountry.value = countryCode

      // 在下一个渲染周期后，为新的卡片添加动画
      nextTick(() => {
        animateCards()
        isAnimating.value = false
      })
    }
  })

  cards.forEach(card => {
    timeline.to(card, {
      opacity: 0,
      y: 20,
      scale: 0.95,
      duration: 0.2,
      ease: 'power1.out',
      stagger: 0.05
    }, 0)
  })
}


// 动画卡片方法
const animateCards = () => {
  const cards = document.querySelectorAll('.school-card')

  gsap.fromTo(cards,
    {
      opacity: 0,
      y: 30,
      scale: 0.95
    },
    {
      opacity: 1,
      y: 0,
      scale: 1,
      duration: 0.5,
      stagger: 0.05,
      ease: 'back.out(1.2)',
      clearProps: 'all'
    }
  )
}

// 跳转到学校详情页
const goToSchool = (schoolId) => {
  router.push(`/schools/${schoolId}`)
}

// 监听排序和筛选变化
watch([sortBy, degreeFilter], () => {
  if (isAnimating.value) return

  isAnimating.value = true

  // 先淡出当前学校卡片
  const cards = document.querySelectorAll('.school-card')
  const timeline = gsap.timeline({
    onComplete: () => {
      // 在下一个渲染周期后，为新的卡片添加动画
      nextTick(() => {
        animateCards()
        isAnimating.value = false
      })
    }
  })

  cards.forEach(card => {
    timeline.to(card, {
      opacity: 0,
      y: 20,
      scale: 0.95,
      duration: 0.2,
      ease: 'power1.out',
      stagger: 0.05
    }, 0)
  })
})

// 从URL参数中获取初始国家并加载数据
onMounted(async () => {
  restoreCountryViewState()
  if (route.query.country) {
    const countryCode = route.query.country
    if (countries.some(c => c.code === countryCode)) {
      selectedCountry.value = countryCode
    }
  }

  try {
    // 获取学校数据
    await fetchSchoolData()

    // 初始化卡片动画
    nextTick(() => {
      animateCards()
    })
  } catch (err) {
    error.value = err?.message || '加载数据失败'
  }

  // 添加鼠标移动效果
  const onMouseMove = (e) => {
    const cards = document.querySelectorAll('.school-card')
    cards.forEach(card => {
      const rect = card.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top

      if (x > 0 && x < rect.width && y > 0 && y < rect.height) {
        card.style.setProperty('--mouse-x', `${x}px`)
        card.style.setProperty('--mouse-y', `${y}px`)
      }
    })
  }
  document.addEventListener('mousemove', onMouseMove)

  onUnmounted(() => {
    document.removeEventListener('mousemove', onMouseMove)
  })
})

onActivated(() => {
  restoreCountryViewState()
})
</script>

<style scoped>
/* 全局样式变量 - 与r.vue保持一致 */
:root {
  --primary-color: #0071e3;
  --secondary-color: #86b7fe;
  --dark-color: #1d1d1f;
  --light-color: #f5f5f7;
  --gray-color: #86868b;
  --success-color: #4cd964;
  --warning-color: #ff9500;
  --danger-color: #ff3b30;
  --border-radius: 12px;
  --transition-speed: 0.3s;
}

/* 容器样式 */
.country-container {
  min-height: 100vh;
  width: 100%;
  background-color: #F9F8F4;
  padding: 0;
  margin: 0;
  position: relative;
}

/* 国家选择横幅 */
.country-hero {
  background-color: #F9F8F4;
  color: #1d1d1f;
  padding: 4rem 1rem 2rem;
  text-align: center;
  margin-top: 0;
  width: 100%;
  box-sizing: border-box;
}

.country-hero-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #86868b;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.country-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 1.5rem;
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.gold-separator {
  width: 60px;
  height: 4px;
  background-color: #C5A059;
  margin: 0 auto 2rem;
  border-radius: 2px;
}

.country-subtitle {
  font-size: 1.25rem;
  max-width: 600px;
  margin: 0 auto;
  color: #86868b;
  line-height: 1.6;
}

/* 国家选择区域 */
.country-selector-section {
  background-color: white;
  padding: 2rem 1rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 80px;
  z-index: 100;
}

.country-selector-container {
  max-width: 1200px;
  margin: 0 auto;
}

.country-tabs {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.country-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.5rem;
  cursor: pointer;
  border-radius: 12px;
  transition: all var(--transition-speed) ease;
  min-width: 80px;
  background-color: #F9F8F4;
  border: 1px solid transparent;
}

.country-tab:hover {
  background-color: white;
  border-color: #C5A059;
  color: #C5A059;
  transform: translateY(-2px);
}

.country-tab.active {
  background-color: #C5A059;
  color: white;
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.3);
  transform: translateY(-2px);
}

.country-name {
  font-weight: 500;
  font-size: 1rem;
}



/* 学校列表区域 */
.schools-section {
  padding: 2rem 1rem;
  /* 减少上下内边距 */
  background-color: white;
}

.schools-container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-container {
  width: 100%;
  margin: 0 auto 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.back-button {
  position: absolute;
  left: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.08);
  padding: 0 20px;
  height: 50px;
  border-radius: 20px;
  font-size: 15px;
  color: var(--dark-color);
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  z-index: 10;
}

.back-button:hover {
  border-color: #C5A059;
  color: #C5A059;
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(197, 160, 89, 0.15);
}

.search-input {
  width: 100%;
  max-width: 600px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 30px;
  padding-left: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05) !important;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 6px 16px rgba(197, 160, 89, 0.15) !important;
  border-color: #C5A059 !important;
}

.search-input :deep(.el-input__inner) {
  height: 50px;
  font-size: 1.1rem;
}

/* 筛选器 */
.filter-container {
  margin-bottom: 2rem;
  background-color: var(--light-color);
  border-radius: var(--border-radius);
  padding: 1.5rem;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.filter-row {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-label {
  font-weight: 500;
  color: var(--dark-color);
  white-space: nowrap;
}

/* Element Plus Select 样式覆盖 */
:deep(.el-select) {
  min-width: 140px;
}

:deep(.el-input__wrapper) {
  background-color: white;
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover) {
  background-color: #f5f5f5;
}

/* 学校卡片网格 - 三列布局 */
.schools-grid {
  display: grid;
  /* 改为三列 */
  grid-template-columns: repeat(3, 1fr);
  /* 减少间距 */
  gap: 1rem;
  margin-bottom: 2rem;
}

.school-card {
  background-color: white;
  border-radius: 15px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  padding: 1.25rem;
  overflow: hidden;
}

.school-card-content {
  display: flex;
  align-items: center;
  gap: 1.25rem;
  width: 100%;
}

.school-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  border-color: rgba(197, 160, 89, 0.3);
}

.school-card:active {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
}

/* 学校logo样式 */
.school-logo {
  flex-shrink: 0;
  width: 60px;
  /* 减小logo尺寸 */
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e5e7eb;
}

.school-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 6px;
  /* 减少内边距 */
  box-sizing: border-box;
}

/* 学校详情样式 */
.school-details {
  flex: 1;
  min-width: 0;
  /* 防止文本溢出 */
}

.school-name {
  font-size: 1rem;
  /* 稍微减小字体 */
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.2rem 0;
  /* 减少间距 */
  line-height: 1.2;
}

.school-name-en {
  font-size: 0.8rem;
  /* 稍微减小字体 */
  color: #6b7280;
  margin: 0 0 0.3rem 0;
  /* 减少间距 */
  line-height: 1.1;
}

.school-location {
  font-size: 0.75rem;
  /* 稍微减小字体 */
  color: #9ca3af;
  margin: 0;
}

/* 学校统计数据样式 */
.school-stats {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 改为居中 */
  justify-content: center;
  /* 垂直居中 */
  gap: 0;
  min-width: 80px;
  /* 确保有足够宽度 */
}

.ranking-section {
  display: flex;
  justify-content: center;
  width: 100%;
}

.ranking-item {
  text-align: center;
  min-width: 60px;
}

.ranking-number {
  display: block;
  font-size: 1.5rem;
  /* 加大字体 */
  font-weight: 700;
  line-height: 1;
  margin-bottom: 0.4rem;
}

.qs-ranking .ranking-number {
  color: #ea580c;
  /* 橙色 */
}

.ranking-label {
  font-size: 0.65rem;
  /* 稍微减小字体 */
  color: #6b7280;
  display: block;
}

.stats-section {
  display: flex;
  gap: 0.75rem;
  /* 减少间距 */
}

.stat-item {
  text-align: center;
  min-width: 40px;
  /* 减少最小宽度 */
}

.stat-label {
  font-size: 0.65rem;
  /* 稍微减小字体 */
  color: #6b7280;
  display: block;
  margin-bottom: 0.2rem;
  /* 减少间距 */
}

.stat-value {
  font-size: 0.9rem;
  /* 稍微减小字体 */
  font-weight: 600;
  color: #1f2937;
  display: block;
}


.load-more-button {
  background: none;
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
  padding: 0.75rem 2rem;
  border-radius: 30px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-speed) ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 auto;
}

.load-more-button:hover {
  background-color: var(--primary-color);
  color: white;
}

.load-more-icon {
  transition: transform 0.3s ease;
}

.load-more-button:hover .load-more-icon {
  transform: translateY(3px);
}

/* 学校卡片动画 */
.school-grid-enter-active,
.school-grid-leave-active {
  transition: all 0.5s ease;
}

.school-grid-enter-from,
.school-grid-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .schools-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .country-tabs {
    gap: 1rem;
  }

  .country-tab {
    padding: 0.75rem;
  }

  .country-flag {
    width: 50px;
    height: 33px;
  }

  .country-name {
    font-size: 0.875rem;
  }



  .filter-row {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  .section-title {
    font-size: 2rem;
  }

  /* 移动端学校卡片调整 */
  .schools-grid {
    grid-template-columns: 1fr;
    /* 移动端改为单列 */
    gap: 0.75rem;
    /* 减少移动端间距 */
  }

  .school-card {
    padding: 0.75rem;
    /* 减少移动端内边距 */
  }

  .school-card-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
    /* 减少间距 */
  }

  .school-logo {
    width: 50px;
    /* 移动端进一步减小logo */
    height: 50px;
    align-self: center;
  }

  .school-details {
    text-align: center;
    width: 100%;
  }

  .school-stats {
    align-items: center;
    width: 100%;
    gap: 0.5rem;
    /* 减少间距 */
  }

  .ranking-section,
  .stats-section {
    justify-content: center;
    gap: 0.5rem;
    /* 减少间距 */
  }
}

/* 加载状态样式 */
.loading-state {
  min-height: 400px;
  padding: 2rem;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius);
}

.loading-state .el-skeleton {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

/* 错误状态样式 */
.error-state {
  min-height: 200px;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.error-state .el-alert {
  width: 100%;
  max-width: 600px;
}

/* 空状态样式 */
.empty-state {
  min-height: 300px;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(245, 247, 250, 0.5);
  border-radius: var(--border-radius);
}

/* 添加以下样式 */
.school-faculties,
.school-majors {
  margin-top: 0.5rem;
}

.faculties-title,
.majors-title {
  font-size: 0.75rem;
  color: var(--gray-color);
  background-color: #f0f5ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  display: inline-block;
  margin: 0.25rem 0;
}

/* 加载更多按钮样式 - 黑白风格 */
.load-more-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 2rem 0;
  padding: 1rem;
}

.load-more-btn {
  background: linear-gradient(135deg, #000 0%, #333 50%, #000 100%) !important;
  border: 2px solid #000 !important;
  color: white !important;
  padding: 12px 32px !important;
  border-radius: 25px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 0.5px !important;
  text-transform: uppercase !important;
  cursor: pointer !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2) !important;
  position: relative !important;
  overflow: hidden !important;
}

.load-more-btn:hover {
  background: linear-gradient(135deg, #333 0%, #666 50%, #333 100%) !important;
  border-color: #333 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3) !important;
}

.load-more-btn:active {
  transform: translateY(0) !important;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2) !important;
}

.load-more-btn::before {
  content: "" !important;
  position: absolute !important;
  top: 0 !important;
  left: -100% !important;
  width: 100% !important;
  height: 100% !important;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent) !important;
  transition: left 0.5s !important;
}

.load-more-btn:hover::before {
  left: 100% !important;
}
</style>
