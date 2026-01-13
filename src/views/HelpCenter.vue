<template>
  <div class="help-container">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>
    <header class="help-header">
      <div class="header-content">
        <h1>帮助中心</h1>
        <p class="header-subtitle">欢迎使用新英美留学系统，这里提供全面的使用指南和操作说明</p>
        <div class="header-search">
          <input 
            type="text" 
            placeholder="搜索整个帮助文档..." 
            v-model="globalSearch" 
            @keyup.enter="handleGlobalSearch"
          />
          <button class="search-button" @click="handleGlobalSearch">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          </button>
        </div>
      </div>
    </header>

    <main class="help-main">
      <aside class="help-sidebar">
        <div class="sidebar-container">
          <div class="search-box">
            <input type="text" placeholder="搜索当前分类..." v-model="searchQuery" />
            <div class="search-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            </div>
          </div>
          <nav class="nav">
            <div 
              v-for="(section, index) in helpSections" 
              :key="index"
              class="nav-section"
            >
              <div 
                class="nav-item-title" 
                :class="{ active: activeSection === index }"
                @click="activeSection = index"
              >
                <span class="nav-icon" v-html="getSectionIcon(section.title)"></span>
                {{ section.title }}
              </div>
            </div>
          </nav>
        </div>
      </aside>

      <div class="content">
        <div v-if="activeSection !== null" class="content-container">
          <div class="content-header">
            <h2>{{ helpSections[activeSection].title }}</h2>
            <p v-if="helpSections[activeSection].description" class="section-description">
              {{ helpSections[activeSection].description }}
            </p>
          </div>
          
          <div class="content-body">
            <div v-if="showAllResults" class="global-search-results">
              <div class="search-results-header">
                <h3>搜索结果: "{{ globalSearch }}"</h3>
                <button class="clear-search" @click="clearGlobalSearch">返回</button>
              </div>
              
              <div v-if="globalSearchResults.length > 0">
                <div v-for="(result, resultIndex) in globalSearchResults" :key="resultIndex" class="search-result-item">
                  <div class="result-section">{{ result.section }}</div>
                  <h4 class="result-title" @click="goToResult(result)">{{ result.title }}</h4>
                  <p class="result-snippet" v-html="result.snippet"></p>
                </div>
              </div>
              
              <div v-else class="no-results">
                <p>没有找到与 "{{ globalSearch }}" 相关的内容</p>
                <button @click="clearGlobalSearch">清除搜索</button>
              </div>
            </div>
            
            <div v-else>
              <div 
                v-for="(item, itemIndex) in filteredItems" 
                :key="itemIndex" 
                class="help-item"
              >
                <div class="help-item-header">
                  <h3>{{ item.title }}</h3>
                  <span v-if="item.tag" class="help-item-tag">{{ item.tag }}</span>
                </div>
                <div class="help-item-content" v-html="item.content"></div>
              </div>

              <div v-if="filteredItems.length === 0 && searchQuery" class="no-results">
                <p>没有找到与 "{{ searchQuery }}" 相关的内容</p>
                <button @click="searchQuery = ''">清除搜索</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <!-- 页脚 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'
import FooterBar from '../components/FooterBar.vue'

// 使用异步组件加载NavBar，避免循环引用问题
const NavBar = defineAsyncComponent(() => import('../components/NavBar.vue'))

