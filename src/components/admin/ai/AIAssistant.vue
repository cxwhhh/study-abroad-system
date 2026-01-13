<template>
  <div class="ai-assistant">
    <div class="ai-interface" :class="[{ 'single-column': !showChatSection }, modeClass]">
      <div class="ai-features">
        <div v-if="showImportSection" class="feature-section">
          <div class="section-header">
            <div class="section-title">
              <FileText class="section-icon" />
              <h3>学生资料提取</h3>
            </div>
          </div>
          <div class="import-layout">
            <div class="import-actions-panel">
              <div class="import-intro">
                <div class="template-guide">
                  <div class="template-guide-steps">
                    <span>① 下载模板</span>
                    <span>② 学生填写</span>
                    <span>③ 上传解析</span>
                    <span>④ 一键入库</span>
                  </div>
                  <div class="template-guide-note">无需提前提供用户ID；新用户会基于邮箱/手机号自动创建并分配ID</div>
                </div>
              </div>

              <div class="template-actions">
                <el-button class="template-download-btn" type="primary" @click="downloadStudentTemplate">
                  下载Word模版
                </el-button>
              </div>

              <div class="upload-container">
                <el-upload class="resume-uploader" drag action="#" :auto-upload="false" :on-change="handleFileChange"
                  :file-list="fileList"
                  accept=".docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document">
                  <div class="el-upload__text">
                    <Upload class="upload-icon" />
                    <div>将已填写的Word模板拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip">支持docx文件，最大10MB</div>
                  </div>
                </el-upload>
                <el-button type="primary" class="process-btn" :disabled="!fileList.length" @click="processDocument"
                  :loading="processingFile">
                  提取上传Word
                </el-button>
              </div>

              <div class="import-divider"></div>

              <div class="import-actions-row">
                <el-button @click="resetOnlineTemplate">重置编辑器模板</el-button>
                <el-button type="primary" :disabled="!onlineTemplateText.trim()" :loading="processingOnline"
                  @click="processOnlineTemplate">
                  从编辑器提取
                </el-button>
              </div>

              <div class="student-preview">
                <div class="student-preview-title">用户信息预览</div>
                <el-descriptions :column="1" size="small" border class="student-preview-table">
                  <el-descriptions-item v-for="item in studentPreviewItems" :key="item.key" :label="item.label">
                    <span class="student-preview-value">{{ item.value || '—' }}</span>
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>

            <div class="word-editor-panel">
              <div class="word-editor-canvas">
                <div class="word-toolbar">
                  <div class="toolbar-group">
                    <el-button-group>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('undo')">
                        <Undo class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('redo')">
                        <Redo class="toolbar-icon" />
                      </el-button>
                    </el-button-group>
                  </div>

                  <div class="toolbar-group">
                    <el-button-group>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('bold')">
                        <Bold class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('italic')">
                        <Italic class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('underline')">
                        <Underline class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('removeFormat')">
                        <Eraser class="toolbar-icon" />
                      </el-button>
                    </el-button-group>
                  </div>

                  <div class="toolbar-group">
                    <el-select v-model="blockFormat" size="small" style="width: 120px" @change="applyBlockFormat">
                      <el-option label="正文" value="P" />
                      <el-option label="标题 1" value="H1" />
                      <el-option label="标题 2" value="H2" />
                    </el-select>
                  </div>

                  <div class="toolbar-group">
                    <el-button-group>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('justifyLeft')">
                        <AlignLeft class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('justifyCenter')">
                        <AlignCenter class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('justifyRight')">
                        <AlignRight class="toolbar-icon" />
                      </el-button>
                    </el-button-group>
                  </div>

                  <div class="toolbar-group">
                    <el-button-group>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('insertUnorderedList')">
                        <List class="toolbar-icon" />
                      </el-button>
                      <el-button size="small" @mousedown.prevent @click="execEditorCommand('insertOrderedList')">
                        <ListOrdered class="toolbar-icon" />
                      </el-button>
                    </el-button-group>
                  </div>
                </div>

                <div class="word-page">
                  <div ref="wordEditorRef" class="word-page-editor" contenteditable="true" spellcheck="false"
                    @input="handleEditorInput" @keyup="saveEditorSelection" @mouseup="saveEditorSelection"
                    data-placeholder="这里是一张空白Word页面。保持每行 @字段名: 值 的格式，左侧点击“从编辑器提取”。"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="showEssaySection" class="feature-section">
          <div class="section-header">
            <div class="section-title">
              <Edit class="section-icon" />
              <h3>文书优化</h3>
            </div>
          </div>
          <div class="feature-description">
            <p>通过AI技术优化学生个人陈述、推荐信等申请文书，提供专业修改建议</p>
          </div>
          <el-input v-model="essayContent" type="textarea" :rows="8" placeholder="在此粘贴需要优化的文书内容..." />
          <div class="essay-actions">
            <el-select v-model="essayType" placeholder="选择文书类型">
              <el-option label="个人陈述 (PS)" value="personal_statement" />
              <el-option label="动机信 (ML)" value="motivation_letter" />
              <el-option label="推荐信" value="recommendation_letter" />
              <el-option label="简历" value="resume" />
            </el-select>
            <el-button type="primary" :disabled="!essayContent" @click="improveEssay" :loading="processingEssay">
              优化文书
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="showChatSection" class="ai-chat">
        <div class="chat-header">
          <h3>AI 聊天助手</h3>
          <div class="chat-header-actions">
            <el-button class="custom-button" @click="showApiSettings = true">
              <Settings class="btn-icon" />
              <span>API设置</span>
            </el-button>
            <el-button class="custom-button" type="primary" @click="resetConversation">
              <RefreshCw class="btn-icon" />
              <span>新会话</span>
            </el-button>
            <el-tooltip content="开启调试模式可以查看API请求细节" placement="top">
              <el-switch v-model="debugMode" active-text="调试模式" />
            </el-tooltip>
            <div class="api-status" :class="apiConnected ? 'connected' : 'disconnected'">
              {{ apiConnected ? 'API已连接' : 'API未连接' }}
            </div>
          </div>
        </div>

        <div class="chat-body">
          <div class="chat-messages" ref="chatMessagesContainer">
            <!-- 显示调试信息 -->
            <div v-if="debugMode" class="debug-info">
              <small>消息数组长度: {{ chatMessages ? chatMessages.length : 'undefined' }}</small>
              <small>是否为数组: {{ Array.isArray(chatMessages) ? '是' : '否' }}</small>
            </div>

            <!-- 有消息时显示消息 -->
            <div v-for="(message, index) in chatMessages" :key="`msg-${index}`" class="message"
              :class="message.role === 'assistant' ? 'assistant' : 'user'">
              <div class="message-avatar">
                <User v-if="message.role === 'user'" />
                <Zap v-else />
              </div>
              <div class="message-content">
                <div v-if="message.loading" class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
                <div v-else v-html="formatMessage(message.content || '')"></div>

                <!-- 调试模式下显示完整API响应 -->
                <div v-if="debugMode && message.role === 'assistant' && message.fullResponse" class="full-response">
                  <div class="response-toggle" @click="toggleResponse(index)">
                    {{ expandedResponses[index] ? '隐藏完整响应' : '显示完整响应' }}
                  </div>
                  <pre v-if="expandedResponses[index]"
                    class="response-data">{{ JSON.stringify(message.fullResponse, null, 2) }}</pre>
                </div>
              </div>
            </div>

            <!-- 无消息时显示欢迎 -->
            <div v-if="!chatMessages || chatMessages.length === 0" class="empty-chat-message">
              <Zap size="48" />
              <p>与AI助手开始对话吧！</p>
            </div>
          </div>

          <div class="chat-controls">
            <div class="chat-input">
              <div class="chat-composer">
                <el-input v-model="userMessage" type="textarea" :autosize="{ minRows: 1, maxRows: 6 }"
                  placeholder="输入问题，Enter发送，Ctrl+Enter换行" @keydown="handleKeyDown" />
                <el-button type="primary" :disabled="!userMessage.trim() || sendingMessage" @click="handleSendMessage()"
                  :loading="sendingMessage" class="send-btn">
                  发送
                </el-button>
              </div>
            </div>

            <div class="chat-footer">
              <div v-if="sendingMessage" class="sending-status">
                <div class="spinner"></div>
                正在获取AI回复...
              </div>
              <div v-else class="chat-tips">
                提示: 按Enter发送，Ctrl+Enter换行
                <span v-if="!apiConnected" class="api-status-warning">
                  | <el-tooltip content="API未连接，请开启调试模式并点击测试API连接按钮" placement="top">
                    <span class="error-text">⚠️ API未连接</span>
                  </el-tooltip>
                </span>
                <span v-if="debugMode" class="debug-actions">
                  | <a href="#" @click.prevent="forceResetChat">强制重置聊天</a>
                  | <a href="#" @click.prevent="logChatState">打印聊天状态</a>
                  | <a href="#" @click.prevent="manualResetChat" class="emergency-reset">🔧 手动安全重置</a>
                  | <a href="#" @click.prevent="emergencyResetComponent" class="emergency-reset">⚠️ 紧急重置组件</a>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 处理结果弹窗 -->
    <el-dialog v-model="showResultDialog" title="处理结果" width="80%">
      <div class="result-container">
        <div v-if="extractedInfo" class="extracted-info">
          <h3>提取的学生信息</h3>
          <div class="info-grid">
            <div v-for="(value, key) in extractedInfo" :key="key" class="info-item">
              <div class="info-label">{{ formatLabel(key) }}</div>
              <div class="info-value">{{ value }}</div>
            </div>
          </div>
          <div class="dialog-actions">
            <el-button type="primary" @click="saveExtractedInfo">导入到系统</el-button>
            <el-button @click="showResultDialog = false">关闭</el-button>
          </div>
        </div>
        <div v-else-if="improvedEssay" class="improved-essay">
          <h3>优化后的文书</h3>
          <div class="essay-comparison">
            <div class="original-essay">
              <h4>原文</h4>
              <div class="essay-content">{{ essayContent }}</div>
            </div>
            <div class="improved-essay-content">
              <h4>优化后</h4>
              <div class="essay-content">{{ improvedEssay }}</div>
            </div>
          </div>
          <div class="dialog-actions">
            <el-button type="primary" @click="copyImprovedEssay">复制优化文本</el-button>
            <el-button @click="showResultDialog = false">关闭</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- API设置弹窗 -->
    <el-dialog v-if="showChatSection" v-model="showApiSettings" title="AI接口设置" width="500px">
      <div class="api-settings">
        <el-form label-position="top">
          <el-form-item label="API密钥状态">
            <div class="api-key-status">
              <el-tag :type="apiConnected ? 'success' : 'danger'">
                {{ apiConnected ? 'API已连接' : 'API未连接' }}
              </el-tag>
              <div class="form-tip">使用固定API密钥: sk-2614***********a3f</div>
            </div>
          </el-form-item>
          <el-form-item label="API模型">
            <el-select v-model="apiModel" placeholder="选择API模型">
              <el-option label="Deepseek Chat (默认)" value="deepseek-chat" />
              <el-option label="Deepseek Reasoner" value="deepseek-reasoner" />
            </el-select>
          </el-form-item>
        </el-form>
        <div class="api-settings-options">
          <div></div>
          <el-button size="small" type="primary" @click="testConnection" :loading="testingConnection">测试连接</el-button>
        </div>
        <div class="api-actions">
          <el-button type="primary" @click="saveApiSettings">保存设置</el-button>
          <el-button @click="showApiSettings = false">取消</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 调试信息面板 -->
    <div v-if="showChatSection && debugMode" class="debug-panel">
      <h4>API调试信息</h4>
      <div class="debug-item">
        <strong>API状态:</strong>
        <span :class="apiConnected ? 'debug-status-ok' : 'debug-status-error'">
          {{ apiConnected ? '已连接' : '未连接' }}
        </span>
      </div>
      <div class="debug-item">
        <strong>API模型:</strong>
        <span :class="apiConnected ? 'debug-status-ok' : 'debug-status-error'">
          {{ apiModel }}
          <small>({{ apiModel === 'deepseek-chat' ? 'DeepSeek-V3-0324' : 'DeepSeek-R1-0528' }})</small>
        </span>
      </div>
      <div class="debug-item">
        <strong>聊天消息状态:</strong>
        <span :class="Array.isArray(chatMessages) ? 'debug-status-ok' : 'debug-status-error'">
          {{ Array.isArray(chatMessages) ? `数组(${chatMessages.length})` : '非数组' }}
        </span>
      </div>
      <div class="debug-item">
        <strong>上次API请求:</strong>
        <pre class="debug-code">{{ lastApiRequest }}</pre>
      </div>
      <div class="debug-item">
        <strong>上次API响应:</strong>
        <pre class="debug-code">{{ lastApiResponse }}</pre>
      </div>
      <div class="debug-item">
        <strong>紧急恢复工具:</strong>
        <div class="debug-recovery-tools">
          <el-button size="small" type="warning" @click="forceResetChat">
            强制重置聊天
          </el-button>
          <el-button size="small" type="warning" @click="manualResetChat">
            手动安全重置
          </el-button>
          <el-button size="small" type="danger" @click="emergencyResetComponent">
            紧急重置组件
          </el-button>
        </div>
      </div>
      <div class="debug-actions-panel">
        <el-button size="small" type="primary" @click="testConnection">测试API连接</el-button>
        <el-button size="small" @click="logChatState">打印聊天状态</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed, onBeforeUnmount, watch, reactive } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';
