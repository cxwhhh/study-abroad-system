<template>
  <div class="major-index">
    <nav-bar></nav-bar>

    <!-- 专业banner -->
    <div class="major-banner">
      <div class="banner-content">
        <div class="section-label">GLOBAL MAJORS</div>
        <h1 class="major-title">专业库</h1>
        <div class="gold-separator"></div>
        <p class="major-subtitle">探索全球顶尖学府的热门专业，找到最适合你的学术方向</p>
      </div>
    </div>



    <!-- 搜索和筛选器及专业列表 -->
    <div class="majors-container">
      <div class="container">
        <!-- 搜索框 -->
        <div class="search-container">
          <el-input v-model="searchQuery" placeholder="搜索专业名称..." :prefix-icon="Search" clearable size="large"
            class="search-input" />
        </div>

        <!-- 筛选器 -->
        <div class="filter-container">
          <div class="filter-row">
            <div class="filter-group">
              <span class="filter-label">学校:</span>
              <el-select v-model="filterForm.school" placeholder="选择学校" clearable filterable size="large">
                <el-option label="全部学校" value="" />
                <el-option v-for="school in schoolOptions" :key="school.value" :label="school.label"
                  :value="school.value" />
              </el-select>
            </div>
            <div class="filter-group">
              <span class="filter-label">专业分类:</span>
              <el-select v-model="filterForm.category" placeholder="选择分类" clearable size="large">
                <el-option label="全部分类" value="" />
                <el-option label="工程技术" value="engineering" />
                <el-option label="商科管理" value="business" />
                <el-option label="医学健康" value="medicine" />
                <el-option label="人文艺术" value="arts" />
                <el-option label="理学科学" value="science" />
                <el-option label="社会科学" value="social" />
              </el-select>
            </div>
            <div class="filter-group">
              <span class="filter-label">学位类型:</span>
              <el-select v-model="filterForm.degree" placeholder="选择学位" clearable size="large">
                <el-option label="全部学位" value="" />
                <el-option label="本科" value="bachelor" />
                <el-option label="硕士" value="master" />
                <el-option label="博士" value="phd" />
              </el-select>
            </div>
            <div class="filter-group">
              <span class="filter-label">热门程度:</span>
              <el-select v-model="filterForm.popularity" placeholder="选择热门程度" clearable size="large">
                <el-option label="全部" value="" />
                <el-option label="最热门" value="hot" />
                <el-option label="热门" value="popular" />
                <el-option label="新兴" value="emerging" />
              </el-select>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <p>正在加载专业数据...</p>
        </div>

        <div v-if="!loading" class="majors-grid">
          <div v-for="major in visibleMajors" :key="`${major.schoolId}-${major.id || major.majorId}`" class="major-card"
            @click="goToMajorDetail(major.schoolId, major.id || major.majorId)">
            <!-- 左侧：学校Logo -->
            <div class="card-left">
              <div class="school-logo-wrapper">
                <img :src="getSchoolLogo(major.schoolId)" :alt="`${major.schoolName}校徽`" class="school-logo"
                  loading="lazy" decoding="async" @error="onSchoolLogoError">
              </div>
              <div class="major-country">
                {{ major.schoolCountry || '未知地区' }}
              </div>
            </div>

            <!-- 中间：专业详情 -->
            <div class="card-middle">
              <div class="major-header-row">
                <h3 class="major-full-title">
                  <span class="school-title">{{ major.schoolName }}</span>
                  <span class="separator">|</span>
                  <span class="card-major-title">{{ major.name }}</span>
                </h3>
              </div>
              <div class="major-english-name" v-if="major.englishName || major.schoolEnglishName">
                {{ major.englishName || major.name }}
              </div>

              <div class="major-tags">
                <span v-for="(tag, index) in major.tags" :key="index" class="major-tag">
                  {{ tag }}<span v-if="index < major.tags.length - 1" class="tag-separator">,</span>
                </span>
                <span class="major-tag">{{ getCategoryName(major.category) }}</span>
              </div>

              <div class="major-badges">
                <span class="status-badge">9月入学已截止 (2026入学季)</span>
                <span class="ranking-badge" v-if="major.subjectRanking && major.subjectRanking !== '-'">
                  {{ getCategoryName(major.category).slice(0, 2) }} QS: {{ major.subjectRanking }}
                </span>
              </div>
            </div>

            <!-- 右侧：排名和收藏 -->
            <div class="card-right">
              <div class="ranking-info" v-if="major.qsRanking && major.qsRanking !== '-'">
                <div class="ranking-number">{{ major.qsRanking }}</div>
                <div class="ranking-label">QS排名</div>
              </div>
              <div class="favorite-action" @click.stop="toggleFavorite(major)">
                <el-icon :size="20" :color="major.isFavorite ? '#f56c6c' : '#909399'">
                  <component :is="major.isFavorite ? StarFilled : Star" />
                </el-icon>
                <span class="favorite-text">收藏</span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="!loading" ref="sentinel" class="load-sentinel"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import { Search, Loading, Star, StarFilled } from '@element-plus/icons-vue'
