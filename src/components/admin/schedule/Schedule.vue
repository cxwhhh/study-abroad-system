<template>
  <div class="schedule-container">
    <!-- 顶部筛选工具栏 -->
    <FilterBar
      :availableTypes="scheduleTypes"
      @search="handleSearch"
      @filter-change="handleFilterChange"
      class="filter-bar"
    />

          <div class="schedule-content">
      <!-- 日历组件 -->
    <div class="calendar-section">
        <CalendarView
          :schedules="filteredSchedules"
          :selectedDate="selectedDate"
          @date-select="handleDateSelect"
        />
      </div>

      <!-- 日程列表组件 -->
      <div class="list-section">
        <ScheduleList
          :schedules="schedulesForSelectedDate"
          :date="selectedDate"
          :isDateSelected="Boolean(selectedDate)"
          title="日程列表"
          empty-text="暂无日程安排"
          @view="handleViewSchedule"
          @edit="handleEditSchedule"
          @delete="handleDeleteSchedule"
          @add="handleAddSchedule"
        />
      </div>
    </div>

    <!-- 日程详情模态框 -->
    <ScheduleDetails
      :visible="detailsModalVisible"
      :schedule="currentSchedule"
      @close="closeDetailsModal"
      @edit="handleEditFromDetails"
      @delete="handleDeleteFromDetails"
    />

    <!-- 添加/编辑日程模态框 -->
    <ScheduleForm
      :visible="formModalVisible"
      :schedule="currentSchedule"
      @close="closeFormModal"
      @save="saveSchedule"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FilterBar from './FilterBar.vue'
import CalendarView from './CalendarView.vue'
import ScheduleList from './ScheduleList.vue'
import ScheduleDetails from './ScheduleDetails.vue'
import ScheduleForm from './ScheduleForm.vue'

// 日程数据
const schedules = ref([])
const scheduleTypes = ['会议', '面试', '课程', '其他']

// 日程筛选和搜索状态
const filters = ref({
  important: false,
  types: [],
  dateRange: {
    start: '',
    end: ''
  }
})
const searchQuery = ref('')

// 日期选择状态
const selectedDate = ref(new Date().toISOString().split('T')[0])

// 模态框状态
const detailsModalVisible = ref(false)
const formModalVisible = ref(false)
const currentSchedule = ref({})

// 初始化数据
onMounted(() => {
  // 模拟获取日程数据
  fetchSchedules()
})

// 获取日程数据
const fetchSchedules = () => {
  // 这里模拟异步获取数据
  setTimeout(() => {
    // 模拟数据
    schedules.value = [
  {
    id: 1,
        title: '产品研讨会',
        date: new Date().toISOString().split('T')[0], // 使用今天的日期
        time: '10:00',
    type: '会议',
        description: '讨论新产品功能和路线图',
        important: true,
        location: '会议室A',
        participants: '张三,李四,王五',
        reminder: '15min'
  },
  {
    id: 2,
        title: '候选人面试',
        date: new Date().toISOString().split('T')[0], // 使用今天的日期
        time: '14:30',
    type: '面试',
        description: '前端开发工程师岗位',
        important: true,
        location: '会议室B',
        participants: '张三,HR',
        reminder: '30min'
  },
  {
    id: 3,
        title: '项目周会',
        date: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0], // 明天
        time: '09:30',
    type: '会议',
        description: '项目进展汇报',
        important: false,
        location: '线上会议',
        participants: '张三,李四,王五,赵六',
        reminder: 'none'
  },
  {
    id: 4,
        title: 'Vue.js课程培训',
        date: new Date(new Date().setDate(new Date().getDate() + 2)).toISOString().split('T')[0], // 后天
        time: '13:00',
        type: '课程',
        description: 'Vue.js基础到高级的培训课程',
        important: false,
        location: '培训室',
        participants: '全体前端',
        reminder: '1hour'
      },
      {
        id: 5,
        title: '客户会议',
        date: new Date(new Date().setDate(new Date().getDate() - 1)).toISOString().split('T')[0], // 昨天
        time: '15:00',
    type: '会议',
        description: '与客户讨论需求',
        important: true,
        location: '会议室C',
        participants: '张三,客户代表',
        reminder: '15min'
      }
    ]
  }, 500)
}

