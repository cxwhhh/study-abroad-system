<template>
  <div class="major-detail-page">
    <nav-bar />

    <header class="major-hero">
      <div class="container">
        <div class="hero-main">
          <div class="school-mark">
            <img class="school-mark-img" :src="getSchoolLogo(schoolId)" :alt="schoolName" decoding="async"
              @error="onSchoolLogoError" />
          </div>

          <div class="hero-text">
            <h1 class="hero-title">
              <span class="hero-title-school">{{ schoolName }}</span>
              <span class="hero-title-divider"> - </span>
              <span class="hero-title-major">{{ majorData.name }}</span>
            </h1>
            <p class="hero-subtitle">{{ majorData.shortDescription || '了解这个专业的核心内容和就业前景' }}</p>
            <div class="hero-tags">
              <span class="tag tag-primary">{{ majorData.popularityText }}</span>
              <span class="tag">{{ getCategoryName(majorData.category) }}</span>
            </div>
          </div>
        </div>

        <div class="hero-toolbar">
          <button class="toolbar-back" type="button" @click="goBack">
            <el-icon>
              <ArrowLeft />
            </el-icon>
            返回上一级
          </button>

          <div class="toolbar-actions">
            <a v-if="officialWebsite" class="toolbar-pill toolbar-pill-primary" :href="officialWebsite" target="_blank"
              rel="noopener noreferrer">
              官网链接
            </a>
            <button class="toolbar-pill" type="button" @click="openScoreDialog">分数要求</button>
            <button class="toolbar-pill" type="button" @click="openScoreDialog('ielts')">雅思</button>
            <button class="toolbar-pill" type="button" @click="openScoreDialog('toefl')">托福</button>
            <button class="toolbar-pill" type="button" @click="openScoreDialog('gpa')">GPA</button>
          </div>
        </div>
      </div>
    </header>

    <el-dialog v-model="scoreDialogVisible" title="分数与申请要求" width="560px">
      <el-tabs v-model="scoreActiveTab" class="score-tabs">
        <el-tab-pane label="语言" name="language">
          <div class="score-grid">
            <div class="score-item">
              <div class="score-label">IELTS</div>
              <div class="score-value">{{ schoolData.ieltsRequirement || '—' }}</div>
            </div>
            <div class="score-item">
              <div class="score-label">TOEFL</div>
              <div class="score-value">{{ schoolData.toeflRequirement || '—' }}</div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="标化" name="standardized">
          <div class="score-grid">
            <div class="score-item">
              <div class="score-label">GRE</div>
              <div class="score-value">{{ schoolData.greRequirement || '—' }}</div>
            </div>
            <div class="score-item">
              <div class="score-label">GMAT</div>
              <div class="score-value">{{ schoolData.gmatRequirement || '—' }}</div>
            </div>
            <div class="score-item">
              <div class="score-label">SAT</div>
              <div class="score-value">{{ schoolData.satRequirement || '—' }}</div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="学术" name="academic">
          <div class="score-grid">
            <div class="score-item">
              <div class="score-label">平均录取GPA</div>
              <div class="score-value">{{ schoolData.averageGpa || '—' }}</div>
            </div>
            <div class="score-item score-item-wide">
              <div class="score-label">入学要求</div>
              <div class="score-text">{{ schoolData.entryRequirement || '—' }}</div>
            </div>
            <div class="score-item score-item-wide">
              <div class="score-label">申请截止</div>
              <div class="score-text">{{ schoolData.applicationDeadline || '—' }}</div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <main class="major-content">
      <div class="container content-grid">
        <section class="panel">
          <h2 class="panel-title">专业介绍</h2>
          <div class="intro-content">
            <p class="intro-paragraph">{{ majorData.description || '暂无专业介绍' }}</p>

            <div class="panel-divider"></div>

            <h2 class="panel-title panel-title-section">专业申请信息</h2>
            <div class="apply-table-wrapper">
              <div class="apply-table">
                <template v-for="(group, groupIndex) in applyInfoGroups" :key="groupIndex">
                  <div class="apply-row apply-row-label">
                    <div v-for="item in group" :key="item.key" class="apply-cell apply-cell-label">
                      {{ item.label }}
                    </div>
                  </div>
                  <div class="apply-row apply-row-value">
                    <div v-for="item in group" :key="`${item.key}-value`" class="apply-cell apply-cell-value">
                      <a v-if="item.href" class="apply-link" :href="item.href" target="_blank"
                        rel="noopener noreferrer">
                        {{ item.value }}
                      </a>
                      <span v-else>{{ item.value }}</span>
                    </div>
                  </div>
                </template>
              </div>
            </div>

            <div class="panel-divider"></div>

            <div class="panel-section-header">
              <h2 class="panel-title panel-title-section">核心课程</h2>
              <button v-if="coreCourses.length > 0" class="section-toggle" type="button" @click="toggleCoreCourses">
                {{ coreCoursesExpanded ? '收起' : '展开' }}
              </button>
            </div>

            <div v-if="coreCourses.length > 0">
              <div v-if="!coreCoursesExpanded" class="collapsed-state">已收起，点击展开查看</div>
              <ul v-else class="course-list">
                <li v-for="course in coreCourses" :key="course.id" class="course-item">
                  <div class="course-title">
                    <strong>{{ course.courseName }}</strong>
                    <span v-if="course.courseEnglishName" class="course-title-en">({{ course.courseEnglishName
                    }})</span>
                  </div>
                  <div class="course-details">
                    <span class="course-pill">{{ course.courseType }}</span>
                    <span class="course-pill">{{ course.credits }}学分</span>
                    <span class="course-pill">第{{ course.semester }}学期</span>
                  </div>
                  <p v-if="course.courseDescription" class="course-description">{{ course.courseDescription }}</p>
                </li>
              </ul>
            </div>
            <div v-else class="empty-state">暂无核心课程信息</div>

            <div class="panel-divider"></div>

            <h2 class="panel-title panel-title-section">就业方向</h2>
            <div v-if="majorData.careerProspects">
              <p class="intro-paragraph">{{ majorData.careerProspects }}</p>
            </div>
            <div v-else class="empty-state">暂无就业方向信息</div>
          </div>
        </section>

        <aside class="sidebar">
          <section class="panel">
            <h2 class="panel-title">数据概览</h2>
            <div class="metric-list">
              <div class="metric-item">
                <div class="metric-icon">
                  <el-icon>
                    <Briefcase />
                  </el-icon>
                </div>
                <div class="metric-body">
                  <div class="metric-label">就业率</div>
                  <div class="metric-value">{{ majorData.employmentRate || '—' }}</div>
                </div>
              </div>

              <div class="metric-item">
                <div class="metric-icon">
                  <el-icon>
                    <Money />
                  </el-icon>
                </div>
                <div class="metric-body">
                  <div class="metric-label">平均薪资</div>
                  <div class="metric-value">{{ majorData.averageSalary || '—' }}</div>
                </div>
              </div>

              <div class="metric-item">
                <div class="metric-icon">
                  <el-icon>
                    <User />
                  </el-icon>
                </div>
                <div class="metric-body">
                  <div class="metric-label">在校学生</div>
                  <div class="metric-value">{{ majorData.studentCount || '—' }}</div>
                </div>
              </div>

              <div class="metric-item">
                <div class="metric-icon">
                  <el-icon>
                    <Clock />
                  </el-icon>
                </div>
                <div class="metric-body">
                  <div class="metric-label">学制</div>
                  <div class="metric-value">{{ majorData.duration || '—' }}</div>
                </div>
              </div>

              <div class="metric-item">
                <div class="metric-icon">
                  <el-icon>
                    <Star />
                  </el-icon>
                </div>
                <div class="metric-body">
                  <div class="metric-label">专业排名</div>
                  <div class="metric-value">{{ majorData.ranking || '—' }}</div>
                </div>
              </div>
            </div>
          </section>

          <section class="panel">
            <h2 class="panel-title">院校</h2>
            <button class="school-link" type="button" @click="goToSchool(schoolId)">
              <span class="school-link-left">
                <span class="school-link-name">{{ schoolName }}</span>
                <span v-if="schoolData.city || schoolData.country" class="school-link-meta">
                  {{ [schoolData.city, schoolData.country].filter(Boolean).join(' · ') }}
                </span>
              </span>
              <el-icon class="school-link-arrow">
                <ArrowRight />
              </el-icon>
            </button>
          </section>
        </aside>
      </div>
    </main>

    <footer-bar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import FooterBar from '../../components/FooterBar.vue'
