const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'src/components/admin/ai/AIAssistant.vue');
let content = fs.readFileSync(filePath, 'utf8');

// 更新API Base URL的默认值
const oldApiBaseUrl = "const apiBaseUrl = ref(localStorage.getItem('deepseekApiBaseUrl') || 'https://api.deepseek.com/v1');";
const newApiBaseUrl = "const apiBaseUrl = ref(localStorage.getItem('deepseekApiBaseUrl') || 'https://api.deepseek.com');";

content = content.replace(oldApiBaseUrl, newApiBaseUrl);

fs.writeFileSync(filePath, content);
console.log('API configuration updated successfully!');
