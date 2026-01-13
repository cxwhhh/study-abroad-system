# 新英美留学项目结构

本文档提供新英美留学系统的项目结构总览，帮助开发者快速了解代码组织和功能模块。

## 项目概述

新英美留学是一个全功能的留学申请管理系统，包含学生端和管理员端，支持留学申请全流程管理、学校信息浏览、申请材料管理、进度跟踪和录取管理等功能。

## 技术栈

- **前端**：Vue.js 3 + Element Plus + Tailwind CSS
- **后端**：Spring Boot + MyBatis
- **数据库**：MySQL

## 详细目录结构

```
xingyingmei/
├── src/                                 # 源代码目录
│   ├── api/                             # 前端API接口定义
│   │   ├── facultyService.js            # 学院API服务
│   │   ├── logs.js                      # 日志API服务
│   │   ├── majorService.js              # 专业API服务
│   │   └── schoolService.js             # 学校API服务
│   │
│   ├── components/                      # Vue组件
│   │   ├── FooterBar.vue                # 页脚组件
│   │   ├── Login.vue                    # 登录组件
│   │   ├── NavBar.vue                   # 导航栏组件
│   │   │
│   │   └── admin/                       # 管理员端组件
│   │       ├── analytics/               # 数据分析组件
│   │       ├── application/             # 申请管理组件
│   │       │   ├── AddApplicationModal.vue
│   │       │   ├── ApplicationBasicInfo.vue
│   │       │   ├── ApplicationDetailModal.vue
│   │       │   ├── ApplicationEditModal.vue
│   │       │   ├── ApplicationManagement.vue
│   │       │   ├── ApplicationMaterials.vue
│   │       │   ├── ApplicationMonitor.vue
│   │       │   ├── ApplicationResult.vue
│   │       │   ├── index.js
│   │       │   └── README.md
│   │       │
│   │       ├── finance/                 # 财务管理组件
│   │       ├── institution/             # 院校管理组件
│   │       │   ├── AddFacultyModal.vue
│   │       │   ├── AddInstitutionModal.vue
│   │       │   ├── AddMajorModal.vue
│   │       │   ├── DeleteConfirmModal.vue
│   │       │   ├── EditInstitutionModal.vue
│   │       │   ├── Institution.vue
│   │       │   ├── ViewInstitutionModal.vue
│   │       │   ├── constants.js
│   │       │   └── index.js
│   │       │
│   │       ├── logs/                    # 日志管理组件
│   │       ├── notice/                  # 通知管理组件
│   │       ├── permission/              # 权限管理组件
│   │       ├── schedule/                # 日程管理组件
│   │       │   ├── CalendarView.vue
│   │       │   ├── FilterBar.vue
│   │       │   ├── Schedule.vue
│   │       │   ├── ScheduleDetails.vue
│   │       │   ├── ScheduleForm.vue
│   │       │   └── ScheduleList.vue
│   │       │
│   │       ├── student/                 # 学生管理组件
│   │       │   ├── AddStudentModal.vue
│   │       │   ├── StudentDetailModal.vue
│   │       │   ├── StudentEditModal.vue
│   │       │   ├── StudentManagement.vue
│   │       │   └── index.js
│   │       │
│   │       ├── system/                  # 系统设置组件
│   │       └── user/                    # 用户管理组件
│   │
│   ├── main/                            # 后端Java源码
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── study/               # 主包
│   │   │   │   │   ├── common/          # 通用工具和常量
│   │   │   │   │   ├── config/          # 配置类
│   │   │   │   │   ├── controller/      # REST API控制器
│   │   │   │   │   ├── dto/             # 数据传输对象
│   │   │   │   │   ├── entity/          # 数据库实体类
│   │   │   │   │   ├── mapper/          # MyBatis映射
│   │   │   │   │   ├── repository/      # 数据访问层
│   │   │   │   │   └── service/         # 业务逻辑层
│   │   │   │   │       ├── admin/       # 管理员服务
│   │   │   │   │       ├── application/ # 申请相关服务
│   │   │   │   │       ├── files/       # 文件处理服务
│   │   │   │   │       ├── imagesload/  # 图片上传服务
│   │   │   │   │       ├── impl/        # 服务实现类
│   │   │   │   │       ├── log/         # 日志服务
│   │   │   │   │       ├── login/       # 登录认证服务
│   │   │   │   │       ├── schools/     # 学校信息服务
│   │   │   │   │       ├── students/    # 学生管理服务
│   │   │   │   │       └── user/        # 用户服务
│   │   │   │   │
│   │   │   │   └── xingyingmei/         # 扩展包
│   │   │   │       └── config/          # 扩展配置
│   │   │   │
│   │   └── resources/                   # 资源文件
│   │       ├── db/                      # 数据库脚本
│   │       ├── mapper/                  # MyBatis映射XML
│   │       ├── static/                  # 静态资源
│   │       └── templates/               # 模板文件
│   │
│   ├── router/                          # 前端路由配置
│   │   └── index.js                     # 路由定义
│   │
│   ├── utils/                           # 工具函数
│   │   └── request.js                   # 网络请求工具
│   │
│   ├── views/                           # 页面级组件
│   │   ├── Country.vue                  # 国家页面
│   │   ├── CustomerService.vue          # 客户服务页面
│   │   ├── FileUpload.vue               # 文件上传页面
│   │   ├── HelpCenter.vue               # 帮助中心页面
│   │   ├── Home.vue                     # 首页
│   │   ├── HomeFirst.vue                # 首页备选布局
│   │   ├── ImageUpload.vue              # 图片上传页面
│   │   ├── Profile.vue                  # 个人资料页面
│   │   ├── SchoolList.vue               # 学校列表页面
│   │   ├── schools.vue                  # 学校详情页面
│   │   │
│   │   ├── application/                 # 申请相关页面
│   │   │   └── study.vue                # 留学申请流程页面
│   │   │
│   │   ├── admin/                       # 管理员页面
│   │   │   ├── admin1.vue               # 管理员主界面
│   │   │   └── adminlogin.vue           # 管理员登录页面
│   │   │
│   │   ├── schools/                     # 学校相关页面
│   │   │
│   │   └── steps/                       # 申请步骤页面
│   │       ├── AiSelect.vue             # AI选校推荐
│   │       ├── ApplicationSteps.vue     # 申请步骤总览
│   │       ├── Assistant.vue            # 申请助手
│   │       ├── Interview.vue            # 面试准备阶段
│   │       ├── Materials.vue            # 材料准备阶段
│   │       ├── Preparation.vue          # 申请准备阶段
│   │       ├── Rebate.vue               # 返利申请
│   │       ├── Submission.vue           # 申请提交阶段
│   │       ├── Visa.vue                 # 签证申请阶段
│   │       ├── r.vue                    # 辅助页面
│   │       ├── rebateStart.vue          # 返利开始页面
│   │       └── self-more.vue            # 更多信息页面
│   │
│   └── App.vue                          # 主应用组件
│
├── public/                              # 静态资源
│   ├── images/                          # 图片资源
│   │   ├── experts/                     # 顾问专家图片
│   │   ├── flags/                       # 国家旗帜图片
│   │   └── schools/                     # 学校图片
│   └── videos/                          # 视频资源
│
├── backup/                              # 备份文件
└── logs/                                # 日志文件
```

