<template>
  <div class="todo-tasks-widget">
    <div class="widget-header">
      <div class="header-info">
        <CheckSquare class="header-icon" />
        <div>
          <h3>待办任务</h3>
          <p>{{ incompleteTasks }} / {{ todoItems.length }} 项</p>
        </div>
      </div>
      <div class="header-actions">
        <button class="icon-btn" @click="$emit('addTask')">
          <Plus />
        </button>
      </div>
    </div>
    <div class="widget-content">
      <div class="todo-list">
        <div 
          v-for="(item, index) in todoItems.slice(0, 5)" 
          :key="index"
          class="todo-item"
          :class="{ completed: item.completed }"
        >
          <input 
            type="checkbox" 
            :checked="!!item.completed"
            @change="toggleCompleted(item)"
            class="todo-checkbox"
          />
          <div class="todo-content">
            <div class="todo-title">{{ item.title }}</div>
            <div class="todo-meta">
              <span class="todo-priority" :class="item.priority">
                {{ getPriorityText(item.priority) }}
              </span>
              <span class="todo-due">{{ item.dueDate }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { CheckSquare, Plus } from 'lucide-vue-next'

const props = defineProps({
  componentData: {
    type: Object,
    default: () => ({})
  },
  dashboardData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['addTask', 'updateTodoItems'])

const todoItems = computed(() => {
  const list = props.dashboardData?.todoItems
  return Array.isArray(list) ? list : []
})

const incompleteTasks = computed(() => {
  return todoItems.value.filter(item => !item.completed).length
})

const toggleCompleted = (item) => {
  const next = todoItems.value.map(x => {
    if (x?.id === item?.id) return { ...x, completed: !x.completed }
    return x
  })
  emit('updateTodoItems', next)
}

const getPriorityText = (priority) => {
  const map = {
    urgent: '紧急',
    high: '高',
    normal: '普通',
    low: '低'
  }
  return map[priority] || '普通'
}
</script>

<style scoped>
.todo-tasks-widget {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.widget-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.widget-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.widget-header p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.icon-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(24, 144, 255, 0.1);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #1890ff;
  transition: all 0.3s ease;
}

.icon-btn:hover {
  background: rgba(24, 144, 255, 0.2);
}

.icon-btn svg {
  width: 14px;
  height: 14px;
}

.widget-content {
  padding: 20px;
  flex: 1;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item.completed {
  opacity: 0.6;
}

.todo-item.completed .todo-title {
  text-decoration: line-through;
}

.todo-checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  cursor: pointer;
  accent-color: #1890ff;
  margin-top: 2px;
}

.todo-content {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.3;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.todo-priority {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
}

.todo-priority.urgent {
  background: rgba(245, 34, 45, 0.1);
  color: #f5222d;
}

.todo-priority.high {
  background: rgba(250, 140, 22, 0.1);
  color: #fa8c16;
}

.todo-priority.normal {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.todo-due {
  font-size: 11px;
  color: #999;
}
</style> 
