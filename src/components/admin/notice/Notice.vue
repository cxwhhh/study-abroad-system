<template>
  <div class="notice-center">
    <!-- 主内容区 -->
    <div class="communication-container" :class="{ 'contacts-collapsed': contactsCollapsed }">
      <div v-if="contactsCollapsed" class="contacts-collapsed-handle" @mouseenter="onContactsHandleEnter"
        @mouseleave="onContactsHandleLeave"></div>

      <Transition name="contacts-overlay">
        <div v-if="contactsCollapsed && contactsOverlayVisible" class="contacts-overlay"
          @mouseenter="onContactsOverlayEnter" @mouseleave="onContactsOverlayLeave">
          <div class="contacts-panel contacts-panel-overlay">
            <div class="contacts-header">
              <div class="contacts-title-row">
                <h3 class="panel-title">联系人</h3>
                <button class="btn-icon contacts-collapse-btn" title="展开联系人" @click="pinOpenContacts">
                  <ChevronsRight :size="18" />
                </button>
              </div>

              <div>
                <div class="contacts-search">
                  <input type="text" v-model="contactSearchQuery" placeholder="搜索..." class="contacts-search-input" />
                  <Search class="search-icon" />
                </div>
                <div class="contacts-tabs">
                  <button class="contact-tab" :class="{ active: activeContactTab === 'recent' }"
                    @click="activeContactTab = 'recent'">最近对话</button>
                  <button class="contact-tab" :class="{ active: activeContactTab === 'teachers' }"
                    @click="activeContactTab = 'teachers'">教师</button>
                  <button class="contact-tab" :class="{ active: activeContactTab === 'applications' }"
                    @click="activeContactTab = 'applications'">申请学生</button>
                </div>
              </div>
            </div>
            <div class="contacts-list">
              <div v-for="contact in filteredContacts" :key="contactRowKey(contact)" class="contact-item"
                :class="{ active: selectedContact && selectedContact.id === contact.id }"
                @click="selectContactFromOverlay(contact)">
                <div class="contact-info">
                  <div class="contact-top-row">
                    <div class="contact-name-row">
                      <div class="contact-name">{{ contact.name }}</div>
                      <div v-if="contact.unreadCount > 0" class="unread-pill">{{ contact.unreadCount }}</div>
                    </div>
                    <div class="contact-time">{{ contact.lastMessage.time }}</div>
                  </div>
                  <div class="contact-middle-row">
                    <div v-if="contact.type === 'application' && contact.institution"
                      class="contact-institution-inline">
                      {{ contact.institution }}<span v-if="contact.major"> - {{ contact.major }}</span>
                      <span v-if="(contact.applications?.length || 0) > 1" class="contact-multi-app">（{{
                        contact.applications.length }}个专业）</span>
                    </div>
                    <div v-else class="contact-role-badge">{{ contact.role }}</div>
                    <span v-if="contact.type === 'application'" class="application-status"
                      :class="applicationStatusClass(contact)">
                      {{ applicationStatusText(contact) }}
                    </span>
                  </div>
                  <div class="contact-preview">{{ contact.lastMessage.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Transition>

      <!-- 左侧联系人列表 -->
      <Transition name="contacts-panel">
        <div v-if="!contactsCollapsed" class="contacts-panel">
          <div class="contacts-header">
            <div class="contacts-title-row">
              <h3 class="panel-title">联系人</h3>
              <button class="btn-icon contacts-collapse-btn" title="收起联系人" @click="collapseContacts">
                <ChevronsLeft :size="18" />
              </button>
            </div>

            <div>
              <div class="contacts-search">
                <input type="text" v-model="contactSearchQuery" placeholder="搜索..." class="contacts-search-input" />
                <Search class="search-icon" />
              </div>
              <div class="contacts-tabs">
                <button class="contact-tab" :class="{ active: activeContactTab === 'recent' }"
                  @click="activeContactTab = 'recent'">最近对话</button>
                <button class="contact-tab" :class="{ active: activeContactTab === 'teachers' }"
                  @click="activeContactTab = 'teachers'">教师</button>
                <button class="contact-tab" :class="{ active: activeContactTab === 'applications' }"
                  @click="activeContactTab = 'applications'">申请学生</button>
              </div>
            </div>
          </div>
          <div class="contacts-list">
            <div v-for="contact in filteredContacts" :key="contactRowKey(contact)" class="contact-item"
              :class="{ active: selectedContact && selectedContact.id === contact.id }" @click="selectContact(contact)">
              <div class="contact-info">
                <div class="contact-top-row">
                  <div class="contact-name-row">
                    <div class="contact-name">{{ contact.name }}</div>
                    <div v-if="contact.unreadCount > 0" class="unread-pill">{{ contact.unreadCount }}</div>
                  </div>
                  <div class="contact-time">{{ contact.lastMessage.time }}</div>
                </div>
                <div class="contact-middle-row">
                  <div v-if="contact.type === 'application' && contact.institution" class="contact-institution-inline">
                    {{ contact.institution }}<span v-if="contact.major"> - {{ contact.major }}</span>
                    <span v-if="(contact.applications?.length || 0) > 1" class="contact-multi-app">（{{
                      contact.applications.length }}个专业）</span>
                  </div>
                  <div v-else class="contact-role-badge">{{ contact.role }}</div>
                  <span v-if="contact.type === 'application'" class="application-status"
                    :class="applicationStatusClass(contact)">
                    {{ applicationStatusText(contact) }}
                  </span>
                </div>
                <div class="contact-preview">{{ contact.lastMessage.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </Transition>

      <!-- 中间消息列表 -->
      <div class="messages-panel">
        <div v-if="!selectedContact" class="empty-messages">
          <div class="empty-messages-icon">
            <MessageSquare />
          </div>
          <h3>选择联系人开始对话</h3>
          <p>从左侧列表选择一个联系人来查看消息历史并开始对话</p>
        </div>
        <template v-else>
          <div class="messages-header">
            <div class="conversation-info">
              <h3 class="conversation-name">{{ selectedContact.name }}</h3>
              <div class="conversation-meta">{{ selectedContact.role }} · {{ selectedContact.status === 'online' ? '在线'
                : '离线' }}</div>
              <div v-if="selectedContact.type === 'application'" class="conversation-app-select-wrapper">
                <span class="conversation-app-label">
                  专业<span v-if="applicationOptions.length > 1">（{{ applicationOptions.length }}）</span>：
                </span>
                <select class="conversation-app-select" :value="selectedApplicationId"
                  :disabled="applicationOptions.length <= 1" @change="onSelectedApplicationChange">
                  <option v-for="app in applicationOptions" :key="app.applicationId" :value="app.applicationId">
                    {{ formatApplicationOption(app) }}
                  </option>
                </select>
                <ChevronDown class="conversation-app-select-icon" />
              </div>
            </div>
            <div class="conversation-actions">
              <button class="btn-icon" title="搜索消息">
                <Search />
              </button>
              <button class="btn-icon" title="查看详情">
                <Info />
              </button>
            </div>
          </div>
          <div class="messages-list" ref="messagesList">
            <div class="message-date-divider">
              <span>{{ formatDate(new Date()) }}</span>
            </div>
            <div v-for="(message, index) in selectedContactMessages" :key="index" class="message-item"
              :class="{ 'message-sent': message.isSent, 'message-received': !message.isSent }">
              <div class="message-avatar" v-if="!message.isSent">
                <img v-if="selectedContact.avatar" :src="selectedContact.avatar" alt="" />
                <div v-else class="avatar-placeholder"
                  :style="{ backgroundColor: generateAvatarColor(selectedContact.name) }">
                  {{ selectedContact.name.charAt(0) }}
                </div>
              </div>
              <div class="message-content">
                <div class="message-bubble">
                  {{ message.content }}
                  <div v-if="message.attachment" class="message-attachment">
                    <Paperclip class="attachment-icon" />
                    <span class="attachment-name">{{ message.attachment.name }}</span>
                  </div>
                </div>
                <div class="message-time">{{ message.time }}</div>
                <div class="message-status" v-if="message.isSent">
                  <Check v-if="message.read" class="status-icon read" />
                  <Check v-else class="status-icon sent" />
                </div>
              </div>
            </div>
          </div>
          <div class="message-composer">
            <div class="composer-actions">
              <button class="btn-icon" title="添加附件">
                <Paperclip style="width: 16px; height: 16px;" />
              </button>
              <button class="btn-icon" title="添加表情">
                <Smile style="width: 16px; height: 16px;" />
              </button>
            </div>
            <textarea v-model="newMessage" class="message-input" placeholder="输入消息..."
              @keydown.enter.prevent="sendMessage"></textarea>
            <button class="send-button" @click="sendMessage" :disabled="!newMessage.trim()">
              <Send style="width: 18px; height: 18px;" />
            </button>
          </div>
        </template>
      </div>

      <!-- 右侧通知列表 -->
      <div class="notifications-panel">
        <div class="notifications-header">
          <h3 class="panel-title">系统通知</h3>
          <div class="notification-actions">
            <button class="btn-icon" title="标记所有为已读" @click="markAllAsRead">
              <CheckSquare />
            </button>
            <button class="btn-icon" title="筛选" @click="showFilterModal = true">
              <Filter />
            </button>
          </div>
        </div>
        <div class="notifications-list">
          <div v-if="filteredNotifications.length === 0" class="empty-notifications">
            <div class="empty-icon">
              <Bell />
            </div>
            <p>暂无通知</p>
          </div>
          <div v-for="notification in filteredNotifications" :key="notification.id" class="notification-item"
            :class="{ unread: !notification.read }" @click="viewNotificationDetail(notification)">
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
    </div>

    <!-- 筛选通知模态框 -->
    <div v-if="showFilterModal" class="modal-overlay" @click.self="showFilterModal = false">
      <div class="modal-container filter-modal">
        <div class="modal-header">
          <h3 class="modal-title">筛选通知</h3>
          <button class="close-btn" @click="showFilterModal = false">
            <X />
          </button>
        </div>
        <div class="modal-body">
          <div class="filter-form">
            <div class="filter-group">
              <label>通知类型</label>
              <div class="select-wrapper">
                <select v-model="filters.type">
                  <option value="">全部</option>
                  <option value="system">系统通知</option>
                  <option value="application">申请相关</option>
                  <option value="message">私信消息</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="filter-group">
              <label>优先级</label>
              <div class="select-wrapper">
                <select v-model="filters.priority">
                  <option value="">全部</option>
                  <option value="urgent">紧急</option>
                  <option value="important">重要</option>
                  <option value="normal">普通</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="filter-group">
              <label>状态</label>
              <div class="select-wrapper">
                <select v-model="filters.read">
                  <option value="">全部</option>
                  <option value="unread">未读</option>
                  <option value="read">已读</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="filter-group">
              <label>时间范围</label>
              <div class="select-wrapper">
                <select v-model="filters.timeRange">
                  <option value="">全部</option>
                  <option value="today">今天</option>
                  <option value="week">本周</option>
                  <option value="month">本月</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="resetFilters">重置</button>
          <button class="btn-primary" @click="applyFilters">应用筛选</button>
        </div>
      </div>
    </div>

    <!-- 发送通知模态框 -->
    <div v-if="showSendNoticeModal" class="modal-overlay">
      <div class="modal-container send-notice-modal">
        <div class="modal-header">
          <h3 class="modal-title">发送通知</h3>
          <button class="close-btn" @click="showSendNoticeModal = false">
            <X />
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>接收对象 <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="newNotice.receiver" class="form-select">
                <option value="">请选择接收对象</option>
                <option value="all">所有学生</option>
                <option value="specific_student">指定学生</option>
                <option value="specific_teacher">指定老师</option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
          </div>

          <!-- 指定学生选择区域 -->
          <div class="form-group" v-if="newNotice.receiver === 'specific_student'">
            <label>指定学生 <span class="required">*</span></label>
            <div class="recipient-selector" :class="{ 'focused': studentSelectorFocused }" @click="focusStudentInput"
              ref="studentSelector">
              <div class="recipient-tags">
                <div v-for="student in selectedStudents" :key="student.id" class="recipient-tag">
                  {{ student.name }}
                  <button class="tag-remove-btn" @click.stop="removeRecipient(student, 'student')">×</button>
                </div>
              </div>
              <input type="text" class="recipient-input" placeholder="请输入学生姓名" v-model="studentSearchText"
                @focus="onStudentInputFocus" ref="studentInput" />
              <div class="recipient-dropdown" v-if="studentSelectorFocused && filteredStudents.length > 0">
                <div v-for="student in filteredStudents" :key="student.id" class="recipient-item"
                  @click.stop="addRecipient(student, 'student')">
                  {{ student.name }} <span class="recipient-detail">({{ student.grade }} - {{ student.school }})</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 指定老师选择区域 -->
          <div class="form-group" v-if="newNotice.receiver === 'specific_teacher'">
            <label>指定老师 <span class="required">*</span></label>
            <div class="recipient-selector" :class="{ 'focused': teacherSelectorFocused }" @click="focusTeacherInput"
              ref="teacherSelector">
              <div class="recipient-tags">
                <div v-for="teacher in selectedTeachers" :key="teacher.id" class="recipient-tag">
                  {{ teacher.name }}
                  <button class="tag-remove-btn" @click.stop="removeRecipient(teacher, 'teacher')">×</button>
                </div>
              </div>
              <input type="text" class="recipient-input" placeholder="请输入老师姓名" v-model="teacherSearchText"
                @focus="onTeacherInputFocus" ref="teacherInput" />
              <div class="recipient-dropdown" v-if="teacherSelectorFocused && filteredTeachers.length > 0">
                <div v-for="teacher in filteredTeachers" :key="teacher.id" class="recipient-item"
                  @click.stop="addRecipient(teacher, 'teacher')">
                  {{ teacher.name }} <span class="recipient-detail">({{ teacher.department }})</span>
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>通知类型 <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="newNotice.type" class="form-select">
                <option value="system">系统通知</option>
                <option value="application">申请相关</option>
                <option value="message">私信消息</option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
          </div>

          <div class="form-group">
            <label>优先级 <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="newNotice.priority" class="form-select">
                <option value="normal">普通</option>
                <option value="important">重要</option>
                <option value="urgent">紧急</option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
          </div>

          <div class="form-group">
            <label>通知标题 <span class="required">*</span></label>
            <input type="text" v-model="newNotice.title" class="form-input" placeholder="请输入通知标题" />
          </div>

          <div class="form-group">
            <label>通知内容 <span class="required">*</span></label>
            <textarea v-model="newNotice.message" class="form-textarea" placeholder="请输入通知内容" rows="5"></textarea>
          </div>

          <div class="form-group">
            <label>附件</label>
            <div class="file-upload">
              <input type="file" id="notice-attachment" class="file-input" multiple @change="handleFileUpload" />
              <label for="notice-attachment" class="file-upload-label">
                <div class="upload-icon">
                  <Upload />
                </div>
                <span>选择文件</span>
              </label>
            </div>
            <div class="file-tip">
              点击上传或拖拽文件支持常见文档格式，最大 10MB
            </div>
          </div>

          <div class="form-options">
            <label class="checkbox-container">
              <input type="checkbox" v-model="newNotice.options.sendSMS">
              <span class="checkmark"></span>
              同时发送短信通知
            </label>
            <label class="checkbox-container">
              <input type="checkbox" v-model="newNotice.options.sendEmail">
              <span class="checkmark"></span>
              同时发送邮件通知
            </label>
            <label class="checkbox-container">
              <input type="checkbox" v-model="newNotice.options.requireConfirm">
              <span class="checkmark"></span>
              需要学生确认回执
            </label>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showSendNoticeModal = false">取消</button>
          <button class="btn-send" @click="sendNotice">发送</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import {
  Bell,
  MessageSquare,
  Send,
  Search,
  ChevronDown,
  FileText,
  Check,
  ChevronsLeft,
  ChevronsRight,
  X,
  Upload,
  Info,
  Paperclip,
  Smile,
  CheckSquare,
  Filter
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

import notificationStore from './notificationStore'

// 筛选条件
const filters = reactive({
  type: '',
  priority: '',
  read: '',
  timeRange: []
})

const allNotices = computed(() => notificationStore.notifications.value)

// 联系人相关数据
const contactSearchQuery = ref('')
const activeContactTab = ref('recent')
const selectedContact = ref(null)
const selectedApplicationId = ref(null)
const newMessage = ref('')
const contactsCollapsed = ref(false)
const contactsOverlayVisible = ref(false)
const contactsOverlayHovering = ref(false)
let contactsOverlayHideTimer = null
const contactsOverlayHideDelayMs = 180
const showFilterModal = ref(false)
const showSendNoticeModal = ref(false)

// 联系人数据 - 从messages表获取
const contacts = ref([])

// 申请学生数据 - 从application_schools表获取
const applicationStudents = ref([])

// 消息历史记录 - 从messages表获取
const messageHistory = ref([])

const contactRowKey = (contact) => {
  if (!contact) return ''
  const type = contact.type || 'user'
  return `${type}-${contact.id}`
}

const collapseContacts = () => {
  if (contactsOverlayHideTimer) window.clearTimeout(contactsOverlayHideTimer)
  contactsOverlayHideTimer = null
  contactsOverlayHovering.value = false
  contactsOverlayVisible.value = false
  contactsCollapsed.value = true
}

const pinOpenContacts = () => {
  if (contactsOverlayHideTimer) window.clearTimeout(contactsOverlayHideTimer)
  contactsOverlayHideTimer = null
  contactsOverlayHovering.value = false
  contactsOverlayVisible.value = false
  contactsCollapsed.value = false
}

const onContactsHandleEnter = () => {
  if (!contactsCollapsed.value) return
  if (contactsOverlayHideTimer) window.clearTimeout(contactsOverlayHideTimer)
  contactsOverlayHideTimer = null
  contactsOverlayVisible.value = true
}

const onContactsHandleLeave = () => {
  if (contactsOverlayHideTimer) window.clearTimeout(contactsOverlayHideTimer)
  contactsOverlayHideTimer = window.setTimeout(() => {
    if (contactsOverlayHovering.value) return
    contactsOverlayVisible.value = false
  }, contactsOverlayHideDelayMs)
}

const onContactsOverlayEnter = () => {
  contactsOverlayHovering.value = true
  if (contactsOverlayHideTimer) window.clearTimeout(contactsOverlayHideTimer)
  contactsOverlayHideTimer = null
}

const onContactsOverlayLeave = () => {
  contactsOverlayHovering.value = false
  if (contactsOverlayHideTimer) window.clearTimeout(contactsOverlayHideTimer)
  contactsOverlayHideTimer = window.setTimeout(() => {
    if (contactsOverlayHovering.value) return
    contactsOverlayVisible.value = false
  }, contactsOverlayHideDelayMs)
}

const selectContactFromOverlay = (contact) => {
  contactsOverlayVisible.value = false
  selectContact(contact)
}

// 筛选联系人
const filteredContacts = computed(() => {
  let result = []

  // 根据标签选择数据源
  if (activeContactTab.value === 'applications') {
    result = applicationStudents.value
  } else if (activeContactTab.value === 'recent') {
    // 最近对话：显示有消息记录的联系人，按最后消息时间排序
    result = contacts.value.filter(contact => contact.lastMessage && contact.lastMessage.content)
      .sort((a, b) => (b.lastMessage.timestamp || 0) - (a.lastMessage.timestamp || 0))
  } else {
    result = contacts.value
  }

  // 根据搜索关键词筛选
  if (contactSearchQuery.value.trim()) {
    const query = contactSearchQuery.value.trim().toLowerCase()
    result = result.filter(contact =>
      contact.name.toLowerCase().includes(query) ||
      (contact.role && contact.role.toLowerCase().includes(query)) ||
      (contact.institution && contact.institution.toLowerCase().includes(query)) ||
      (contact.major && contact.major.toLowerCase().includes(query))
    )
  }

  // 根据标签筛选（非申请学生和最近对话的情况）
  if (activeContactTab.value === 'teachers') {
    result = result.filter(contact => contact.type === 'teacher')
  }

  return result
})

// 获取选中联系人的消息
const selectedContactMessages = computed(() => {
  if (!selectedContact.value) return []
  return messageHistory.value
})

const toDate = (value) => {
  if (value instanceof Date) return value
  if (value === null || value === undefined || value === '') return null
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return null
  return date
}

// 筛选系统通知
const filteredNotifications = computed(() => {
  let filtered = allNotices.value || []

  // 按通知类型筛选
  if (filters.type) {
    filtered = filtered.filter(notice => notice.type === filters.type)
  }

  // 按优先级筛选
  if (filters.priority) {
    filtered = filtered.filter(notice => notice.priority === filters.priority)
  }

  // 按已读状态筛选
  if (filters.read) {
    const isRead = filters.read === 'read'
    filtered = filtered.filter(notice => notice.read === isRead)
  }

  const asDate = (value) => {
    if (value instanceof Date) return value
    if (value === null || value === undefined || value === '') return null
    const d = new Date(value)
    if (Number.isNaN(d.getTime())) return null
    return d
  }

  const noticeDate = (notice) => {
    return asDate(notice?.createdAt) || asDate(notice?.createTime) || asDate(notice?.time)
  }

  if (typeof filters.timeRange === 'string' && filters.timeRange) {
    const now = new Date()
    let start = null
    if (filters.timeRange === 'today') {
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    } else if (filters.timeRange === 'week') {
      const day = now.getDay() || 7
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - (day - 1))
      start.setHours(0, 0, 0, 0)
    } else if (filters.timeRange === 'month') {
      start = new Date(now.getFullYear(), now.getMonth(), 1)
    }

    if (start) {
      filtered = filtered.filter(notice => {
        const d = noticeDate(notice)
        return d && d >= start && d <= now
      })
    }
  } else if (Array.isArray(filters.timeRange) && filters.timeRange.length === 2) {
    const start = asDate(filters.timeRange[0])
    const end = asDate(filters.timeRange[1])
    if (start && end) {
      filtered = filtered.filter(notice => {
        const d = noticeDate(notice)
        return d && d >= start && d <= end
      })
    }
  }

  return filtered.sort((a, b) => {
    const da = noticeDate(a)
    const db = noticeDate(b)
    return (db?.getTime?.() || 0) - (da?.getTime?.() || 0)
  })
})

// 获取申请学生列表
const fetchApplicationStudents = async () => {
  try {
    const res = await request.get('/application-school/list')
    const applicationList = Array.isArray(res?.data) ? res.data : []

    if (applicationList.length > 0) {
      const studentMap = new Map()

      applicationList.forEach(app => {
        const userId = app?.userId
        if (userId === null || userId === undefined) return

        const key = String(userId)
        const createdAt = toDate(app.createTime || app.applicationDate)
        const timestamp = createdAt?.getTime() || 0

        const applicationItem = {
          applicationId: app.id,
          institution: app.schoolName || app.institution || app.school,
          major: app.majorName || app.major || app.program,
          applicationStatus: app.applicationStatus,
          applicationStatusDisplay: app.applicationStatusDisplay,
          applicationDate: app.createTime || app.applicationDate,
          timestamp
        }

        if (!studentMap.has(key)) {
          studentMap.set(key, {
            id: userId,
            name: app.studentName || app.userName || '未知学生',
            role: '申请学生',
            avatar: null,
            status: 'online',
            unreadCount: 0,
            lastMessage: {
              content: '',
              time: '',
              createTime: null,
              timestamp: 0,
              isStatusStub: true
            },
            type: 'application',
            userId: userId,
            studentId: app.studentId,
            email: app.studentEmail || app.userEmail || app.email,
            phone: app.phone,
            applications: []
          })
        }

        const contact = studentMap.get(key)
        contact.applications.push(applicationItem)

        if (!contact.name || contact.name === '未知学生') {
          contact.name = app.studentName || app.userName || contact.name
        }
        if (!contact.email) {
          contact.email = app.studentEmail || app.userEmail || app.email
        }
        if (!contact.phone) {
          contact.phone = app.phone
        }

        if (timestamp >= (contact.lastMessage?.timestamp || 0)) {
          const statusText = app.applicationStatusDisplay || app.applicationStatus || '待审核'
          contact.lastMessage = {
            content: `申请状态: ${statusText}`,
            time: formatTime(createdAt || (app.createTime || app.applicationDate)),
            createTime: createdAt?.toISOString() || (app.createTime || app.applicationDate),
            timestamp,
            isStatusStub: true
          }
          contact.applicationId = app.id
          contact.institution = applicationItem.institution
          contact.major = applicationItem.major
          contact.applicationStatus = app.applicationStatus
          contact.applicationStatusDisplay = app.applicationStatusDisplay
        }
      })

      const list = Array.from(studentMap.values())
      list.forEach(contact => {
        if (Array.isArray(contact.applications)) {
          contact.applications.sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))
          if (!contact.applicationId && contact.applications[0]?.applicationId) {
            const first = contact.applications[0]
            contact.applicationId = first.applicationId
            contact.institution = first.institution
            contact.major = first.major
            contact.applicationStatus = first.applicationStatus
            contact.applicationStatusDisplay = first.applicationStatusDisplay
          }
        } else {
          contact.applications = []
        }
      })

      applicationStudents.value = list
    } else {
      applicationStudents.value = []
    }
  } catch (error) {
    console.error('获取申请学生列表失败:', error)
    ElMessage.error('获取申请学生列表失败')
    applicationStudents.value = []
  }
}

// 获取联系人列表
const fetchContacts = async () => {
  try {
    // 获取当前管理员信息
    let currentUserId = 1
    try {
      const adminInfo = localStorage.getItem('adminInfo')
      if (adminInfo) {
        const currentUser = JSON.parse(adminInfo)
        currentUserId = currentUser.id
      }
    } catch (error) {
      console.error('解析管理员信息失败:', error)
    }

    const [receivedRes, sentRes] = await Promise.all([
      request.get(`/messages/user/${currentUserId}`),
      request.get(`/messages/sender/${currentUserId}`)
    ])

    const receivedMessages = Array.isArray(receivedRes?.data) ? receivedRes.data : []
    const sentMessages = Array.isArray(sentRes?.data) ? sentRes.data : []
    const messages = receivedMessages.concat(sentMessages)

    if (messages.length > 0) {
      const applicationNameByUserId = new Map(
        (applicationStudents.value || []).map(s => [Number(s.id), s.name])
      )

      const contactsMap = new Map()

      messages.forEach(msg => {
        const isSentByMe = msg.senderId === currentUserId
        const contactId = isSentByMe ? msg.receiverId : msg.senderId
        const contactName = isSentByMe
          ? (msg.receiverName || msg.receiverNickname || msg.receiverUsername || applicationNameByUserId.get(Number(contactId)) || `用户${contactId}`)
          : (msg.senderName || msg.senderNickname || msg.senderUsername || applicationNameByUserId.get(Number(contactId)) || `用户${contactId}`)
        const contactType = isSentByMe ? (msg.receiverType || 'student') : (msg.senderType || 'student')
        const contactKey = String(contactId)

        const msgCreateTime = msg.createTime || msg.createdAt || msg.create_at || msg.timestamp
        const msgDate = toDate(msgCreateTime)
        const msgTimestamp = msgDate?.getTime() || 0

        if (!contactsMap.has(contactKey)) {
          contactsMap.set(contactKey, {
            id: contactId,
            name: contactName,
            role: '学生', // 默认为学生，可以根据实际情况调整
            avatar: null,
            status: 'offline', // 默认离线状态
            unreadCount: 0,
            lastMessage: {
              content: msg.content,
              time: formatTime(msgDate || msgCreateTime),
              createTime: msgDate?.toISOString() || msgCreateTime,
              timestamp: msgTimestamp
            },
            type: contactType,
            messages: []
          })
        }

        const contact = contactsMap.get(contactKey)
        contact.messages.push(msg)

        if (msgTimestamp > (contact.lastMessage?.timestamp || 0)) {
          contact.lastMessage = {
            content: msg.content,
            time: formatTime(msgDate || msgCreateTime),
            createTime: msgDate?.toISOString() || msgCreateTime,
            timestamp: msgTimestamp
          }
        }

        if (!isSentByMe && msg.status === 'unread') {
          contact.unreadCount++
        }
      })

      contacts.value = Array.from(contactsMap.values())
    } else {
      contacts.value = []
    }
  } catch (error) {
    console.error('获取联系人列表失败:', error)
    ElMessage.error('获取联系人列表失败')
    contacts.value = []
  }
}

// 获取与特定联系人的消息历史
const fetchMessageHistory = async (contactId, applicationId) => {
  try {
    // 获取当前管理员信息
    let currentUserId = 1
    try {
      const adminInfo = localStorage.getItem('adminInfo')
      if (adminInfo) {
        const currentUser = JSON.parse(adminInfo)
        currentUserId = currentUser.id
      }
    } catch (error) {
      console.error('解析管理员信息失败:', error)
    }

    // 同时获取接收到的消息和发送出去的消息
    const [receivedResponse, sentResponse] = await Promise.all([
      request.get(`/messages/user/${currentUserId}`),
      request.get(`/messages/sender/${currentUserId}`)
    ])

    let allMessages = []
    const receivedList = Array.isArray(receivedResponse?.data) ? receivedResponse.data : []
    const sentList = Array.isArray(sentResponse?.data) ? sentResponse.data : []

    // 处理接收到的消息
    if (receivedList.length > 0) {
      const receivedMessages = receivedList.map(msg => ({
        id: msg.id,
        content: msg.content,
        time: formatTime(toDate(msg.createTime || msg.createdAt) || (msg.createTime || msg.createdAt)),
        isSent: false, // 接收到的消息
        read: msg.isRead || msg.status === 'read',
        sender: msg.senderName,
        receiver: msg.receiverName,
        senderId: msg.senderId,
        receiverId: msg.receiverId,
        createTime: toDate(msg.createTime || msg.createdAt)?.toISOString() || (msg.createTime || msg.createdAt),
        applicationId: msg.applicationId ?? msg.application_id ?? msg.appId ?? msg.app_id
      }))
      allMessages = allMessages.concat(receivedMessages)
    }

    // 处理发送出去的消息
    if (sentList.length > 0) {
      const sentMessages = sentList.map(msg => ({
        id: msg.id,
        content: msg.content,
        time: formatTime(toDate(msg.createTime || msg.createdAt) || (msg.createTime || msg.createdAt)),
        isSent: true, // 发送出去的消息
        read: msg.isRead || msg.status === 'read',
        sender: msg.senderName,
        receiver: msg.receiverName,
        senderId: msg.senderId,
        receiverId: msg.receiverId,
        createTime: toDate(msg.createTime || msg.createdAt)?.toISOString() || (msg.createTime || msg.createdAt),
        applicationId: msg.applicationId ?? msg.application_id ?? msg.appId ?? msg.app_id
      }))
      allMessages = allMessages.concat(sentMessages)
    }

    // 按时间排序所有消息
    allMessages.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))

    const relatedMessages = allMessages.filter(msg => msg.senderId === contactId || msg.receiverId === contactId)
    const hasAnyApplicationId = relatedMessages.some(m => m.applicationId !== null && m.applicationId !== undefined && m.applicationId !== '')
    const shouldFilterByApplication = applicationId !== null && applicationId !== undefined && applicationId !== '' && hasAnyApplicationId

    messageHistory.value = shouldFilterByApplication
      ? relatedMessages.filter(m => String(m.applicationId) === String(applicationId))
      : relatedMessages
  } catch (error) {
    console.error('获取消息历史失败:', error)
    ElMessage.error('获取消息历史失败')
    messageHistory.value = []
  }
}