## 前端结构

### 核心页面 (`src/views/`)

- `HomeFirst.vue` - 首页
- `Profile.vue` - 用户个人资料页
- `SchoolList.vue` - 学校列表页
- `schools.vue` - 学校详情页
- `application/study.vue` - 留学申请流程页面
- `Country.vue` - 留学国家介绍页面
- `CustomerService.vue` - 客户服务页面
- `HelpCenter.vue` - 帮助中心
- `FileUpload.vue` - 文件上传页面
- `ImageUpload.vue` - 图片上传页面

### 申请步骤页面 (`src/views/steps/`)

- `ApplicationSteps.vue` - 申请步骤总览
- `Preparation.vue` - 申请准备阶段
- `Materials.vue` - 材料准备阶段
- `Submission.vue` - 申请提交阶段
- `Interview.vue` - 面试准备阶段
- `Visa.vue` - 签证申请阶段
- `AiSelect.vue` - AI选校推荐
- `Assistant.vue` - 申请助手
- `Rebate.vue` - 返利申请
- `rebateStart.vue` - 返利开始页面

### 管理员页面 (`src/views/admin/`)

- `admin1.vue` - 管理员主界面
- `adminlogin.vue` - 管理员登录页面

### 管理员组件 (`src/components/admin/`)

#### 申请管理 (`application/`)
- `ApplicationManagement.vue` - 申请管理主界面
- `ApplicationMonitor.vue` - 申请监控面板
- `ApplicationDetailModal.vue` - 申请详情弹窗
- `ApplicationEditModal.vue` - 申请编辑弹窗
- `ApplicationMaterials.vue` - 申请材料管理
- `ApplicationResult.vue` - 申请结果管理

