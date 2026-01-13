import { ref, computed } from 'vue';
import request from '@/utils/request';

// 默认通知数据
const defaultNotifications = [
    {
        id: 1,
        type: 'system',
        title: '系统维护通知',
        message: '系统将于今晚22:00-24:00进行例行维护，请提前保存重要数据。',
        time: '10分钟前',
        createdAt: new Date(new Date().getTime() - 10 * 60 * 1000),
        read: false,
        priority: 'important',
        priorityText: '重要',
        category: '系统通知'
    },
    {
        id: 2,
        type: 'application',
        title: '申请材料提醒',
        message: '张明的哈佛大学申请材料已经准备完成，请及时审核。',
        time: '30分钟前',
        createdAt: new Date(new Date().getTime() - 30 * 60 * 1000),
        read: false,
        priority: 'urgent',
        priorityText: '紧急',
        category: '申请相关'
    },
    {
        id: 3,
        type: 'message',
        title: '李华的咨询消息',
        message: '李华询问关于托福考试准备的问题，请及时回复。',
        time: '2小时前',
        createdAt: new Date(new Date().getTime() - 2 * 60 * 60 * 1000),
        read: true,
        priority: 'normal',
        priorityText: '普通',
        category: '私信消息'
    },
    {
        id: 4,
        type: 'system',
        title: '新功能上线通知',
        message: '系统新增申请材料智能审核功能，现在可以自动检查材料完整性。',
        time: '昨天',
        createdAt: new Date(new Date().getTime() - 24 * 60 * 60 * 1000),
        read: true,
        priority: 'important',
        priorityText: '重要',
        category: '系统通知'
    },
    {
        id: 5,
        type: 'application',
        title: '申请截止日期提醒',
        message: '多所学校2023年秋季入学申请将于下周截止，请提醒相关学生尽快完成。',
        time: '3天前',
        createdAt: new Date(new Date().getTime() - 3 * 24 * 60 * 60 * 1000),
        read: true,
        priority: 'urgent',
        priorityText: '紧急',
        category: '申请相关'
    }
];

// 创建一个共享状态
const notifications = ref(defaultNotifications);
const initialized = ref(false);
const loading = ref(false);

const getCurrentUserId = () => {
    let currentUserId = 1;
    try {
        const adminInfo = localStorage.getItem('adminInfo');
        if (adminInfo) {
            const currentUser = JSON.parse(adminInfo);
            if (currentUser?.id !== null && currentUser?.id !== undefined) {
                currentUserId = currentUser.id;
            }
        }
    } catch (error) {
        currentUserId = 1;
    }
    return currentUserId;
};

const formatRelativeTime = (value) => {
    const date = value instanceof Date ? value : new Date(value);
    if (Number.isNaN(date.getTime())) return '';

    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffSeconds = Math.floor(diffMs / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffSeconds < 30) return '刚刚';
    if (diffMinutes < 60) return `${diffMinutes}分钟前`;
    if (diffHours < 24) return `${diffHours}小时前`;
    if (diffDays === 1) return '昨天';
    if (diffDays < 30) return `${diffDays}天前`;
    return date.toLocaleDateString('zh-CN');
};

const normalizeType = (raw) => {
    const text = String(raw || '').toLowerCase();
    if (text.includes('application')) return 'application';
    if (text.includes('message')) return 'message';
    if (text.includes('system') || text.includes('notification')) return 'system';
    return 'system';
};

const normalizePriority = (raw) => {
    const text = String(raw || '').toLowerCase();
    if (text === 'urgent' || text === 'high') return 'urgent';
    if (text === 'important' || text === 'medium') return 'important';
    if (text === 'normal' || text === 'low') return 'normal';
    return 'normal';
};

const priorityTextByValue = (priority) => {
    if (priority === 'urgent') return '紧急';
    if (priority === 'important') return '重要';
    return '普通';
};

