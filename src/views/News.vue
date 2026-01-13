<template>
  <div class="news-page">
    <nav-bar></nav-bar>

    <template v-if="isDetail">
      <div class="detail-hero">
        <div class="container detail-hero-content">
          <button class="detail-back" type="button" @click="goBack">返回留学资讯</button>
        </div>
      </div>

      <div class="detail-section">
        <div class="container">
          <div v-if="detailLoading" class="detail-card">
            <div class="detail-title">加载中...</div>
          </div>

          <div v-else-if="selectedArticle" class="detail-card">
            <div class="detail-meta">
              <div class="detail-category">{{ getCategoryName(selectedArticle.category) }}</div>
              <div class="detail-meta-row">
                <span class="detail-author">{{ selectedArticle.author }}</span>
                <span class="detail-dot">•</span>
                <span class="detail-date">{{ formatDate(selectedArticle.date) }}</span>
                <span class="detail-dot">•</span>
                <span class="detail-read">{{ selectedArticle.readTime }}</span>
                <span class="detail-dot">•</span>
                <span class="detail-views">{{ formatViews(selectedArticle.views) }} 阅读</span>
              </div>
            </div>

            <h1 class="detail-title">{{ selectedArticle.title }}</h1>

            <div class="detail-tags">
              <span v-for="tag in selectedArticle.tags" :key="tag" class="detail-tag">{{ tag }}</span>
            </div>

            <div class="detail-image" v-if="selectedArticle.image">
              <img :src="selectedArticle.image" :alt="selectedArticle.title" loading="lazy" decoding="async" />
            </div>

            <div class="detail-content">
              <p v-for="(p, idx) in detailParagraphs" :key="idx" class="detail-paragraph">{{ p }}</p>
            </div>
          </div>

          <div v-else class="detail-card detail-not-found">
            <div class="detail-title">未找到该资讯</div>
            <button class="detail-back secondary" type="button" @click="goBack">返回留学资讯</button>
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="country-hero">
        <div class="country-hero-content">
          <div class="section-label">LATEST NEWS</div>
          <h1 class="country-title">留学资讯</h1>
          <div class="gold-separator"></div>
          <p class="country-subtitle">紧跟全球教育趋势，掌握最新政策动向</p>
        </div>
      </div>

      <div class="country-selector-section">
        <div class="container">
          <div class="filter-tabs">
            <button v-for="category in categories" :key="category.id"
              :class="['filter-tab', { active: activeCategory === category.id }]" @click="activeCategory = category.id">
              <span class="tab-text">{{ category.name }}</span>
            </button>
          </div>
        </div>
      </div>

      <div class="news-section">
        <div class="container">
          <div v-if="listLoading" class="news-empty">加载中...</div>
          <div v-else-if="paddedNews.length === 0" class="news-empty">暂无资讯</div>
          <div class="news-grid">
            <article v-for="article in paddedNews" :key="article.id"
              :class="['news-card', { placeholder: article.__placeholder }]" @click="handleCardClick(article)">
              <template v-if="!article.__placeholder">
                <div class="news-image">
                  <img :src="article.image" :alt="article.title" loading="lazy" decoding="async" />
                  <div class="news-overlay">
                    <div class="news-category-badge">{{ getCategoryName(article.category) }}</div>
                  </div>
                </div>
                <div class="news-content">
                  <div class="news-meta-top">
                    <span class="news-author">{{ article.author }}</span>
                    <span class="news-read-time">{{ article.readTime }}</span>
                  </div>
                  <h3 class="news-title">{{ article.title }}</h3>
                  <p class="news-summary">{{ article.summary }}</p>
                  <div class="news-footer">
                    <div class="news-tags">
                      <span v-for="tag in article.tags.slice(0, 2)" :key="tag" class="tag">{{ tag }}</span>
                    </div>
                    <div class="news-stats">
                      <span class="news-views">{{ formatViews(article.views) }} 阅读</span>
                      <span class="news-date">{{ formatDate(article.date) }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </article>
          </div>
        </div>
      </div>
    </template>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

// 筛选分类
const categories = ref([
  { id: 'all', name: '全部资讯', icon: '📚' },
  { id: 'policy', name: '政策解读', icon: '📋' },
  { id: 'application', name: '申请指南', icon: '📝' },
  { id: 'life', name: '留学生活', icon: '🏠' },
  { id: 'career', name: '就业前景', icon: '💼' }
])

const activeCategory = ref('all')

const news = ref([])
const detailArticle = ref(null)
const detailLoading = ref(false)
const detailNotFound = ref(false)
const listLoading = ref(false)

const normalizeDateString = (value) => {
  if (value == null) return ''
  const str = String(value).trim()
  if (!str) return ''
  if (/^\d{4}-\d{2}-\d{2}T/.test(str)) return str
  if (/^\d{4}-\d{2}-\d{2}\s+\d/.test(str)) return str.replace(' ', 'T')
  return str
}

const normalizeCategory = (value) => {
  const raw = value == null ? '' : String(value).trim()
  if (!raw) return 'application'
  const known = new Set(['policy', 'application', 'life', 'career'])
  if (known.has(raw)) return raw
  if (raw.includes('政策')) return 'policy'
  if (raw.includes('申请') || raw.includes('院校') || raw.includes('奖学金') || raw.includes('考试') || raw.includes('排名') || raw.includes('行前') || raw.includes('规划') || raw.includes('动态')) {
    return 'application'
  }
  if (raw.includes('生活')) return 'life'
  if (raw.includes('就业') || raw.includes('职业') || raw.includes('专业')) return 'career'
  return 'application'
}

const resolveImageByCategory = (categoryId) => {
  const map = {
    policy: '/images/news-1.jpg',
    application: '/images/news2.jpg',
    life: '/images/news3.jpg',
    career: '/images/news4.jpg'
  }
  return map[categoryId] || '/images/news2.jpg'
}

const mapServerNews = (item) => {
  const categoryId = normalizeCategory(item?.category)
  const title = item?.title ? String(item.title) : ''
  const summary = item?.summary ? String(item.summary) : ''
  const content = item?.content ? String(item.content) : ''
  const image = item?.imageUrl ? String(item.imageUrl) : resolveImageByCategory(categoryId)
  const date = normalizeDateString(item?.createTime) || new Date().toISOString()
  const views = typeof item?.views === 'number' ? item.views : Number(item?.views || 0)
  const tags = [getCategoryName(categoryId), '留学资讯'].filter(Boolean)
  return {
    id: item?.id,
    title,
    summary,
    content,
    image,
    category: categoryId,
    date,
    views: Number.isFinite(views) ? views : 0,
    tags,
    author: '新英美资讯团队',
    readTime: '6分钟',
    featured: false
  }
}

const getArticleTime = (article) => {
  const date = new Date(normalizeDateString(article?.date))
  const time = date.getTime()
  return Number.isNaN(time) ? 0 : time
}

const getArticleNumericId = (article) => {
  const n = Number(article?.id)
  return Number.isFinite(n) ? n : -1
}

const sortArticlesDesc = (list) => {
  return list
    .slice()
    .sort((a, b) => getArticleTime(b) - getArticleTime(a) || getArticleNumericId(b) - getArticleNumericId(a))
}

const dedupeArticles = (list) => {
  const seen = new Set()
  const result = []

  for (const item of list) {
    if (!item) continue

    const idKey = item.id == null ? '' : `id:${String(item.id)}`
    const titleKey = item.title ? String(item.title).trim() : ''
    const dateKey = item.date ? String(item.date).trim() : ''
    const signatureKey = titleKey && dateKey ? `sig:${titleKey}__${dateKey}` : ''

    const key = idKey || signatureKey
    if (!key) continue
    if (seen.has(key)) continue

    seen.add(key)
    result.push(item)
  }

  return result
}

const fetchNewsList = async () => {
  listLoading.value = true
  try {
    const res = await request.get('/news')
    const list = Array.isArray(res?.data) ? res.data : []
    news.value = sortArticlesDesc(dedupeArticles(list.map(mapServerNews).filter((a) => a && a.id != null)))
  } catch (e) {
    news.value = []
  } finally {
    listLoading.value = false
  }
}

const fetchNewsDetail = async (id) => {
  detailLoading.value = true
  detailNotFound.value = false
  try {
    const res = await request.get(`/news/${id}`)
    const item = res?.data
    if (!item || item.id == null) {
      detailNotFound.value = true
      detailArticle.value = null
      return
    }
    const mapped = mapServerNews(item)
    detailArticle.value = mapped
    const exists = news.value.some((n) => String(n.id) === String(mapped.id))
    if (!exists) news.value = sortArticlesDesc(dedupeArticles([mapped, ...news.value]))
  } catch (e) {
    detailNotFound.value = true
    detailArticle.value = null
  } finally {
    detailLoading.value = false
  }
}

const buildDetailFallback = (article) => {
  const title = article && article.title ? String(article.title) : ''
  const summary = article && article.summary ? String(article.summary) : ''
  const categoryId = article && article.category ? String(article.category) : ''
  const categoryName = categories.value.find((c) => c.id === categoryId)?.name || '留学资讯'
  const tags = Array.isArray(article && article.tags) ? article.tags.filter(Boolean).slice(0, 3) : []

  const blocks = []
  if (summary) blocks.push(summary)
  if (tags.length) blocks.push(`关键词：${tags.join('、')}。`)
  blocks.push(`围绕「${title || categoryName}」，本文从背景变化、关键要点与执行建议三个维度展开，帮助你在信息密集的申请季快速抓住重点。`)
  blocks.push(`第一部分：背景与趋势。${categoryName}相关信息通常会受到政策更新、院校口径与申请时间线的影响，建议你优先确认官方来源与发布时间。`)
  blocks.push(`第二部分：关键要点。建议把内容拆成“适用对象、核心条件、材料清单、时间节点”四项逐一核对，避免遗漏导致返工。`)
  blocks.push(`第三部分：实操建议。提前准备可复用材料（成绩单、语言、简历、推荐人信息等），并把关键节点写入日历；若不确定，优先咨询学校官网或官方渠道。`)
  return blocks.join('\n\n')
}

const isDetail = computed(() => typeof route.params.id !== 'undefined')

const selectedArticle = computed(() => {
  if (!isDetail.value) return null
  const id = String(route.params.id)
  if (detailNotFound.value) return null
  return detailArticle.value || news.value.find((a) => String(a.id) === id) || null
})

const detailParagraphs = computed(() => {
  const article = selectedArticle.value
  if (!article) return []
  const raw = article.content || buildDetailFallback(article)
  const normalized = String(raw)
    .replace(/\r\n/g, '\n')
    .replace(/\r/g, '\n')
    .replace(/\u00A0/g, ' ')
    .trim()
  if (!normalized) return ['暂无内容。']
  return normalized.split(/\n{2,}/).map((p) => p.trim()).filter(Boolean)
})

// 筛选后的资讯（不包含精选）
const filteredNews = computed(() => {
  if (activeCategory.value === 'all') {
    return news.value
  }
  return news.value.filter(article => article.category === activeCategory.value)
})

const sortedFilteredNews = computed(() => sortArticlesDesc(filteredNews.value))

const paddedNews = computed(() => {
  const list = sortedFilteredNews.value
  const remainder = list.length % 3
  const padCount = remainder === 0 ? 0 : 3 - remainder
  if (padCount === 0) return list
  const placeholders = Array.from({ length: padCount }, (_, i) => ({
    id: `__placeholder__${activeCategory.value}__${i}`,
    __placeholder: true
  }))
  return [...list, ...placeholders]
})

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : ''
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(normalizeDateString(dateString))
  if (Number.isNaN(date.getTime())) return String(dateString || '')
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 格式化阅读量
const formatViews = (views) => {
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return views.toString()
}

// 阅读文章
const readArticle = (article) => {
  if (!article) return
  router.push(`/news/${article.id}`)
}

const handleCardClick = (article) => {
  if (!article || article.__placeholder) return
  readArticle(article)
}

const goBack = () => {
  router.push('/news')
}

watch(
  () => route.fullPath,
  () => {
    window.scrollTo({ top: 0 })
  }
)

watch(
  () => route.params.id,
  (id) => {
    detailArticle.value = null
    detailNotFound.value = false
    if (typeof id === 'undefined') return
    fetchNewsDetail(id)
  },
  { immediate: true }
)

onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.news-page {
  min-height: 100vh;
  background: #F9F8F4;
  font-family: sans-serif;
  color: #292524;
}

.detail-hero {
  padding: 110px 0 24px;
}

.detail-hero-content {
  display: flex;
  align-items: center;
}

.detail-back {
  appearance: none;
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #fff;
  color: #1d1d1f;
  padding: 10px 14px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.detail-back:hover {
  border-color: #E7D4A8;
  color: #A88442;
}

.detail-back.secondary {
  margin-top: 16px;
}

.detail-section {
  padding: 0 0 70px;
}

.detail-card {
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 18px;
  padding: 28px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.04);
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 14px;
}

.detail-category {
  display: inline-flex;
  width: fit-content;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(197, 160, 89, 0.12);
  color: #A88442;
  font-weight: 700;
  font-size: 12px;
}

.detail-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: #78716C;
  font-size: 13px;
  font-weight: 600;
}

