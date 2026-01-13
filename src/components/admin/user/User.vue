<template>
  <div class="user-content">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">用户管理</h2>
        <p class="page-subtitle">管理系统老师账号、角色和权限</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="debugStatistics" title="刷新统计">
          <RefreshCw class="button-icon" />
          刷新统计
        </el-button>
        <el-button class="custom-button" type="default" @click="showImportModal = true">
          <Upload class="button-icon" />
          导入用户
        </el-button>
        <el-button class="custom-button" type="primary" @click="showAddUserModal = true">
          <UserPlus class="button-icon" />
          添加用户
        </el-button>
      </div>
    </div>

    <!-- 用户统计卡片 -->
    <div class="user-stats">
      <div class="stat-card">
        <div class="stat-icon" style="background-color: #e6f7ff; color: #1890ff;">
          <Users />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.totalUsers || 0 }}</h3>
          <p>用户总数</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background-color: #f6ffed; color: #52c41a;">
          <UserCheck />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.activeUsers || 0 }}</h3>
          <p>活跃用户</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background-color: #f0f9ff; color: #0070f3;">
          <Shield />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.superAdminUsers || 0 }}</h3>
          <p>超级管理员</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background-color: #fff7e6; color: #fa8c16;">
          <Settings />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.normalAdminUsers || 0 }}</h3>
          <p>普通管理员</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background-color: #fff0f6; color: #eb2f96;">
          <DollarSign />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.financialUsers || 0 }}</h3>
          <p>财务人员</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background-color: #f0f5ff; color: #2f54eb;">
          <BarChart2 />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.dataAnalystUsers || 0 }}</h3>
          <p>数据分析师</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background-color: #f9f0ff; color: #722ed1;">
          <UserX />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.inactiveUsers || 0 }}</h3>
          <p>未激活用户</p>
        </div>
      </div>
    </div>

    <!-- 用户表格 -->
    <div class="users-table">
      <table>
        <thead>
          <tr>
            <th>
              <input type="checkbox" class="table-checkbox" v-model="selectAll" @change="toggleSelectAll" />
            </th>
            <th>教师</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>最后登录</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="admin in admins" :key="admin.id">
            <td>
              <input type="checkbox" class="table-checkbox" v-model="admin.selected" @change="handleSelect" />
            </td>
            <td>
              <div class="user-cell">
                <div class="user-avatar">
                  <div class="avatar-placeholder" :style="{ backgroundColor: generateAvatarColor(admin.name) }">
                    {{ admin.name ? admin.name.charAt(0) : '?' }}
                  </div>
                </div>
                <div class="user-info">
                  <div class="user-name">{{ admin.name }}</div>
                  <div class="user-username">@{{ admin.username }}</div>
                </div>
              </div>
            </td>
            <td>{{ admin.email || '未设置' }}</td>
            <td>
              <span :class="['role-badge', admin.role]">{{ getRoleName(admin.role) }}</span>
            </td>
            <td>
              <span :class="['status-badge', getStatusClass(admin.status)]">{{ getStatusName(admin.status) }}</span>
            </td>
            <td>{{ formatDate(admin.lastLoginTime) }}</td>
            <td>
              <div class="action-buttons">
                <button class="btn-icon-sm" @click="viewAdmin(admin)" title="查看详情">
                  <Eye />
                </button>
                <button class="btn-icon-sm" @click="editAdmin(admin)" title="编辑">
                  <Edit />
                </button>
                <button class="btn-icon-sm" @click="deleteAdmin(admin)" title="删除">
                  <Trash2 />
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="admins.length === 0">
            <td colspan="7" class="no-data">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="table-footer">
      <div class="bulk-actions">
        <div class="select-wrapper">
          <select v-model="bulkAction" class="bulk-select">
            <option value="">批量操作</option>
            <option value="1">激活用户</option>
            <option value="2">禁用用户</option>
            <option value="delete">删除用户</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
        <button class="btn apply-btn" @click="applyBulkAction" :disabled="!bulkAction">应用</button>
      </div>

      <div class="pagination">
        <button class="pagination-button" :disabled="currentPage === 1" @click="changePage(1)">
          <ChevronsLeft />
        </button>
        <button class="pagination-button" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
          <ChevronLeft />
        </button>
        <button 
          v-for="page in displayedPages" 
          :key="page" 
          :class="['pagination-number', { active: currentPage === page }]"
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
    
    <!-- 添加用户模态框 -->
    <el-dialog v-model="showAddUserModal" title="添加教师" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="超级管理员" value="superAdmin" />
            <el-option label="普通管理员" value="normalAdmin" />
            <el-option label="咨询顾问" value="consultant" />
            <el-option label="财务人员" value="financial" />
            <el-option label="数据分析师" value="dataAnalyst" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="活跃" :value="1" />
            <el-option label="未激活" :value="0" />
            <el-option label="已禁用" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddUserModal = false">取消</el-button>
          <el-button type="primary" @click="submitAddAdmin">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑用户模态框 -->
    <el-dialog v-model="showEditUserModal" title="编辑教师" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="formData.username" placeholder="请输入用户名" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="超级管理员" value="superAdmin" />
            <el-option label="普通管理员" value="normalAdmin" />
            <el-option label="咨询顾问" value="consultant" />
            <el-option label="财务人员" value="financial" />
            <el-option label="数据分析师" value="dataAnalyst" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="活跃" :value="1" />
            <el-option label="未激活" :value="0" />
            <el-option label="已禁用" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditUserModal = false">取消</el-button>
          <el-button type="primary" @click="submitEditAdmin">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 导入用户模态框 -->
    <el-dialog v-model="showImportModal" title="导入教师" width="500px">
      <el-upload
        class="upload-demo"
        drag
        action="/api/adminuser/import"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
      >
        <div class="el-upload__text">
          <Upload style="width: 48px; height: 48px; color: #1890ff; margin-bottom: 8px" />
          <div>将文件拖到此处，或<em>点击上传</em></div>
          <div class="upload-tip">支持 .xlsx, .xls 格式文件</div>
        </div>
      </el-upload>
      <div class="import-template">
        <p>请先下载导入模板：</p>
        <el-button type="primary" @click="downloadTemplate">下载模板</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import {
  Upload,
  UserPlus,
  Users,
  UserCheck,
  Shield,
  UserX,
  ChevronDown,
  Search,
  Filter,
  RefreshCw,
  Eye,
  Edit,
  Trash2,
  ChevronsLeft,
  ChevronLeft,
  ChevronRight,
  ChevronsRight,
  Briefcase,
  Award,
  Clock,
  Settings,
  MessageCircle,
  DollarSign,
  BarChart2
} from 'lucide-vue-next'