const normalizeNotification = (msg) => {
    const createdAt = msg?.createTime || msg?.createdAt || msg?.time || msg?.created_at;
    const createdAtDate = createdAt instanceof Date ? createdAt : new Date(createdAt);
    const safeCreatedAt = Number.isNaN(createdAtDate.getTime()) ? new Date() : createdAtDate;

    const priority = normalizePriority(msg?.priority);
    const type = normalizeType(msg?.messageType || msg?.type);

    return {
        id: msg?.id,
        type,
        title: msg?.title || '系统通知',
        message: msg?.content ?? msg?.message ?? '',
        time: formatRelativeTime(safeCreatedAt),
        createdAt: safeCreatedAt,
        read: Boolean(msg?.read) || msg?.status === 'read' || msg?.isRead === true,
        priority,
        priorityText: priorityTextByValue(priority),
        category: msg?.category || (type === 'application' ? '申请相关' : type === 'message' ? '私信消息' : '系统通知')
    };
};

const fetchNotifications = async (options = {}) => {
    loading.value = true;
    try {
        const userId = options.userId ?? getCurrentUserId();
        const response = await request.get('/messages/type/notification', {
            params: { userId }
        });

        const list = Array.isArray(response?.data) ? response.data : [];
        if (list.length > 0) {
            notifications.value = list.map(normalizeNotification);
        } else {
            notifications.value = [];
        }
        initialized.value = true;
    } catch (error) {
        if (!initialized.value) {
            notifications.value = defaultNotifications;
            initialized.value = true;
        }
    } finally {
        loading.value = false;
    }
};

// 统计
const statistics = computed(() => {
    const unreadCount = notifications.value.filter(notice => !notice.read).length;
    const urgentCount = notifications.value.filter(notice => notice.priority === 'urgent').length;
    const applicationCount = notifications.value.filter(notice => notice.type === 'application').length;

    return {
        total: notifications.value.length,
        unread: unreadCount,
        urgent: urgentCount,
        application: applicationCount
    };
});

// 添加新通知
const addNotification = (notification) => {
    // 生成一个唯一的ID
    const newId = Math.max(...notifications.value.map(n => n.id), 0) + 1;

    // 设置默认值
    const newNotification = {
        id: newId,
        createdAt: new Date(),
        read: false,
        ...notification
    };

    // 添加到通知列表最前面
    notifications.value.unshift(newNotification);

    return newNotification;
};

// 标记通知为已读
const markAsRead = async (notificationId) => {
    const index = notifications.value.findIndex(n => n.id === notificationId);
    if (index !== -1) {
        notifications.value[index].read = true;
    }

    try {
        await request.put(`/messages/${notificationId}`, { status: 'read' });
    } catch (error) {
    }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
    const unread = notifications.value.filter(n => !n.read);
    notifications.value.forEach(notice => {
        notice.read = true;
    });

    const updatePromises = unread.map(notice => request.put(`/messages/${notice.id}`, { status: 'read' }));
    try {
        await Promise.all(updatePromises);
    } catch (error) {
    }
};

// 删除通知
const deleteNotification = (notificationId) => {
    const index = notifications.value.findIndex(n => n.id === notificationId);
    if (index !== -1) {
        notifications.value.splice(index, 1);
    }
};

// 清空所有通知
const clearAllNotifications = () => {
    notifications.value = [];
};

// 按条件获取通知
const getNotifications = (filters = {}) => {
    let result = [...notifications.value];

    // 按类型过滤
    if (filters.type) {
        result = result.filter(n => n.type === filters.type);
    }

    // 按优先级过滤
    if (filters.priority) {
        result = result.filter(n => n.priority === filters.priority);
    }

    // 按已读状态过滤
    if (filters.read !== undefined) {
        result = result.filter(n => n.read === filters.read);
    }

    // 按时间排序（默认最新的在前）
    result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    // 返回指定数量
    return filters.limit ? result.slice(0, filters.limit) : result;
};

export default {
    notifications,
    initialized,
    loading,
    statistics,
    fetchNotifications,
    addNotification,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    clearAllNotifications,
    getNotifications
}; 
