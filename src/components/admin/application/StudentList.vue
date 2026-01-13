<template>
  <div class="students-list-panel">
    <div class="students-list">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="filteredStudentsAll.length === 0" class="empty-list">
        <UserX class="empty-icon" />
        <p>无符合条件的学生</p>
      </div>
      <div v-else>
        <div
          v-for="student in filteredStudents"
          :key="student.id"
          class="student-list-item"
          :class="{ active: selectedStudent === student.name }"
          @click="selectStudent(student)"
        >
          <div class="student-avatar">
            <div
              class="avatar-text"
              :style="{ backgroundColor: props.generateAvatarColorFn(student.name) }"
            >
              {{ getInitials(student.name) }}
            </div>
          </div>
          <div class="student-info">
            <div class="student-name">{{ student.name }}</div>
            <div class="student-detail">{{ student.currentSchool || student.major || '暂无详细信息' }}</div>
          </div>
        </div>
        
        <!-- 分页控制 -->
        <div v-if="totalPages > 1" class="pagination-controls">
          <button class="page-button" :disabled="currentPage === 1" @click="prevPage">
            上一页
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button class="page-button" :disabled="currentPage === totalPages" @click="nextPage">
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search, UserX } from 'lucide-vue-next'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const props = defineProps({
  selectedStudent: {
    type: String,
    default: ''
  },
  generateAvatarColorFn: {
    type: Function,
    required: true
  },
  // 新增筛选参数
  filters: {
    type: Object,
    default: () => ({
      status: '',
      country: '',
      degree: ''
    })
  },
  applications: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['select-student'])

const students = ref([])
const searchQuery = ref('')
const loading = ref(false)
const searchTimeout = ref(null) // 用于防抖处理

// 获取学生列表
const fetchStudents = async () => {
  try {
    loading.value = true
    const res = await request.get('/student/admin/students')
    students.value = res?.data || []
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 带防抖功能的过滤学生
const filterStudents = () => {
  // 清除之前的超时调用
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  // 设置新的超时调用（300毫秒防抖）
  searchTimeout.value = setTimeout(() => {
    // 防抖后的处理逻辑在这里，但由于我们使用了计算属性，这里不需要额外的代码
    console.log('搜索查询:', searchQuery.value)
  }, 300)
}

// 计算过滤后的学生列表 - 添加分页
const pageSize = ref(30) // 每页显示30个学生
const currentPage = ref(1)

// 计算总页数
const totalPages = computed(() => {
  const filteredCount = filteredStudentsAll.value.length
  return Math.ceil(filteredCount / pageSize.value)
})

// 所有过滤后的学生（不分页）
const filteredStudentsAll = computed(() => {
  let filtered = students.value;
  
  // 如果有筛选条件，根据学生的申请进行筛选
  if (props.filters.status || props.filters.country || props.filters.degree) {
    filtered = students.value.filter(student => {
      // 获取该学生的所有申请
      const studentApps = props.applications.filter(app => 
        app.student && Number(app.student.id) === Number(student.id)
      );
      
      // 如果学生没有申请，不显示
      if (studentApps.length === 0) return false;
      
      // 检查是否有申请符合筛选条件
      return studentApps.some(app => {
        let matches = true;
        
        if (props.filters.status && app.status !== props.filters.status) {
          matches = false;
        }
        
        if (props.filters.country && app.country !== props.filters.country) {
          matches = false;
        }
        
        if (props.filters.degree && app.degree !== props.filters.degree) {
          matches = false;
        }
        
        return matches;
      });
    });
  }
  
  // 如果有搜索查询，进一步过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(student => 
      student.name.toLowerCase().includes(query) || 
      (student.currentSchool && student.currentSchool.toLowerCase().includes(query)) ||
      (student.major && student.major.toLowerCase().includes(query))
    );
  }
  
  return filtered;
})

// 当前页的学生（分页后）
const filteredStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredStudentsAll.value.slice(start, end)
})

// 监听搜索查询变化，重置分页
watch([searchQuery, () => props.filters], () => {
  currentPage.value = 1
}, { deep: true })

// 分页处理函数
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

// 获取姓名首字母
const getInitials = (name) => {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

// 选择学生
const selectStudent = (student) => {
  emit('select-student', student)
}

// 组件加载时获取学生列表
onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.students-list-panel {
  flex: 0 0 300px;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 16px;
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.search-wrapper {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 0 12px;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 8px 8px 8px 32px;
  border: none;
  background-color: transparent;
  font-size: 14px;
  outline: none;
}

.search-icon {
  position: absolute;
  left: 10px;
  color: #999;
  width: 16px;
  height: 16px;
}

.students-list {
  padding: 8px;
  overflow-y: auto;
  flex: 1;
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.students-list::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.student-list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin-bottom: 4px;
  border: 2px solid transparent;
  position: relative;
}

.student-list-item:hover {
  background-color: #f5f7fa;
  border-color: #e1e8ed;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.student-list-item.active {
  background-color: #e6f7ff;
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
  transform: translateY(-2px);
}

.student-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
  color: white;
}

.student-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
}

.student-name {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.student-detail {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}

.empty-icon {
  width: 48px;
  height: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.loading-spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1890ff;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .students-list-panel {
    flex: 1;
    height: auto;
    max-height: 300px;
  }
}

/* 添加分页控制样式 */
.pagination-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 0;
  gap: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}

.page-button {
  padding: 4px 12px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  color: #333;
}

.page-button:hover:not(:disabled) {
  background-color: #e6f7ff;
  color: #1890ff;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 12px;
  color: #666;
}
</style>
