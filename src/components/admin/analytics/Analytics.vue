<template>
  <div class="analytics-content">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">数据分析</h2>
        <p class="page-subtitle">查看学生申请和机构数据分析</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="exportReport">
          <Download class="button-icon" />
          导出报告
        </el-button>
        <el-button class="custom-button" type="primary" @click="refreshData">
          <RefreshCw class="button-icon" />
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 快速统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(24, 144, 255, 0.2) 0%, rgba(24, 144, 255, 0.1) 100%);">
          <Users class="stat-icon-svg" style="color: #1890ff;" />
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ statistics.totalStudents }}</h3>
          <p class="stat-label">学生总数</p>
          <div class="stat-trend up">
            <TrendingUp class="trend-icon" />
            <span>{{ statistics.studentsGrowth }}%</span>
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(82, 196, 26, 0.2) 0%, rgba(82, 196, 26, 0.1) 100%);">
          <FileText class="stat-icon-svg" style="color: #52c41a;" />
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ statistics.totalApplications }}</h3>
          <p class="stat-label">申请总数</p>
          <div class="stat-trend up">
            <TrendingUp class="trend-icon" />
            <span>{{ statistics.applicationsGrowth }}%</span>
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(250, 140, 22, 0.2) 0%, rgba(250, 140, 22, 0.1) 100%);">
          <Building class="stat-icon-svg" style="color: #fa8c16;" />
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ statistics.totalInstitutions }}</h3>
          <p class="stat-label">合作院校</p>
          <div class="stat-trend up">
            <TrendingUp class="trend-icon" />
            <span>{{ statistics.institutionsGrowth }}%</span>
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(245, 34, 45, 0.2) 0%, rgba(245, 34, 45, 0.1) 100%);">
          <CheckCircle class="stat-icon-svg" style="color: #f5222d;" />
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ statistics.successRate }}%</h3>
          <p class="stat-label">申请成功率</p>
          <div class="stat-trend up">
            <TrendingUp class="trend-icon" />
            <span>{{ statistics.successRateGrowth }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 申请趋势图表 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">申请趋势</h3>
          <div class="chart-controls">
            <div class="chart-period-selector">
              <button v-for="period in periods" :key="period.value"
                :class="['period-button', { active: activePeriod === period.value }]"
                @click="activePeriod = period.value">
                {{ period.label }}
              </button>
            </div>
          </div>
        </div>
        <div class="chart-container">
          <!-- 图表将在这里渲染 -->
          <div class="chart-placeholder">
            <BarChart class="placeholder-icon" />
            <p>申请趋势图表将在此处显示</p>
          </div>
        </div>
      </div>

      <!-- 申请国家分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">申请国家分布</h3>
          <div class="chart-controls">
            <button class="chart-action-button">
              <Filter class="button-icon-sm" />
              筛选
            </button>
          </div>
        </div>
        <div class="chart-container">
          <!-- 图表将在这里渲染 -->
          <div class="chart-placeholder">
            <PieChart class="placeholder-icon" />
            <p>申请国家分布图表将在此处显示</p>
          </div>
        </div>
      </div>

      <!-- 申请阶段分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">申请阶段分布</h3>
          <div class="chart-controls">
            <button class="chart-action-button">
              <Filter class="button-icon-sm" />
              筛选
            </button>
          </div>
        </div>
        <div class="chart-container">
          <!-- 图表将在这里渲染 -->
          <div class="chart-placeholder">
            <PieChart class="placeholder-icon" />
            <p>申请阶段分布图表将在此处显示</p>
          </div>
        </div>
      </div>

      <!-- 成功申请分析 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">成功申请分析</h3>
          <div class="chart-controls">
            <button class="chart-action-button">
              <Filter class="button-icon-sm" />
              筛选
            </button>
          </div>
        </div>
        <div class="chart-container">
          <!-- 图表将在这里渲染 -->
          <div class="chart-placeholder">
            <BarChart class="placeholder-icon" />
            <p>成功申请分析图表将在此处显示</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 详细数据表格 -->
    <div class="data-table-section">
      <div class="section-header">
        <h3 class="section-title">详细申请数据</h3>
        <div class="section-actions">
          <div class="search-box">
            <Search class="search-icon" />
            <input type="text" placeholder="搜索..." class="search-input" v-model="searchQuery" />
          </div>
          <button class="action-button">
            <Download class="button-icon-sm" />
            导出
          </button>
        </div>
      </div>

      <div class="data-table">
        <table>
          <thead>
            <tr>
              <th>国家</th>
              <th>申请数量</th>
              <th>已完成</th>
              <th>成功率</th>
              <th>平均处理时间</th>
              <th>趋势</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in filteredTableData" :key="index">
              <td>
                <div class="country-cell">
                  <span class="country-flag">{{ item.flag }}</span>
                  <span>{{ item.country }}</span>
                </div>
              </td>
              <td>{{ item.applications }}</td>
              <td>{{ item.completed }}</td>
              <td>
                <div class="success-rate">
                  <div class="rate-bar">
                    <div class="rate-fill" :style="{ width: item.successRate + '%' }"></div>
                  </div>
                  <span>{{ item.successRate }}%</span>
                </div>
              </td>
              <td>{{ item.avgProcessTime }}</td>
              <td>
                <div class="trend-cell">
                  <TrendingUp v-if="item.trend > 0" class="trend-up" />
                  <TrendingDown v-else class="trend-down" />
                  <span :class="item.trend > 0 ? 'trend-up' : 'trend-down'">{{ Math.abs(item.trend) }}%</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Users,
  FileText,
  Building,
  CheckCircle,
  TrendingUp,
  TrendingDown,
  Download,
  RefreshCw,
  Filter,
  BarChart,
  PieChart,
  Search
} from 'lucide-vue-next'