import schoolService from '@/api/schoolService'
import majorService from '@/api/majorService'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, Briefcase, Clock, Money, Star, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 专业分类映射
const categoryMap = {
  'engineering': '工程技术',
  'business': '商科管理',
  'medicine': '医学健康',
  'arts': '人文艺术',
  'science': '理学科学',
  'social': '社会科学'
}

// 获取路由参数
const schoolId = route.params.schoolId
const majorId = route.params.majorId

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

const defaultSchoolImage = resolvePublicUrl('images/schools/default-school.jpg')

// 学校数据
const schoolData = ref({
  id: schoolId,
  name: '加载中...',
  chineseName: '',
  country: '',
  city: '',
  ranking: '',
  tuition: '',
  description: ''
})

// 专业详情数据
const majorData = ref({
  id: majorId,
  schoolId: schoolId,
  name: '计算机科学',
  category: 'engineering',
  shortDescription: '学习编程、算法、软件开发等技术',
  description: '',
  icon: 'fas fa-laptop-code',
  popularity: 'hot',
  popularityText: '最热门',
  schoolCount: 156,
  studentCount: 12500,
  averageSalary: '£45,000',
  careerProspects: ''
})

// 核心课程数据
const coreCourses = ref([])

const schoolName = computed(() => schoolData.value.name || schoolData.value.chineseName || '院校')
const scoreDialogVisible = ref(false)
const scoreActiveTab = ref('language')
const coreCoursesExpanded = ref(false)