import axios from 'axios';
import { Document, Packer, Paragraph, TextRun } from 'docx';
import {
  FileText, Upload, Edit, User, Zap, RefreshCw,
  Settings, MessageSquare,
  Bold, Italic, Underline, Eraser,
  AlignLeft, AlignCenter, AlignRight,
  List, ListOrdered,
  Undo, Redo
} from 'lucide-vue-next';

const props = defineProps({
  mode: {
    type: String,
    default: 'full'
  }
});

const showImportSection = computed(() => props.mode === 'full' || props.mode === 'import');
const showEssaySection = computed(() => props.mode === 'full' || props.mode === 'assistant');
const showChatSection = computed(() => props.mode === 'full' || props.mode === 'assistant');
const modeClass = computed(() => `mode-${props.mode || 'full'}`);

// 以下代码帮助诊断聊天消息的渲染问题
const isMounted = ref(false);

// 状态变量
const apiKey = ref(localStorage.getItem('deepseekApiKey') || '');
const apiModel = ref(localStorage.getItem('deepseekApiModel') || 'deepseek-chat');
const apiBaseUrl = ref(localStorage.getItem('deepseekApiBaseUrl') || 'https://api.deepseek.com');
const showAdvancedSettings = ref(false);
const testingConnection = ref(false);
const apiConnected = ref(false);
const showApiSettings = ref(false);
const fileList = ref([]);
const processingFile = ref(false);
const onlineTemplateText = ref('');
const processingOnline = ref(false);
const wordEditorRef = ref(null);
const savedEditorRange = ref(null);
const blockFormat = ref('P');
const showResultDialog = ref(false);
const extractedInfo = ref(null);
const essayContent = ref('');
const essayType = ref('personal_statement');
const processingEssay = ref(false);
const improvedEssay = ref(null);
const userMessage = ref('');
// 确保初始化为空数组
const chatMessages = ref([]);
const sendingMessage = ref(false);
const debugMode = ref(false);
const lastApiRequest = ref('无');
const lastApiResponse = ref('无');
// 用于跟踪展开的响应
const expandedResponses = reactive({});
// 防止递归更新的标志
const isUpdatingChatMessages = ref(false);