// 选择联系人
const selectContact = (contact) => {
  selectedContact.value = contact
  if (contact?.type === 'application') {
    selectedApplicationId.value = contact.applicationId || (Array.isArray(contact.applications) ? contact.applications[0]?.applicationId : null) || null
  } else {
    selectedApplicationId.value = null
  }
  // 获取与该联系人的消息历史
  fetchMessageHistory(contact.id, selectedApplicationId.value)
}

const applicationOptions = computed(() => {
  if (!selectedContact.value || selectedContact.value.type !== 'application') return []
  const apps = selectedContact.value.applications
  if (Array.isArray(apps) && apps.length) return apps
  if (selectedContact.value.applicationId) {
    return [{
      applicationId: selectedContact.value.applicationId,
      institution: selectedContact.value.institution,
      major: selectedContact.value.major,
      applicationStatus: selectedContact.value.applicationStatus,
      applicationStatusDisplay: selectedContact.value.applicationStatusDisplay,
      applicationDate: selectedContact.value.applicationDate
    }]
  }
  return []
})

const formatApplicationOption = (app) => {
  const school = app?.institution || '未知院校'
  const major = app?.major ? ` - ${app.major}` : ''
  return `${school}${major}`
}

const applicationStatusText = (contact) => {
  const display = contact?.applicationStatusDisplay
  if (display) return display
  const raw = String(contact?.applicationStatus || '').toLowerCase()
  if (raw === 'pending' || raw === 'under_review' || raw === 'reviewing') return '待审核'
  if (raw === 'approved' || raw === 'enrolled' || raw === 'offer') return '已通过'
  if (raw === 'rejected' || raw === 'refused') return '已拒绝'
  return contact?.applicationStatus || '申请中'
}

