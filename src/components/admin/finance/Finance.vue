<template>
  <div class="finance-content">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">财务管理</h2>
        <p class="page-subtitle">管理财务收支和账单信息</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="exportFinancialReport">
          <Download class="button-icon" />
          导出报表
        </el-button>
        <el-button class="custom-button" type="primary" @click="showAddTransactionModal = true">
          <Plus class="button-icon" />
          记录交易
        </el-button>
      </div>
    </div>
    
    <!-- 财务概览卡片 -->
    <div class="finance-summary">
      <div class="summary-card income">
        <div class="summary-header">
          <h3>收入总计</h3>
          <TrendingUp />
        </div>
        <p class="summary-amount">¥{{ formatNumber(financeSummary.totalIncome) }}</p>
        <div class="summary-footer">
          <span>较上月 <span class="growth">+{{ financeSummary.incomeGrowth }}%</span></span>
        </div>
      </div>
      
      <div class="summary-card expense">
        <div class="summary-header">
          <h3>支出总计</h3>
          <TrendingDown />
        </div>
        <p class="summary-amount">¥{{ formatNumber(financeSummary.totalExpense) }}</p>
        <div class="summary-footer">
          <span>较上月 <span class="growth">+{{ financeSummary.expenseGrowth }}%</span></span>
        </div>
      </div>
      
      <div class="summary-card profit">
        <div class="summary-header">
          <h3>利润</h3>
          <DollarSign />
        </div>
        <p class="summary-amount">¥{{ formatNumber(financeSummary.profit) }}</p>
        <div class="summary-footer">
          <span>较上月 <span class="growth">+{{ financeSummary.profitGrowth }}%</span></span>
        </div>
      </div>
      
      <div class="summary-card pending">
        <div class="summary-header">
          <h3>待收款</h3>
          <Clock />
        </div>
        <p class="summary-amount">¥{{ formatNumber(financeSummary.pendingPayment) }}</p>
        <div class="summary-footer">
          <span>{{ financeSummary.pendingCount }}笔待收款</span>
        </div>
      </div>
    </div>
    
    <!-- 财务筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label>日期范围</label>
        <div class="select-wrapper">
          <select v-model="filters.dateRange">
            <option value="today">今天</option>
            <option value="thisWeek">本周</option>
            <option value="thisMonth">本月</option>
            <option value="lastMonth">上月</option>
            <option value="thisYear">今年</option>
            <option value="custom">自定义</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      
      <div class="filter-group">
        <label>交易类型</label>
        <div class="select-wrapper">
          <select v-model="filters.transactionType">
            <option value="">全部</option>
            <option value="income">收入</option>
            <option value="expense">支出</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      
      <div class="filter-group">
        <label>分类</label>
        <div class="select-wrapper">
          <select v-model="filters.category">
            <option value="">全部</option>
            <option value="tuition">学费</option>
            <option value="service">服务费</option>
            <option value="consulting">咨询费</option>
            <option value="salary">工资</option>
            <option value="rent">房租</option>
            <option value="utilities">水电费</option>
            <option value="other">其他</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      
      <div class="filter-search">
        <label>搜索</label>
        <div class="search-input-wrapper">
          <Search class="search-input-icon" />
          <input 
            type="text" 
            v-model="filters.search" 
            placeholder="搜索交易信息..." 
            class="filter-search-input" 
          />
        </div>
      </div>
      
      <div class="filter-actions">
        <el-button class="custom-button" type="default" @click="applyFilters">
          <Filter class="button-icon" />
          筛选
        </el-button>
        <el-button class="custom-button" type="default" @click="resetFilters">
          <RefreshCw class="button-icon" />
          重置
        </el-button>
      </div>
    </div>
    
    <!-- 交易记录表格 -->
    <div class="transactions-table">
      <table>
        <thead>
          <tr>
            <th>交易时间</th>
            <th>交易类型</th>
            <th>金额</th>
            <th>分类</th>
            <th>相关账户</th>
            <th>描述</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(transaction, index) in filteredTransactions" :key="index">
            <td>{{ transaction.date }}</td>
            <td>
              <span :class="['transaction-type', transaction.type]">
                {{ transaction.type === 'income' ? '收入' : '支出' }}
              </span>
            </td>
            <td :class="{'income-amount': transaction.type === 'income', 'expense-amount': transaction.type === 'expense'}">
              {{ transaction.type === 'income' ? '+' : '-' }}¥{{ formatNumber(transaction.amount) }}
            </td>
            <td>{{ getCategoryName(transaction.category) }}</td>
            <td>{{ transaction.account }}</td>
            <td>{{ transaction.description }}</td>
            <td>
              <span :class="['status-badge', transaction.status]">
                {{ getStatusName(transaction.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button class="btn-icon-sm" @click="viewTransaction(transaction)" title="查看详情">
                  <Eye />
                </button>
                <button class="btn-icon-sm" @click="editTransaction(transaction)" title="编辑">
                  <Edit />
                </button>
                <button class="btn-icon-sm" @click="deleteTransaction(transaction)" title="删除">
                  <Trash2 />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <div class="pagination-info">
        显示 {{ paginationInfo.start }}-{{ paginationInfo.end }} 条，共 {{ transactions.length }} 条
      </div>
      <div class="pagination">
        <button class="pagination-button" :disabled="currentPage === 1" @click="changePage(1)">
          <ChevronsLeft />
        </button>
        <button class="pagination-button" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
          <ChevronLeft />
        </button>
        <button 
          v-for="page in displayPages" 
          :key="page" 
          class="pagination-number" 
          :class="{ active: currentPage === page }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
        <button class="pagination-button" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">
          <ChevronRight />
        </button>
        <button class="pagination-button" :disabled="currentPage === totalPages" @click="changePage(totalPages)">
          <ChevronsRight />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Download,
  Plus,
  TrendingUp,
  TrendingDown,
  DollarSign,
  Clock,
  ChevronDown,
  Search,
  Filter,
  RefreshCw,
  Eye,
  Edit,
  Trash2,
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight
} from 'lucide-vue-next'

// 财务概览数据
const financeSummary = ref({
  totalIncome: 1258600,
  incomeGrowth: 12.5,
  totalExpense: 768400,
  expenseGrowth: 8.3,
  profit: 490200,
  profitGrowth: 15.2,
  pendingPayment: 350000,
  pendingCount: 12
})

// 筛选条件
const filters = ref({
  dateRange: 'thisMonth',
  transactionType: '',
  category: '',
  search: ''
})

// 交易记录数据
const transactions = ref([
  {
    date: '2023-04-08',
    type: 'income',
    amount: 15000,
    category: 'tuition',
    account: '张明',
    description: '哈佛大学申请服务费',
    status: 'completed'
  },
  {
    date: '2023-04-07',
    type: 'income',
    amount: 12000,
    category: 'service',
    account: '李华',
    description: '文书修改服务费',
    status: 'completed'
  },
  {
    date: '2023-04-06',
    type: 'expense',
    amount: 8500,
    category: 'salary',
    account: '王老师',
    description: '3月份工资',
    status: 'completed'
  },
  {
    date: '2023-04-05',
    type: 'income',
    amount: 18000,
    category: 'tuition',
    account: '刘芳',
    description: '牛津大学申请服务费',
    status: 'pending'
  },
  {
    date: '2023-04-03',
    type: 'expense',
    amount: 5000,
    category: 'rent',
    account: '办公室租赁',
    description: '4月份办公室租金',
    status: 'completed'
  }
])

// 模态框状态
const showAddTransactionModal = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = computed(() => Math.ceil(filteredTransactions.value.length / pageSize.value))
const paginationInfo = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value + 1
  const end = Math.min(start + pageSize.value - 1, transactions.value.length)
  return { start, end }
})
const displayPages = computed(() => {
  const pages = []
  const maxDisplayPages = 5
  
  let start = Math.max(1, currentPage.value - 2)
  let end = Math.min(totalPages.value, start + maxDisplayPages - 1)
  
  if (end - start + 1 < maxDisplayPages) {
    start = Math.max(1, end - maxDisplayPages + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

// 筛选后的交易数据
const filteredTransactions = computed(() => {
  let result = [...transactions.value]
  
  if (filters.value.transactionType) {
    result = result.filter(item => item.type === filters.value.transactionType)
  }
  
  if (filters.value.category) {
    result = result.filter(item => item.category === filters.value.category)
  }
  
  if (filters.value.search) {
    const query = filters.value.search.toLowerCase()
    result = result.filter(item => 
      item.description.toLowerCase().includes(query) ||
      item.account.toLowerCase().includes(query)
    )
  }
  
  // 按日期倒序排序
  result.sort((a, b) => new Date(b.date) - new Date(a.date))
  
  return result
})

// 格式化金额
const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
}

// 获取分类名称
const getCategoryName = (category) => {
  const categoryMap = {
    'tuition': '学费',
    'service': '服务费',
    'consulting': '咨询费',
    'salary': '工资',
    'rent': '房租',
    'utilities': '水电费',
    'other': '其他'
  }
  return categoryMap[category] || category
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    'completed': '已完成',
    'pending': '待处理',
    'canceled': '已取消'
  }
  return statusMap[status] || status
}

// 应用筛选
const applyFilters = () => {
  currentPage.value = 1
  // 实际应用筛选的逻辑已在 computed 中实现
}

// 重置筛选
const resetFilters = () => {
  filters.value = {
    dateRange: 'thisMonth',
    transactionType: '',
    category: '',
    search: ''
  }
  currentPage.value = 1
}

// 分页操作
const changePage = (page) => {
  currentPage.value = page
}

// 导出财务报表
const exportFinancialReport = () => {
  ElMessage.info('财务报表导出功能开发中')
}

// 查看交易详情
const viewTransaction = (transaction) => {
  console.log('查看交易详情', transaction)
  // 实现查看交易详情的逻辑
}

// 编辑交易
const editTransaction = (transaction) => {
  console.log('编辑交易', transaction)
  // 实现编辑交易的逻辑
}

// 删除交易
const deleteTransaction = (transaction) => {
  console.log('删除交易', transaction)
  // 实现删除交易的逻辑
  ElMessage.success('交易记录已删除')
}
</script>

<style scoped>
/* 财务管理页面样式 */
.finance-content {
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

.button-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
}

/* 财务概览卡片 */
.finance-summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.summary-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.summary-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #666;
  margin: 0;
}

.summary-amount {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 16px 0;
}

.summary-footer {
  font-size: 12px;
  color: #999;
}

.growth {
  color: #52c41a;
}

.income .summary-amount {
  color: #52c41a;
}

.expense .summary-amount {
  color: #f5222d;
}

.profit .summary-amount {
  color: #1890ff;
}

.pending .summary-amount {
  color: #fa8c16;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.select-wrapper {
  position: relative;
}

.select-wrapper select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  background-color: #fff;
  min-width: 140px;
  appearance: none;
  font-size: 14px;
  transition: all 0.3s;
}