import schoolService from '@/api/schoolService'
import majorService from '@/api/majorService'
import { ElMessage } from 'element-plus'

const router = useRouter()

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

const defaultSchoolImage = resolvePublicUrl('images/schools/default-school.jpg')

// 专业分类映射
const categoryMap = {
  'engineering': '工程技术',
  'business': '商科管理',
  'medicine': '医学健康',
  'arts': '人文艺术',
  'science': '理学科学',
  'social': '社会科学'
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  return categoryMap[categoryId] || categoryId
}

// 搜索查询
const searchQuery = ref('')

// 筛选表单
const filterForm = ref({
  category: '',
  degree: '',
  popularity: '',
  school: ''
})

// 数据状态
const loading = ref(false)
const schools = ref([])
const schoolMajors = ref([])

const normalizeSchoolId = (value) => {
  if (value === null || value === undefined) return ''
  return String(value)
}

const enrichMajor = (major, school) => {
  const majorName = major?.name || major?.majorName || ''
  const schoolName = school?.name || school?.chineseName || school?.englishName || major?.schoolName || ''
  return {
    ...major,
    id: major.id || major.majorId || `${major.schoolId}-${majorName}`,
    name: majorName,
    englishName: major.englishName || '',
    schoolId: normalizeSchoolId(major?.schoolId ?? school?.id),
    schoolName,
    schoolEnglishName: school?.englishName || '',
    schoolCountry: school?.country || major?.schoolCountry,
    icon: getDefaultIcon(majorName),
    popularity: getPopularityLevel(major),
    popularityText: getPopularityText(major),
    category: getCategoryFromMajor(majorName),
    averageSalary: major.averageSalary || '待更新',
    description: major.description || (schoolName ? `${schoolName}的${majorName}专业` : majorName),
    qsRanking: school?.qsRanking || school?.ranking || school?.worldRanking || '-',
    subjectRanking: major?.qsRanking || major?.ranking || '-',
    deadline: major?.deadline || '9月入学已截止 (2026入学季)', // 模拟数据，实际应从API获取
    tags: major?.tags || ['商科', '管理', '金融'], // 模拟数据
    isFavorite: false // 模拟收藏状态
  }
}

const runWithConcurrency = async (items, limit, worker) => {
  let index = 0
  const runners = Array.from({ length: Math.min(limit, items.length) }, async () => {
    while (index < items.length) {
      const current = items[index++]
      await worker(current)
    }
  })
  await Promise.all(runners)
}

const fetchSchoolMajorsFallback = async () => {
  const allSchoolMajors = []
  await runWithConcurrency(schools.value, 6, async (school) => {
    const majorsData = await schoolService.getSchoolMajors(school.id)
    if (!Array.isArray(majorsData) || majorsData.length === 0) return
    majorsData.forEach((major) => {
      allSchoolMajors.push(enrichMajor(major, school))
    })
  })
  schoolMajors.value = allSchoolMajors
}

const getSchoolLogoFromSchool = (school) => {
  if (!school) return ''

  const imageFields = [
    school.imageUrl,
    school.logoUrl,
    school.image,
    school.logo
  ]

  for (const imageField of imageFields) {
    if (
      imageField &&
      imageField !== null &&
      imageField !== 'null' &&
      imageField !== 'undefined' &&
      typeof imageField === 'string' &&
      imageField.trim() !== ''
    ) {
      const cleanPath = imageField.trim().replace(/\n/g, '').replace(/\r/g, '').replace(/\\/g, '/')
      if (!cleanPath) continue

      if (cleanPath.startsWith('http') || cleanPath.startsWith('data:') || cleanPath.startsWith('blob:')) {
        return encodeURI(cleanPath)
      }

      if (cleanPath.startsWith('/')) {
        return encodeURI(resolvePublicUrl(cleanPath))
      }

      if (!cleanPath.includes('/')) {
        return encodeURI(resolvePublicUrl(`images/schools/${cleanPath}`))
      }

      return encodeURI(resolvePublicUrl(cleanPath))
    }
  }

  return ''
}