// 切换响应显示状态
const toggleResponse = (index) => {
  expandedResponses[index] = !expandedResponses[index];
};

// 初始化
onMounted(() => {
  if (showImportSection.value && !onlineTemplateText.value.trim()) {
    onlineTemplateText.value = getDefaultTemplateText();
  }
  if (showImportSection.value) {
    setEditorContentFromText(onlineTemplateText.value || getDefaultTemplateText());
  }

  if (!showChatSection.value) {
    isMounted.value = true;
    return;
  }

  console.log('组件挂载，初始化聊天状态');

  try {
    // 设置固定API密钥
    apiKey.value = "sk-26144901518f43c8ad661f315e820a3f";

    // 确保模型设置正确
    if (!apiModel.value || (apiModel.value !== 'deepseek-chat' && apiModel.value !== 'deepseek-reasoner')) {
      apiModel.value = 'deepseek-chat'; // 默认使用 DeepSeek-V3-0324
      localStorage.setItem('deepseekApiModel', apiModel.value);
    }

    console.log(`初始化模型: ${apiModel.value} (${apiModel.value === 'deepseek-chat' ? 'DeepSeek-V3-0324' : 'DeepSeek-R1-0528'})`);

    // 强制初始化为空数组
    chatMessages.value = [];

    console.log('组件基本初始化完成，添加欢迎消息');

    // 添加欢迎消息
    chatMessages.value.push({
      role: 'assistant',
      content: '您好！我是AI助手，可以帮助您处理学生信息、优化申请文书，以及解答留学申请相关问题。请输入您的问题开始对话。'
    });

    // 标记组件已挂载
    isMounted.value = true;

    // 添加全局未捕获异常处理
    window.addEventListener('error', handleGlobalError);

    // 延迟测试API连接
    setTimeout(() => {
      testConnection().catch(e => console.error('初始API测试失败:', e));
    }, 1000);
  } catch (error) {
    console.error('组件挂载过程中发生错误:', error);
  }
});

// 组件卸载时清理
onBeforeUnmount(() => {
  console.log('组件卸载，执行清理操作');
  try {
    // 移除全局错误处理
    window.removeEventListener('error', handleGlobalError);

    // 重置各种状态
    sendingMessage.value = false;
    testingConnection.value = false;
  } catch (error) {
    console.error('组件卸载清理过程中发生错误:', error);
  }
});

// 全局错误处理 - 简化版
const handleGlobalError = (event) => {
  console.error('全局错误:', event);

  // 显示友好错误消息
  ElMessage.error('应用发生错误，请刷新页面');

  // 重置发送状态
  sendingMessage.value = false;
};

// 安全地向数组添加元素
const safeArrayPush = (array, item) => {
  try {
    if (Array.isArray(array)) {
      array.push(item);
      return true;
    } else {
      console.error('尝试向非数组对象使用push方法', array);
      return false;
    }
  } catch (error) {
    console.error('向数组添加元素时出错', error);
    return false;
  }
};

// 完全重写API调用函数，保存完整响应
const callSimpleAPI = async (message) => {
  if (!message || typeof message !== 'string') {
    throw new Error('无效的消息内容');
  }

  console.log('调用API，消息内容:', message.substring(0, 30) + '...');

  try {
    // 请求体
    const requestBody = {
      model: apiModel.value, // 使用用户在设置中选择的模型
      messages: [
        { role: "system", content: "You are a helpful assistant." },
        { role: "user", content: message }
      ],
      temperature: 0.7
    };

    // 更新调试信息
    if (debugMode.value) {
      lastApiRequest.value = JSON.stringify(requestBody, null, 2);
    }

    // 发送请求
    const response = await fetch("https://api.deepseek.com/v1/chat/completions", {
      method: "POST",
      headers: {
        "Authorization": "Bearer sk-26144901518f43c8ad661f315e820a3f",
        "Content-Type": "application/json",
        "X-Local-Auth": "edu-dev-bypass-2025",
        "X-Client-Env": "localhost"
      },
      body: JSON.stringify(requestBody)
    });

    // 获取原始文本响应
    const responseText = await response.text();
    console.log('API响应状态:', response.status);

    // 检查HTTP错误
    if (!response.ok) {
      console.error(`API HTTP错误 ${response.status}:`, responseText);
      throw new Error(`API错误 ${response.status}: ${responseText.substring(0, 100)}`);
    }

    // 解析JSON
    let data;
    try {
      data = JSON.parse(responseText);
    } catch (parseError) {
      console.error('API响应JSON解析错误:', parseError);
      throw new Error('无法解析API响应');
    }

    // 更新调试信息
    if (debugMode.value) {
      lastApiResponse.value = JSON.stringify(data, null, 2);
    }

    // 打印完整响应以便调试
    console.log('API完整响应:', data);

    // 检查数据结构
    if (!data || !data.choices || !data.choices[0] || !data.choices[0].message) {
      console.error('无效的API响应格式:', data);
      throw new Error('API返回了无效的响应格式');
    }

    const responseContent = data.choices[0].message.content || '抱歉，我无法理解您的问题。';
    console.log('提取的响应内容:', responseContent);

    // API连接状态更新为成功
    apiConnected.value = true;

    // 返回完整响应对象
    return {
      content: responseContent,
      fullResponse: data
    };
  } catch (error) {
    console.error('API调用失败:', error);
    // 连接状态更新为失败
    apiConnected.value = false;
    throw error;
  }
};