// 统计数据
const statistics = ref({
  totalStudents: 842,
  studentsGrowth: 12,
  totalApplications: 1568,
  applicationsGrowth: 15,
  totalInstitutions: 65,
  institutionsGrowth: 8,
  successRate: 76,
  successRateGrowth: 5
})

// 时间周期选项
const periods = [
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '季', value: 'quarter' },
  { label: '年', value: 'year' }
]
const activePeriod = ref('month')

// 表格数据
const tableData = ref([
  {
    country: '美国',
    flag: '🇺🇸',
    applications: 685,
    completed: 432,
    successRate: 78,
    avgProcessTime: '45天',
    trend: 12
  },
  {
    country: '英国',
    flag: '🇬🇧',
    applications: 423,
    completed: 321,
    successRate: 82,
    avgProcessTime: '38天',
    trend: 15
  },
  {
    country: '加拿大',
    flag: '🇨🇦',
    applications: 267,
    completed: 189,
    successRate: 72,
    avgProcessTime: '42天',
    trend: 8
  },
  {
    country: '澳大利亚',
    flag: '🇦🇺',
    applications: 193,
    completed: 138,
    successRate: 65,
    avgProcessTime: '50天',
    trend: -3
  }
])

// 搜索功能
const searchQuery = ref('')
const filteredTableData = computed(() => {
  if (!searchQuery.value) return tableData.value

  const query = searchQuery.value.toLowerCase()
  return tableData.value.filter(item =>
    item.country.toLowerCase().includes(query)
  )
})

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据已刷新')
}

// 导出报告
const exportReport = () => {
  ElMessage.info('报告导出功能开发中')
}
</script>

<style scoped>
/* 数据分析页面样式 */
.analytics-content {
  padding: 0px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #111;
}

.page-subtitle {
  color: #666;
  font-size: 14px;
}

.page-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon-svg {
  width: 24px;
  height: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.stat-trend.up {
  color: #52c41a;
}

.stat-trend.down {
  color: #f5222d;
}

.trend-icon {
  width: 14px;
  height: 14px;
}

/* 图表区域 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.chart-controls {
  display: flex;
  gap: 12px;
}

.chart-period-selector {
  display: flex;
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 4px;
}

.period-button {
  background: none;
  border: none;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.period-button.active {
  background-color: white;
  color: #1890ff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-action-button {
  background: none;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.chart-action-button:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.button-icon-sm {
  width: 14px;
  height: 14px;
}

.chart-container {
  padding: 20px;
  height: 300px;
}

.chart-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.placeholder-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 12px;
}

/* 数据表格 */
.data-table-section {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-box {
  position: relative;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  color: #999;
}

.search-input {
  padding: 6px 12px 6px 32px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 12px;
  width: 200px;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.action-button {
  background-color: white;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.action-button:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.data-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  background-color: #fafafa;
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.country-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.country-flag {
  font-size: 16px;
}

.success-rate {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rate-bar {
  width: 60px;
  height: 6px;
  background-color: #f5f5f5;
  border-radius: 3px;
  overflow: hidden;
}

.rate-fill {
  height: 100%;
  background-color: #52c41a;
  border-radius: 3px;
}

.trend-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-up {
  color: #52c41a;
}

.trend-down {
  color: #f5222d;
}

.button-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .section-actions {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }
}
</style>