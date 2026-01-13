<template>
  <div class="permission-content">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">权限设置</h2>
        <p class="page-subtitle">管理用户角色及其系统权限</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="showImportModal = true">
          <Upload class="button-icon" />
          导入配置
        </el-button>
        <el-button class="custom-button" type="primary" @click="showAddRoleModal = true">
          <Plus class="button-icon" />
          添加角色
        </el-button>
      </div>
    </div>

    <!-- 角色列表 -->
    <div class="roles-section">
      <div class="section-header">
        <h3 class="section-title">角色管理</h3>
        <div class="section-actions">
          <button class="action-button" @click="refreshRoles">
            <RefreshCw class="button-icon-sm" />
            刷新
          </button>
        </div>
      </div>

      <div class="roles-list">
        <div 
          v-for="role in roles" 
          :key="role.id" 
          class="role-card" 
          :class="{ active: activeRole === role.id }"
          @click="activeRole = role.id"
        >
          <div class="role-icon" :style="{ backgroundColor: role.color }">
            <component :is="role.icon" />
          </div>
          <div class="role-info">
            <h4 class="role-name">{{ role.name }}</h4>
            <p class="role-description">{{ role.description }}</p>
            <div class="role-users">
              <Users class="user-icon" />
              <span>{{ role.userCount }} 名用户</span>
            </div>
          </div>
          <div class="role-actions">
            <button class="btn-icon-sm" @click.stop="editRole(role)" title="编辑角色">
              <Edit />
            </button>
            <button class="btn-icon-sm" :disabled="role.isDefault" @click.stop="deleteRole(role)" title="删除角色">
              <Trash2 />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 权限设置 -->
    <div class="permissions-section">
      <div class="section-header">
        <h3 class="section-title">
          权限设置
          <span v-if="getActiveRoleName()" class="subtitle">- {{ getActiveRoleName() }}</span>
        </h3>
        <div class="section-actions">
          <button class="action-button-primary" @click="savePermissions">
            <Save class="button-icon-sm" />
            保存更改
          </button>
        </div>
      </div>

      <div v-if="activeRole" class="permissions-container">
        <div v-for="(modulePerms, moduleName) in permissions" :key="moduleName" class="permission-module">
          <div class="module-header">
            <div class="module-title">
              <component :is="getModuleIcon(moduleName)" class="module-icon" />
              <h4>{{ getModuleName(moduleName) }}</h4>
            </div>
            <div class="module-toggle">
              <div class="switch">
                <input 
                  type="checkbox" 
                  :id="`module-${moduleName}`" 
                  :checked="isModuleChecked(moduleName)"
                  @change="toggleModule(moduleName)"
                />
                <label :for="`module-${moduleName}`"></label>
              </div>
            </div>
          </div>

          <div class="permission-list">
            <div v-for="(perm, index) in modulePerms" :key="index" class="permission-item">
              <div class="permission-info">
                <h5>{{ perm.name }}</h5>
                <p>{{ perm.description }}</p>
              </div>
              <div class="permission-toggle">
                <div class="switch">
                  <input 
                    type="checkbox" 
                    :id="`perm-${moduleName}-${index}`" 
                    v-model="perm.enabled"
                    @change="updatePermission(moduleName, index)"
                  />
                  <label :for="`perm-${moduleName}-${index}`"></label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="select-role-prompt">
        <Shield class="prompt-icon" />
        <p>请选择一个角色以配置其权限</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Upload,
  Plus,
  Users,
  RefreshCw,
  Edit,
  Trash2,
  Save,
  Shield,
  Settings,
  FileText,
  User,
  Building,
  BarChart,
  DollarSign,
  Calendar
} from 'lucide-vue-next'

// 角色数据
const roles = ref([
  {
    id: 1,
    name: '超级管理员',
    description: '拥有所有系统权限',
    userCount: 3,
    isDefault: true,
    color: '#1890ff',
    icon: Shield
  },
  {
    id: 2,
    name: '普通管理员',
    description: '拥有大部分系统权限',
    userCount: 8,
    isDefault: true,
    color: '#52c41a',
    icon: User
  },
  {
    id: 3,
    name: '咨询顾问',
    description: '负责学生咨询和申请跟踪',
    userCount: 15,
    isDefault: false,
    color: '#fa8c16',
    icon: FileText
  },
  {
    id: 4,
    name: '财务人员',
    description: '负责财务管理和支付处理',
    userCount: 5,
    isDefault: false,
    color: '#eb2f96',
    icon: DollarSign
  },
  {
    id: 5,
    name: '数据分析师',
    description: '负责数据分析和报表生成',
    userCount: 4,
    isDefault: false,
    color: '#722ed1',
    icon: BarChart
  }
])

