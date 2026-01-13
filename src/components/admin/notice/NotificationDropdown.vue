<template>
  <div class="notification-container" ref="containerRef">
    <div class="icon-button" @click="toggleDropdown">
      <Bell />
      <span class="badge">{{ unreadCount }}</span>
    </div>
    
    <!-- 通知下拉菜单 -->
    <Transition name="fade">
      <div v-if="showDropdown" class="notification-dropdown" :style="dropdownStyle">
        <div class="dropdown-header">
          <h3>通知中心</h3>
          <div class="dropdown-actions">
            <button class="btn-icon-sm" title="标记所有为已读" @click.stop="markAllAsRead">
              <CheckSquare />
            </button>
            <button class="btn-icon-sm" title="查看全部" @click.stop="goToNotificationCenter">
              <ExternalLink />
            </button>
          </div>
        </div>
        <div class="dropdown-body">
          <div v-if="filteredNotifications.length === 0" class="empty-notifications">
            <div class="empty-icon">
              <Bell />
            </div>
            <p>暂无未读通知</p>
          </div>
          <div v-else class="notification-list">
            <div v-for="notification in filteredNotifications.slice(0, 5)" :key="notification.id" 
                class="notification-item" 
                :class="{ unread: !notification.read }"
                @click.stop="viewNotificationDetail(notification)">
              <div class="notification-icon" :class="notification.type">
                <Bell v-if="notification.type === 'system'" />
                <FileText v-else-if="notification.type === 'application'" />
                <MessageSquare v-else />
              </div>
              <div class="notification-content">
                <h4 class="notification-title">{{ notification.title }}</h4>
                <p class="notification-message">{{ notification.message }}</p>
                <div class="notification-meta">
                  <span class="notification-time">{{ notification.time }}</span>
                  <span class="notification-tag" :class="notification.priority">{{ notification.priorityText }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="dropdown-footer">
          <button class="view-all-btn" @click.stop="goToNotificationCenter">
            查看全部通知 <ArrowRight class="arrow-icon" />
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { Bell, MessageSquare, FileText, CheckSquare, ExternalLink, ArrowRight } from 'lucide-vue-next';
import { ElMessage } from 'element-plus';
import notificationStore from './notificationStore';

const emit = defineEmits(['navigate']);

// 内部状态
const showDropdown = ref(false);
const containerRef = ref(null);
const dropdownStyle = ref({});

// 从notificationStore获取通知数据
const { notifications } = notificationStore;

// 计算未读通知数量
const unreadCount = computed(() => {
  return notificationStore.statistics.value.unread;
});

// 过滤通知列表，优先显示未读通知
const filteredNotifications = computed(() => {
  // 获取未读通知，如果没有则获取最近的5条
  const unreadNotices = notificationStore.getNotifications({ read: false });
  return unreadNotices.length > 0 
    ? unreadNotices 
    : notificationStore.getNotifications({ limit: 5 });
});

// 计算下拉菜单位置
const calculateDropdownPosition = () => {
  if (!containerRef.value) return;
  
  const rect = containerRef.value.getBoundingClientRect();
  dropdownStyle.value = {
    position: 'fixed',
    top: `${rect.bottom + 8}px`,
    left: `${rect.left - 200}px`, // 向左偏移以适应菜单宽度
    zIndex: 99999
  };
};

// 切换通知下拉菜单显示状态
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
  
  if (showDropdown.value) {
    nextTick(() => {
      calculateDropdownPosition();
    });
    document.addEventListener('click', closeDropdown);
  } else {
    document.removeEventListener('click', closeDropdown);
  }
};

// 关闭通知下拉框
const closeDropdown = (event) => {
  const container = containerRef.value;
  if (container && !container.contains(event.target)) {
    showDropdown.value = false;
    document.removeEventListener('click', closeDropdown);
  }
};

// 查看通知详情
const viewNotificationDetail = (notification) => {
  // 标记为已读
  notificationStore.markAsRead(notification.id);
  
  ElMessage.info(`查看通知: ${notification.title}`);
  showDropdown.value = false;
};

// 标记所有通知为已读
const markAllAsRead = () => {
  notificationStore.markAllAsRead();
  ElMessage.success('已将所有通知标记为已读');
};

// 跳转到通知中心页面
const goToNotificationCenter = () => {
  showDropdown.value = false;
  emit('navigate', 'notification');
};

// 组件卸载前移除事件监听器
onBeforeUnmount(() => {
  document.removeEventListener('click', closeDropdown);
});

onMounted(() => {
  notificationStore.fetchNotifications();
});
</script>

<style scoped>
.notification-container {
  position: relative;
}

.icon-button {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-button:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

.badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #f5222d;
  color: white;
  font-size: 10px;
  border-radius: 10px;
  padding: 0 5px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 通知下拉菜单 */
.notification-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 320px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 99999;
}

.dropdown-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.dropdown-actions {
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

.dropdown-body {
  max-height: 400px;
  overflow-y: auto;
}

.empty-notifications {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: #999;
}

.empty-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  color: #d9d9d9;
}

.notification-list {
  padding: 8px 0;
}

.notification-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.notification-item:hover {
  background-color: #f9f9f9;
}

.notification-item.unread {
  background-color: #f0f7ff;
}

.notification-item.unread:hover {
  background-color: #e6f7ff;
}

.notification-icon {
  width: 36px;
  height: 36px;
  border-radius: 18px;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-icon.system {
  background-color: #e6f7ff;
  color: #1890ff;
}

.notification-icon.application {
  background-color: #f6ffed;
  color: #52c41a;
}

.notification-icon.message {
  background-color: #fff7e6;
  color: #fa8c16;
}

.notification-content {
  flex: 1;
  overflow: hidden;
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.notification-message {
  font-size: 12px;
  color: #666;
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notification-time {
  font-size: 11px;
  color: #999;
}

.notification-tag {
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 10px;
  background-color: #f5f5f5;
  color: #666;
}

.notification-tag.urgent {
  background-color: #fff1f0;
  color: #f5222d;
}

.notification-tag.important {
  background-color: #fff7e6;
  color: #fa8c16;
}

.dropdown-footer {
  padding: 10px 16px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

.view-all-btn {
  width: 100%;
  background: none;
  border: none;
  color: #1890ff;
  font-size: 12px;
  font-weight: 400;
  cursor: pointer;
  padding: 6px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.3s;
}

.view-all-btn:hover {
  background-color: #f0f7ff;
  border-radius: 6px;
}

.arrow-icon {
  width: 14px;
  height: 14px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