export default {
  name: 'HelpCenter',
  components: {
    NavBar,
    FooterBar,
  },
  data() {
    return {
      activeSection: 0,
      searchQuery: '',
      globalSearch: '',
      showAllResults: false,
      globalSearchResults: [],
      helpSections: [
        {
          title: '快速入门',
          description: '新用户必读指南，快速了解平台功能和使用方法',
          items: [
            {
              title: '平台介绍',
              tag: '新手必读',
              content: '<strong>欢迎使用新英美留学平台！</strong><br><br>新英美留学是一个专业的留学申请服务平台，致力于为学生提供全方位的留学申请支持。我们的平台集成了院校查询、申请管理、材料准备、进度跟踪等核心功能。<br><br><strong>平台优势：</strong><ul><li>🎯 <strong>智能匹配</strong>：基于您的背景和需求，智能推荐最适合的院校和专业</li><li>📋 <strong>全程管理</strong>：从申请准备到录取结果，全程跟踪申请进度</li><li>📚 <strong>资源丰富</strong>：提供最新的院校信息、申请要求和留学资讯</li><li>👨‍🎓 <strong>专业指导</strong>：经验丰富的留学顾问提供一对一咨询服务</li><li>🔒 <strong>安全可靠</strong>：严格保护用户隐私，确保申请材料安全</li></ul>'
            },
            {
              title: '如何开始使用',
              tag: '操作指南',
              content: '<strong>三步开启您的留学之旅：</strong><br><br><strong>第一步：注册账号</strong><br>1. 点击页面右上角的"登录/注册"按钮<br>2. 填写基本信息完成注册<br>3. 验证邮箱激活账号<br><br><strong>第二步：完善个人资料</strong><br>1. 登录后进入"个人中心"<br>2. 填写教育背景、语言成绩等信息<br>3. 上传必要的证明文件<br><br><strong>第三步：开始申请</strong><br>1. 使用"智能选校"功能筛选目标院校<br>2. 查看详细的申请要求和截止日期<br>3. 准备申请材料并提交申请<br><br><strong>💡 小贴士：</strong>建议先完善个人资料，这样系统能为您提供更精准的院校推荐。'
            },
            {
              title: '主要功能导航',
              tag: '功能介绍',
              content: '<strong>平台核心功能一览：</strong><br><br>🏫 <strong>院校查询</strong><br>• 搜索全球优质院校信息<br>• 查看专业设置和申请要求<br>• 对比不同院校的优势特色<br><br>🎯 <strong>智能选校</strong><br>• 基于个人背景智能匹配院校<br>• 提供录取概率评估<br>• 生成个性化申请方案<br><br>📋 <strong>申请管理</strong><br>• 创建和管理多个申请项目<br>• 跟踪申请进度和状态<br>• 设置重要日期提醒<br><br>📄 <strong>材料中心</strong><br>• 上传和管理申请材料<br>• 使用标准化模板<br>• 在线预览和编辑文档<br><br>👨‍🏫 <strong>专家咨询</strong><br>• 预约一对一咨询服务<br>• 获得专业的申请建议<br>• 参与在线答疑活动'
            }
          ]
        },
        {
          title: '申请指导',
          description: '详细的留学申请流程指导和注意事项',
          items: [
            {
              title: '留学申请完整流程',
              tag: '流程指南',
              content: '<strong>留学申请的完整时间线：</strong><br><br><strong>📅 提前1-2年：规划阶段</strong><br>• 确定留学目标国家和专业方向<br>• 了解目标院校的申请要求<br>• 开始准备语言考试（托福/雅思/GRE等）<br>• 提升学术背景和实践经验<br><br><strong>📅 提前8-12个月：准备阶段</strong><br>• 完成标准化考试并获得理想分数<br>• 确定最终申请院校名单<br>• 准备申请材料（文书、推荐信等）<br>• 联系推荐人并索取推荐信<br><br><strong>📅 提前3-8个月：申请阶段</strong><br>• 在线提交申请表格<br>• 上传所有必需材料<br>• 缴纳申请费用<br>• 跟踪申请状态<br><br><strong>📅 申请后：等待与决定</strong><br>• 准备面试（如需要）<br>• 等待录取结果<br>• 比较offer并做出最终选择<br>• 办理签证和入学手续'
            },
            {
              title: '申请材料准备清单',
              tag: '材料准备',
              content: '<strong>标准申请材料清单：</strong><br><br><strong>📋 基础材料</strong><br>• 在线申请表格<br>• 个人陈述/动机信<br>• 学术成绩单（中英文）<br>• 学位证书/在读证明<br>• 语言成绩单（托福/雅思等）<br>• 标准化考试成绩（GRE/GMAT等）<br><br><strong>📋 推荐材料</strong><br>• 2-3封推荐信<br>• 推荐人联系方式<br>• 推荐表格（如学校要求）<br><br><strong>📋 补充材料</strong><br>• 简历/CV<br>• 作品集（艺术类专业）<br>• 研究计划（研究型项目）<br>• 工作证明（如有工作经验）<br>• 奖学金申请材料<br><br><strong>💡 重要提醒：</strong><br>• 所有材料需要英文版本<br>• 成绩单需要学校官方认证<br>• 提前准备，避免临时抱佛脚<br>• 不同学校可能有特殊要求'
            },
            {
              title: '文书写作指导',
              tag: '文书技巧',
              content: '<strong>个人陈述写作要点：</strong><br><br><strong>✍️ 结构安排</strong><br>• <strong>开头</strong>：吸引人的开场，明确申请动机<br>• <strong>主体</strong>：学术背景、实践经验、研究兴趣<br>• <strong>结尾</strong>：未来规划和对学校的期待<br><br><strong>✍️ 内容要点</strong><br>• 突出个人特色和独特经历<br>• 展示学术能力和研究潜力<br>• 说明选择该校该专业的原因<br>• 体现对未来的清晰规划<br><br><strong>✍️ 写作技巧</strong><br>• 用具体事例支撑观点<br>• 避免空洞的套话<br>• 保持逻辑清晰、语言流畅<br>• 控制字数在要求范围内<br><br><strong>✍️ 常见误区</strong><br>• ❌ 过度夸大个人能力<br>• ❌ 复制粘贴模板内容<br>• ❌ 忽视学校具体要求<br>• ❌ 语法错误和拼写错误<br><br><strong>💡 建议：</strong>完成初稿后，请专业老师或有经验的朋友帮忙修改。'
            }
          ]
        },
        {
          title: '系统概述',
          description: '了解新英美留学系统的基本功能与使用方法',
          items: [
            {
              title: '什么是新英美留学助手系统？',
              tag: '系统介绍',
              content: '新英美留学助手系统是一个综合性留学管理平台，专为留学顾问和管理人员设计，提供一站式留学申请管理解决方案。本系统涵盖了从学生信息管理、院校选择、申请材料准备到签证办理的全流程支持。<br><br>系统的核心功能包括：用户管理、申请进度跟踪、文书管理、数据分析和智能推荐等，能够有效提高工作效率，降低管理难度，为留学顾问提供强大的技术支持。'
            },
            {
              title: '系统主要功能概览',
              tag: '功能导航',
              content: '<strong>系统包含以下核心模块：</strong><ul><li><strong>用户管理中心</strong>：管理学生和内部用户账号，设置权限，审核资料</li><li><strong>申请管理系统</strong>：追踪每位学生的申请进度，设置提醒，更新状态</li><li><strong>院校资源库</strong>：查询和更新院校信息、专业设置、申请要求等</li><li><strong>材料管理中心</strong>：上传、审核和管理学生申请材料，提供模板</li><li><strong>数据分析平台</strong>：生成各类数据报表，分析申请成功率和转化率</li><li><strong>系统管理工具</strong>：配置系统参数，管理数据备份，监控系统状态</li></ul>'
            },
            {
              title: '如何登录后台管理系统',
              tag: '快速入门',
              content: '<strong>登录步骤：</strong><br>1. 打开浏览器，访问系统管理后台地址：https://[系统域名]/admin<br>2. 输入您的管理员账号和密码<br>3. 首次登录时，系统会要求您修改初始密码并完成安全设置<br>4. 登录成功后，您将看到管理控制台首页<br><br><strong>安全提示：</strong><ul><li>请使用复杂密码并定期更换</li><li>不要在公共设备上保存登录状态</li><li>完成工作后请及时登出系统</li><li>如遇登录问题，请联系系统管理员</li></ul>'
            },
            {
              title: '系统界面导航指南',
              tag: '界面指南',
              content: '<strong>管理后台界面主要由以下部分组成：</strong><br><br>1. <strong>顶部导航栏</strong>：显示当前登录用户、消息通知、帮助中心和退出选项<br>2. <strong>左侧菜单栏</strong>：包含所有功能模块入口，可以折叠/展开<br>3. <strong>主内容区</strong>：显示当前选中功能的操作界面<br>4. <strong>个人工作台</strong>：展示待办任务、最近访问和工作提醒<br><br><strong>常用导航路径：</strong><ul><li>查看学生信息：左侧菜单 → 用户管理 → 学生列表</li><li>处理申请进度：左侧菜单 → 申请管理 → 进度跟踪</li><li>查看系统报表：左侧菜单 → 数据分析 → 统计报表</li><li>系统设置调整：左侧菜单 → 系统管理 → 参数设置</li></ul>'
            },
            {
              title: '常见操作快速指南',
              tag: '操作指南',
              content: '<strong>新手必学的基础操作：</strong><ul><li><strong>查询信息</strong>：大多数列表页面顶部都有搜索框，支持按姓名、ID、状态等条件筛选</li><li><strong>导出数据</strong>：列表页面通常右上角有"导出"按钮，可将当前数据导出为Excel</li><li><strong>批量操作</strong>：选中多条记录后，可使用列表上方的批量操作按钮</li><li><strong>详情查看</strong>：点击记录中的"查看"按钮或直接点击ID/姓名进入详情页</li><li><strong>状态更新</strong>：在详情页可更改状态、添加备注、上传文件等</li><li><strong>关联操作</strong>：详情页通常提供关联数据的快速入口，如学生详情页可直接查看该学生的申请记录</li></ul><br><strong>助手功能：</strong>系统右下角的"智能助手"可随时提供操作指导和问题解答，新用户可优先使用此功能熟悉系统。'
            }
          ]
        },
        {
          title: '后端管理指南',
          description: '针对系统管理员与后端开发人员的使用说明与操作流程',
          items: [
            {
              title: '管理平台登录与权限控制',
              tag: '基础操作',
              content: '后端管理平台入口位于系统根路径+"/admin"，使用管理员账号（非普通用户账号）访问。<br><br><strong>登录流程：</strong><br>1. 访问管理平台地址 https://[系统域名]/admin<br>2. 输入管理员用户名和密码<br>3. 完成双因素认证（如已启用）<br><br><strong>权限级别说明：</strong><ul><li><strong>超级管理员</strong>：拥有所有权限，包括用户管理、系统配置、日志查看等</li><li><strong>内容管理员</strong>：可管理系统内容，包括学校信息、申请材料模板等</li><li><strong>用户管理员</strong>：负责用户账号管理、审核与支持</li><li><strong>数据分析员</strong>：可访问统计报表与数据分析模块</li></ul><br>如需调整权限设置，请联系系统超级管理员。'
            },
            {
              title: '用户管理与数据维护',
              tag: '日常管理',
              content: '<strong>用户管理操作：</strong><br><br>1. <strong>用户列表查看</strong>：在左侧导航栏选择"用户管理"→"用户列表"，可查看所有注册用户<br>2. <strong>用户信息编辑</strong>：点击用户列表中的"编辑"按钮，可修改用户信息、调整权限等<br>3. <strong>账号状态管理</strong>：可将用户账号设为"正常"、"禁用"或"待审核"状态<br>4. <strong>批量操作</strong>：选择多个用户后，可执行批量审核、导出等操作<br><br><strong>数据维护工作：</strong><ul><li><strong>院校信息更新</strong>：定期更新院校录取率、排名、专业设置等信息</li><li><strong>申请材料模板维护</strong>：根据各校最新要求更新申请材料模板</li><li><strong>系统公告发布</strong>：在"内容管理"→"系统公告"中发布重要通知</li><li><strong>常见问题维护</strong>：更新FAQ内容，确保信息准确性</li></ul><br>所有操作都会记录在系统日志中，可在"系统工具"→"操作日志"查看。'
            },
            {
              title: '系统监控与故障处理',
              tag: '运维操作',
              content: '作为后端管理人员，您需要定期检查系统运行状态并处理可能出现的问题。<br><br><strong>系统监控：</strong><br>1. 访问监控面板：在管理后台左侧导航选择"系统监控"→"服务状态"<br>2. 查看关键指标：CPU使用率、内存占用、请求响应时间、错误率等<br>3. 服务健康检查：确认所有微服务状态正常（绿色表示正常，红色表示异常）<br><br><strong>常见问题处理：</strong><ul><li><strong>服务不可用</strong>：检查对应服务器状态，可尝试在"服务管理"中重启服务</li><li><strong>数据库连接异常</strong>：检查数据库服务是否正常，连接池配置是否合理</li><li><strong>高CPU/内存占用</strong>：分析监控日志，识别可能的性能瓶颈，必要时联系技术支持</li><li><strong>缓存异常</strong>：在"缓存管理"中清除并重建缓存</li></ul><br>如遇复杂问题，请通过管理平台的"工单系统"创建工单，技术团队将优先处理。'
            },
            {
              title: '数据备份与恢复',
              tag: '安全操作',
              content: '系统数据的定期备份与恢复是保障业务连续性的关键工作。<br><br><strong>数据备份：</strong><br>1. <strong>自动备份</strong>：系统每日凌晨2点自动执行全量备份，保存在指定存储位置<br>2. <strong>手动备份</strong>：在"系统工具"→"数据备份"中点击"创建备份"执行手动备份<br>3. <strong>备份策略</strong>：系统保留最近30天的每日备份、最近12个月的月度备份<br><br><strong>数据恢复操作：</strong><ol><li>进入"系统工具"→"数据恢复"页面</li><li>从备份列表中选择要恢复的备份点</li><li>选择恢复方式：完全恢复或选择性恢复</li><li>点击"开始恢复"并输入管理员密码确认</li><li>等待恢复过程完成（视数据量可能需要5-30分钟）</li></ol><br><strong>注意事项：</strong>数据恢复操作会影响当前系统数据，建议在非高峰期执行，并提前通知用户系统维护。'
            },
            {
              title: '系统配置与参数调整',
              tag: '高级设置',
              content: '系统提供了丰富的配置选项，可根据实际需求进行调整。<br><br><strong>核心配置项：</strong><ul><li><strong>系统参数</strong>：在"系统设置"→"参数配置"中可调整系统基本参数</li><li><strong>邮件配置</strong>：设置邮件服务器信息，测试邮件发送功能</li><li><strong>短信配置</strong>：配置短信服务提供商API参数</li><li><strong>OSS存储</strong>：配置文件存储服务参数</li><li><strong>支付接口</strong>：设置支付网关参数与回调地址</li></ul><br><strong>性能优化配置：</strong><ul><li><strong>缓存策略</strong>：调整缓存过期时间、缓存容量等参数</li><li><strong>连接池设置</strong>：根据并发量调整数据库连接池大小</li><li><strong>线程池配置</strong>：调整系统线程池参数以适应不同负载</li><li><strong>限流规则</strong>：设置API访问限流阈值，防止过载</li></ul><br><strong>重要提示：</strong>修改系统核心配置后，需要在测试环境验证无误后再应用到生产环境。部分配置修改后需重启相关服务才能生效。'
            },
            {
              title: '统计报表与数据分析',
              tag: '数据分析',
              content: '系统提供丰富的数据统计与分析功能，帮助管理员了解系统使用情况和业务指标。<br><br><strong>核心报表：</strong><ul><li><strong>用户增长报表</strong>：展示用户注册、活跃度等指标的变化趋势</li><li><strong>申请转化漏斗</strong>：分析从注册到提交申请各阶段的转化率</li><li><strong>录取率分析</strong>：按学校、专业、背景等维度分析申请结果</li><li><strong>系统使用热图</strong>：展示功能模块的使用频率与时间分布</li></ul><br><strong>使用方法：</strong><ol><li>在左侧导航栏选择"统计分析"→所需报表类型</li><li>设置查询条件（时间范围、用户群体等）</li><li>点击"生成报表"查看可视化结果</li><li>可通过"导出"按钮将报表导出为Excel或PDF格式</li></ol><br><strong>自定义报表：</strong>系统支持创建自定义报表，在"统计分析"→"自定义报表"中，选择所需数据字段和展示方式，保存后可定期自动生成。'
            },
            {
              title: '系统升级与版本管理',
              tag: '维护指南',
              content: '作为后端管理人员，您需要了解系统升级流程和版本管理规范。<br><br><strong>升级流程：</strong><ol><li><strong>升级通知</strong>：技术团队会提前发布升级计划，包含版本号、功能变更、预计维护时间</li><li><strong>环境备份</strong>：执行升级前需完成数据库和配置文件的完整备份</li><li><strong>升级执行</strong>：在"系统维护"→"版本升级"中执行升级操作或按照升级文档手动操作</li><li><strong>功能验证</strong>：升级完成后，按照测试清单验证核心功能</li><li><strong>回滚准备</strong>：如发现严重问题，使用备份执行回滚操作</li></ol><br><strong>版本命名规则：</strong><ul><li>格式为X.Y.Z，如2.5.3</li><li>X表示主版本号，有重大架构变更时增加</li><li>Y表示次版本号，有新功能添加时增加</li><li>Z表示修订号，修复bug或小改进时增加</li></ul><br><strong>升级注意事项：</strong><ul><li>升级前通知所有用户，建议在使用低峰期执行</li><li>按照升级文档检查系统兼容性要求</li><li>升级后清理缓存，避免使用旧版缓存数据</li></ul>'
            },
            {
              title: '数据导入与导出操作',
              tag: '数据处理',
              content: '作为系统管理员，您经常需要执行数据批量导入和导出操作，特别是在学期初或招生季节。<br><br><strong>数据导入：</strong><ol><li>访问"系统管理"→"数据导入"页面</li><li>选择要导入的数据类型：学生信息、院校数据、录取数据等</li><li>下载对应的Excel模板，按照要求填写数据</li><li>点击"选择文件"上传填写好的Excel文件</li><li>系统会自动验证数据格式，显示验证结果</li><li>确认无误后，点击"开始导入"按钮</li><li>等待导入完成，查看导入报告</li></ol><br><strong>批量导出：</strong><ol><li>访问"系统管理"→"数据导出"页面</li><li>选择需要导出的数据类型</li><li>设置筛选条件（时间范围、状态等）</li><li>选择导出格式（Excel、CSV、PDF）</li><li>点击"生成报表"按钮</li><li>下载生成的文件</li></ol><br><strong>注意事项：</strong><ul><li>大批量数据导入建议在系统使用低峰期进行</li><li>超过10000条记录的导入会自动使用异步处理，可在"任务中心"查看进度</li><li>所有导入操作都会记录操作日志，便于追溯</li><li>数据导出支持定时任务，可在"定时任务"中设置自动导出计划</li></ul>'
            },
            {
              title: '学生管理与咨询跟进',
              tag: '服务管理',
              content: '管理系统中的学生信息和咨询服务是管理员的重要工作。<br><br><strong>学生信息管理：</strong><ul><li><strong>学生资料维护</strong>：在"用户管理"→"学生列表"中查看和编辑学生个人信息</li><li><strong>学术背景审核</strong>：验证并确认学生上传的成绩单、证书等资料真实性</li><li><strong>申请进度管理</strong>：跟踪每位学生的申请进度，在"申请管理"→"进度跟踪"中更新状态</li><li><strong>分组管理</strong>：根据年级、申请国家等维度创建学生分组，便于批量操作</li></ul><br><strong>咨询服务管理：</strong><ol><li><strong>咨询预约处理</strong>：在"服务管理"→"咨询预约"中查看学生预约，指派顾问</li><li><strong>服务记录管理</strong>：记录每次咨询内容和建议，在"服务记录"中填写详细服务笔记</li><li><strong>跟进提醒</strong>：设置跟进提醒，系统会通过待办事项提醒按时跟进</li><li><strong>满意度评价</strong>：查看学生对咨询服务的评价反馈，持续改进服务质量</li></ol><br><strong>实用技巧：</strong><ul><li>使用"快速标签"功能给学生添加标签，如"优质申请者"、"需重点跟进"等</li><li>开启"服务智能提醒"，系统会根据学生申请阶段自动提醒需要进行的服务</li><li>使用"批量消息"功能同时向多位学生发送重要通知</li><li>定期查看"服务质量报告"，了解服务效果和改进方向</li></ul>'
            },
            {
              title: '内容管理与知识库维护',
              tag: '内容运营',
              content: '系统中的内容资源需要定期更新，以保证信息的准确性和时效性。<br><br><strong>院校信息管理：</strong><ul><li><strong>院校基本信息</strong>：在"内容管理"→"院校库"中更新学校介绍、排名、费用等基本信息</li><li><strong>专业设置更新</strong>：维护各院校最新的专业设置、课程内容和入学要求</li><li><strong>申请要求变更</strong>：及时更新各校申请材料要求、截止日期和考试要求变化</li><li><strong>成功案例管理</strong>：添加和更新录取成功案例，展示学生背景和申请策略</li></ul><br><strong>知识库维护：</strong><ol><li><strong>文章管理</strong>：在"内容管理"→"文章中心"发布和更新留学相关文章</li><li><strong>常见问题</strong>：根据用户反馈，定期更新FAQ内容，添加新问题和答案</li><li><strong>政策解读</strong>：发布各国最新留学政策变化的解读文章</li><li><strong>表格模板</strong>：更新申请所需的各类表格和文书模板</li></ol><br><strong>内容审核流程：</strong><ol><li>内容创建：由内容编辑创建初稿</li><li>专家审核：由学术顾问或资深顾问审核内容准确性</li><li>终审发布：管理员终审后发布到系统</li><li>定期检查：每季度检查已发布内容的时效性，标记需更新内容</li></ol><br>系统支持内容版本控制，可查看内容的历史版本并在必要时回滚。'
            },
            {
              title: '权限分配与团队管理',
              tag: '团队协作',
              content: '合理的权限分配和高效的团队管理是系统正常运行的基础。<br><br><strong>权限管理操作：</strong><ol><li>访问"系统管理"→"权限管理"→"角色设置"</li><li>系统预设了多种角色：超级管理员、内容管理员、咨询顾问、数据分析师等</li><li>可点击"创建角色"自定义新角色，并设置权限范围</li><li>在"用户管理"中为每位后台用户分配适当角色</li><li>支持临时权限授予，可设置权限的有效期限</li></ol><br><strong>团队协作功能：</strong><ul><li><strong>工作分配</strong>：在"工作台"→"任务分配"中创建任务并分配给团队成员</li><li><strong>进度跟踪</strong>：通过"任务看板"实时查看各项工作的完成情况</li><li><strong>内部消息</strong>：使用系统内置的消息功能进行团队沟通</li><li><strong>审批流程</strong>：设置多级审批流程，如内容发布、重要数据修改等操作需多人审核</li><li><strong>操作日志</strong>：所有管理操作都有详细日志记录，支持按用户、时间、操作类型筛选</li></ul><br><strong>最佳实践：</strong><ul><li>遵循"最小权限原则"，仅授予用户完成工作所需的最小权限集</li><li>定期审计用户权限，移除不再需要的权限</li><li>关键操作设置二次确认或多人审批</li><li>为临时工作人员创建有时间限制的账号</li><li>定期组织系统使用培训，提高团队操作效率</li></ul>'
            },
            {
              title: '系统使用技巧与快捷操作',
              tag: '效率提升',
              content: '掌握这些实用技巧可以显著提高您的工作效率。<br><br><strong>界面操作技巧：</strong><ul><li><strong>快捷键</strong>：按下"?"键查看所有可用快捷键，常用如Ctrl+S(保存)、Ctrl+F(搜索)</li><li><strong>自定义仪表盘</strong>：在"个人中心"→"偏好设置"中自定义您的管理首页布局</li><li><strong>批量操作</strong>：在列表页面，可选中多条记录后进行批量审核、导出等操作</li><li><strong>高级筛选</strong>：使用"高级筛选"功能组合多个条件精确定位数据</li><li><strong>列表视图定制</strong>：自定义列表显示的字段和排序方式，并保存为个人视图</li></ul><br><strong>数据处理技巧：</strong><ul><li><strong>报表订阅</strong>：设置常用报表的自动生成和邮件推送</li><li><strong>数据导出计划</strong>：创建定期数据导出任务，自动将数据发送到指定邮箱</li><li><strong>模板管理</strong>：创建并保存常用的回复模板、Excel导入模板等</li><li><strong>批量更新</strong>：使用"批量编辑"功能同时修改多条记录的相同字段</li><li><strong>数据校验规则</strong>：设置自定义的数据验证规则，确保录入数据的准确性</li></ul><br><strong>工作流优化：</strong><ul><li><strong>自动化流程</strong>：在"系统设置"→"自动化规则"中设置触发条件和自动操作</li><li><strong>待办事项</strong>：使用系统待办功能管理日常工作，支持优先级设置和提醒</li><li><strong>常用链接</strong>：将经常访问的页面添加到"我的收藏"，一键直达</li><li><strong>批量通知</strong>：使用筛选条件选择目标用户群体，发送批量系统通知</li><li><strong>定时任务</strong>：设置在系统负载低的时间段自动执行耗时操作</li></ul><br>系统每月更新"效率提示"，建议定期查看以了解最新功能和使用技巧。'
            }
          ]
        },
        {
          title: '系统设置',
          description: '系统参数配置、保存生效与常见问题排查',
          items: [
            {
              title: '系统设置入口与保存规则',
              tag: '必读',
              content: '<strong>入口：</strong>后台管理左侧菜单 → 系统设置。<br><br><strong>保存规则：</strong><br>1. 修改任意配置后，点击右上角<strong>保存设置</strong><br>2. 保存成功后立即生效（部分界面类设置可能需要刷新页面）<br>3. 如需回退到初始配置，点击<strong>恢复默认</strong><br><br><strong>常见注意：</strong><ul><li>保存前请检查必填项（如系统名称、通知频率等）</li><li>不要在公共设备上保存敏感配置</li></ul>'
            },
            {
              title: '基本信息设置说明',
              tag: '基础配置',
              content: '<strong>系统名称/系统简介：</strong>用于后台页面标题与系统信息展示。<br><br><strong>系统LOGO：</strong>用于后台标识展示。建议使用清晰的方形PNG/JPG图片。'
            },
            {
              title: '界面设置说明',
              tag: '外观',
              content: '<strong>主题色：</strong>影响按钮、高亮与主要强调色。<br><strong>显示模式：</strong>浅色/深色模式切换。<br><strong>布局密度：</strong>开启后界面更紧凑，适合小屏。<br><strong>侧边栏默认状态：</strong>决定后台左侧菜单默认展开或折叠。'
            },
            {
              title: '通知与邮件设置说明',
              tag: '消息',
              content: '<strong>通知方式：</strong>可开启邮件/浏览器/声音提醒。<br><strong>通知频率：</strong>建议保持“实时通知”，避免错过关键状态变更。<br><strong>免打扰：</strong>开启后按设定时间段暂停通知。<br><br><strong>邮件服务：</strong>填写SMTP服务器与账号信息后，可点击“测试邮件设置”验证。'
            },
            {
              title: '备份与安全设置说明',
              tag: '安全',
              content: '<strong>自动备份：</strong>开启后按频率生成备份，并按保留数量清理旧备份。<br><strong>密码策略：</strong>建议选择中等或强策略，并设置合理最小长度。<br><strong>登录失败锁定：</strong>用于防止暴力破解，建议保持开启。'
            },
            {
              title: '常见问题：保存后不生效怎么办？',
              tag: '排查',
              content: '<strong>建议按顺序排查：</strong><ol><li>确认点击了“保存设置”并提示保存成功</li><li>刷新页面后再次查看是否保持</li><li>清理浏览器缓存后重试</li><li>确认没有多个窗口同时修改导致覆盖</li></ol>'
            }
          ]
        },
        {
          title: '账户管理',
          description: '学习如何管理您的账户设置和个人信息',
          items: [
            {
              title: '如何注册账户？',
              content: '点击网站右上角的"登录/注册"按钮，选择"新用户注册"，填写您的邮箱、设置密码并完成验证即可。注册成功后，您将自动登录系统。'
            },
            {
              title: '忘记密码怎么办？',
              content: '如果忘记密码，请点击登录页面的"忘记密码"链接，输入您注册时使用的邮箱，系统将发送密码重置链接到您的邮箱。按照邮件中的指示操作即可重置密码。'
            },
            {
              title: '如何修改个人信息？',
              content: '登录后，点击右上角的用户头像，选择"个人中心"，在个人信息页面可以更新您的基本资料、联系方式等信息。修改完成后，点击"保存"按钮即可。'
            },
            {
              title: '如何确保账户安全？',
              content: '为了确保您的账户安全，我们建议：<ul><li>使用强密码（包含字母、数字和特殊字符）</li><li>定期更换密码</li><li>不要与他人共享您的账户信息</li><li>在使用公共设备后记得登出账户</li><li>开启两步验证（如果可用）</li></ul>'
            }
          ]
        },
        {
          title: '申请准备',
          description: '规划和准备您的留学申请，获取专业建议',
          items: [
            {
              title: '留学申请时间规划',
              tag: '关键规划',
              content: '合理的时间规划是留学申请成功的关键。根据不同国家和项目，申请时间线会有所不同。<br><br><strong>美国留学时间规划（以秋季入学为例）：</strong><ul><li><strong>提前14-16个月</strong>：确定留学目标，准备标准化考试</li><li><strong>提前12-14个月</strong>：研究目标院校和专业，准备资金证明</li><li><strong>提前10-12个月</strong>：参加并完成标准化考试，准备申请材料</li><li><strong>提前8-10个月</strong>：撰写个人陈述和文书材料，联系推荐人</li><li><strong>提前6-8个月</strong>：提交申请，准备面试</li><li><strong>提前4-6个月</strong>：接收录取结果，做最终选择</li><li><strong>提前3-4个月</strong>：申请签证，安排住宿和行程</li></ul><br>我们的系统会根据您设定的目标入学时间，自动生成个性化的申请时间线，并通过提醒功能确保您不会错过任何重要截止日期。'
            },
            {
              title: '如何选择适合的学校和专业',
              tag: '择校指南',
              content: '选择适合的学校和专业需要考虑多方面因素，包括学术实力、地理位置、费用、就业前景等。<br><br><strong>选校考虑因素：</strong><ul><li><strong>学术匹配度</strong>：您的学术背景和研究兴趣与目标院校的契合度</li><li><strong>排名与声誉</strong>：院校在特定专业领域的排名和业内认可度</li><li><strong>地理位置</strong>：气候条件、城市/乡村环境、文化氛围、安全因素</li><li><strong>费用与奖学金</strong>：学费、生活费用以及可获得的财务资助机会</li><li><strong>就业前景</strong>：毕业生就业率、薪资水平、实习机会和校友网络</li><li><strong>录取难度</strong>：根据您的背景评估被录取的可能性</li></ul><br>我们的系统提供智能择校工具，通过分析您的学术背景、职业目标和个人偏好，结合历年录取数据，推荐最适合您的院校和专业选择。您还可以查看详细的院校对比报告，全面了解不同选择的优劣势。'
            },
            {
              title: '提高申请竞争力的策略',
              tag: '提升指南',
              content: '在激烈的留学申请竞争中，您需要全方位提升自己的申请竞争力。<br><br><strong>提升竞争力的关键策略：</strong><ul><li><strong>学术表现</strong>：保持优异的GPA，选择具有挑战性的课程，参与研究项目</li><li><strong>标准化考试</strong>：获得有竞争力的TOEFL/IELTS、GRE/GMAT分数</li><li><strong>专业经验</strong>：获取与目标专业相关的实习、研究或工作经验</li><li><strong>课外活动</strong>：参与能展示领导力、团队合作和社会责任感的活动</li><li><strong>个人陈述</strong>：撰写能反映您独特经历和价值观的个人陈述</li><li><strong>推荐信</strong>：获取能详细描述您能力和潜力的强有力推荐信</li></ul><br>我们的系统提供个性化的提升计划，根据您的申请目标和当前背景，制定针对性的提升策略，并提供详细的行动步骤和资源推荐。'
            },
            {
              title: '留学费用与奖学金申请',
              tag: '资金规划',
              content: '留学费用是许多申请者关注的核心问题，合理的财务规划和奖学金申请可以有效减轻经济负担。<br><br><strong>留学费用构成：</strong><ul><li><strong>学费</strong>：根据国家、院校和专业的不同，年学费从几万到几十万人民币不等</li><li><strong>生活费</strong>：包括住宿、饮食、交通、医疗保险等日常开销</li><li><strong>其他费用</strong>：申请费、签证费、机票、教材费、语言考试费等</li></ul><br><strong>奖学金类型：</strong><ul><li><strong>院校奖学金</strong>：由院校直接提供的学术奖学金、助学金、研究/教学助理岗位</li><li><strong>政府奖学金</strong>：如国家留学基金委奖学金、目标国政府奖学金等</li><li><strong>私人机构奖学金</strong>：由基金会、企业或其他组织提供的奖学金</li><li><strong>专项奖学金</strong>：针对特定学科、研究方向或背景学生的奖学金</li></ul><br>我们的系统提供留学费用计算器和奖学金匹配工具，帮助您估算总体留学成本，并根据您的背景自动匹配适合申请的奖学金项目，同时提供奖学金申请指导和模板。'
            }
          ]
        },
        {
          title: '材料准备',
          description: '了解如何准备和提交高质量的申请材料',
          items: [
            {
              title: '需要准备哪些申请材料？',
              content: '常见的申请材料包括个人简历、个人陈述、推荐信、成绩单、语言成绩证明等。在"材料准备"模块，系统会根据您选择的学校和专业，智能生成需要准备的材料清单。'
            },
            {
              title: '如何上传和管理申请材料？',
              content: '在"材料准备"模块，您可以看到材料清单，点击相应的材料类型，根据提示上传文件或填写内容。系统支持文档和图片格式，上传后可以随时查看和更新。所有材料会安全存储在您的账户中，方便随时调用。'
            },
            {
              title: '个人陈述写作技巧',
              content: '撰写个人陈述时，请注意以下几点：<ul><li>清晰表达您的学术兴趣和职业目标</li><li>突出您的独特经历和成就</li><li>解释为何选择该学校和专业</li><li>展示您的研究能力和学术潜力</li><li>保持简洁明了，避免冗长</li><li>确保无拼写和语法错误</li></ul>'
            }
          ]
        },
        {
          title: '提交申请',
          description: '掌握申请提交流程与跟踪技巧',
          items: [
            {
              title: '如何跟踪申请进度？',
              content: '在"提交申请"模块，您可以看到所有申请的学校列表及其当前状态。系统会自动更新申请进度，并通过邮件或站内消息提醒您重要的时间节点和操作要求。'
            },
            {
              title: '申请截止日期如何查看？',
              content: '系统会在每所学校的详情页面显示申请截止日期，并在"提交申请"模块的时间线上标注重要日期。您还可以在个人日历中查看所有申请相关的时间节点。'
            },
            {
              title: '如何支付申请费用？',
              content: '大多数学校需要支付申请费用。在"提交申请"页面，您可以看到每所学校的申请费用金额。系统支持多种支付方式，包括信用卡、PayPal等。支付完成后，您将收到电子收据。'
            }
          ]
        },
        {
          title: '面试准备',
          description: '为留学申请面试做好充分准备',
          items: [
            {
              title: '如何准备面试？',
              content: '在"面试准备"模块，系统提供了各类院校和专业的面试指南，包括常见问题和回答技巧。您可以使用模拟面试功能进行练习，系统会根据您的表现给出改进建议。'
            },
            {
              title: '面试模拟功能如何使用？',
              content: '点击"面试准备"中的"开始模拟面试"按钮，选择目标学校和专业，系统会生成针对性的面试问题。您可以录制回答，系统会分析您的语言表达、内容逻辑等方面，并提供改进建议。'
            },
            {
              title: '面试常见问题及应对策略',
              content: '面试中常见的问题包括：<ul><li>"请介绍一下你自己"</li><li>"为什么选择我们学校/专业？"</li><li>"你的职业规划是什么？"</li><li>"谈谈你的优缺点"</li><li>"你有什么问题想问我们？"</li></ul>在回答这些问题时，请保持简洁、真实，并突出与申请项目相关的经历和能力。'
            }
          ]
        },
        {
          title: '签证办理',
          description: '了解留学签证申请流程与材料准备',
          items: [
            {
              title: '需要准备哪些签证材料？',
              content: '在"签证办理"模块，系统会根据您申请的国家，提供详细的签证材料清单和准备指南。常见材料包括录取通知书、财产证明、体检报告等，具体要求因国家而异。'
            },
            {
              title: '如何预约签证面试？',
              content: '系统提供各国签证中心的官方链接和预约指南。在"签证办理"模块，点击"预约签证面试"，选择目标国家，系统会引导您完成预约流程。'
            },
            {
              title: '签证面试技巧',
              content: '参加签证面试时，请注意以下几点：<ul><li>准时到达，着装得体</li><li>带齐所有必要文件的原件和复印件</li><li>准确、简洁地回答问题</li><li>展示您有足够的资金支持学习</li><li>证明您有强烈的回国意愿</li><li>表现出对学习计划的热情和明确性</li></ul>'
            }
          ]
        },
        {
          title: '常见问题',
          description: '解答用户在使用过程中遇到的常见问题',
          items: [
            {
              title: '系统出现问题怎么办？',
              content: '如果您在使用过程中遇到技术问题，可以查看本帮助中心的常见问题解答，或发送邮件至support@xingyingmei.com寻求支持。您还可以在系统内使用"反馈"功能向我们报告问题。'
            },
            {
              title: '如何获取更多留学咨询服务？',
              content: '除了系统提供的自助工具外，我们还提供专业的留学咨询服务。您可以在"个人中心"页面点击"咨询服务"，预约专业顾问一对一指导。'
            },
            {
              title: '如何更新系统通知设置？',
              content: '在"个人中心"的"通知设置"中，您可以自定义接收哪些类型的通知以及通知方式（邮件、短信等）。我们建议保持重要通知的开启状态，以免错过关键信息。'
            },
            {
              title: '数据安全与隐私保护',
              content: '我们严格遵守数据保护法规，采取多重措施保护您的个人信息：<ul><li>所有数据传输采用SSL加密</li><li>敏感信息使用高级加密算法存储</li><li>定期进行安全审计和漏洞修复</li><li>严格的内部访问控制</li><li>不会未经您的同意与第三方分享您的个人数据</li></ul>'
            },
            {
              title: '如何联系客服？',
              tag: '客服支持',
              content: '我们提供多种联系方式为您提供支持：<br><br><strong>在线客服：</strong><ul><li>工作时间：周一至周五 9:00-18:00</li><li>点击页面右下角的"在线客服"按钮即可开始对话</li><li>平均响应时间：2分钟内</li></ul><br><strong>邮件支持：</strong><ul><li>技术问题：support@xingyingmei.com</li><li>留学咨询：consulting@xingyingmei.com</li><li>商务合作：business@xingyingmei.com</li><li>邮件响应时间：24小时内</li></ul><br><strong>电话支持：</strong><ul><li>客服热线：400-888-9999</li><li>服务时间：周一至周日 8:00-22:00</li><li>紧急情况可拨打24小时热线：400-888-0000</li></ul>'
            },
            {
              title: '系统维护与更新通知',
              tag: '系统公告',
              content: '为了提供更好的服务体验，我们会定期进行系统维护和功能更新：<br><br><strong>维护时间：</strong><ul><li>常规维护：每周日凌晨2:00-4:00</li><li>重大更新：提前一周通过邮件和系统通知告知</li><li>紧急维护：会在系统首页显著位置公告</li></ul><br><strong>更新内容：</strong><ul><li>功能优化和新功能发布</li><li>安全补丁和性能提升</li><li>用户体验改进</li><li>数据备份和系统稳定性增强</li></ul><br>维护期间可能会影响部分功能的使用，我们会尽量缩短维护时间。如有紧急需求，请联系客服获取支持。'
            }
          ]
        },
        {
          title: '技术支持',
          description: '获取技术问题的专业解答和解决方案',
          items: [
            {
              title: '浏览器兼容性问题',
              tag: '技术问题',
              content: '为了获得最佳使用体验，我们建议使用以下浏览器：<br><br><strong>推荐浏览器：</strong><ul><li>Chrome 90+（推荐）</li><li>Firefox 88+</li><li>Safari 14+</li><li>Edge 90+</li></ul><br><strong>不支持的浏览器：</strong><ul><li>Internet Explorer（所有版本）</li><li>过旧版本的Chrome、Firefox等</li></ul><br><strong>常见问题解决：</strong><ul><li>页面显示异常：清除浏览器缓存和Cookie</li><li>功能无法使用：检查是否启用了JavaScript</li><li>上传文件失败：检查网络连接和文件大小限制</li><li>登录问题：尝试无痕/隐私模式浏览</li></ul>'
            },
            {
              title: '文件上传问题',
              tag: '上传支持',
              content: '在使用文件上传功能时，请注意以下要求和限制：<br><br><strong>支持的文件格式：</strong><ul><li>文档类：PDF, DOC, DOCX, TXT</li><li>图片类：JPG, JPEG, PNG, GIF</li><li>表格类：XLS, XLSX, CSV</li><li>压缩包：ZIP, RAR</li></ul><br><strong>文件大小限制：</strong><ul><li>单个文件：最大50MB</li><li>图片文件：最大10MB</li><li>批量上传：总大小不超过200MB</li></ul><br><strong>上传失败解决方案：</strong><ul><li>检查文件格式是否支持</li><li>确认文件大小未超出限制</li><li>检查网络连接稳定性</li><li>尝试重新登录后再次上传</li><li>如问题持续，请联系技术支持</li></ul>'
            },
            {
              title: '账户安全设置',
              tag: '安全指南',
              content: '保护您的账户安全是我们共同的责任，请遵循以下安全建议：<br><br><strong>密码安全：</strong><ul><li>使用至少8位字符的强密码</li><li>包含大小写字母、数字和特殊字符</li><li>不要使用生日、姓名等容易猜测的信息</li><li>定期更换密码（建议3-6个月）</li><li>不要在多个网站使用相同密码</li></ul><br><strong>登录安全：</strong><ul><li>启用两步验证（如可用）</li><li>不要在公共设备上保存登录信息</li><li>使用后及时退出账户</li><li>定期检查登录记录</li></ul><br><strong>信息保护：</strong><ul><li>不要与他人分享账户信息</li><li>谨慎处理包含个人信息的邮件</li><li>及时更新联系方式</li><li>发现异常活动立即联系客服</li></ul>'
            },
            {
              title: '移动端使用指南',
              tag: '移动支持',
              content: '我们的系统支持移动设备访问，为您提供便捷的移动体验：<br><br><strong>移动端功能：</strong><ul><li>响应式设计，自适应各种屏幕尺寸</li><li>支持触摸操作和手势导航</li><li>优化的移动端界面布局</li><li>离线缓存，提升加载速度</li></ul><br><strong>推荐配置：</strong><ul><li>iOS 12+ / Android 8+</li><li>Safari / Chrome / Firefox 移动版</li><li>稳定的网络连接（WiFi或4G）</li></ul><br><strong>移动端限制：</strong><ul><li>部分高级功能仅在桌面端可用</li><li>大文件上传建议使用桌面端</li><li>复杂表单填写推荐桌面端操作</li></ul><br>如在移动端遇到问题，建议切换到桌面端或联系技术支持。'
            }
          ]
        },
        {
          title: '联系我们',
          description: '多种方式联系我们的专业团队',
          items: [
            {
              title: '公司信息',
              tag: '联系方式',
              content: '<strong>星影美留学服务平台</strong><br><br><strong>公司地址：</strong><br>北京市朝阳区建国门外大街1号国贸大厦A座2008室<br><br><strong>邮政编码：</strong>100020<br><br><strong>营业时间：</strong><br>周一至周五：9:00-18:00<br>周六：10:00-16:00<br>周日及法定节假日休息<br><br><strong>公司网站：</strong><br>www.xingyingmei.com<br><br><strong>社交媒体：</strong><br>微信公众号：星影美留学<br>新浪微博：@星影美留学官方<br>知乎：星影美留学'
            },
            {
              title: '专业咨询团队',
              tag: '专家服务',
              content: '我们拥有经验丰富的留学咨询专家团队，为您提供专业指导：<br><br><strong>美国留学专家：</strong><ul><li>李老师 - 10年美国名校申请经验</li><li>专长：商科、工程类专业申请</li><li>成功案例：哈佛、斯坦福、MIT等顶尖院校</li></ul><br><strong>英国留学专家：</strong><ul><li>王老师 - 8年英国留学咨询经验</li><li>专长：文科、艺术类专业申请</li><li>成功案例：牛津、剑桥、帝国理工等名校</li></ul><br><strong>加拿大留学专家：</strong><ul><li>张老师 - 6年加拿大留学规划经验</li><li>专长：理工科、医学类专业申请</li><li>成功案例：多伦多大学、UBC、麦吉尔等</li></ul><br>预约专家咨询请拨打：400-888-9999'
            },
            {
              title: '意见反馈',
              tag: '用户反馈',
              content: '您的意见和建议对我们非常重要，请通过以下方式向我们反馈：<br><br><strong>在线反馈：</strong><ul><li>登录系统后，点击页面底部的"意见反馈"</li><li>填写详细的问题描述和改进建议</li><li>我们会在48小时内回复您的反馈</li></ul><br><strong>邮件反馈：</strong><ul><li>发送邮件至：feedback@xingyingmei.com</li><li>请在邮件标题中注明"用户反馈"</li><li>详细描述您遇到的问题或建议</li></ul><br><strong>电话反馈：</strong><ul><li>拨打客服热线：400-888-9999</li><li>选择"意见反馈"服务</li><li>我们的客服人员会详细记录您的反馈</li></ul><br>感谢您帮助我们不断改进服务质量！'
            }
          ]
        }
      ]
    }
  },
  computed: {
    filteredItems() {
      if (!this.searchQuery || !this.searchQuery.trim()) {
        return this.helpSections[this.activeSection].items;
      }
      
      const query = this.searchQuery.toLowerCase().trim();
      return this.helpSections[this.activeSection].items.filter(item => 
        item.title.toLowerCase().includes(query) || 
        item.content.toLowerCase().replace(/<[^>]*>?/gm, '').includes(query) ||
        (item.tag && item.tag.toLowerCase().includes(query))
      );
    }
  },
  methods: {
    getSectionIcon(sectionTitle) {
      const icons = {
        '系统概述': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>',
        '后端管理指南': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg>',
        '系统设置': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>',
        '账户管理': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>',
        '申请准备': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>',
        '材料准备': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>',
        '提交申请': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>',
        '面试准备': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>',
        '签证办理': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>',
        '常见问题': '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>'
      };
      
      return icons[sectionTitle] || '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>';
    },
    handleGlobalSearch() {
      if (!this.globalSearch.trim()) {
        this.clearGlobalSearch();
        return;
      }
      
      const query = this.globalSearch.toLowerCase();
      const results = [];
      
      this.helpSections.forEach((section, sectionIndex) => {
        section.items.forEach((item, itemIndex) => {
          if (item.title.toLowerCase().includes(query) || 
              item.content.toLowerCase().includes(query)) {
            
            // 创建摘要，突出显示匹配文本
            let snippet = item.content;
            // 移除HTML标签
            snippet = snippet.replace(/<[^>]*>?/gm, '');
            // 截取相关上下文
            const index = snippet.toLowerCase().indexOf(query);
            if (index > -1) {
              const start = Math.max(0, index - 50);
              const end = Math.min(snippet.length, index + query.length + 50);
              snippet = (start > 0 ? '...' : '') + 
                        snippet.substring(start, end) + 
                        (end < snippet.length ? '...' : '');
            } else {
              snippet = snippet.substring(0, 100) + (snippet.length > 100 ? '...' : '');
            }
            
            results.push({
              section: section.title,
              sectionIndex: sectionIndex,
              itemIndex: itemIndex,
              title: item.title,
              snippet: snippet
            });
          }
        });
      });
      
      this.globalSearchResults = results;
       this.showAllResults = results.length > 0;
     },
    clearGlobalSearch() {
      this.globalSearch = '';
      this.showAllResults = false;
      this.globalSearchResults = [];
    },
    goToResult(result) {
      this.activeSection = result.sectionIndex;
      this.searchQuery = '';
      this.showAllResults = false;
      this.globalSearch = '';
      
      // 等待DOM更新后滚动到目标元素
      this.$nextTick(() => {
        const elements = document.querySelectorAll('.help-item');
        if (elements && elements[result.itemIndex]) {
          elements[result.itemIndex].scrollIntoView({ behavior: 'smooth' });
        }
      });
    }
  }
}
</script>

