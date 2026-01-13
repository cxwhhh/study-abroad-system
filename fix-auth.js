const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'src/components/admin/ai/AIAssistant.vue');
let content = fs.readFileSync(filePath, 'utf8');

// 替换所有的 'Authorization': `Bearer ${apiKey.value}` 为 'Authorization': apiKey.value
content = content.replace(/'Authorization': `Bearer \${apiKey\.value}`/g, "'Authorization': apiKey.value");

fs.writeFileSync(filePath, content);
console.log('Authorization headers updated successfully!');