.select-wrapper select:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #999;
  pointer-events: none;
}

.filter-search {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.search-input-wrapper {
  position: relative;
}

.search-input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #999;
}

.filter-search-input {
  padding: 8px 12px 8px 36px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  width: 100%;
  font-size: 14px;
  transition: all 0.3s;
}

.filter-search-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.filter-actions {
  display: flex;
  gap: 8px;
}

/* 交易表格 */
.transactions-table {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 14px;
  color: #666;
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.transaction-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.transaction-type.income {
  background-color: #f6ffed;
  color: #52c41a;
}

.transaction-type.expense {
  background-color: #fff1f0;
  color: #f5222d;
}

.income-amount {
  color: #52c41a;
  font-weight: 500;
}

.expense-amount {
  color: #f5222d;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-badge.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-badge.canceled {
  background-color: #f5f5f5;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-icon-sm:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.pagination-info {
  font-size: 14px;
  color: #666;
}

.pagination {
  display: flex;
  gap: 4px;
}

.pagination-button {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-button:disabled {
  cursor: not-allowed;
  color: #d9d9d9;
}

.pagination-button:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-number {
  min-width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.pagination-number:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-number.active {
  background-color: #1890ff;
  border-color: #1890ff;
  color: white;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .finance-summary {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .finance-summary {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-wrap: wrap;
  }
  
  .filter-group {
    flex: 1 0 calc(50% - 8px);
    min-width: 140px;
  }
  
  .filter-search {
    flex: 1 0 100%;
    margin-top: 16px;
  }
  
  .filter-actions {
    flex: 1 0 100%;
    justify-content: flex-end;
    margin-top: 16px;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 16px;
    align-items: center;
  }
  
  .pagination {
    order: -1;
  }
}
</style> 