<style scoped>
.help-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
  color: #2c3e50;
  min-height: 100vh;
  width: 100%;
  background-color: #f8fafc;
  display: flex;
  flex-direction: column;
}

.help-header {
  padding: 3.5rem 2rem;
  text-align: center;
  background: linear-gradient(to right, #3498db, #2c3e50);
  color: white;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.help-header::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 100%;
  height: 15px;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.05), transparent);
  pointer-events: none;
}

.header-content {
  max-width: 1080px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.header-content h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transform: translateY(0);
  transition: transform 0.3s ease;
  text-align: center;
}

.header-subtitle {
  font-size: 1.25rem;
  font-weight: 400;
  opacity: 0.9;
  max-width: 36rem;
  margin: 0 auto 2.5rem auto;
  line-height: 1.6;
  text-align: center;
}

.header-search {
  max-width: 600px;
  margin: 0 auto;
  position: relative;
  transition: all 0.3s ease;
}

.header-search:focus-within {
  transform: scale(1.02);
}

.header-search input {
  width: 100%;
  padding: 1rem 1.5rem;
  padding-right: 2.5rem;
  border-radius: 50px;
  border: none;
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
  font-size: 1.1rem;
  outline: none;
  transition: all 0.3s;
  backdrop-filter: blur(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-search input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.header-search input:focus {
  background-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.search-button {
  position: absolute;
  right: 2px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
  border-radius: 50%;
  transition: all 0.2s;
}

.search-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.help-main {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  gap: 2rem;
  flex: 1;
}

.help-sidebar {
  width: 280px;
  flex-shrink: 0;
  min-width: 280px;
}

.sidebar-container {
  position: sticky;
  top: 2rem;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 10rem);
  transition: all 0.3s ease;
}

.search-box {
  margin-bottom: 1.5rem;
  position: relative;
}

.search-box input {
  width: 100%;
  padding: 0.75rem 1rem;
  padding-right: 2.5rem;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background-color: white;
  font-size: 0.9rem;
  outline: none;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
  height: 44px;
  box-sizing: border-box;
}

.search-box input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.search-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.nav {
  overflow-y: auto;
  flex: 1;
}

.nav-section {
  margin-bottom: 0.5rem;
}

.nav-item-title {
  padding: 0.75rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
  font-weight: 500;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
  color: #4b5563;
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  min-height: 44px;
}

.nav-icon {
  display: inline-flex;
  margin-right: 8px;
  color: #64748b;
  flex-shrink: 0;
}

.nav-item-title:hover {
  background-color: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.nav-item-title:hover .nav-icon {
  color: #3498db;
}

.nav-item-title.active {
  background-color: rgba(52, 152, 219, 0.15);
  color: #3498db;
  font-weight: 500;
}

.nav-item-title.active .nav-icon {
  color: #3498db;
}

.content {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 280px - 2rem);
}

.content-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 100%;
  min-height: 600px;
}

.content-header {
  padding: 2rem 2.5rem;
  border-bottom: 1px solid #eaecef;
  background-color: #f8fafc;
}

.content-header h2 {
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0;
  color: #334155;
}

.section-description {
  margin-top: 0.5rem;
  font-size: 0.95rem;
  color: #64748b;
}

.content-body {
  padding: 2rem 2.5rem;
  min-height: 500px;
}

.help-item {
  margin-bottom: 2.5rem;
  border-bottom: 1px dashed #eaecef;
  padding-bottom: 2rem;
}

.help-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.help-item-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.help-item-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0;
  color: #3498db;
}