// 根据筛选条件和搜索过滤日程
const filteredSchedules = computed(() => {
  return schedules.value.filter(schedule => {
    // 搜索条件
    if (searchQuery.value && 
        !schedule.title.toLowerCase().includes(searchQuery.value.toLowerCase()) && 
        !schedule.description?.toLowerCase().includes(searchQuery.value.toLowerCase())) {
      return false
    }

    // 重要性筛选
    if (filters.value.important && !schedule.important) {
      return false
    }

    // 类型筛选
    if (filters.value.types.length > 0 && !filters.value.types.includes(schedule.type)) {
      return false
    }

    // 日期范围筛选
    if (filters.value.dateRange.start && new Date(schedule.date) < new Date(filters.value.dateRange.start)) {
      return false
    }
    if (filters.value.dateRange.end && new Date(schedule.date) > new Date(filters.value.dateRange.end)) {
      return false
    }

    return true
  })
})

// 获取选定日期的日程
const schedulesForSelectedDate = computed(() => {
  return filteredSchedules.value.filter(schedule => 
    schedule.date === selectedDate.value
  ).sort((a, b) => a.time.localeCompare(b.time))
})

// 处理日期选择
const handleDateSelect = (date) => {
  selectedDate.value = date
}

// 处理搜索
const handleSearch = (query) => {
  searchQuery.value = query
}

// 处理筛选条件变化
const handleFilterChange = (newFilters) => {
  filters.value = newFilters
}

// 查看日程
const handleViewSchedule = (schedule) => {
  currentSchedule.value = schedule
  detailsModalVisible.value = true
}

// 添加日程
const handleAddSchedule = () => {
  currentSchedule.value = {
    date: selectedDate.value
  }
  formModalVisible.value = true
}

// 编辑日程
const handleEditSchedule = (schedule) => {
  currentSchedule.value = { ...schedule }
  formModalVisible.value = true
}

// 从详情页编辑日程
const handleEditFromDetails = (schedule) => {
  detailsModalVisible.value = false
  currentSchedule.value = { ...schedule }
  formModalVisible.value = true
}

// 删除日程
const handleDeleteSchedule = (schedule) => {
  confirmDelete(schedule)
}

// 从详情页删除日程
const handleDeleteFromDetails = (schedule) => {
  detailsModalVisible.value = false
  confirmDelete(schedule)
}

// 确认删除
const confirmDelete = (schedule) => {
  ElMessageBox.confirm(
    `确定要删除日程 "${schedule.title}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      deleteSchedule(schedule.id)
    })
    .catch(() => {})
}

// 执行删除操作
const deleteSchedule = (id) => {
  // 这里应该是发送请求到服务器删除数据
  // 这里模拟删除操作
  schedules.value = schedules.value.filter(item => item.id !== id)
  ElMessage({
    type: 'success',
    message: '删除成功',
  })
}

// 保存日程
const saveSchedule = (scheduleData) => {
  if (scheduleData.id) {
    // 更新已有日程
    const index = schedules.value.findIndex(item => item.id === scheduleData.id)
    if (index !== -1) {
      schedules.value[index] = { ...scheduleData }
      ElMessage({
        type: 'success',
        message: '日程更新成功',
      })
    }
  } else {
    // 创建新日程
    // 生成新的ID
    const newId = Math.max(0, ...schedules.value.map(s => s.id)) + 1
    const newSchedule = {
      ...scheduleData,
      id: newId
    }
    schedules.value.push(newSchedule)
    ElMessage({
      type: 'success',
      message: '日程创建成功',
    })
  }
  closeFormModal()
}

// 关闭详情模态框
const closeDetailsModal = () => {
  detailsModalVisible.value = false
}

// 关闭表单模态框
const closeFormModal = () => {
  formModalVisible.value = false
  currentSchedule.value = {}
}
</script>

<style scoped>
.schedule-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background-color: #f9fafc;
  min-height: calc(100vh - 48px);
}

.filter-bar {
  border-radius: 16px;
  background: linear-gradient(to right, #ffffff, #f0f7ff);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(231, 241, 255, 0.8);
  backdrop-filter: blur(10px);
}

.schedule-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
  height: calc(100vh - 200px);
}

@media (min-width: 1024px) {
  .schedule-content {
    flex-direction: row;
    align-items: stretch;
  }
  
  .calendar-section {
    flex: 3;
    border-radius: 16px;
    background: linear-gradient(145deg, #ffffff, #f8fbff);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(231, 241, 255, 0.8);
    transition: all 0.3s ease;
    height: 100%;
    overflow: hidden;
  }
  
  .calendar-section:hover {
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
  
  .list-section {
    flex: 2;
    border-radius: 16px;
    background: linear-gradient(145deg, #ffffff, #f8fbff);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(231, 241, 255, 0.8);
    transition: all 0.3s ease;
    height: 100%;
    overflow: auto;
    backdrop-filter: blur(10px);
  }
  
  .list-section:hover {
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
}
</style> 