const applicationStatusClass = (contact) => {
  const raw = String(contact?.applicationStatus || '').toLowerCase()
  if (raw === 'pending' || raw === 'under_review' || raw === 'reviewing') return 'pending'
  if (raw === 'approved' || raw === 'enrolled' || raw === 'offer') return 'approved'
  if (raw === 'rejected' || raw === 'refused') return 'rejected'
  return 'default'
}

const onSelectedApplicationChange = (event) => {
  const nextApplicationId = event?.target?.value
  selectedApplicationId.value = nextApplicationId

  if (!selectedContact.value || selectedContact.value.type !== 'application') return

  const nextApp = applicationOptions.value.find(a => String(a.applicationId) === String(nextApplicationId))
  if (nextApp) {
    selectedContact.value.applicationId = nextApp.applicationId
    selectedContact.value.institution = nextApp.institution
    selectedContact.value.major = nextApp.major
    selectedContact.value.applicationStatus = nextApp.applicationStatus
    selectedContact.value.applicationStatusDisplay = nextApp.applicationStatusDisplay
    selectedContact.value.applicationDate = nextApp.applicationDate

    if (selectedContact.value.lastMessage?.isStatusStub) {
      const createdAt = toDate(nextApp.applicationDate)
      const statusText = nextApp.applicationStatusDisplay || nextApp.applicationStatus || '待审核'
      selectedContact.value.lastMessage = {
        content: `申请状态: ${statusText}`,
        time: formatTime(createdAt || nextApp.applicationDate),
        createTime: createdAt?.toISOString() || nextApp.applicationDate,
        timestamp: nextApp.timestamp || createdAt?.getTime() || 0,
        isStatusStub: true
      }
    }
  }

  fetchMessageHistory(selectedContact.value.id, selectedApplicationId.value)
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedContact.value) return

  try {
    // 获取当前管理员信息
    let currentUserId = 1
    let currentUserName = '管理员'
    try {
      const adminInfo = localStorage.getItem('adminInfo')
      if (adminInfo) {
        const currentUser = JSON.parse(adminInfo)
        currentUserId = currentUser.id
        currentUserName = currentUser.nickname || currentUser.name || currentUser.username || '管理员'
      }
    } catch (error) {
      console.error('解析管理员信息失败:', error)
    }

    // 构建消息数据
    const messageData = {
      senderId: currentUserId,
      senderName: currentUserName,
      senderType: 'teacher',
      receiverId: selectedContact.value.id,
      receiverName: selectedContact.value.name,
      receiverType: selectedContact.value.type === 'application' ? 'student' : selectedContact.value.type,
      title: '消息通知',
      content: newMessage.value.trim(),
      messageType: 'general_message',
      priority: 'normal',
      status: 'active'
    }

    // 如果是申请学生，添加申请ID
    if (selectedContact.value.type === 'application' && selectedApplicationId.value) {
      messageData.applicationId = selectedApplicationId.value
    }

    // 发送消息到后端
    const res = await request.post('/messages', messageData)

    if (res) {
      // 立即添加消息到本地历史记录，确保实时显示
      const newMsg = {
        id: res.data?.id || Date.now(),
        content: newMessage.value.trim(),
        time: formatTime(new Date()),
        isSent: true,
        read: false,
        sender: currentUserName,
        receiver: selectedContact.value.name,
        senderId: currentUserId,
        receiverId: selectedContact.value.id,
        createTime: new Date().toISOString(),
        applicationId: messageData.applicationId
      }

      // 确保消息按时间顺序插入
      messageHistory.value.push(newMsg)
      messageHistory.value.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))

      // 更新联系人最后一条消息
      const lastMessageInfo = {
        content: newMessage.value.trim(),
        time: '刚刚',
        createTime: new Date().toISOString(),
        isStatusStub: false
      }

      if (selectedContact.value.type === 'application') {
        const contactIndex = applicationStudents.value.findIndex(c => c.id === selectedContact.value.id)
        if (contactIndex !== -1) {
          applicationStudents.value[contactIndex].lastMessage = lastMessageInfo
          applicationStudents.value[contactIndex].unreadCount = 0 // 重置未读数
        }
      } else {
        const contactIndex = contacts.value.findIndex(c => c.id === selectedContact.value.id)
        if (contactIndex !== -1) {
          contacts.value[contactIndex].lastMessage = lastMessageInfo
          contacts.value[contactIndex].unreadCount = 0 // 重置未读数
        }
      }

      // 清空输入框
      newMessage.value = ''

      ElMessage.success('消息发送成功')

      // 立即滚动到底部
      nextTick(() => {
        if (messagesList.value) {
          messagesList.value.scrollTop = messagesList.value.scrollHeight
        }
      })
    } else {
      throw new Error('发送失败')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    await notificationStore.markAllAsRead()
    ElMessage.success('已将所有通知标记为已读')
  } catch (error) {
    console.error('标记通知为已读失败:', error)
    ElMessage.error('标记通知为已读失败')
  }
}

