# 申请管理模块化重构

为了优化代码结构和提高可维护性，我们对原有的ApplicationManagement.vue文件进行了模块化重构，将四个主要标签页内容拆分为独立组件：

## 新增组件

1. **ApplicationBasicInfo.vue** - 基本信息标签页，显示申请的ID、学位类型、批次等基础信息
2. **ApplicationMaterials.vue** - 申请材料标签页，管理材料完成度和材料列表
3. **ApplicationMonitor.vue** - 申请监控标签页，提供状态监控、问题处理和快速操作
4. **ApplicationResult.vue** - 录取结果标签页，显示面试结果和相关信息

## 优化内容

1.  将原有的超长 `ApplicationManagement.vue` 拆分为更小、更易维护的组件。
2.  提高了代码复用性，每个组件专注于自己的功能。
3.  优化了申请监控页面，专注于帮助管理员追踪：
    *   材料投递状态
    *   Offer等待情况
    *   签证信息提供
    *   学生活跃度
    *   问题处理
    *   快速操作
4.  **统一管理通用工具函数**：将如日期格式化 (`formatDate`)、计算剩余天数 (`getRemainingDays`)、判断截止日期紧急性 (`isDeadlineUrgent`) 等通用函数集中在父组件 `ApplicationManagement.vue` 中，并通过 props (如 `formatDateFn`, `getRemainingDaysFn`, `isDeadlineUrgentFn`) 传递给子组件 (`ApplicationBasicInfo.vue`, `ApplicationResult.vue`)，避免了在子组件中重复定义，增强了代码的一致性和可维护性。
5.  **实现了子组件到父组件的事件通信机制**：例如，在 `ApplicationResult.vue` 中发送签证信息的操作，改为由子组件触发一个自定义事件 (`request-send-visa-info`)，父组件 `ApplicationManagement.vue` 监听此事件并通过 `handleSendVisaInfoRequest` 方法统一处理业务逻辑（包括更新申请状态 `visaInfoProvided` 和用户提示），使得组件职责更清晰，降低了耦合度。

## 使用方法

所有核心子组件 (`ApplicationBasicInfo.vue`, `ApplicationMaterials.vue`, `ApplicationMonitor.vue`, `ApplicationResult.vue`) 都接受一个 `application` 对象作为 props。此外，为了实现功能和逻辑的统一管理，父组件 `ApplicationManagement.vue` 还会向子组件传递必要的工具函数和监听子组件发出的事件。

例如，`ApplicationResult.vue` 的引用方式如下：

```vue
<ApplicationResult
  :application="selectedApplication"
  :formatDateFn="formatDate"
  :getRemainingDaysFn="getRemainingDays"
  :isDeadlineUrgentFn="isDeadlineUrgent"
  @request-send-visa-info="handleSendVisaInfoRequest"
/>
```

-   **Props 传递**:
    -   `:application`: 核心的申请数据对象。
    -   `:formatDateFn`: 用于格式化日期的函数。
    -   `:getRemainingDaysFn`: 用于计算剩余天数的函数。
    -   `:isDeadlineUrgentFn`: 用于判断截止日期是否紧急的函数。
-   **事件监听**:
    -   `@request-send-visa-info`: 监听子组件发出的“请求发送签证信息”事件，并由 `handleSendVisaInfoRequest` 方法处理。

子组件内部通过 `defineProps` 接收这些函数，并通过 `emit` 触发事件：

```javascript
// 在 ApplicationResult.vue
const props = defineProps({
  application: Object,
  formatDateFn: Function,
  getRemainingDaysFn: Function,
  isDeadlineUrgentFn: Function
});

const emit = defineEmits(['request-send-visa-info']);

const sendVisaInfo = () => {
  emit('request-send-visa-info', props.application);
};
```

## 注意事项

- 使用前需确保导入相应组件
- 为确保组件正常工作，application对象需要包含所需属性 