// 页面数据
const admins = ref([])
const statistics = ref({
  totalUsers: 0,
  activeUsers: 0,
  superAdminUsers: 0,
  normalAdminUsers: 0,
  consultantUsers: 0,
  financialUsers: 0,
  dataAnalystUsers: 0,
  inactiveUsers: 0
})

// 筛选条件
const filters = ref({
  role: '',
  status: '',
  search: ''
})

// 模态框状态
const showAddUserModal = ref(false)
const showEditUserModal = ref(false)
const showImportModal = ref(false)

// 表单数据
const formData = ref({
  id: null,
  username: '',
  name: '',
  email: '',
  password: '',
  role: 'normalAdmin',
  status: 1
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))
const displayedPages = computed(() => {
  const pages = []
  const maxPages = 5
  let start = Math.max(1, currentPage.value - 2)
  let end = Math.min(totalPages.value, start + maxPages - 1)
  
  if (end - start + 1 < maxPages) {
    start = Math.max(1, end - maxPages + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

// 批量操作
const selectAll = ref(false)
const bulkAction = ref('')

// 初始化
onMounted(async () => {
  await fetchStatistics()
  await fetchAdmins()
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await axios.get('/api/adminuser/statistics')
    if (response.data.code === 200) {
      statistics.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取管理员/教师列表
const fetchAdmins = async () => {
  try {
    const params = {
      ...filters.value,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await axios.post('/api/adminuser/filter', params)
    if (response.data.code === 200) {
      const data = response.data.data.data || []
      // 添加selected属性用于批量选择
      admins.value = data.map(admin => ({
        ...admin,
        selected: false
      }))
      totalItems.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取教师列表失败')
    }
  } catch (error) {
    console.error('获取教师列表失败', error)
    ElMessage.error('获取教师列表失败')
  }
}

// 生成头像颜色
const generateAvatarColor = (name) => {
  if (!name) return 'hsl(0, 70%, 60%)'
  
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  
  const hue = Math.abs(hash % 360)
  return `hsl(${hue}, 70%, 60%)`
}

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    // 新的驼峰格式
    'superAdmin': '超级管理员',
    'normalAdmin': '普通管理员',
    'consultant': '咨询顾问',
    'financial': '财务人员',
    'dataAnalyst': '数据分析师',
    'admin': '管理员',
    'teacher': '教师',
    'counselor': '留学顾问',
    'supervisor': '系主任',
    'other': '其他',
    // 兼容旧格式
    'super_admin': '超级管理员',
    'normal_admin': '普通管理员',
    'data_analyst': '数据分析师'
  }
  return roleMap[role] || role
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '活跃',
    0: '未激活',
    2: '已禁用'
  }
  return statusMap[status] || '未知'
}

// 获取状态CSS类
const getStatusClass = (status) => {
  const statusMap = {
    1: 'active',
    0: 'inactive',
    2: 'disabled'
  }
  return statusMap[status] || ''
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '从未登录'
  
  try {
    // 尝试转换为Date对象
    const dateObj = new Date(date)
    
    // 检查日期是否有效
    if (isNaN(dateObj.getTime())) {
      return '无效日期'
    }
    
    // 格式化为 YYYY-MM-DD HH:mm
    const year = dateObj.getFullYear()
    const month = String(dateObj.getMonth() + 1).padStart(2, '0')
    const day = String(dateObj.getDate()).padStart(2, '0')
    const hours = String(dateObj.getHours()).padStart(2, '0')
    const minutes = String(dateObj.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (e) {
    console.error('日期格式化错误:', e)
    return '无效日期'
  }
}

// 全选/取消全选
const toggleSelectAll = () => {
  admins.value.forEach(admin => {
    admin.selected = selectAll.value
  })
}

// 处理单个选择
const handleSelect = () => {
  selectAll.value = admins.value.every(admin => admin.selected)
}

// 应用筛选器
const applyFilters = async () => {
  currentPage.value = 1
  await fetchAdmins()
}

// 重置筛选器
const resetFilters = async () => {
  filters.value = {
    role: '',
    status: '',
    search: ''
  }
  currentPage.value = 1
  await fetchAdmins()
}

// 分页操作
const changePage = (page) => {
  currentPage.value = page
}

// 应用批量操作
const applyBulkAction = async () => {
  const selectedAdmins = admins.value.filter(admin => admin.selected)
  
  if (selectedAdmins.length === 0) {
    ElMessage.warning('请先选择教师')
    return
  }
  
  if (bulkAction.value === 'delete') {
    ElMessageBox.confirm('确定要删除选中的教师吗？此操作不可逆', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        // 逐个删除选中的用户
        for (const admin of selectedAdmins) {
          await axios.delete(`/api/adminuser/${admin.id}`)
        }
        ElMessage.success(`已删除 ${selectedAdmins.length} 个教师`)
        await fetchAdmins()
        await fetchStatistics()
      } catch (error) {
        console.error('批量删除失败', error)
        ElMessage.error('批量删除失败')
      }
    }).catch(() => {})
  } else {
    try {
      const adminIds = selectedAdmins.map(admin => admin.id)
      const status = parseInt(bulkAction.value)
      
      const response = await axios.post('/api/adminuser/batch-update-status', {
        adminIds,
        status
      })
      
      if (response.data.code === 200) {
        const count = response.data.data
        const action = status === 1 ? '激活' : '禁用'
        ElMessage.success(`已${action} ${count} 个教师`)
        await fetchAdmins()
        await fetchStatistics()
      } else {
        ElMessage.error(response.data.message || '操作失败')
      }
    } catch (error) {
      console.error('批量操作失败', error)
      ElMessage.error('批量操作失败')
    }
  }
  
  bulkAction.value = ''
  selectAll.value = false
}

// 查看用户
const viewAdmin = (admin) => {
  // TODO: 实现查看详情
  console.log('查看教师', admin)
}

// 编辑用户
const editAdmin = (admin) => {
  formData.value = { ...admin }
  showEditUserModal.value = true
}

// 提交编辑
const submitEditAdmin = async () => {
  try {
    const response = await axios.post('/api/adminuser/update', formData.value)
    if (response.data.code === 200) {
      ElMessage.success('教师信息更新成功')
      showEditUserModal.value = false
      await fetchStatistics()
      await fetchAdmins()
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新教师信息失败', error)
    ElMessage.error('更新教师信息失败')
  }
}

// 删除用户
const deleteAdmin = (admin) => {
  ElMessageBox.confirm(`确定要删除教师 ${admin.name} 吗？此操作不可逆`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.delete(`/api/adminuser/${admin.id}`)
      if (response.data.code === 200) {
        ElMessage.success('教师删除成功')
        await fetchStatistics()
        await fetchAdmins()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除教师失败', error)
      ElMessage.error('删除教师失败')
    }
  }).catch(() => {})
}

// 提交添加
const submitAddAdmin = async () => {
  try {
    const response = await axios.post('/api/adminuser/add', formData.value)
    if (response.data.code === 200) {
      ElMessage.success('教师添加成功')
      showAddUserModal.value = false
      formData.value = {
        username: '',
        name: '',
        email: '',
        password: '',
        role: 'normalAdmin',
        status: 1
      }
      await fetchStatistics()
      await fetchAdmins()
    } else {
      ElMessage.error(response.data.message || '添加失败')
    }
  } catch (error) {
    console.error('添加教师失败', error)
    ElMessage.error('添加教师失败')
  }
}

// 处理导入成功
const handleImportSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('导入成功')
    showImportModal.value = false
    fetchStatistics()
    fetchAdmins()
  } else {
    ElMessage.error(response.message || '导入失败')
  }
}

// 处理导入失败
const handleImportError = () => {
  ElMessage.error('导入失败')
}

// 下载模板
const downloadTemplate = () => {
  window.location.href = '/api/adminuser/template/download'
}

// 添加调试统计功能
const debugStatistics = async () => {
  try {
    await fetchStatistics()
    console.log('刷新后的统计数据:', statistics.value)
    ElMessage.success('统计数据已刷新')
  } catch (error) {
    console.error('刷新统计数据失败', error)
    ElMessage.error('刷新统计数据失败')
  }
}
</script>

<style scoped>
/* 用户管理页面样式 */
.user-content {
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

/* 统计卡片 */
.user-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info h3 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #333;
}

.stat-info p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 用户表格 */
.users-table {
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
  color: #666;
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.table-checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.user-username {
  font-size: 12px;
  color: #999;
}

.role-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.role-badge.super_admin, .role-badge.superAdmin {
  background-color: #f0f9ff;
  color: #0070f3;
}

.role-badge.normal_admin, .role-badge.normalAdmin {
  background-color: #fff7e6;
  color: #fa8c16;
}

.role-badge.consultant {
  background-color: #e6fffb;
  color: #13c2c2;
}

.role-badge.financial {
  background-color: #fff0f6;
  color: #eb2f96;
}

.role-badge.data_analyst, .role-badge.dataAnalyst {
  background-color: #f0f5ff;
  color: #2f54eb;
}

.role-badge.admin {
  background-color: #fff7e6;
  color: #fa8c16;
}

.role-badge.teacher {
  background-color: #e6f7ff;
  color: #1890ff;
}

.role-badge.counselor {
  background-color: #e6fffb;
  color: #13c2c2;
}

.role-badge.supervisor {
  background-color: #fcffe6;
  color: #a0d911;
}

.role-badge.other {
  background-color: #f9f0ff;
  color: #722ed1;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-badge.active {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.inactive {
  background-color: #f5f5f5;
  color: #999;
}

.status-badge.disabled {
  background-color: #fff1f0;
  color: #f5222d;
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

/* 表格底部 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.bulk-actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 8px 16px;
  border-radius: 8px;
  background-color: #1890ff;
  color: white;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:hover {
  background-color: #40a9ff;
}

.btn:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  gap: 4px;
}

.pagination-button {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-button:disabled {
  color: #999;
  cursor: not-allowed;
}

.pagination-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.pagination-button:hover:not(:disabled),
.pagination-number:hover:not(.active) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-number.active {
  background-color: #1890ff;
  border-color: #1890ff;
  color: white;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 24px 0;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.import-template {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px dashed #e8e8e8;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .user-stats {
    grid-template-columns: repeat(2, 1fr);
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
}

@media (max-width: 768px) {
  .user-stats {
    grid-template-columns: 1fr;
  }
  
  .table-footer {
    flex-direction: column;
    gap: 16px;
  }
  
  .bulk-actions, .pagination {
    width: 100%;
  }
  
  .bulk-actions {
    justify-content: space-between;
  }
  
  .pagination {
    justify-content: center;
  }
}

/* 批量操作相关样式 */
.select-wrapper {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f0f2f5;
}

.bulk-select {
  width: 150px;
  padding: 8px 12px;
  appearance: none;
  border: none;
  background-color: transparent;
  font-size: 14px;
  cursor: pointer;
  outline: none;
  padding-right: 30px;
}

.select-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  pointer-events: none;
}

.apply-btn {
  background-color: #1890ff;
  border-radius: 8px;
  transition: all 0.3s;
  font-weight: 500;
}

.apply-btn:hover {
  background-color: #096dd9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.apply-btn:disabled {
  background-color: #d9d9d9;
  color: #999;
}
</style> 