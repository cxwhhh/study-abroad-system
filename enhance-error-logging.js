const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'src/components/admin/ai/AIAssistant.vue');
let content = fs.readFileSync(filePath, 'utf8');

// 在错误处理中添加更多详细信息
const oldErrorHandling = `  } catch (error) {
    console.error('API调用失败:', error);
    if (error.response) {
      console.error('错误详情:', error.response.data);
    }
    throw error;
  }`;

const newErrorHandling = `  } catch (error) {
    console.error('API调用失败:', error);
    if (error.response) {
      console.error('错误详情:', error.response.data);
      console.error('状态码:', error.response.status);
      console.error('请求配置:', {
        url: error.response.config.url,
        method: error.response.config.method,
        headers: error.response.config.headers,
        baseURL: error.response.config.baseURL
      });
    }
    throw error;
  }`;

content = content.replace(oldErrorHandling, newErrorHandling);

fs.writeFileSync(filePath, content);
console.log('Error handling enhanced successfully!');