#### 学生管理 (`student/`)
- `StudentManagement.vue` - 学生管理主界面
- `StudentDetailModal.vue` - 学生详情弹窗
- `StudentEditModal.vue` - 学生信息编辑
- `AddStudentModal.vue` - 新增学生弹窗

#### 院校管理 (`institution/`)
- `Institution.vue` - 院校管理主界面
- `AddInstitutionModal.vue` - 新增院校弹窗
- `EditInstitutionModal.vue` - 编辑院校信息
- `AddFacultyModal.vue` - 新增学院弹窗
- `AddMajorModal.vue` - 新增专业弹窗

#### 其他管理功能
- `schedule/` - 日程安排管理
  - `Schedule.vue` - 日程主界面
  - `CalendarView.vue` - 日历视图
  - `ScheduleList.vue` - 日程列表
  - `ScheduleForm.vue` - 日程表单
- `finance/` - 财务管理
- `notice/` - 通知管理
  - `Notice.vue` - 通知管理界面
  - `NotificationDropdown.vue` - 通知下拉菜单
- `logs/` - 系统日志
- `analytics/` - 数据分析
- `system/` - 系统设置
- `user/` - 用户管理
- `permission/` - 权限管理

### 公共组件
- `NavBar.vue` - 导航栏
- `FooterBar.vue` - 页脚
- `Login.vue` - 登录组件

## 后端结构 (`src/main/java/`)

### 核心包

- `com.study` - 主包
  - `controller/` - REST API控制器
  - `service/` - 业务逻辑层
    - `admin/` - 管理员服务
    - `application/` - 申请相关服务
    - `files/` - 文件处理服务
    - `login/` - 登录认证服务
    - `schools/` - 学校信息服务
    - `students/` - 学生管理服务
  - `entity/` - 数据库实体类
  - `repository/` - 数据访问层
  - `dto/` - 数据传输对象
  - `mapper/` - MyBatis映射
  - `config/` - 配置类
  - `common/` - 通用工具和常量

### 主要实体 (`entity/`)

- `User.java` - 用户实体
- `Admin.java` - 管理员实体
- `Student.java` - 学生实体
- `Application.java` - 申请实体
- `School.java` - 学校实体
- `Faculty.java` - 学院实体
- `Major.java` - 专业实体
- `Material.java` - 申请材料实体

## 主要功能模块

### 1. 用户认证与授权
- 用户登录/注册
- 权限管理
- 用户角色划分

### 2. 学校浏览与申请
- 学校信息展示
- 专业信息浏览
- 申请流程引导

### 3. 申请管理
- 申请创建与编辑
- 材料上传与管理
- 申请状态跟踪
- 录取结果管理

### 4. 学生管理
- 学生信息管理
- 学生档案维护
- 学生与申请关联

### 5. 院校管理
- 院校信息维护
- 学院与专业管理
- 录取标准配置

### 6. 系统管理
- 系统设置
- 日志管理
- 数据备份与恢复

## 前后端交互

前端通过 `src/api/` 目录下的服务函数与后端REST API进行交互，遵循RESTful设计原则。

### 核心API服务

- `schoolService.js` - 学校相关API
  - 获取学校列表、详情
  - 按国家、类型筛选学校
  - 搜索学校
  - 管理学校信息（增删改）
  - 获取学校下的学院、专业和申请
  
- `facultyService.js` - 学院相关API
  - 获取学院列表
  - 添加/编辑/删除学院
  - 获取学院下的专业

- `majorService.js` - 专业相关API
  - 获取专业列表
  - 添加/编辑/删除专业
  - 获取专业详情和申请要求

- `logs.js` - 日志相关API
  - 记录系统操作日志
  - 查询日志

### API特性

- 请求/响应拦截器用于日志记录和错误处理
- 安全请求封装处理各类错误情况
- 前后端字段映射确保兼容性
- 统一的错误处理机制

## 数据模型

### 核心实体