// 调用Deepseek API
const callDeepseekAPI = async (prompt) => {
  console.log('开始调用API，提示:', prompt);

  if (!apiConnected.value) {
    console.log('API未连接，尝试连接API');
    try {
      await testConnection();
      if (!apiConnected.value) {
        ElMessage.warning('API未连接，请先在设置中测试连接');
        showApiSettings.value = true;
        throw new Error('API未连接');
      }
    } catch (error) {
      console.error('自动连接API失败:', error);
      ElMessage.warning('API未连接，请先在设置中测试连接');
      showApiSettings.value = true;
      throw new Error('API未连接');
    }
  }

  try {
    console.log('准备API请求...');
    // 使用fetch API调用Deepseek
    const response = await fetch("https://api.deepseek.com/v1/chat/completions", {
      method: "POST",
      headers: {
        "Authorization": "Bearer sk-26144901518f43c8ad661f315e820a3f",
        "Content-Type": "application/json",
        "X-Local-Auth": "edu-dev-bypass-2025",
        "X-Client-Env": "localhost"
      },
      body: JSON.stringify({
        model: "deepseek-chat",
        messages: [
          { role: "system", content: "You are a helpful assistant." },
          { role: "user", content: prompt }
        ],
        temperature: 0.7
      })
    });

    console.log('API响应状态:', response.status);

    // 读取响应文本
    const responseText = await response.text();
    console.log('API响应文本:', responseText);

    if (!response.ok) {
      throw new Error(`${response.status} - ${responseText}`);
    }

    let data;
    try {
      data = JSON.parse(responseText);
      console.log('解析后的API响应:', data);
    } catch (parseError) {
      console.error('解析JSON失败:', parseError);
      throw new Error('无法解析API响应: ' + responseText);
    }

    // 检查响应格式
    if (!data.choices || !data.choices[0] || !data.choices[0].message || !data.choices[0].message.content) {
      console.error('格式不正确的API响应:', data);
      throw new Error('API响应格式不正确');
    }

    // 从响应中提取内容
    return data.choices[0].message.content;
  } catch (error) {
    console.error('API调用失败:', error);
    throw error;
  }
};

// 处理文件上传
const handleFileChange = (file) => {
  fileList.value = [file];
};