.detail-dot {
  color: #c0b9b2;
}

.detail-title {
  margin: 0;
  font-size: 26px;
  line-height: 1.35;
  color: #1d1d1f;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 14px 0 18px;
}

.detail-tag {
  padding: 6px 10px;
  border-radius: 999px;
  background: #F9F8F4;
  border: 1px solid rgba(0, 0, 0, 0.06);
  color: #57534E;
  font-size: 12px;
  font-weight: 600;
}

.detail-image {
  margin: 18px 0 20px;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.detail-image img {
  width: 100%;
  height: 380px;
  display: block;
  object-fit: cover;
}

.detail-content {
  color: #2c2c2c;
  font-size: 15px;
  line-height: 1.85;
}

.detail-paragraph {
  margin: 0 0 14px;
}

@media (max-width: 768px) {
  .detail-image img {
    height: 220px;
  }
}

.detail-not-found {
  text-align: center;
}

/* 英雄区域样式 (Updated to match Country.vue) */
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

.country-selector-section {
  background-color: white;
  padding: 2rem 1rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 2rem;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s ease;
  min-width: 100px;
  background-color: #F9F8F4;
  border: 1px solid transparent;
  color: #57534E;
  font-family: sans-serif;
  font-size: 15px;
  font-weight: 500;
  box-shadow: none;
}

.filter-tab:hover {
  background-color: white;
  border-color: #C5A059;
  color: #C5A059;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.filter-tab.active {
  background: #C5A059;
  color: white;
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.3);
  border-color: #C5A059;
  transform: translateY(-2px);
}

