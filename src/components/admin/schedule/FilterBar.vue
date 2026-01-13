<template>
  <div class="filter-bar">
    <div class="filter-section">
      <div class="search-box">
        <Search class="search-icon" />
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索日程..."
          @input="onSearch"
        />
        <button
          v-if="searchQuery"
          class="clear-button"
          @click="clearSearch"
        >
          <X class="clear-icon" />
        </button>
      </div>
    </div>
    
    <div class="filter-section">
      <div class="filter-buttons">
        <span class="filter-label">筛选：</span>
        
        <button
          class="filter-btn"
          :class="{ active: filters.important }"
          @click="toggleFilter('important')"
        >
          <Star :class="['filter-icon', { filled: filters.important }]" />
          重要
        </button>
        
        <div class="types-dropdown">
          <button
            class="filter-btn"
            :class="{ active: hasTypeFilters }"
            @click="toggleTypeDropdown"
          >
            <Tag class="filter-icon" />
            类型
            <ChevronDown class="dropdown-icon" />
          </button>
          
          <div v-if="typeDropdownOpen" class="dropdown-menu">
            <div
              v-for="type in availableTypes"
              :key="type"
              class="dropdown-item"
              @click="toggleTypeFilter(type)"
            >
              <div class="checkbox">
                <input
                  type="checkbox"
                  :id="`type-${type}`"
                  :checked="filters.types.includes(type)"
                  @change="toggleTypeFilter(type)"
                />
                <label :for="`type-${type}`">{{ type }}</label>
              </div>
            </div>
            <div class="dropdown-actions">
              <button class="dropdown-btn" @click="clearTypeFilters">清除</button>
            </div>
          </div>
        </div>
        
        <div class="date-dropdown">
          <button
            class="filter-btn"
            :class="{ active: filters.dateRange.start || filters.dateRange.end }"
            @click="toggleDateDropdown"
          >
            <Calendar class="filter-icon" />
            日期范围
            <ChevronDown class="dropdown-icon" />
          </button>
          
          <div v-if="dateDropdownOpen" class="dropdown-menu date-menu">
            <div class="date-range">
              <div class="date-input">
                <label>开始日期</label>
                <input
                  type="date"
                  v-model="filters.dateRange.start"
                  @change="onDateRangeChange"
                />
              </div>
              <div class="date-input">
                <label>结束日期</label>
                <input
                  type="date"
                  v-model="filters.dateRange.end"
                  @change="onDateRangeChange"
                />
              </div>
            </div>
            <div class="dropdown-actions">
              <button class="dropdown-btn" @click="clearDateRange">清除</button>
            </div>
          </div>
        </div>
        
        <button
          v-if="hasActiveFilters"
          class="clear-filters-btn"
          @click="clearAllFilters"
        >
          清除所有筛选
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import {
  Search,
  X,
  Star,
  Tag,
  Calendar,
  ChevronDown
} from 'lucide-vue-next'

const props = defineProps({
  availableTypes: {
    type: Array,
    default: () => ['会议', '面试', '课程', '其他']
  }
})

const emit = defineEmits(['filter-change', 'search'])

// 筛选状态
const filters = ref({
  important: false,
  types: [],
  dateRange: {
    start: '',
    end: ''
  }
})

// 搜索状态
const searchQuery = ref('')
const typeDropdownOpen = ref(false)
const dateDropdownOpen = ref(false)

// 计算是否有活跃的类型筛选
const hasTypeFilters = computed(() => {
  return filters.value.types.length > 0
})

// 计算是否有任何活跃筛选
const hasActiveFilters = computed(() => {
  return (
    filters.value.important ||
    hasTypeFilters.value ||
    filters.value.dateRange.start ||
    filters.value.dateRange.end
  )
})

// 搜索操作
const onSearch = () => {
  emit('search', searchQuery.value)
}

// 清除搜索
const clearSearch = () => {
  searchQuery.value = ''
  emit('search', '')
}

// 切换筛选状态
const toggleFilter = (filter) => {
  filters.value[filter] = !filters.value[filter]
  emitFilterChange()
}

// 切换类型筛选
const toggleTypeFilter = (type) => {
  const index = filters.value.types.indexOf(type)
  if (index === -1) {
    filters.value.types.push(type)
  } else {
    filters.value.types.splice(index, 1)
  }
  emitFilterChange()
}

// 切换类型下拉菜单
const toggleTypeDropdown = () => {
  typeDropdownOpen.value = !typeDropdownOpen.value
  if (typeDropdownOpen.value) {
    dateDropdownOpen.value = false
    nextTick(() => {
      positionDropdownMenu('types-dropdown');
    })
  }
}

// 切换日期下拉菜单
const toggleDateDropdown = () => {
  dateDropdownOpen.value = !dateDropdownOpen.value
  if (dateDropdownOpen.value) {
    typeDropdownOpen.value = false
    nextTick(() => {
      positionDropdownMenu('date-dropdown');
    })
  }
}