// 权限数据
const permissions = ref({
  dashboard: [
    { name: '查看仪表盘', description: '允许查看管理仪表盘', enabled: true },
    { name: '查看统计数据', description: '允许查看汇总统计数据', enabled: true }
  ],
  users: [
    { name: '查看用户', description: '允许查看用户列表', enabled: true },
    { name: '添加用户', description: '允许添加新用户', enabled: true },
    { name: '编辑用户', description: '允许编辑用户信息', enabled: true },
    { name: '删除用户', description: '允许删除用户账号', enabled: false }
  ],
  applications: [
    { name: '查看申请', description: '允许查看申请列表', enabled: true },
    { name: '处理申请', description: '允许处理和更新申请状态', enabled: true },
    { name: '删除申请', description: '允许删除申请记录', enabled: false }
  ],
  institutions: [
    { name: '查看院校', description: '允许查看院校列表', enabled: true },
    { name: '添加院校', description: '允许添加新院校', enabled: true },
    { name: '编辑院校', description: '允许编辑院校信息', enabled: true },
    { name: '删除院校', description: '允许删除院校', enabled: false }
  ],
  schedule: [
    { name: '查看日程', description: '允许查看日程安排', enabled: true },
    { name: '添加日程', description: '允许添加新日程', enabled: true },
    { name: '编辑日程', description: '允许编辑日程信息', enabled: true },
    { name: '删除日程', description: '允许删除日程', enabled: true }
  ],
  finance: [
    { name: '查看财务', description: '允许查看财务数据', enabled: true },
    { name: '记录交易', description: '允许记录新交易', enabled: false },
    { name: '编辑交易', description: '允许编辑交易记录', enabled: false },
    { name: '删除交易', description: '允许删除交易记录', enabled: false },
    { name: '生成报表', description: '允许生成财务报表', enabled: true }
  ],
  analytics: [
    { name: '查看报表', description: '允许查看数据分析报表', enabled: true },
    { name: '导出数据', description: '允许导出分析数据', enabled: true },
    { name: '配置报表', description: '允许配置和自定义报表', enabled: false }
  ],
  settings: [
    { name: '系统设置', description: '允许修改系统设置', enabled: false },
    { name: '权限管理', description: '允许管理角色和权限', enabled: false },
    { name: '查看日志', description: '允许查看系统日志', enabled: true }
  ]
})

// 当前激活的角色
const activeRole = ref(1)

// 模态框状态
const showAddRoleModal = ref(false)
const showImportModal = ref(false)

// 获取激活的角色名称
const getActiveRoleName = () => {
  const role = roles.value.find(r => r.id === activeRole.value)
  return role ? role.name : ''
}

// 获取模块图标
const getModuleIcon = (moduleName) => {
  const iconMap = {
    dashboard: Settings,
    users: User,
    applications: FileText,
    institutions: Building,
    schedule: Calendar,
    finance: DollarSign,
    analytics: BarChart,
    settings: Settings
  }
  return iconMap[moduleName] || Settings
}

// 获取模块名称
const getModuleName = (moduleName) => {
  const nameMap = {
    dashboard: '控制台',
    users: '用户管理',
    applications: '申请管理',
    institutions: '院校管理',
    schedule: '日程安排',
    finance: '财务管理',
    analytics: '数据分析',
    settings: '系统设置'
  }
  return nameMap[moduleName] || moduleName
}

// 检查模块是否全部选中
const isModuleChecked = (moduleName) => {
  return permissions.value[moduleName].every(perm => perm.enabled)
}

// 切换模块所有权限
const toggleModule = (moduleName) => {
  const isChecked = isModuleChecked(moduleName)
  permissions.value[moduleName].forEach(perm => {
    perm.enabled = !isChecked
  })
}

// 更新权限
const updatePermission = (moduleName, index) => {
  // 这里可以添加权限变更的逻辑，例如检查依赖关系
  console.log(`更新权限: ${moduleName}.${index}`)
}

// 刷新角色列表
const refreshRoles = () => {
  ElMessage.success('角色列表已刷新')
}

// 编辑角色
const editRole = (role) => {
  console.log('编辑角色', role)
  // 显示编辑角色模态框
}

// 删除角色
const deleteRole = (role) => {
  if (role.isDefault) {
    ElMessage.error('无法删除默认角色')
    return
  }
  
  console.log('删除角色', role)
  ElMessage.success(`角色"${role.name}"已删除`)
}

// 保存权限设置
const savePermissions = () => {
  ElMessage.success('权限设置已保存')
}
</script>

<style scoped>
/* 权限设置页面样式 */
.permission-content {
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

.button-icon-sm {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

/* 区块样式 */
.roles-section,
.permissions-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.subtitle {
  font-size: 14px;
  font-weight: normal;
  color: #666;
  margin-left: 8px;
}

.section-actions {
  display: flex;
  gap: 12px;
}

.action-button {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  background-color: white;
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-button:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.action-button-primary {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #1890ff;
  background-color: #1890ff;
  display: flex;
  align-items: center;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-button-primary:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

/* 角色列表 */
.roles-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.role-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.role-card:hover {
  border-color: #d9d9d9;
  background-color: #fafafa;
}

.role-card.active {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.role-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.role-info {
  flex: 1;
  overflow: hidden;
}

.role-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.role-description {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-users {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.user-icon {
  width: 12px;
  height: 12px;
}

.role-actions {
  display: flex;
  gap: 8px;
}

.btn-icon-sm {
  width: 28px;
  height: 28px;
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
  background-color: rgba(0, 0, 0, 0.05);
  color: #1890ff;
}

.btn-icon-sm:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

/* 权限设置 */
.permissions-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.permission-module {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.module-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.module-title h4 {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  color: #333;
}

.module-icon {
  width: 18px;
  height: 18px;
  color: #1890ff;
}

.permission-list {
  padding: 0 16px;
}

.permission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.permission-item:last-child {
  border-bottom: none;
}

.permission-info {
  flex: 1;
}

.permission-info h5 {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.permission-info p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.switch label {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  border-radius: 20px;
  cursor: pointer;
  transition: .4s;
}

.switch label:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

.switch input:checked + label {
  background-color: #1890ff;
}

.switch input:checked + label:before {
  transform: translateX(20px);
}

/* 选择角色提示 */
.select-role-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
}

.prompt-icon {
  width: 48px;
  height: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .roles-list {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .section-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .permission-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .permission-toggle {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 