// 查看通知详情
const viewNotificationDetail = async (notification) => {
  try {
    if (!notification.read) await notificationStore.markAsRead(notification.id)
    ElMessage.info(`查看通知: ${notification.title}`)
  } catch (error) {
    console.error('更新通知状态失败:', error)
    ElMessage.error('更新通知状态失败')
  }
}

// 格式化日期
const formatDate = (date) => {
  const d = toDate(date)
  if (!d) return ''
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

// 格式化时间
const formatTime = (date) => {
  const d = toDate(date)
  if (!d) return ''
  const now = new Date()
  const diff = now - d

  // 今天的消息显示时间
  if (diff < 24 * 60 * 60 * 1000 && d.getDate() === now.getDate()) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }

  // 昨天的消息
  if (diff < 48 * 60 * 60 * 1000 && now.getDate() - d.getDate() === 1) {
    return `昨天 ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
  }

  // 更早的消息
  return formatDate(d)
}

// 不需要此函数，统计数据由notificationStore自动管理

// 应用筛选条件
const applyFilters = () => {
  showFilterModal.value = false
  ElMessage.success('筛选条件已应用')
}

// 重置筛选条件
const resetFilters = () => {
  filters.type = ''
  filters.priority = ''
  filters.read = ''
  filters.timeRange = []
  showFilterModal.value = false
  ElMessage.success('筛选条件已重置')
}

// 引用DOM元素
const messagesList = ref(null)

// 生成头像背景色
const generateAvatarColor = (name) => {
  if (!name) return '#1890ff'

  // 简单哈希算法
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }

  // 转为HSL色调（120°范围内的蓝绿色调）
  const h = Math.abs(hash % 120) + 180
  return `hsl(${h}, 70%, 50%)`
}

// 初始化
onMounted(async () => {
  // 滚动消息到底部
  if (messagesList.value) {
    messagesList.value.scrollTop = messagesList.value.scrollHeight
  }

  // 获取申请学生列表
  await fetchApplicationStudents()

  // 获取联系人列表
  await fetchContacts()

  // 获取通知列表
  notificationStore.fetchNotifications()
})
</script>

<style scoped>
.notice-center {
  padding: 0px;
  height: calc(100vh - 80px);
}

/* 主容器布局 */
.communication-container {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 280px;
  gap: 16px;
  height: calc(100vh - 140px);
  margin-bottom: 20px;
  position: relative;
}

.communication-container.contacts-collapsed {
  grid-template-columns: minmax(0, 1fr) 280px;
}

.contacts-collapsed-handle {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 14px;
  z-index: 30;
}

.contacts-overlay {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 220px;
  z-index: 25;
  pointer-events: auto;
}

.contacts-panel.contacts-panel-overlay {
  height: 100%;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.18);
}

.contacts-overlay-enter-active,
.contacts-overlay-leave-active {
  transition: opacity 220ms ease, transform 220ms ease;
}

.contacts-overlay-enter-from,
.contacts-overlay-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}

.contacts-overlay-enter-to,
.contacts-overlay-leave-from {
  opacity: 1;
  transform: translateX(0);
}

.contacts-panel-enter-active,
.contacts-panel-leave-active {
  transition: opacity 240ms ease, transform 240ms ease;
}

.contacts-panel-enter-from,
.contacts-panel-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.contacts-panel-enter-to,
.contacts-panel-leave-from {
  opacity: 1;
  transform: translateX(0);
}

.contacts-panel-leave-active {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 220px;
  z-index: 20;
}

/* 联系人面板 */
.contacts-panel {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.contacts-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.contacts-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.contacts-collapse-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background-color: #f5f7fa;
  font-size: 16px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #333;
}

.contacts-search {
  position: relative;
  margin-bottom: 12px;
}

.contacts-search-input {
  width: 100%;
  padding: 8px 12px 8px 32px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 13px;
}

.contact-multi-app {
  margin-left: 6px;
  font-size: 12px;
  color: #999;
}

.conversation-app-select-wrapper {
  margin-top: 6px;
  position: relative;
  width: fit-content;
  display: flex;
  align-items: center;
  gap: 8px;
}

.conversation-app-label {
  font-size: 12px;
  color: #999;
}

.conversation-app-select {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 6px 28px 6px 10px;
  font-size: 12px;
  color: #666;
  background: #fff;
  outline: none;
  appearance: none;
}

.conversation-app-select-icon {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  color: #999;
  pointer-events: none;
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  width: 14px;
  height: 14px;
}

.contacts-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  margin: 0 -16px;
}

.contact-tab {
  flex: 1;
  padding: 8px 0;
  background: none;
  border: none;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  position: relative;
}

.contact-tab.active {
  color: #1890ff;
  font-weight: 500;
}

.contact-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  width: 60%;
  height: 2px;
  background-color: #1890ff;
}

.contacts-list {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.contact-item:hover {
  background-color: #f9f9f9;
}

.contact-item.active {
  background-color: #e6f7ff;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 12px;
  position: relative;
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 500;
  color: white;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 18px;
  height: 18px;
  background-color: #f5222d;
  border-radius: 50%;
  color: white;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.contact-name-row {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-name {
  font-weight: 500;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-pill {
  height: 18px;
  min-width: 18px;
  padding: 0 6px;
  border-radius: 999px;
  background-color: #f5222d;
  color: #fff;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-time {
  font-size: 12px;
  color: #b0b0b0;
  white-space: nowrap;
}

.contact-middle-row {
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.contact-institution-inline {
  flex: 1;
  min-width: 0;
  font-size: 12px;
  color: #888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-role-badge {
  font-size: 11px;
  color: #666;
  background-color: #f5f7fa;
  border: 1px solid #e8e8e8;
  padding: 2px 8px;
  border-radius: 999px;
  white-space: nowrap;
}

.application-status {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}

.application-status.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.application-status.approved {
  background-color: #f6ffed;
  color: #52c41a;
}

.application-status.rejected {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.application-status.default {
  background-color: #f5f7fa;
  color: #666;
}

.contact-preview {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #d9d9d9;
  margin-left: 8px;
}

.contact-status.online {
  background-color: #52c41a;
}

.contact-status.away {
  background-color: #fa8c16;
}

/* 消息面板 */
.messages-panel {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.messages-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.conversation-info {
  flex: 1;
}

.conversation-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #333;
}

.conversation-meta {
  font-size: 12px;
  color: #999;
}

.conversation-actions {
  display: flex;
  gap: 8px;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background-color: #f5f7fa;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 18px;
}

.btn-icon.contacts-collapse-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  font-size: 16px;
}

.btn-icon:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

.composer-actions .btn-icon {
  width: 36px;
  height: 36px;
  border: 1px solid #e8e8e8;
  background-color: white;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #f5f7fa;
}

.message-date-divider {
  display: flex;
  align-items: center;
  margin: 16px 0;
  color: #999;
  font-size: 12px;
}

.message-date-divider::before,
.message-date-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #e8e8e8;
}

.message-date-divider span {
  padding: 0 10px;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
  max-width: 80%;
}

.message-sent {
  flex-direction: row-reverse;
  align-self: flex-end;
  margin-left: auto;
}

.message-received {
  align-self: flex-start;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
}

.message-sent .message-avatar {
  margin-right: 0;
  margin-left: 8px;
}

.message-content {
  position: relative;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 18px;
  position: relative;
  font-size: 14px;
  line-height: 1.5;
  max-width: 100%;
}

.message-sent .message-bubble {
  background-color: #e6f7ff;
  color: #333;
  border-top-right-radius: 4px;
}

.message-received .message-bubble {
  background-color: white;
  color: #333;
  border-top-left-radius: 4px;
}

.message-time {
  font-size: 10px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.message-status {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 2px;
}

.status-icon {
  width: 14px;
  height: 14px;
}

.status-icon.sent {
  color: #d9d9d9;
}

.status-icon.read {
  color: #1890ff;
}

.message-attachment {
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 4px 8px;
  border-radius: 4px;
  margin-top: 6px;
}

.attachment-icon {
  width: 14px;
  height: 14px;
  margin-right: 6px;
  color: #1890ff;
}

.attachment-name {
  font-size: 12px;
  color: #1890ff;
}

.message-composer {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-top: 1px solid #f0f0f0;
  background-color: white;
}

.composer-actions {
  display: flex;
  gap: 10px;
  margin-right: 14px;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 22px;
  resize: none;
  max-height: 120px;
  min-height: 22px;
  font-size: 14px;
  line-height: 1.5;
  outline: none;
  transition: border-color 0.3s;
}

.message-input:focus {
  border-color: #1890ff;
}

.send-button {
  margin-left: 14px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #1890ff;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.send-button:hover {
  background-color: #40a9ff;
}

.send-button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.empty-messages {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
  color: #999;
}

.empty-messages-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: #d9d9d9;
}

.empty-messages h3 {
  font-size: 16px;
  margin-bottom: 8px;
  color: #666;
}

.empty-messages p {
  font-size: 14px;
  color: #999;
}

/* 通知面板 */
.notifications-panel {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notifications-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-actions {
  display: flex;
  gap: 8px;
}

.notifications-list {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.notification-item {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
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
  line-clamp: 2;
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

/* 过滤器模态框 */
.filter-modal {
  width: 380px;
  max-width: 90vw;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 16px;
}

.filter-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #f0f0f0;
}

.btn-secondary {
  padding: 6px 16px;
  background-color: white;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
}

.btn-primary {
  padding: 6px 16px;
  background-color: #1890ff;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .communication-container {
    grid-template-columns: 200px minmax(0, 1fr) 260px;
    height: calc(100vh - 120px);
  }

  .communication-container.contacts-collapsed {
    grid-template-columns: minmax(0, 1fr) 260px;
  }

  .contacts-overlay {
    width: 200px;
  }
}

@media (max-width: 992px) {
  .communication-container {
    grid-template-columns: 240px 1fr;
    height: calc(100vh - 120px);
  }

  .communication-container.contacts-collapsed {
    grid-template-columns: 1fr;
  }

  .contacts-overlay {
    width: 240px;
  }

  .notifications-panel {
    display: none;
  }
}

@media (max-width: 768px) {
  .communication-container {
    grid-template-columns: 1fr;
    height: calc(100vh - 100px);
  }

  .contacts-collapsed-handle {
    display: none;
  }

  .contacts-panel {
    display: none;
  }

  .messages-panel {
    height: calc(100vh - 120px);
  }
}
</style>
