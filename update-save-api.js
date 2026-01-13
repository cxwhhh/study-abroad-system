const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'src/components/admin/ai/AIAssistant.vue');
let content = fs.readFileSync(filePath, 'utf8');

// 替换saveApiSettings函数
const oldSaveApi = `// 保存API设置
const saveApiSettings = () => {
  localStorage.setItem('deepseekApiKey', apiKey.value);
  localStorage.setItem('deepseekApiModel', apiModel.value);
  showApiSettings.value = false;
  checkApiConnection();
  ElMessage.success('API设置已保存');
};`;

const newSaveApi = `// 保存API设置
const saveApiSettings = () => {
  // 检查API密钥格式
  if (apiKey.value && !apiKey.value.startsWith('sk-')) {
    ElMessage.warning('API密钥应以sk-开头，请检查格式');
    return;
  }
  
  localStorage.setItem('deepseekApiKey', apiKey.value);
  localStorage.setItem('deepseekApiModel', apiModel.value);
  localStorage.setItem('deepseekApiBaseUrl', apiBaseUrl.value);
  showApiSettings.value = false;
  checkApiConnection();
  ElMessage.success('API设置已保存');
};`;

content = content.replace(oldSaveApi, newSaveApi);

fs.writeFileSync(filePath, content);
console.log('SaveApiSettings function updated successfully!');