const escapeHtml = (text) => {
  return String(text ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
};

const textToEditorHtml = (text) => {
  return String(text ?? '')
    .split(/\r?\n/)
    .map(line => (line && line.length ? `<div>${escapeHtml(line)}</div>` : `<div><br></div>`))
    .join('');
};

const syncTemplateTextFromEditor = () => {
  const el = wordEditorRef.value;
  if (!el)
    return;
  const rawText = (el.innerText || '').replace(/\u00A0/g, ' ');
  onlineTemplateText.value = rawText;
};

const setEditorContentFromText = async (text) => {
  await nextTick();
  const el = wordEditorRef.value;
  if (!el)
    return;
  el.innerHTML = textToEditorHtml(text);
  syncTemplateTextFromEditor();
};

const saveEditorSelection = () => {
  const sel = window.getSelection?.();
  if (!sel || sel.rangeCount === 0)
    return;
  savedEditorRange.value = sel.getRangeAt(0);
};

const restoreEditorSelection = () => {
  const el = wordEditorRef.value;
  const sel = window.getSelection?.();
  if (!el || !sel)
    return;
  el.focus();
  if (savedEditorRange.value) {
    try {
      sel.removeAllRanges();
      sel.addRange(savedEditorRange.value);
    } catch (e) {
    }
  }
};

const execEditorCommand = (command, value = null) => {
  restoreEditorSelection();
  try {
    document.execCommand(command, false, value);
  } catch (e) {
  }
  saveEditorSelection();
  syncTemplateTextFromEditor();
};

const applyBlockFormat = (format) => {
  if (!format)
    return;
  const normalized = String(format).trim();
  if (!normalized)
    return;
  const tag = normalized.toLowerCase();
  execEditorCommand('formatBlock', `<${tag}>`);
};

const handleEditorInput = () => {
  syncTemplateTextFromEditor();
  saveEditorSelection();
};

const getDefaultTemplateText = () => {
  const lines = [
    '学生信息采集模板（请勿删除 @字段名 前缀）',
    '填写说明：每行以 @字段名 开头，冒号后填写内容；留空表示暂无。系统会根据邮箱/手机号自动创建或匹配账号并分配用户ID。国家用逗号分隔。日期格式：YYYY-MM-DD。',
    '',
    '@name(中文姓名): ',
    '@englishName(英文名): ',
    '@gender(性别 男/女): ',
    '@birthDate(出生日期 YYYY-MM-DD): ',
    '@nationality(国籍): ',
    '@passportNo(护照号): ',
    '@phone(手机号): ',
    '@email(邮箱): ',
    '@wechat(微信): ',
    '@currentSchool(当前学校): ',
    '@major(当前专业): ',
    '@gpa(GPA 如 3.85/4.0 或 87/100): ',
    '@gpaScale(GPA满分 如 4.0/100): ',
    '@toefl(托福): ',
    '@ielts(雅思): ',
    '@gre(GRE): ',
    '@gmat(GMAT): ',
    '@targetCountries(目标国家 用逗号分隔): ',
    '@notes(备注/补充): '
  ];
  return lines.join('\n');
};

const resetOnlineTemplate = () => {
  const nextText = getDefaultTemplateText();
  onlineTemplateText.value = nextText;
  setEditorContentFromText(nextText);
};

const generateDocxBlobFromText = async (text) => {
  const paragraphs = text
    .split(/\r?\n/)
    .map(line => new Paragraph({ children: [new TextRun({ text: line ?? '' })] }));

  const doc = new Document({
    sections: [{ children: paragraphs }]
  });

  return await Packer.toBlob(doc);
};

const processOnlineTemplate = async () => {
  if (!onlineTemplateText.value.trim()) {
    ElMessage.warning('请先填写内容');
    return;
  }

  processingOnline.value = true;
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在生成并提取信息...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    const blob = await generateDocxBlobFromText(onlineTemplateText.value);
    const formData = new FormData();
    formData.append('file', blob, 'student_profile_online_edit.docx');

    const res = await axios.post('/api/student/template/parse', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    const payload = res?.data;
    if (!payload || payload.code !== 200) {
      throw new Error(payload?.message || '解析失败');
    }

    extractedInfo.value = payload.data;
    improvedEssay.value = null;
    showResultDialog.value = true;
  } catch (error) {
    console.error('在线编辑提取失败:', error);
    ElMessage.error('在线编辑提取失败: ' + (error?.message || '未知错误'));
  } finally {
    processingOnline.value = false;
    loadingInstance.close();
  }
};

const downloadStudentTemplate = async () => {
  try {
    const res = await axios.get('/api/student/template', {
      responseType: 'blob'
    })
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `student_profile_template.docx`
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (e) {
    ElMessage.error('下载失败')
  }
}

// 处理文档提取信息
const processDocument = async () => {
  if (!fileList.value.length) {
    ElMessage.warning('请先上传文档');
    return;
  }

  processingFile.value = true;
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在提取信息...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    const rawFile = fileList.value[0]?.raw || fileList.value[0]
    const name = rawFile?.name || ''
    if (!name.toLowerCase().endsWith('.docx')) {
      throw new Error('当前仅支持docx模板文件')
    }

    const formData = new FormData()
    formData.append('file', rawFile)

    const res = await axios.post('/api/student/template/parse', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    const payload = res?.data
    if (!payload || payload.code !== 200) {
      throw new Error(payload?.message || '解析失败')
    }

    extractedInfo.value = payload.data
    improvedEssay.value = null
    showResultDialog.value = true
  } catch (error) {
    console.error('处理文档失败:', error);
    ElMessage.error('处理文档失败: ' + (error?.message || '未知错误'));
  } finally {
    processingFile.value = false;
    loadingInstance.close();
  }
};

// 优化文书
const improveEssay = async () => {
  if (!essayContent.value) {
    ElMessage.warning('请输入文书内容');
    return;
  }

  processingEssay.value = true;
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在优化文书...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    // 构建提示
    const prompt = `请优化以下${getEssayTypeName()}:\n\n${essayContent.value}\n\n请提供优化后的版本，保持原意但提升语言表达和逻辑性。`;

    console.log('开始优化文书，提示:', prompt);

    // 添加额外的连接状态检查
    if (!apiConnected.value) {
      console.log('API未连接，尝试连接');
      await testConnection();
      if (!apiConnected.value) {
        throw new Error('API未连接，请先在设置中连接API');
      }
    }

    // 准备请求
    const requestBody = {
      model: apiModel.value, // 使用用户在设置中选择的模型
      messages: [{ role: "user", content: prompt }],
      temperature: 0.7
    };

    console.log('文书优化请求:', requestBody);

    // 直接使用fetch API
    const response = await fetch("https://api.deepseek.com/v1/chat/completions", {
      method: "POST",
      headers: {
        "Authorization": "Bearer sk-26144901518f43c8ad661f315e820a3f",
        "Content-Type": "application/json",
        "X-Local-Auth": "edu-dev-bypass-2025",
        "X-Client-Env": "localhost"
      },
      body: JSON.stringify(requestBody)
    });

    console.log('文书优化API响应状态:', response.status);

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`${response.status} - ${errorText}`);
    }

    const responseText = await response.text();
    console.log('API响应文本:', responseText);

    let data;
    try {
      data = JSON.parse(responseText);
      console.log('文书优化API响应:', data);
    } catch (parseError) {
      throw new Error('解析响应失败: ' + responseText);
    }

    if (!data.choices || !data.choices[0] || !data.choices[0].message) {
      throw new Error('API响应格式不正确');
    }

    improvedEssay.value = data.choices[0].message.content;
    extractedInfo.value = null;
    showResultDialog.value = true;
  } catch (error) {
    console.error('优化文书失败:', error);
    ElMessage.error('优化文书失败: ' + error.message);
  } finally {
    processingEssay.value = false;
    loadingInstance.close();
  }
};

// 安全地更新聊天消息数组，避免递归更新
const updateChatMessages = (newMessages) => {
  // 防止递归调用
  if (isUpdatingChatMessages.value) {
    console.warn('避免递归更新 chatMessages');
    return;
  }

  try {
    isUpdatingChatMessages.value = true;
    chatMessages.value = Array.isArray(newMessages) ? [...newMessages] : [];
  } finally {
    // 使用微任务确保在当前事件循环结束后重置标志
    Promise.resolve().then(() => {
      isUpdatingChatMessages.value = false;
    });
  }
};

// 完全重写处理发送消息函数
// 处理键盘事件
const handleKeyDown = (e) => {
  if (e.key === 'Enter') {
    if (e.ctrlKey) {
      // Ctrl+Enter: 换行，手动插入换行符
      const textarea = e.target;
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const value = userMessage.value;

      // 在光标位置插入换行符
      userMessage.value = value.substring(0, start) + '\n' + value.substring(end);

      // 恢复光标位置
      nextTick(() => {
        textarea.selectionStart = textarea.selectionEnd = start + 1;
      });

      e.preventDefault();
      return;
    } else {
      // Enter: 发送消息
      e.preventDefault();
      handleSendMessage();
    }
  }
};

const handleSendMessage = (e) => {
  // 检查消息是否为空或者是否正在发送中
  if (!userMessage.value || !userMessage.value.trim() || sendingMessage.value) {
    return;
  }

  // 安全获取消息文本并清空输入框
  const messageText = userMessage.value.trim();
  userMessage.value = '';

  // 防止重复点击
  sendingMessage.value = true;

  // 确保chatMessages是数组
  if (!Array.isArray(chatMessages.value)) {
    console.warn('chatMessages不是数组，初始化为空数组');
    chatMessages.value = [];
  }

  console.log('将添加用户消息到聊天:', messageText);

  // 添加用户消息
  chatMessages.value.push({ role: 'user', content: messageText });

  // 立即滚动到底部
  scrollToBottom();

  // 延迟处理API请求，避免循环更新
  setTimeout(async () => {
    try {
      console.log('添加AI消息loading状态');
      // 添加加载状态消息
      chatMessages.value.push({
        role: 'assistant',
        content: '',
        loading: true
      });

      scrollToBottom();

      // 调用API
      let apiResponse;
      try {
        console.log('调用API...');
        apiResponse = await callSimpleAPI(messageText);
        console.log('API调用成功，返回内容:', apiResponse.content);
      } catch (apiError) {
        console.error('API调用失败:', apiError);
        apiResponse = {
          content: `抱歉，API调用失败: ${apiError?.message || '未知错误'}`,
          fullResponse: null
        };
      }

      // 找到并替换loading消息
      console.log('替换loading消息为AI回复');
      const updatedMessages = chatMessages.value.map(msg => {
        if (msg.loading) {
          return {
            role: 'assistant',
            content: apiResponse.content,
            fullResponse: apiResponse.fullResponse
          };
        }
        return msg;
      });

      // 更新聊天记录
      chatMessages.value = updatedMessages;

      console.log('聊天记录更新完成，当前消息数:', chatMessages.value.length);

      // 最后滚动
      scrollToBottom();
    } catch (error) {
      console.error('处理消息内部错误:', error);
      ElMessage.error('处理消息出错: ' + (error?.message || '未知错误'));
    } finally {
      // 无论如何，重置发送状态
      sendingMessage.value = false;
    }
  }, 100);
};