.help-item-tag {
  font-size: 0.75rem;
  margin-left: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 9999px;
  background-color: #e5f2ff;
  color: #3498db;
}

.help-item-content {
  line-height: 1.8;
  color: #4b5563;
  font-size: 1rem;
}

.help-item-content ul {
  padding-left: 1.5rem;
  margin: 1rem 0;
}

.help-item-content ul li {
  margin-bottom: 0.75rem;
  position: relative;
}

.help-item-content strong {
  color: #334155;
  font-weight: 600;
}

.help-item-content a {
  color: #3498db;
  text-decoration: none;
  transition: all 0.2s;
}

.help-item-content a:hover {
  text-decoration: underline;
}

.no-results {
  padding: 2rem;
  text-align: center;
  background-color: #f8fafc;
  border-radius: 8px;
  margin-top: 2rem;
}

.no-results p {
  margin-bottom: 1rem;
  color: #6b7280;
}

.no-results button {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  background-color: #3498db;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.no-results button:hover {
  background-color: #2980b9;
}

.global-search-results {
  padding: 1rem 0;
}

.search-results-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.search-results-header h3 {
  font-size: 1.25rem;
  color: #334155;
  margin: 0;
}

.clear-search {
  background-color: #f1f5f9;
  color: #64748b;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.clear-search:hover {
  background-color: #e2e8f0;
  color: #475569;
}

.search-result-item {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.search-result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.result-section {
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 0.5rem;
}

.result-title {
  font-size: 1.125rem;
  color: #3498db;
  margin: 0 0 0.75rem 0;
  cursor: pointer;
}

.result-title:hover {
  text-decoration: underline;
}

.result-snippet {
  font-size: 0.95rem;
  color: #4b5563;
  margin: 0;
  line-height: 1.6;
}





@media (max-width: 1024px) {
  .help-main {
    flex-direction: column;
  }
  
  .help-sidebar {
    width: 100%;
  }
  
  .sidebar-container {
    position: relative;
    top: 0;
    height: auto;
    margin-bottom: 2rem;
  }
  
  .nav {
    max-height: 300px;
  }
}

@media (max-width: 640px) {
  .help-header {
    padding: 2.5rem 1rem;
  }
  
  .header-content h1 {
    font-size: 2.25rem;
  }
  
  .help-main {
    padding: 0 1rem;
  }
  
  .content-header, .content-body {
    padding: 1.5rem;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .footer-links {
    justify-content: center;
  }
}
</style>