const schoolLogoById = computed(() => {
  const map = new Map()
  for (const school of schools.value) {
    const id = normalizeSchoolId(school?.id)
    if (!id) continue
    const url = getSchoolLogoFromSchool(school)
    if (url) map.set(id, url)
  }
  return map
})

// 获取学校校徽
const getSchoolLogo = (schoolId) => {
  const id = normalizeSchoolId(schoolId)
  const cached = schoolLogoById.value.get(id)
  if (cached) return cached
  return defaultSchoolImage
}

const onSchoolLogoError = (event) => {
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
    pathname = new URL(img.src, window.location.origin).pathname
  } catch {
    pathname = ''
  }

  const filename = pathname.split('/').pop()
  if (attempt === 0 && filename && pathname.includes('/images/schools/')) {
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

// 根据专业名称获取默认图标
const getDefaultIcon = (majorName) => {
  if (!majorName) return 'fas fa-graduation-cap'

  const name = majorName.toLowerCase()
  if (name.includes('计算机') || name.includes('computer') || name.includes('软件')) return 'fas fa-laptop-code'
  if (name.includes('商') || name.includes('管理') || name.includes('business')) return 'fas fa-briefcase'
  if (name.includes('医') || name.includes('medicine') || name.includes('health')) return 'fas fa-stethoscope'
  if (name.includes('金融') || name.includes('finance') || name.includes('经济')) return 'fas fa-coins'
  if (name.includes('人工智能') || name.includes('ai') || name.includes('机器学习')) return 'fas fa-robot'
  if (name.includes('心理') || name.includes('psychology')) return 'fas fa-brain'
  if (name.includes('数据') || name.includes('data')) return 'fas fa-chart-bar'
  if (name.includes('营销') || name.includes('marketing')) return 'fas fa-bullhorn'
  if (name.includes('工程') || name.includes('engineering')) return 'fas fa-cogs'
  if (name.includes('艺术') || name.includes('art') || name.includes('设计')) return 'fas fa-palette'
  return 'fas fa-graduation-cap'
}

// 获取热门程度
const getPopularityLevel = (major) => {
  // 可以根据申请人数、就业率等数据来判断
  const popularMajors = ['计算机', '商科', '金融', '数据', '人工智能']
  const majorName = major.name || major.majorName || ''

  for (const popular of popularMajors) {
    if (majorName.includes(popular)) {
      return 'hot'
    }
  }
  return 'popular'
}

// 获取热门程度文本
const getPopularityText = (major) => {
  const level = getPopularityLevel(major)
  const textMap = {
    'hot': '最热门',
    'popular': '热门',
    'emerging': '新兴'
  }
  return textMap[level] || '热门'
}

// 根据专业名称推断分类
const getCategoryFromMajor = (majorName) => {
  if (!majorName) return 'other'

  const name = majorName.toLowerCase()
  if (name.includes('计算机') || name.includes('软件') || name.includes('工程') || name.includes('技术')) return 'engineering'
  if (name.includes('商') || name.includes('管理') || name.includes('金融') || name.includes('经济')) return 'business'
  if (name.includes('医') || name.includes('健康') || name.includes('护理')) return 'medicine'
  if (name.includes('艺术') || name.includes('设计') || name.includes('文学') || name.includes('语言')) return 'arts'
  if (name.includes('数学') || name.includes('物理') || name.includes('化学') || name.includes('生物')) return 'science'
  if (name.includes('心理') || name.includes('社会') || name.includes('政治') || name.includes('法律')) return 'social'
  return 'other'
}

// 初始化数据
const initData = async () => {
  const cachedSchools = schoolService.peekAllSchools()
  const cachedMajors = majorService.peekAllMajors()
  loading.value = !(Array.isArray(cachedSchools) && Array.isArray(cachedMajors))
  try {
    const [schoolsResult, majorsResult] = await Promise.allSettled([
      schoolService.getAllSchoolsCached(),
      majorService.getAllMajorsCached()
    ])

    if (schoolsResult.status === 'fulfilled') {
      schools.value = schoolsResult.value?.data || []
    }

    if (majorsResult.status === 'fulfilled') {
      const all = Array.isArray(majorsResult.value?.data) ? majorsResult.value.data : []
      if (all.length > 0) {
        const schoolById = new Map(schools.value.map(s => [normalizeSchoolId(s.id), s]))
        schoolMajors.value = all.map(m => enrichMajor(m, schoolById.get(normalizeSchoolId(m.schoolId))))
      } else if (Array.isArray(schools.value) && schools.value.length > 0) {
        await fetchSchoolMajorsFallback()
      }
    } else if (Array.isArray(schools.value) && schools.value.length > 0) {
      await fetchSchoolMajorsFallback()
    }
  } catch (error) {
    ElMessage.error('数据加载失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  requestAnimationFrame(() => {
    initData()
  })
})