.tab-text {
  font-weight: 500;
  letter-spacing: 0.5px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

/* 筛选区域样式 (Removed old styles) */
/* .filter-section {
  padding: 40px 0;
  background: #F9F8F4;
  position: sticky;
  top: 64px;
  z-index: 10;
  border-bottom: 1px solid rgba(41, 37, 36, 0.05);
  backdrop-filter: blur(10px);
  background: rgba(249, 248, 244, 0.95);
} */

/* .filter-wrapper {
  text-align: center;
}

.filter-title {
  display: none;
}

.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 28px;
  border: 1px solid transparent;
  border-radius: 30px;
  background: #FFFFFF;
  color: #57534E;
  font-family: sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.filter-tab:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  color: #1c1917;
}

.filter-tab.active {
  background: #1c1917;
  color: #F9F8F4;
  box-shadow: 0 8px 20px rgba(28, 25, 23, 0.2);
}

.tab-icon {
  font-size: 1.1rem;
} */

.section-title {
  font-family: "Times New Roman", Times, serif;
  font-size: 32px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 0;
  color: #292524;
  position: relative;
  padding-bottom: 0;
}

.section-title::after {
  display: none;
}

.tag {
  padding: 4px 12px;
  background: white;
  color: #666;
  border-radius: 20px;
  font-size: 12px;
  border: 1px solid #eee;
  transition: all 0.3s ease;
}

.tag:hover {
  background: #292524;
  border-color: #292524;
  color: white;
}

/* 资讯展示区域样式 */
.news-section {
  padding: 16px 0 64px;
  background: #F9F8F4;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.news-card {
  background: white;
  border: none;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.5s cubic-bezier(0.165, 0.84, 0.44, 1);
  cursor: pointer;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.02);
}

