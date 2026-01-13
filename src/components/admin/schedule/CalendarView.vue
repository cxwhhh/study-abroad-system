<template>
  <div class="calendar-section">
    <div class="section-header">
      <h3 class="section-title">
        <CalendarDays class="section-icon" />
        日历视图
      </h3>
      <div class="calendar-controls">
        <button class="calendar-nav-button" @click="previousMonth">
          <ChevronLeft />
        </button>
        <span class="current-month">{{ currentMonthDisplay }}</span>
        <button class="calendar-nav-button" @click="nextMonth">
          <ChevronRight />
        </button>
      </div>
    </div>

    <div class="calendar-container">
      <div class="calendar-header">
        <div v-for="day in weekDays" :key="day" class="calendar-day-header">{{ day }}</div>
      </div>
      <div class="calendar-grid">
        <div 
          v-for="day in calendarDays" 
          :key="day.date" 
          :class="[
            'calendar-day', 
            { 'current-month': day.currentMonth },
            { 'today': day.isToday },
            { 'has-events': day.hasEvents },
            { 'selected': selectedDate === day.date }
          ]"
          @click="onSelectDate(day)"
        >
          <span class="day-number">{{ day.dayNumber }}</span>
          <div v-if="day.hasEvents" class="day-event-indicator">
            <span v-for="(evt, i) in day.eventCount > 3 ? 3 : day.eventCount" :key="i" 
              :style="{ backgroundColor: i === 0 ? '#1890ff' : i === 1 ? '#52c41a' : '#fa8c16' }"></span>
            <span v-if="day.eventCount > 3" class="more-events">+{{ day.eventCount - 3 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  CalendarDays,
  ChevronLeft,
  ChevronRight,
} from 'lucide-vue-next'

const props = defineProps({
  schedules: {
    type: Array,
    required: true
  },
  selectedDate: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['date-select', 'previousMonth', 'nextMonth'])

// 周日历标题
const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

// 当前显示的月份
const currentMonth = ref(new Date())
const currentMonthDisplay = computed(() => {
  const year = currentMonth.value.getFullYear()
  const month = currentMonth.value.getMonth() + 1
  return `${year}年${month}月`
})

// 构建日历数据
const calendarDays = computed(() => {
  const year = currentMonth.value.getFullYear()
  const month = currentMonth.value.getMonth()
  
  // 当月第一天和最后一天
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  // 计算需要显示的前一个月的天数
  const firstDayOfWeek = firstDay.getDay() // 0 是周日，6 是周六
  const prevMonthDays = firstDayOfWeek
  
  // 计算日历中需要显示的总天数
  const daysInMonth = lastDay.getDate()
  const totalDays = Math.ceil((prevMonthDays + daysInMonth) / 7) * 7
  
  // 构建日历天数数组
  const days = []
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  // 前一个月的日期
  const prevMonth = new Date(year, month - 1, 1)
  const daysInPrevMonth = new Date(year, month, 0).getDate()
  
  for (let i = 0; i < prevMonthDays; i++) {
    const dayNumber = daysInPrevMonth - prevMonthDays + i + 1
    const date = new Date(year, month - 1, dayNumber)
    days.push({
      date: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(dayNumber).padStart(2, '0')}`,
      dayNumber,
      currentMonth: false,
      isToday: false,
      hasEvents: hasScheduleOnDate(date),
      eventCount: countSchedulesOnDate(date)
    })
  }
  
  // 当前月的日期
  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(year, month, i)
    const dateString = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const isToday = date.getTime() === today.getTime()
    
    days.push({
      date: dateString,
      dayNumber: i,
      currentMonth: true,
      isToday,
      hasEvents: hasScheduleOnDate(date),
      eventCount: countSchedulesOnDate(date)
    })
  }
  
  // 计算下个月需要显示的天数
  const nextMonthDays = totalDays - days.length
  
  // 下个月的日期
  for (let i = 1; i <= nextMonthDays; i++) {
    const date = new Date(year, month + 1, i)
    days.push({
      date: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`,
      dayNumber: i,
      currentMonth: false,
      isToday: false,
      hasEvents: hasScheduleOnDate(date),
      eventCount: countSchedulesOnDate(date)
    })
  }
  
  return days
})

// 检查指定日期是否有日程
function hasScheduleOnDate(date) {
  const dateString = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  return props.schedules.some(schedule => schedule.date === dateString)
}

// 计算指定日期的日程数量
function countSchedulesOnDate(date) {
  const dateString = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  return props.schedules.filter(schedule => schedule.date === dateString).length
}

// 选择日期查看日程
function onSelectDate(day) {
  emit('date-select', day.date)
}

// 日历导航方法
const previousMonth = () => {
  const date = new Date(currentMonth.value)
  date.setMonth(date.getMonth() - 1)
  currentMonth.value = date
  emit('previousMonth', date)
}

const nextMonth = () => {
  const date = new Date(currentMonth.value)
  date.setMonth(date.getMonth() + 1)
  currentMonth.value = date
  emit('nextMonth', date)
}
</script>

<style scoped>
.calendar-section {
  background-color: transparent;
  border-radius: 16px;
  padding: 24px;
  box-shadow: none;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.section-icon {
  width: 22px;
  height: 22px;
  color: #3b82f6;
}

.calendar-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.calendar-nav-button {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background-color: #fff;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.calendar-nav-button:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background-color: #f0f7ff;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.current-month {
  font-size: 18px;
  font-weight: 600;
  color: #334155;
}

.calendar-container {
  flex: 1;
  border-radius: 12px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  border: 1px solid #e2e8f0;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.calendar-day-header {
  text-align: center;
  font-weight: 600;
  color: #64748b;
  padding: 12px 8px;
  font-size: 14px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: 1fr;
  gap: 1px;
  background-color: #f1f5f9;
  border-radius: 0 0 12px 12px;
  overflow: hidden;
  height: calc(100% - 42px);
}

.calendar-day {
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  height: 100%;
  min-height: 80px;
}

.calendar-day:hover {
  background-color: #f0f7ff;
}

.calendar-day.current-month {
  background-color: white;
}

.calendar-day.today {
  background-color: #f0f7ff;
}

.calendar-day.has-events {
  box-shadow: inset 0 -3px 0 #3b82f6;
}

.calendar-day.selected {
  background-color: #f0f7ff;
  border: 2px solid #3b82f6;
  transform: scale(1.02);
  z-index: 2;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.day-number {
  margin-bottom: 8px;
  font-size: 15px;
  color: #334155;
  font-weight: 500;
}

.today .day-number {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #3b82f6;
  color: white;
  border-radius: 50%;
  font-weight: 600;
}

.day-event-indicator {
  display: flex;
  gap: 3px;
  justify-content: center;
  margin-top: 6px;
}

.day-event-indicator span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  display: inline-block;
}

.more-events {
  font-size: 10px;
  color: #64748b;
  width: auto !important;
  height: auto !important;
  border-radius: 0 !important;
  background-color: transparent !important;
}

@media (max-width: 768px) {
  .calendar-grid {
    min-height: 400px;
  }
  
  .calendar-day {
    padding: 4px;
    min-height: 60px;
  }
}
</style> 