// 筛选后的专业
const filteredMajors = computed(() => {
  return schoolMajors.value.filter(major => {
    // 搜索过滤 - 搜索专业名称和学校名称
    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase()
      const majorName = (major.name || major.majorName || '').toLowerCase()
      const schoolName = (major.schoolName || '').toLowerCase()
      if (!majorName.includes(query) && !schoolName.includes(query)) {
        return false
      }
    }

    // 分类过滤
    if (filterForm.value.category && major.category !== filterForm.value.category) {
      return false
    }

    // 学位过滤
    if (filterForm.value.degree) {
      const degrees = major.degree || major.degreeType || []
      if (Array.isArray(degrees)) {
        if (!degrees.includes(filterForm.value.degree)) {
          return false
        }
      } else {
        if (degrees !== filterForm.value.degree) {
          return false
        }
      }
    }

    // 热门程度过滤
    if (filterForm.value.popularity && major.popularity !== filterForm.value.popularity) {
      return false
    }

    // 学校过滤
    if (filterForm.value.school && normalizeSchoolId(major.schoolId) !== normalizeSchoolId(filterForm.value.school)) {
      return false
    }

    return true
  })
})

const renderLimit = ref(80)
const sentinel = ref(null)
let loadMoreObserver = null

const visibleMajors = computed(() => {
  const list = filteredMajors.value
  const limit = Math.max(0, renderLimit.value)
  return limit >= list.length ? list : list.slice(0, limit)
})

const setupLoadMoreObserver = () => {
  if (loadMoreObserver) {
    loadMoreObserver.disconnect()
    loadMoreObserver = null
  }
  if (!sentinel.value) return

  loadMoreObserver = new IntersectionObserver(
    (entries) => {
      const hit = entries.some(e => e.isIntersecting)
      if (!hit) return
      renderLimit.value = Math.min(renderLimit.value + 80, filteredMajors.value.length)
    },
    { root: null, rootMargin: '600px 0px', threshold: 0.01 }
  )

  loadMoreObserver.observe(sentinel.value)
}

watch([searchQuery, filterForm], () => {
  renderLimit.value = 80
}, { deep: true })

watch(loading, (v) => {
  if (!v) requestAnimationFrame(setupLoadMoreObserver)
})

onMounted(() => {
  if (!loading.value) requestAnimationFrame(setupLoadMoreObserver)
})

onUnmounted(() => {
  if (loadMoreObserver) {
    loadMoreObserver.disconnect()
    loadMoreObserver = null
  }
})

// 获取学校选项
const schoolOptions = computed(() => {
  return schools.value.map(school => ({
    label: school.name || school.chineseName || school.englishName,
    value: school.id
  }))
})



// 跳转到专业详情
const goToMajorDetail = (schoolId, majorId) => {
  router.push(`/schools/${schoolId}/major/${majorId}`)
}

// 切换收藏状态
const toggleFavorite = (major) => {
  major.isFavorite = !major.isFavorite
  ElMessage.success(major.isFavorite ? '已收藏' : '已取消收藏')
}
</script>

<style scoped>
.major-index {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #666;
}

.loading-container .el-icon {
  font-size: 32px;
  margin-bottom: 16px;
  color: #409eff;
}