// 定位下拉菜单
const positionDropdownMenu = (dropdownClass) => {
  const btn = document.querySelector(`.${dropdownClass} .filter-btn`);
  const menu = document.querySelector(`.${dropdownClass} .dropdown-menu`);
  if (!btn || !menu) return;
  
  const btnRect = btn.getBoundingClientRect();
  
  // 设置菜单位置
  if (dropdownClass === 'date-dropdown') {
    menu.style.top = `${btnRect.bottom + 5}px`;
    menu.style.right = `${window.innerWidth - btnRect.right}px`;
    menu.style.left = 'auto';
  } else {
    menu.style.top = `${btnRect.bottom + 5}px`;
    menu.style.left = `${btnRect.left}px`;
  }
}

// 日期范围变化
const onDateRangeChange = () => {
  emitFilterChange()
}

// 清除类型筛选
const clearTypeFilters = () => {
  filters.value.types = []
  emitFilterChange()
}

// 清除日期范围
const clearDateRange = () => {
  filters.value.dateRange.start = ''
  filters.value.dateRange.end = ''
  emitFilterChange()
}

// 清除所有筛选
const clearAllFilters = () => {
  filters.value.important = false
  filters.value.types = []
  filters.value.dateRange.start = ''
  filters.value.dateRange.end = ''
  emitFilterChange()
}

// 向父组件发送筛选变化
const emitFilterChange = () => {
  emit('filter-change', { ...filters.value })
}

// 点击外部时关闭下拉菜单
const handleClickOutside = (event) => {
  const typeDropdownElement = document.querySelector('.types-dropdown')
  const dateDropdownElement = document.querySelector('.date-dropdown')
  
  if (typeDropdownElement && !typeDropdownElement.contains(event.target)) {
    typeDropdownOpen.value = false
  }
  
  if (dateDropdownElement && !dateDropdownElement.contains(event.target)) {
    dateDropdownOpen.value = false
  }
}

// 组件挂载和卸载时添加/移除事件监听
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('resize', handleResize)
})

// 窗口大小改变时重新定位
const handleResize = () => {
  if (typeDropdownOpen.value) {
    positionDropdownMenu('types-dropdown')
  }
  if (dateDropdownOpen.value) {
    positionDropdownMenu('date-dropdown')
  }
}
</script>

<style scoped>
.filter-bar {
  background-color: transparent;
  border-radius: 16px;
  box-shadow: none;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

@media (min-width: 768px) {
  .filter-bar {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
}

.filter-section {
  display: flex;
  align-items: center;
}

.search-box {
  position: relative;
  width: 100%;
}

@media (min-width: 768px) {
  .search-box {
    width: 300px;
  }
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #64748b;
}

.search-box input {
  width: 100%;
  height: 44px;
  padding: 8px 40px 8px 46px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background-color: #f8fafc;
  font-size: 15px;
  color: #1e293b;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.02);
}

.search-box input::placeholder {
  color: #94a3b8;
}

.search-box input:focus {
  border-color: #3b82f6;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  outline: none;
}

.clear-button {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  color: #94a3b8;
  border-radius: 50%;
  transition: all 0.2s;
}

.clear-button:hover {
  background-color: #f1f5f9;
  color: #475569;
}

.clear-icon {
  width: 14px;
  height: 14px;
}

.filter-buttons {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-label {
  font-size: 15px;
  font-weight: 500;
  color: #475569;
  margin-right: 6px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background-color: #f8fafc;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

.filter-btn:hover {
  border-color: #cbd5e1;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.03);
}

.filter-btn.active {
  background-color: #f0f7ff;
  border-color: #bfdbfe;
  color: #3b82f6;
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.08);
}

.filter-icon {
  width: 16px;
  height: 16px;
  transition: all 0.2s;
}

.filter-icon.filled {
  fill: #3b82f6;
  color: #3b82f6;
}

.dropdown-icon {
  width: 14px;
  height: 14px;
  margin-left: 2px;
  transition: transform 0.2s;
}

.types-dropdown, .date-dropdown {
  position: relative;
}

.dropdown-menu {
  position: fixed;
  margin-top: 10px;
  z-index: 9999;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.15);
  min-width: 200px;
  overflow: hidden;
  animation: fadeIn 0.2s ease-out;
  border: 1px solid #e2e8f0;
}

.date-menu {
  width: 320px;
}

.dropdown-item {
  padding: 10px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f8fafc;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 10px;
}

.checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #3b82f6;
}

.checkbox label {
  font-size: 14px;
  color: #475569;
  cursor: pointer;
}

.date-range {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.date-input {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.date-input label {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.date-input input {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  font-size: 14px;
  background-color: #f8fafc;
  color: #1e293b;
  transition: all 0.2s;
}

.date-input input:focus {
  border-color: #3b82f6;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  outline: none;
}

.dropdown-actions {
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f1f5f9;
  margin-top: 6px;
}

.dropdown-btn {
  background: none;
  border: none;
  color: #3b82f6;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.2s;
}

.dropdown-btn:hover {
  background-color: #f0f7ff;
}

.clear-filters-btn {
  margin-left: 8px;
  padding: 8px 14px;
  font-size: 14px;
  font-weight: 500;
  color: #ef4444;
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.clear-filters-btn:hover {
  background-color: #fee2e2;
  border-color: #fecaca;
}

.clear-filters-btn::before {
  content: "×";
  font-size: 18px;
  line-height: 1;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>