const normalizeWebsite = (website) => {
  const value = String(website || '').trim()
  if (!value) return ''
  if (/^https?:\/\//i.test(value)) return value
  return `https://${value}`
}

const officialWebsite = computed(() => normalizeWebsite(schoolData.value.website))

const normalizeLink = (value) => {
  const raw = String(value || '').trim()
  if (!raw) return ''
  if (/^https?:\/\//i.test(raw)) return raw
  if (raw.startsWith('/')) return raw
  return `https://${raw}`
}

const fallbackText = '官网没有明确说明'

const majorWebsite = computed(() => normalizeLink(majorData.value.website))
const majorBrochureUrl = computed(() => normalizeLink(majorData.value.brochureUrl))

const formatDisplay = (value) => {
  if (value === null || value === undefined) return fallbackText
  const str = String(value).trim()
  return str ? str : fallbackText
}

const formatBoolean = (value, trueText = '是', falseText = '否') => {
  if (value === true) return trueText
  if (value === false) return falseText
  return fallbackText
}

const formatTuition = () => {
  const raw = majorData.value.tuitionFee ?? schoolData.value.tuitionFee ?? schoolData.value.tuition
  if (raw === null || raw === undefined || raw === '') return fallbackText
  const amount = typeof raw === 'number' ? raw : Number(String(raw).replace(/,/g, ''))
  const currency = schoolData.value.feeCurrency || schoolData.value.currency || ''

  if (!Number.isFinite(amount)) return formatDisplay(raw)
  const formatted = amount.toLocaleString()
  return currency ? `${currency} ${formatted}` : formatted
}

const applyInfoGroups = computed(() => {
  const ielts =
    majorData.value.ieltsRequirement !== null && majorData.value.ieltsRequirement !== undefined
      ? majorData.value.ieltsRequirement
      : schoolData.value.ieltsRequirement

  const majorAdmission = majorData.value.admissionRequirements || majorData.value.admissionRequirement

  return [
    [
      { key: 'majorName', label: '专业名称', value: formatDisplay(majorData.value.name) },
      { key: 'duration', label: '学制', value: formatDisplay(majorData.value.duration) },
      { key: 'startDate', label: '入学月份', value: formatDisplay(majorData.value.startDate) }
    ],
    [
      { key: 'tuition', label: '学费', value: formatTuition() },
      { key: 'workExp', label: '工作经验(年)', value: formatDisplay(majorData.value.workExperienceRequired) },
      { key: 'admission', label: '入学要求', value: formatDisplay(majorAdmission || schoolData.value.entryRequirement) }
    ],
    [
      { key: 'ielts', label: '雅思要求', value: formatDisplay(ielts) },
      {
        key: 'toefl',
        label: '托福要求',
        value: formatDisplay(majorData.value.toeflRequirement || schoolData.value.toeflRequirement)
      },
      { key: 'language', label: '授课语言', value: formatDisplay(majorData.value.language) }
    ],
    [
      { key: 'gre', label: 'GRE', value: formatDisplay(majorData.value.greRequirement || schoolData.value.greRequirement) },
      {
        key: 'gmat',
        label: 'GMAT',
        value: formatDisplay(majorData.value.gmatRequirement || schoolData.value.gmatRequirement)
      },
      { key: 'portfolio', label: '作品集/论文', value: formatBoolean(majorData.value.thesisRequired, '需要', '不需要') }
    ],
    [
      { key: 'scholarship', label: '奖学金', value: formatBoolean(majorData.value.scholarshipAvailable, '有', '无') },
      { key: 'internship', label: '实习', value: formatBoolean(majorData.value.internshipAvailable, '可', '不可') },
      {
        key: 'creditHours',
        label: '学分',
        value:
          majorData.value.creditHours !== null && majorData.value.creditHours !== undefined
            ? `${majorData.value.creditHours}`
            : fallbackText
      }
    ],
    [
      {
        key: 'schoolWebsite',
        label: '院校官网',
        value: officialWebsite.value ? '打开链接' : fallbackText,
        href: officialWebsite.value || ''
      },
      {
        key: 'majorWebsite',
        label: '专业官网',
        value: majorWebsite.value ? '打开链接' : fallbackText,
        href: majorWebsite.value || ''
      },
      {
        key: 'brochure',
        label: '专业小册子',
        value: majorBrochureUrl.value ? '打开链接' : fallbackText,
        href: majorBrochureUrl.value || ''
      }
    ],
    [
      {
        key: 'deadline',
        label: '申请截止',
        value: formatDisplay(majorData.value.applicationDeadline || schoolData.value.applicationDeadline)
      },
      {
        key: 'avgGpa',
        label: '平均录取GPA',
        value: formatDisplay(schoolData.value.averageGpa)
      },
      {
        key: 'degreeType',
        label: '学位类型',
        value: formatDisplay(majorData.value.degreeType)
      }
    ]
  ]
})

// 获取专业数据
const fetchMajorData = async () => {
  try {
    const response = await majorService.getMajorById(majorId)
    if (response && response.data) {
      majorData.value = {
        ...majorData.value,
        ...response.data,
        id: majorId
      }
    }
  } catch (error) {
    console.error("获取专业数据失败:", error)
    // 使用默认数据，不显示错误消息
  }
}

// 获取核心课程数据
const fetchCoreCourses = async () => {
  try {
    const response = await majorService.getCoreCoursesByMajorId(majorId)
    if (response && response.data) {
      coreCourses.value = response.data
    }
  } catch (error) {
    console.error("获取核心课程数据失败:", error)
    // 使用空数组作为默认值
    coreCourses.value = []
  }
}
const fetchSchoolData = async () => {
  try {
    const response = await schoolService.getSchoolById(schoolId)
    if (response && response.data) {
      schoolData.value = {
        ...response.data,
        id: schoolId
      }
    }
  } catch (error) {
    console.error('获取学校数据失败:', error)
    ElMessage.error('获取学校数据失败')
  }
}

// 获取学校校徽
const getSchoolLogo = (schoolId) => {
  const school = schoolData.value
  if (school) {
    const imageFields = [
      school.imageUrl,
      school.logoUrl,
      school.image,
      school.logo
    ]

    for (const imageField of imageFields) {
      if (imageField &&
        imageField !== null &&
        imageField !== 'null' &&
        imageField !== 'undefined' &&
        typeof imageField === 'string' &&
        imageField.trim() !== '') {

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
  }

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

// 组件挂载时获取数据
onMounted(() => {
  fetchSchoolData()
  fetchMajorData()
  fetchCoreCourses()
})

const getCategoryName = (categoryId) => {
  return categoryMap[categoryId] || categoryId
}

const goBack = () => {
  router.back()
}

const toggleCoreCourses = () => {
  coreCoursesExpanded.value = !coreCoursesExpanded.value
}

const openScoreDialog = (tab) => {
  if (tab === 'gpa') {
    scoreActiveTab.value = 'academic'
  } else if (tab === 'ielts' || tab === 'toefl') {
    scoreActiveTab.value = 'language'
  } else if (tab) {
    scoreActiveTab.value = 'standardized'
  } else {
    scoreActiveTab.value = 'language'
  }
  scoreDialogVisible.value = true
}

const goToSchool = (schoolId) => {
  router.push(`/schools/${schoolId}`)
}
</script>

<style scoped>
.major-detail-page {
  min-height: 100vh;
  background: #F9F8F4;
  color: #1C1917;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.major-hero {
  padding: 110px 0 28px;
  border-bottom: 1px solid rgba(28, 25, 23, 0.06);
}

.hero-main {
  display: grid;
  grid-template-columns: 84px 1fr;
  gap: 18px;
  align-items: center;
}

.school-mark {
  width: 90px;
  height: 90px;
  border-radius: 18px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(28, 25, 23, 0.08);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.05);
}

.school-mark-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-title {
  margin: 6px 0 0;
  font-size: 34px;
  line-height: 1.18;
  letter-spacing: -0.02em;
  font-family: 'Times New Roman', serif;
  font-weight: 700;
}

.hero-title-school,
.hero-title-divider,
.hero-title-major {
  font: inherit;
  font-size: inherit;
  font-weight: inherit;
  line-height: inherit;
  letter-spacing: inherit;
}

.hero-title-divider {
  color: rgba(28, 25, 23, 0.35);
}

.hero-subtitle {
  margin: 10px 0 0;
  font-size: 15px;
  line-height: 1.6;
  color: rgba(28, 25, 23, 0.7);
}

.hero-tags {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.hero-toolbar {
  margin-top: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.toolbar-back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  padding: 0 16px;
  border-radius: 12px;
  border: 1px solid rgba(28, 25, 23, 0.16);
  background: rgba(255, 255, 255, 0.92);
  color: rgba(28, 25, 23, 0.86);
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.toolbar-back:hover {
  border-color: rgba(197, 160, 89, 0.6);
  color: #C5A059;
  background: rgba(197, 160, 89, 0.06);
}

.toolbar-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 12px;
}

.toolbar-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 18px;
  border-radius: 999px;
  border: 1px solid rgba(28, 25, 23, 0.16);
  background: rgba(255, 255, 255, 0.92);
  color: rgba(28, 25, 23, 0.86);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s ease;
}

.toolbar-pill:hover {
  border-color: rgba(197, 160, 89, 0.6);
  color: #C5A059;
  background: rgba(197, 160, 89, 0.06);
}

.toolbar-pill-primary {
  border-color: rgba(197, 160, 89, 0.5);
  color: #C5A059;
  background: rgba(197, 160, 89, 0.1);
}

.tag {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid rgba(28, 25, 23, 0.12);
  background: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  font-weight: 700;
  color: rgba(28, 25, 23, 0.8);
}

.tag-primary {
  border-color: rgba(197, 160, 89, 0.45);
  color: #C5A059;
  background: rgba(197, 160, 89, 0.08);
}

.major-content {
  padding: 28px 0 56px;
  background: transparent;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 22px;
  align-items: start;
}

.panel {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(28, 25, 23, 0.08);
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.06);
}

.panel-title {
  margin: 0 0 14px;
  font-size: 20px;
  letter-spacing: -0.02em;
  font-family: 'Times New Roman', serif;
  font-weight: 700;
}

.panel-title-section {
  margin: 0;
}

.apply-table-wrapper {
  overflow-x: auto;
}

.apply-table {
  min-width: 720px;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(28, 25, 23, 0.08);
  background: rgba(255, 255, 255, 0.92);
}

.apply-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.apply-cell {
  padding: 16px 18px;
  border-right: 1px solid rgba(28, 25, 23, 0.06);
  border-bottom: 1px solid rgba(28, 25, 23, 0.06);
  min-width: 0;
}

.apply-row .apply-cell:last-child {
  border-right: none;
}

.apply-row:last-child .apply-cell {
  border-bottom: none;
}

.apply-row-label .apply-cell {
  background: rgba(249, 248, 244, 0.65);
  font-size: 13px;
  font-weight: 800;
  color: rgba(28, 25, 23, 0.7);
}

.apply-row-value .apply-cell {
  background: rgba(255, 255, 255, 0.92);
  font-size: 14px;
  color: rgba(28, 25, 23, 0.82);
  line-height: 1.6;
  word-break: break-word;
}

.apply-link {
  color: #C5A059;
  font-weight: 800;
  text-decoration: none;
}

.apply-link:hover {
  text-decoration: underline;
}

.panel-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 0 0 14px;
}

.section-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid rgba(28, 25, 23, 0.16);
  background: rgba(255, 255, 255, 0.92);
  color: rgba(28, 25, 23, 0.86);
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.section-toggle:hover {
  border-color: rgba(197, 160, 89, 0.6);
  color: #C5A059;
  background: rgba(197, 160, 89, 0.06);
}

.collapsed-state {
  padding: 12px 12px;
  border-radius: 14px;
  border: 1px dashed rgba(28, 25, 23, 0.18);
  background: rgba(249, 248, 244, 0.65);
  color: rgba(28, 25, 23, 0.6);
  font-size: 13px;
}

.panel-subtitle {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.02em;
  text-transform: uppercase;
  color: rgba(28, 25, 23, 0.7);
  font-family: sans-serif;
}

.panel-divider {
  height: 1px;
  margin: 18px 0;
  background: rgba(28, 25, 23, 0.06);
}

.intro-paragraph {
  margin: 0;
  font-size: 14px;
  line-height: 1.75;
  color: rgba(28, 25, 23, 0.78);
}

.course-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 12px;
}

.course-item {
  padding: 14px 14px;
  border-radius: 14px;
  border: 1px solid rgba(28, 25, 23, 0.08);
  background: rgba(249, 248, 244, 0.65);
}

.course-title {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: baseline;
  font-size: 14px;
  color: rgba(28, 25, 23, 0.88);
}

.course-title-en {
  color: rgba(28, 25, 23, 0.6);
  font-size: 13px;
}

.course-details {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.course-pill {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  border: 1px solid rgba(28, 25, 23, 0.1);
  background: rgba(255, 255, 255, 0.75);
  font-size: 11px;
  font-weight: 600;
  color: rgba(28, 25, 23, 0.75);
}

.course-description {
  margin: 10px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: rgba(28, 25, 23, 0.68);
}

.empty-state {
  padding: 14px 12px;
  border-radius: 14px;
  border: 1px dashed rgba(28, 25, 23, 0.18);
  background: rgba(249, 248, 244, 0.65);
  color: rgba(28, 25, 23, 0.6);
  font-size: 13px;
}

.sidebar {
  display: grid;
  gap: 16px;
}

.metric-list {
  display: grid;
  gap: 12px;
}

.metric-item {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid rgba(28, 25, 23, 0.08);
  background: rgba(249, 248, 244, 0.65);
}

.metric-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(197, 160, 89, 0.12);
  color: #C5A059;
  flex-shrink: 0;
}

.metric-body {
  min-width: 0;
}

.metric-label {
  font-size: 12px;
  color: rgba(28, 25, 23, 0.55);
}

.metric-value {
  margin-top: 2px;
  font-size: 14px;
  font-weight: 800;
  color: rgba(28, 25, 23, 0.88);
}

.school-link {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 14px;
  border-radius: 16px;
  border: 1px solid rgba(28, 25, 23, 0.08);
  background: rgba(249, 248, 244, 0.65);
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.school-link:hover {
  border-color: rgba(197, 160, 89, 0.55);
  box-shadow: 0 16px 36px rgba(197, 160, 89, 0.12);
  transform: translateY(-1px);
}

.school-link-left {
  display: grid;
  gap: 4px;
  min-width: 0;
}

.school-link-name {
  font-weight: 800;
  letter-spacing: -0.01em;
  color: rgba(28, 25, 23, 0.9);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.school-link-meta {
  font-size: 12px;
  color: rgba(28, 25, 23, 0.55);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.school-link-arrow {
  color: rgba(28, 25, 23, 0.55);
  flex-shrink: 0;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-main {
    grid-template-columns: 1fr;
    justify-items: start;
    gap: 14px;
  }

  .apply-table {
    min-width: 640px;
  }

  .hero-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-back {
    justify-content: center;
  }

  .toolbar-actions {
    justify-content: flex-start;
  }

}

@media (prefers-reduced-motion: reduce) {
  .school-link {
    transition: none;
  }
}

@media (prefers-reduced-motion: reduce) {

  .toolbar-back:hover,
  .toolbar-pill:hover,
  .school-link:hover {
    transform: none;
  }
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.score-item {
  border: 1px solid rgba(28, 25, 23, 0.08);
  background: rgba(249, 248, 244, 0.65);
  border-radius: 14px;
  padding: 12px;
}

.score-item-wide {
  grid-column: 1 / -1;
}

.score-label {
  font-size: 12px;
  color: rgba(28, 25, 23, 0.55);
}

.score-value {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 800;
  color: rgba(28, 25, 23, 0.88);
}

.score-text {
  margin-top: 4px;
  font-size: 13px;
  line-height: 1.6;
  color: rgba(28, 25, 23, 0.75);
}

.intro-content li:before {
  content: '•';
  color: #1d1d1f;
  font-weight: bold;
  position: absolute;
  left: 0;
}

.schools-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.school-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
}

.school-item:hover {
  border-color: rgba(0, 0, 0, 0.15);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.school-info h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #1d1d1f;
}

.school-info p {
  color: #86868b;
  font-size: 14px;
  margin-bottom: 8px;
}

.school-details {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #86868b;
}

/* 核心课程样式 */
.course-details {
  display: flex;
  gap: 12px;
  margin: 8px 0;
  font-size: 12px;
}

.course-type {
  background: #e3f2fd;
  color: #1976d2;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.course-credits {
  background: #f3e5f5;
  color: #7b1fa2;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.course-semester {
  background: #e8f5e8;
  color: #388e3c;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.course-description {
  font-size: 13px;
  color: #666;
  margin: 8px 0 0 0;
  line-height: 1.4;
}

.no-data {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
  }

  .major-info {
    flex-direction: column;
    text-align: center;
  }

  .major-stats {
    justify-content: center;
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .school-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