#### 学校 (School)
```
{
  "id": "唯一标识符",
  "name": "学校中文名称",
  "englishName": "学校英文名称",
  "country": "所在国家",
  "countryCode": "国家代码",
  "city": "所在城市",
  "type": "学校类型（公立/私立）",
  "description": "学校描述",
  "worldRanking": "世界排名",
  "timesRanking": "泰晤士排名",
  "qsRanking": "QS排名",
  "tuitionFee": "学费",
  "feeCurrency": "学费货币",
  "acceptanceRate": "录取率",
  "logoUrl": "学校标志URL",
  "imageUrl": "学校图片URL"
}
```

#### 学院 (Faculty)
```
{
  "id": "唯一标识符",
  "schoolId": "所属学校ID",
  "name": "学院中文名称",
  "englishName": "学院英文名称",
  "description": "学院描述"
}
```

#### 专业 (Major)
```
{
  "id": "唯一标识符",
  "facultyId": "所属学院ID",
  "schoolId": "所属学校ID",
  "name": "专业中文名称",
  "englishName": "专业英文名称",
  "degree": "学位类型",
  "duration": "学制",
  "description": "专业描述",
  "requirements": "申请要求",
  "toeflRequirement": "托福要求",
  "ieltsRequirement": "雅思要求"
}
```

#### 申请 (Application)
```
{
  "id": "唯一标识符",
  "studentId": "学生ID",
  "schoolId": "学校ID",
  "majorId": "专业ID",
  "status": "申请状态",
  "degree": "申请学位",
  "round": "申请批次",
  "deadline": "截止日期",
  "materialsProgress": "材料完成度",
  "createTime": "创建时间",
  "consultant": "负责顾问",
  "notes": "申请备注"
}
```

#### 学生 (Student)
```
{
  "id": "唯一标识符",
  "name": "学生姓名",
  "email": "邮箱",
  "phone": "电话",
  "school": "当前学校",
  "major": "当前专业",
  "gpa": "GPA成绩",
  "toefl": "托福成绩",
  "ielts": "雅思成绩",
  "avatar": "头像URL"
}
```

## 开发指南

### 添加新功能

1. 在对应的后端包中添加实体类、服务和控制器
2. 在前端api目录添加相应的API调用函数
3. 创建必要的Vue组件并集成到路由系统

### 修改已有功能

1. 识别功能涉及的前后端组件
2. 确保修改保持前后端一致性
3. 更新相关文档和测试

### 典型的开发流程

1. **创建新功能**
   - 添加后端实体类和数据访问层
   - 实现服务层业务逻辑
   - 创建控制器暴露REST API
   - 前端编写API调用函数
   - 开发前端UI组件
   - 注册路由和菜单项

2. **优化既有功能**
   - 分析当前实现的不足
   - 设计改进方案
   - 先修改后端服务
   - 更新前端组件
   - 测试全流程

3. **修复Bug**
   - 复现问题
   - 定位代码位置
   - 分析根本原因
   - 实施修复
   - 编写/更新测试

### 代码风格与规范

1. **命名规范**
   - Java: 驼峰式命名，类名首字母大写
   - Vue组件: PascalCase命名
   - API端点: kebab-case命名

2. **组件设计原则**
   - 单一职责
   - 高内聚低耦合
   - 可复用性

3. **UI设计规范**
   - 遵循Element Plus组件库设计语言
   - 保持界面一致性
   - 响应式布局支持不同设备

## 常见问题处理

### 界面样式问题
- 检查对应Vue组件的style标签
- 参考Element Plus组件文档
- 确认CSS权重和作用域

### 数据获取问题
- 检查API调用和参数
- 确认后端控制器逻辑
- 检查网络请求状态
- 查看浏览器开发者工具中的网络请求

### 环境配置问题
- 前端开发环境: `npm run serve`
- 后端开发环境: 运行SpringBoot应用
- 数据库连接配置: `application.properties`

### 权限问题
- 检查用户角色和权限设置
- 验证权限拦截器配置
- 确认前端权限控制逻辑

## 测试与部署

### 测试
- 单元测试: JUnit测试后端逻辑
- 集成测试: 测试完整API流程
- E2E测试: 模拟用户操作测试前端界面

### 部署
- 开发环境: 本地运行
- 测试环境: 内部测试服务器
- 生产环境: 云服务器部署

### 监控与维护
- 系统日志收集与分析
- 性能监控
- 定期数据备份 