// 简化的滚动函数
const scrollToBottom = () => {
  try {
    console.log('滚动到底部');
    setTimeout(() => {
      const chatContainer = document.querySelector('.chat-messages');
      if (chatContainer) {
        // 使用原生方法直接滚动，不使用Vue响应式系统
        chatContainer.scrollTop = chatContainer.scrollHeight;
        console.log('滚动完成');
      } else {
        console.warn('找不到聊天容器元素');
      }
    }, 50);
  } catch (e) {
    console.error('滚动到底部出错:', e);
  }
};

// 简化的重置函数
const resetConversation = () => {
  try {
    console.log('重置对话');

    // 先重置状态标志
    sendingMessage.value = false;

    // 清空所有消息
    chatMessages.value = [];

    // 添加欢迎消息
    setTimeout(() => {
      chatMessages.value.push({
        role: 'assistant',
        content: '聊天已重置。您好！我是AI助手，可以帮助您处理学生信息、优化申请文书，以及解答留学申请相关问题。'
      });

      ElMessage.success('会话已重置');
      scrollToBottom();
    }, 100);
  } catch (error) {
    console.error('重置对话外部错误:', error);
  }
};

// 保存API设置
const saveApiSettings = () => {
  // 使用固定的API密钥
  apiKey.value = "sk-26144901518f43c8ad661f315e820a3f";

  // 保存模型选择
  localStorage.setItem('deepseekApiModel', apiModel.value);

  // 关闭设置对话框
  showApiSettings.value = false;

  // 自动测试连接
  testConnection();

  ElMessage.success(`API设置已保存，当前模型: ${apiModel.value === 'deepseek-chat' ? 'DeepSeek-V3-0324' : 'DeepSeek-R1-0528'}`);
};

// 保存提取的信息
const saveExtractedInfo = async () => {
  if (!extractedInfo.value) return
  if (!extractedInfo.value.userId) {
    ElMessage.error('缺少用户ID，无法入库')
    return
  }
  try {
    const res = await axios.post('/api/student/sync', extractedInfo.value)
    const payload = res?.data
    if (!payload || payload.code !== 200) {
      throw new Error(payload?.message || '入库失败')
    }
    ElMessage.success('学生信息已成功导入系统')
    showResultDialog.value = false
  } catch (e) {
    ElMessage.error('导入失败: ' + (e?.message || '未知错误'))
  }
};

// 复制优化后的文书
const copyImprovedEssay = () => {
  navigator.clipboard.writeText(improvedEssay.value)
    .then(() => {
      ElMessage.success('文本已复制到剪贴板');
    })
    .catch(err => {
      console.error('复制失败:', err);
      ElMessage.error('复制失败');
    });
};

// 格式化标签
const formatLabel = (key) => {
  const labels = {
    userId: '用户ID',
    name: '姓名',
    englishName: '英文名',
    gender: '性别',
    birthDate: '出生日期',
    email: '邮箱',
    phone: '电话',
    gpa: 'GPA',
    gpaScale: 'GPA满分',
    toefl: 'TOEFL成绩',
    ielts: 'IELTS成绩',
    gre: 'GRE成绩',
    gmat: 'GMAT成绩',
    nationality: '国籍',
    passportNo: '护照号',
    wechat: '微信',
    currentSchool: '当前学校',
    major: '当前专业',
    targetCountries: '目标国家',
    notes: '备注'
  };
  return labels[key] || key;
};

const studentPreviewItems = computed(() => {
  const orderedKeys = [
    'userId',
    'name',
    'englishName',
    'gender',
    'birthDate',
    'nationality',
    'passportNo',
    'phone',
    'email',
    'wechat',
    'currentSchool',
    'major',
    'gpa',
    'gpaScale',
    'toefl',
    'ielts',
    'gre',
    'gmat',
    'targetCountries',
    'notes'
  ];

  const editorInfo = {};
  const lines = String(onlineTemplateText.value || '').split(/\r?\n/);
  for (const line of lines) {
    const trimmed = String(line || '').trim();
    if (!trimmed.startsWith('@'))
      continue;
    const match = trimmed.match(/^@([a-zA-Z0-9_]+)(?:\([^)]*\))?\s*[:：]\s*(.*)$/);
    if (!match)
      continue;
    const key = match[1];
    const value = String(match[2] ?? '').trim();
    editorInfo[key] = value;
  }

  const extracted = extractedInfo.value && typeof extractedInfo.value === 'object' ? extractedInfo.value : {};

  return orderedKeys.map((key) => {
    const value = extracted[key] ?? editorInfo[key] ?? '';
    return { key, label: formatLabel(key), value };
  });
});

// 获取文书类型名称
const getEssayTypeName = () => {
  const types = {
    'personal_statement': '个人陈述',
    'motivation_letter': '动机信',
    'recommendation_letter': '推荐信',
    'resume': '简历'
  };
  return types[essayType.value] || '文书';
};