.load-sentinel {
  height: 1px;
}

/* Banner样式 */
.major-banner {
  background-color: #F9F8F4;
  color: #1d1d1f;
  padding: 4rem 1rem 2rem;
  text-align: center;
  margin-top: 0;
  width: 100%;
  box-sizing: border-box;
}

.banner-content {
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

.major-title {
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

.major-subtitle {
  font-size: 1.25rem;
  max-width: 600px;
  margin: 0 auto;
  color: #86868b;
  line-height: 1.6;
}



/* 搜索和筛选器样式 */
.search-container {
  max-width: 600px;
  margin: 0 auto 2rem;
  display: flex;
  justify-content: center;
}

.search-input {
  width: 100%;
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
  background-color: #fcfcfc;
  border-radius: 12px;
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
  color: #1d1d1f;
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

/* 专业列表样式 */
.majors-container {
  padding: 40px 0;
  background: white;
}



.majors-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.major-card {
  display: flex;
  background-color: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  padding: 1.5rem;
  transition: all 0.3s ease;
  cursor: pointer;
  align-items: stretch;
  position: relative;
}

.major-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #dcdfe6;
  transform: translateY(-2px);
}

/* Left: Logo */
.card-left {
  flex-shrink: 0;
  margin-right: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.school-logo-wrapper {
  width: 60px;
  height: 60px;
  border: 1px solid #f2f2f2;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 4px;
}

.school-logo {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.major-country {
  margin-top: 8px;
  width: 60px;
  font-size: 0.9rem;
  color: #606266;
  line-height: 2;
  text-align: center;
  white-space: nowrap;
}

/* Middle: Content */
.card-middle {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  min-width: 0;
}

.major-header-row {
  margin-bottom: 2px;
}

.major-full-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #303133;
  margin: 0;
  line-height: 1.4;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.school-title {
  color: #303133;
  font-size: 1.2rem;
}

.separator {
  margin: 0 8px;
  color: #C0C4CC;
  font-weight: 400;
}

.card-major-title {
  color: #303133;
  font-size: 1.2rem;
}

.major-english-name {
  font-size: 0.85rem;
  color: #909399;
  margin-bottom: 6px;
  line-height: 1.4;
}

.major-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
  font-size: 0.85rem;
  color: #606266;
}

.tag-separator {
  margin-right: 4px;
}

.major-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 0;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border: 1px solid #f56c6c;
  color: #f56c6c;
  border-radius: 4px;
  font-size: 0.8rem;
  background-color: #fef0f0;
}

.ranking-badge {
  display: inline-block;
  padding: 2px 8px;
  border: 1px solid #002c5f;
  color: #002c5f;
  border-radius: 4px;
  font-size: 0.8rem;
  background-color: white;
}

/* Right: Action */
.card-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  margin-left: 1.5rem;
  min-width: 100px;
}

.ranking-info {
  text-align: center;
  margin-bottom: 1rem;
}

.ranking-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: #a33238;
  /* Dark red similar to image */
  line-height: 1;
}

.ranking-label {
  font-size: 0.9rem;
  color: #303133;
  margin-top: 4px;
}

.favorite-action {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #909399;
  font-size: 0.9rem;
  transition: all 0.3s;
  margin-top: auto;
}

.favorite-action:hover {
  color: #f56c6c;
}





/* 响应式调整 */
@media (max-width: 768px) {
  .majors-grid {
    grid-template-columns: 1fr;
    max-width: 100%;
  }

  .major-banner {
    padding: 3rem 1rem;
  }

  .banner-content h1 {
    font-size: 2.5rem;
  }

  .banner-content p {
    font-size: 1.125rem;
  }

  .major-card {
    flex-direction: column;
    padding: 1rem;
  }

  .card-left {
    margin-right: 0;
    margin-bottom: 1rem;
    align-items: flex-start;
  }

  .school-logo-wrapper {
    width: 60px;
    height: 60px;
  }

  .card-right {
    margin-left: 0;
    margin-top: 1rem;
    flex-direction: row;
    width: 100%;
    align-items: center;
    justify-content: space-between;
  }

  .ranking-info {
    text-align: left;
    margin-bottom: 0;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .ranking-number {
    font-size: 1.5rem;
  }

  .favorite-action {
    margin-top: 0;
  }
}
</style>