.news-card.placeholder {
  visibility: hidden;
}

.news-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
}

.news-image {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 200px;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s cubic-bezier(0.165, 0.84, 0.44, 1);
}

.news-card:hover .news-image img {
  transform: scale(1.05);
}

.news-overlay {
  position: absolute;
  top: 16px;
  right: 16px;
  left: auto;
  bottom: auto;
  background: none;
  padding: 0;
}

.news-category-badge {
  background: rgba(255, 255, 255, 0.95);
  color: #292524;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  backdrop-filter: blur(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.news-content {
  padding: 18px;
  position: relative;
  z-index: 2;
  min-width: 0;
}

.news-meta-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.news-author {
  color: #C5A059;
  font-weight: 600;
}

.news-read-time {
  color: #999;
}

.news-title {
  font-family: "Times New Roman", Times, serif;
  font-size: 18px;
  font-weight: 600;
  color: #292524;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-summary {
  font-family: sans-serif;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.news-tags {
  display: flex;
  gap: 4px;
}

.news-stats {
  font-size: 12px;
  color: #999;
  display: flex;
  gap: 12px;
}

.load-more-section {
  text-align: center;
  margin-top: 60px;
}

.load-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
  background: #292524;
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 14px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.load-more-btn:hover {
  transform: translateY(-2px);
  background: #C5A059;
  box-shadow: 0 8px 24px rgba(197, 160, 89, 0.3);
}

.load-icon {
  width: 16px;
  height: 16px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1024px) {
  .news-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-stats {
    flex-direction: column;
    gap: 32px;
  }

  .filter-tabs {
    flex-wrap: nowrap;
    overflow-x: auto;
    justify-content: flex-start;
    padding: 0 20px;
    -webkit-overflow-scrolling: touch;
    margin: 0 -20px;
    /* Offset the container padding */
  }

  .filter-tab {
    flex-shrink: 0;
  }

  .news-grid {
    grid-template-columns: 1fr;
  }

  .container {
    padding: 0 24px;
  }
}
</style>