// 更新格式化消息函数，显示完整的API响应
const formatMessage = (message) => {
  if (!message || typeof message !== 'string') {
    console.warn('格式化消息收到非字符串值:', message);
    return '';
  }

  try {
    // 将换行符转换为<br>
    let formatted = message.replace(/\n/g, '<br>');

    // 支持加粗 **text**
    formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

    // 支持斜体 *text*
    formatted = formatted.replace(/\*([^*]+)\*/g, '<em>$1</em>');

    // 支持代码块 ```code```
    formatted = formatted.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>');

    // 支持行内代码 `code`
    formatted = formatted.replace(/`([^`]+)`/g, '<code>$1</code>');

    return formatted;
  } catch (error) {
    console.error('格式化消息失败:', error);
    return String(message); // 确保返回字符串
  }
};

// 监听聊天消息变化 - 修改为更安全的实现
watch(chatMessages, (newVal) => {
  console.log('聊天消息更新:', newVal);

  // 只在非更新过程中检查数组状态
  if (!isUpdatingChatMessages.value && !Array.isArray(newVal)) {
    console.warn('检测到chatMessages不是数组，但不在update中修复');
    // 不在watch中直接修改，避免循环
  }
}, { deep: true });

// 强制重置聊天函数
const forceResetChat = () => {
  try {
    console.log('强制重置聊天');

    // 重置所有状态
    sendingMessage.value = false;
    testingConnection.value = false;
    isUpdatingChatMessages.value = false;

    // 清空数组
    chatMessages.value = [];

    // 添加欢迎消息
    setTimeout(() => {
      chatMessages.value.push({
        role: 'assistant',
        content: '聊天已强制重置。您好！我是AI助手，可以帮助您处理学生信息、优化申请文书，以及解答留学申请相关问题。'
      });

      ElMessage.success('聊天已强制重置');
      scrollToBottom();
    }, 100);
  } catch (error) {
    console.error('强制重置失败:', error);
    ElMessage.error('强制重置失败');
  }
};

// 手动安全重置函数
const manualResetChat = () => {
  console.log('手动安全重置聊天');

  // 立即清除状态
  sendingMessage.value = false;
  testingConnection.value = false;
  isUpdatingChatMessages.value = false;

  // 清空消息数组
  chatMessages.value = [];

  // 添加重置消息
  setTimeout(() => {
    chatMessages.value.push({
      role: 'assistant',
      content: '⚠️ 聊天已手动安全重置。可以重新开始对话。'
    });

    ElMessage({
      message: '聊天已安全重置。如果问题仍然存在，请刷新页面。',
      type: 'warning',
      duration: 5000
    });

    scrollToBottom();

    // 尝试重新连接API
    setTimeout(() => {
      testConnection().catch(() => { });
    }, 1000);
  }, 100);
};

// 完全重写API测试连接函数
const testConnection = async () => {
  try {
    testingConnection.value = true;
    console.log('测试API连接...');

    // 简短测试请求
    const requestBody = {
      model: apiModel.value, // 使用用户在设置中选择的模型
      messages: [{ role: "user", content: "Test" }],
      max_tokens: 5
    };

    if (debugMode.value) {
      console.log('测试连接请求:', requestBody);
    }

    const response = await fetch("https://api.deepseek.com/v1/chat/completions", {
      method: "POST",
      headers: {
        "Authorization": "Bearer sk-26144901518f43c8ad661f315e820a3f",
        "Content-Type": "application/json",
        "X-Local-Auth": "edu-dev-bypass-2025",
        "X-Client-Env": "localhost"
      },
      body: JSON.stringify(requestBody)
    });

    // 读取响应
    const text = await response.text();

    if (!response.ok) {
      throw new Error(`API错误 ${response.status}: ${text}`);
    }

    // 尝试解析
    const data = JSON.parse(text);

    // 验证响应
    if (data && data.choices && data.choices.length > 0) {
      apiConnected.value = true;
      ElMessage.success(`API连接成功 (模型: ${apiModel.value})`);
      return true;
    } else {
      throw new Error('API返回了不完整的响应');
    }
  } catch (error) {
    console.error('API连接测试失败:', error);
    apiConnected.value = false;
    ElMessage.error(`API连接失败: ${error.message}`);
    return false;
  } finally {
    testingConnection.value = false;
  }
};

// 切换API Base URL
const toggleApiUrl = () => {
  apiBaseUrl.value = apiBaseUrl.value.includes('/v1') ? apiBaseUrl.value.replace('/v1', '') : apiBaseUrl.value + '/v1';
};

// 打印聊天状态
const logChatState = () => {
  console.log('当前聊天消息状态:', chatMessages.value);
  console.log('是否数组:', Array.isArray(chatMessages.value));
  console.log('消息数量:', Array.isArray(chatMessages.value) ? chatMessages.value.length : 'N/A');

  try {
    // 尝试克隆一个副本
    const copy = Array.isArray(chatMessages.value) ? [...chatMessages.value] : null;
    console.log('消息副本:', copy);
    ElMessage.info('聊天状态已打印到控制台');
  } catch (error) {
    console.error('创建副本失败:', error);
  }
};

// 紧急重置组件函数
const emergencyResetComponent = () => {
  try {
    console.log('紧急重置组件');

    // 重置状态标志
    sendingMessage.value = false;
    testingConnection.value = false;
    isUpdatingChatMessages.value = false;
    apiConnected.value = false;

    // 移除事件监听器并重新添加
    try {
      window.removeEventListener('error', handleGlobalError);
    } catch (e) {
      console.warn('移除事件监听器失败:', e);
    }

    // 清空消息
    chatMessages.value = [];

    // 添加紧急消息
    setTimeout(() => {
      // 重新添加事件监听器
      window.addEventListener('error', handleGlobalError);

      chatMessages.value.push({
        role: 'assistant',
        content: '⚠️ 组件已紧急重置。请在问题解决后刷新页面。您仍可继续对话，但某些功能可能不正常。'
      });

      ElMessage.warning('组件状态已重置，请刷新页面以完全修复问题');
      scrollToBottom();

      // 延迟测试API连接
      setTimeout(() => {
        testConnection().catch(e => console.error('重置后API连接失败:', e));
      }, 1000);
    }, 100);
  } catch (error) {
    console.error('紧急重置外部错误:', error);
    alert('紧急重置失败，请刷新页面');
  }
};
</script>

<style scoped>
.ai-assistant {
  background-color: #f5f7fa;
  min-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
}

.import-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-top: 12px;
  align-items: stretch;
}

.import-actions-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: #f5f7fa;
  border-radius: 12px;
  padding: 18px;
  min-height: 520px;
  overflow: auto;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.import-actions-row {
  display: flex;
  gap: 12px;
}

.import-divider {
  height: 1px;
  background: #f0f0f0;
}

.word-editor-panel {
  min-height: 520px;
}

.student-preview {
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.student-preview-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.student-preview-table :deep(.el-descriptions__label) {
  width: 120px;
}

.student-preview-value {
  color: #111;
}

@media (min-width: 1024px) {
  .import-layout {
    grid-template-columns: minmax(320px, 420px) 1fr;
  }
}

.word-editor-canvas {
  background-color: #f5f7fa;
  border-radius: 12px;
  padding: 18px;
  min-height: 520px;
  overflow: auto;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.template-download-btn {
  width: 100%;
}

.word-toolbar {
  position: sticky;
  top: 0;
  z-index: 2;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 10px;
  backdrop-filter: blur(10px);
  margin-bottom: 14px;
}

.toolbar-group {
  display: flex;
  align-items: center;
}

.toolbar-icon {
  width: 16px;
  height: 16px;
}

.word-page {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  padding: 48px 56px;
  min-height: 860px;
  max-width: 880px;
  margin: 0 auto;
}

.word-page-editor {
  width: 100%;
  min-height: 760px;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  line-height: 1.8;
  color: #111;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  white-space: pre-wrap;
  word-break: break-word;
}

.word-page-editor:empty::before {
  content: attr(data-placeholder);
  color: #999;
}

.ai-interface {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin: 0 16px 16px;
  min-height: 0;
}

@media (min-width: 768px) {
  .ai-interface {
    display: grid;
    grid-template-columns: minmax(320px, 440px) 1fr;
    gap: 24px;
    align-items: stretch;
  }

  .ai-interface.single-column {
    grid-template-columns: 1fr;
  }
}

.ai-features {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.mode-assistant .ai-features {
  height: clamp(520px, calc(100vh - 220px), 920px);
  min-height: 0;
}

.mode-assistant .ai-features .feature-section {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.mode-assistant .ai-features .feature-section :deep(.el-textarea) {
  flex: 1;
  min-height: 0;
}

.mode-assistant .ai-features .feature-section :deep(.el-textarea__inner) {
  height: 100%;
  min-height: 100%;
}

.feature-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.feature-description {
  margin-bottom: 16px;
  color: #666;
}

.import-intro {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
}

.template-guide-steps {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-weight: 500;
  color: #303133;
}

.template-guide-note {
  margin-top: 6px;
}

.upload-container {
  margin-top: 16px;
}

.upload-icon {
  width: 40px;
  height: 40px;
  color: #999;
  margin-bottom: 8px;
}

.process-btn {
  margin-top: 16px;
  width: 100%;
}

.essay-actions {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.essay-actions .el-select {
  width: 200px;
}

.ai-chat {
  background-color: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  height: clamp(520px, calc(100vh - 220px), 920px);
  min-height: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
  overflow: hidden;
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, rgba(255, 255, 255, 1) 0%, rgba(250, 250, 252, 1) 100%);
}

.chat-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.chat-header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.api-status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
}

.api-status.connected {
  background-color: #f6ffed;
  color: #52c41a;
}

.api-status.disconnected {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.chat-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  /* 防止出现双重滚动条 */
  min-height: 0;
}

.chat-messages {
  flex: 1;
  padding: 18px 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background-color: #f6f7fb;
  position: relative;
}

.chat-controls {
  border-top: 1px solid #f0f0f0;
  background-color: rgba(255, 255, 255, 0.92);
  margin-top: auto;
  /* 确保控制区域在底部 */
  backdrop-filter: blur(10px);
  position: sticky;
  bottom: 0;
}

.chat-input {
  padding: 14px 16px;
  background-color: transparent;
}

.chat-composer {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 78%;
  animation: fadeIn 0.3s ease-in-out;
  position: relative;
  margin-bottom: 4px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.12);
}

.message.assistant .message-avatar {
  background-color: #1677ff;
  color: white;
}

.message.user .message-avatar {
  background-color: #07c160;
  color: white;
}

.message-content {
  padding: 12px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  overflow-wrap: break-word;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  position: relative;
}

.message.assistant .message-content {
  background-color: rgba(255, 255, 255, 0.96);
  border-top-left-radius: 4px;
  margin-left: 12px;
  color: #333;
}

.message.assistant .message-content::before {
  content: "";
  position: absolute;
  left: -8px;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid white;
}

.message.user .message-content {
  background: linear-gradient(135deg, rgba(149, 236, 105, 1) 0%, rgba(129, 227, 94, 1) 100%);
  border-top-right-radius: 4px;
  margin-right: 12px;
  color: #333;
}

.message.user .message-content::before {
  content: "";
  position: absolute;
  right: -8px;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-left: 8px solid rgba(149, 236, 105, 1);
}

.chat-input .el-textarea__inner {
  border-radius: 16px;
  padding: 10px 12px;
  resize: none;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(15, 23, 42, 0.12);
}

.chat-input .send-btn {
  align-self: flex-end;
  border-radius: 14px;
  height: 40px;
  padding: 0 16px;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #1890ff;
  opacity: 0.6;
  animation: typing 1.5s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.3s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.6s;
}

@keyframes typing {

  0%,
  100% {
    transform: translateY(0);
    opacity: 0.6;
  }

  50% {
    transform: translateY(-5px);
    opacity: 1;
  }
}

.chat-footer {
  padding: 8px 16px;
  color: #909399;
  font-size: 12px;
  text-align: center;
  background-color: white;
}

.sending-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(24, 144, 255, 0.2);
  border-top-color: #1890ff;
  border-radius: 50%;
  animation: spinner 1s linear infinite;
}

@keyframes spinner {
  to {
    transform: rotate(360deg);
  }
}

.chat-tips {
  font-style: italic;
}

/* 调试信息样式 */
.debug-info {
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.debug-actions {
  font-size: 12px;
  color: #909399;
}

.emergency-reset {
  color: #ff4d4f;
  font-weight: bold;
}

.api-status-warning {
  color: #e6a23c;
}

.error-text {
  color: #f56c6c;
  cursor: pointer;
}

/* 调试信息面板样式 */
.debug-panel {
  padding: 16px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin: 12px 16px;
  max-height: 500px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 12px;
  line-height: 1.5;
}

.debug-panel h4 {
  margin-top: 0;
  margin-bottom: 12px;
}

.debug-item {
  margin-bottom: 12px;
}

.debug-code {
  background-color: #fff;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 8px 0;
  border: 1px solid #dcdfe6;
  max-height: 150px;
}

.debug-status-ok {
  color: #67c23a;
  font-weight: bold;
}

.debug-status-error {
  color: #f56c6c;
  font-weight: bold;
}

.debug-actions-panel {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

/* 显示完整API响应的样式 */
.full-response {
  margin-top: 10px;
  border-top: 1px dashed #e0e0e0;
  padding-top: 8px;
}

.response-toggle {
  font-size: 12px;
  color: #1890ff;
  cursor: pointer;
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  background-color: rgba(24, 144, 255, 0.1);
  margin-bottom: 6px;
}

.response-data {
  background-color: #f8f8f8;
  padding: 8px;
  border-radius: 4px;
  font-size: 11px;
  overflow-x: auto;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}

/* 调试恢复工具样式 */
.debug-recovery-tools {
  display: flex;
  gap: 8px;
  margin: 8px 0;
}

/* 空消息样式 */
.empty-chat-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}

.empty-chat-message svg {
  margin-bottom: 16px;
  color: #c0c4cc;
}

/* 结果弹窗样式 */
.result-container {
  max-height: 70vh;
  overflow-y: auto;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.info-item {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.info-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.dialog-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.essay-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-top: 16px;
}

.original-essay,
.improved-essay-content {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.essay-content {
  white-space: pre-wrap;
  max-height: 400px;
  overflow-y: auto;
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.6;
}

/* API设置样式 */
.api-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.api-settings-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 12px 0;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.btn-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
}

/* 格式化消息样式 */
:deep(pre) {
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
  font-size: 13px;
}

:deep(code) {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9em;
}

:deep(strong) {
  font-weight: 600;
}

:deep(em) {
  font-style: italic;
}
</style>
