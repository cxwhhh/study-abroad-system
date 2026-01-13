// 测试匹配API的脚本
const axios = require('axios');

async function testMatching() {
  try {
    const matchingRequest = {
      region: '英国',
      studentInfo: {
        userId: 1,
        undergraduateSchool: '北京大学',
        gpa: 3.8,
        gpaScale: '4.0',
        targetMajor: '计算机科学',
        targetDegree: '硕士',
        languageTest: 'IELTS',
        languageScore: 7.0,
        workExperience: '无',
        researchExperience: '有相关项目经验',
        awards: '优秀学生奖学金',
        publications: ''
      }
    };

    console.log('发送匹配请求:', JSON.stringify(matchingRequest, null, 2));
    
    const response = await axios.post('http://localhost:8080/api/matching/perform', matchingRequest, {
      headers: {
        'Content-Type': 'application/json'
      }
    });

    console.log('匹配响应状态:', response.status);
    console.log('匹配响应数据:', JSON.stringify(response.data, null, 2));
    
  } catch (error) {
    console.error('匹配请求失败:', error.message);
    if (error.response) {
      console.error('错误状态:', error.response.status);
      console.error('错误数据:', error.response.data);
    }
  }
}

testMatching();