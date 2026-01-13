<template>
  <div class="login-container">
    <div class="login-header">
      <h1>新英美留学平台</h1>
      <p>管理员登录</p>
    </div>

    <form class="form" @submit.prevent="handleLogin">
      <div class="flex-column">
        <label>用户名/邮箱 </label>
      </div>
      <div class="inputForm">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" viewBox="0 0 32 32" height="20">
          <g data-name="Layer 3" id="Layer_3">
            <path
              d="m30.853 13.87a15 15 0 0 0 -29.729 4.082 15.1 15.1 0 0 0 12.876 12.918 15.6 15.6 0 0 0 2.016.13 14.85 14.85 0 0 0 7.715-2.145 1 1 0 1 0 -1.031-1.711 13.007 13.007 0 1 1 5.458-6.529 2.149 2.149 0 0 1 -4.158-.759v-10.856a1 1 0 0 0 -2 0v1.726a8 8 0 1 0 .2 10.325 4.135 4.135 0 0 0 7.83.274 15.2 15.2 0 0 0 .823-7.455zm-14.853 8.13a6 6 0 1 1 6-6 6.006 6.006 0 0 1 -6 6z">
            </path>
          </g>
        </svg>
        <input v-model="username" placeholder="请输入用户名或邮箱" class="input" type="text">
      </div>

      <div class="flex-column">
        <label>密码 </label>
      </div>
      <div class="inputForm">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" viewBox="-64 0 512 512" height="20">
          <path
            d="m336 512h-288c-26.453125 0-48-21.523438-48-48v-224c0-26.476562 21.546875-48 48-48h288c26.453125 0 48 21.523438 48 48v224c0 26.476562-21.546875 48-48 48zm-288-288c-8.8125 0-16 7.167969-16 16v224c0 8.832031 7.1875 16 16 16h288c8.8125 0 16-7.167969 16-16v-224c0-8.832031-7.1875-16-16-16zm0 0">
          </path>
          <path
            d="m304 224c-8.832031 0-16-7.167969-16-16v-80c0-52.929688-43.070312-96-96-96s-96 43.070312-96 96v80c0 8.832031-7.167969 16-16 16s-16-7.167969-16-16v-80c0-70.59375 57.40625-128 128-128s128 57.40625 128 128v80c0 8.832031-7.167969 16-16 16zm0 0">
          </path>
        </svg>
        <input v-model="password" placeholder="请输入密码" class="input" type="password">
      </div>

      <div class="flex-row">
        <div>
          <input v-model="rememberMe" type="checkbox" id="remember">
          <label for="remember">记住我 </label>
        </div>
        <span class="span" @click="forgotPassword">忘记密码?</span>
      </div>

      <button class="button-submit" type="submit">登 录</button>

      <p class="p line">或使用以下方式登录</p>

      <div class="flex-row">
        <button type="button" class="btn google" @click="loginWithGoogle">
          <svg xml:space="preserve" style="enable-background:new 0 0 512 512;" viewBox="0 0 512 512" y="0px" x="0px"
            xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" id="Layer_1" width="20"
            version="1.1">
            <path
              d="M113.47,309.408L95.648,375.94l-65.139,1.378C11.042,341.211,0,299.9,0,256c0-42.451,10.324-82.483,28.624-117.732h0.014l57.992,10.632l25.404,57.644c-5.317,15.501-8.215,32.141-8.215,49.456C103.821,274.792,107.225,292.797,113.47,309.408z"
              style="fill:#FBBB00;"></path>
            <path
              d="M507.527,208.176C510.467,223.662,512,239.655,512,256c0,18.328-1.927,36.206-5.598,53.451c-12.462,58.683-45.025,109.925-90.134,146.187l-0.014-0.014l-73.044-3.727l-10.338-64.535c29.932-17.554,53.324-45.025,65.646-77.911h-136.89V208.176h138.887L507.527,208.176L507.527,208.176z"
              style="fill:#518EF8;"></path>
            <path
              d="M416.253,455.624l0.014,0.014C372.396,490.901,316.666,512,256,512c-97.491,0-182.252-54.491-225.491-134.681l82.961-67.91c21.619,57.698,77.278,98.771,142.53,98.771c28.047,0,54.323-7.582,76.87-20.818L416.253,455.624z"
              style="fill:#28B446;"></path>
            <path
              d="M419.404,58.936l-82.933,67.896c-23.335-14.586-50.919-23.012-80.471-23.012c-66.729,0-123.429,42.957-143.965,102.724l-83.397-68.276h-0.014C71.23,56.123,157.06,0,256,0C318.115,0,375.068,22.126,419.404,58.936z"
              style="fill:#F14336;"></path>
          </svg>
          谷歌账号登录
        </button>

        <button type="button" class="btn wechat">
          <svg t="1698569955937" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
            p-id="4216" width="20" height="20">
            <path
              d="M1010.8 628c0-141.2-141.3-256.2-299.9-256.2-168 0-300.3 115.1-300.3 256.2 0 141.4 132.3 256.2 300.3 256.2 35.2 0 70.7-8.9 106-17.7l96.8 53-26.6-88.2c70.9-53.2 123.7-123.7 123.7-203.3zM618 588.8c-22.1 0-40-17.9-40-40s17.9-40 40-40 40 17.9 40 40c0 22-17.9 40-40 40z m194.3-0.3c-22.1 0-40-17.9-40-40s17.9-40 40-40 40 17.9 40 40-17.9 40-40 40z"
              fill="#00C800" p-id="4217"></path>
            <path
              d="M366.3 106.9c-194.1 0-353.1 132.3-353.1 300.3 0 97 52.9 176.6 141.3 238.4l-35.3 106.2 123.4-61.9c44.2 8.7 79.6 17.7 123.7 17.7 11.1 0 22.1-0.5 33-1.4-6.9-23.6-10.9-48.3-10.9-74 0-154.3 132.5-279.5 300.2-279.5 11.5 0 22.8 0.8 34 2.1C692 212.6 539.9 106.9 366.3 106.9zM247.7 349.2c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48z m246.6 0c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48z"
              fill="#00C800" p-id="4218"></path>
          </svg>
          微信登录
        </button>
      </div>

      <div class="login-footer">
        <p>© 2023 新英美留学平台 版权所有</p>
      </div>
    </form>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
          baseURL: 'http://localhost:8080/api',
  data() {
    return {
      username: '',
      password: '',
      rememberMe: false,
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      try {
        if (!this.username || !this.password) {
          ElMessage.warning('请输入用户名和密码')
          return
        }

        this.loading = true;

        // 创建axios实例
        const axiosInstance = axios.create({
          baseURL: '/api',
          timeout: 10000,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          withCredentials: true
        });

        // 添加请求拦截器
        axiosInstance.interceptors.request.use(
          config => {
            console.log('发送请求:', config.url);
            return config;
          },
          error => {
            console.error('请求错误:', error);
            return Promise.reject(error);
          }
        );

        // 修改响应拦截器
        axiosInstance.interceptors.response.use(
          response => response,
          error => {
            console.error('响应错误:', error)
            ElMessage.error('请求失败: ' + (error.response?.data?.message || error.message))
            return Promise.reject(error)
          }
        )

        // 调用管理员登录API
        const response = await axiosInstance.post('/admin/login', {
          username: this.username,
          password: this.password
        });

        console.log('登录响应:', response.data);

        if (response.data.code === 200) {
          const adminInfo = {
            ...response.data.data.admin,
            id: response.data.data.adminId,
            nickname: response.data.data.admin.nickname || response.data.data.admin.username,
            role: response.data.data.admin.role || '管理员'
          };

          localStorage.setItem('adminLoggedIn', 'true');
          localStorage.setItem('adminInfo', JSON.stringify(adminInfo));

          // 保存到登录历史
          let loginHistory = [];
          try {
            const historyJson = localStorage.getItem('adminLoginHistory');
            if (historyJson) {
              loginHistory = JSON.parse(historyJson);
            }
          } catch (error) {
            console.error('读取登录历史出错:', error);
            loginHistory = [];
          }

          // 始终保存账号到历史记录，但凭据只在"记住我"选中时保存
          const userToSave = {
            ...adminInfo,
            credentials: this.rememberMe ? {
              username: this.username,
              password: this.encryptPassword(this.password)
            } : null
          };

          // 检查是否已存在该用户
          const existingIndex = loginHistory.findIndex(item => item.id === adminInfo.id);
          if (existingIndex >= 0) {
            // 更新现有记录
            loginHistory[existingIndex] = userToSave;
          } else {
            // 添加新记录
            loginHistory.push(userToSave);
          }

          // 最多保存5个记录
          if (loginHistory.length > 5) {
            loginHistory = loginHistory.slice(-5);
          }

          // 保存更新后的历史
          localStorage.setItem('adminLoginHistory', JSON.stringify(loginHistory));

          if (this.rememberMe) {
            localStorage.setItem('adminCredentials', JSON.stringify({
              username: this.username,
              password: this.encryptPassword(this.password)
            }));
          }

          ElMessage.success('登录成功')
          setTimeout(() => {
            this.$router.push('/admin1')
          }, 1500)
        } else {
          ElMessage.error(response.data.message || '登录失败，请检查用户名和密码')
        }
      } catch (error) {
        console.error('登录出错:', error)
        ElMessage.error(error.response?.data?.message || '服务器连接错误，请稍后再试')
      } finally {
        this.loading = false;
      }
    },

    // 简单加密方法（仅用于记住密码功能）
    encryptPassword(password) {
      // 实际生产环境应使用更安全的加密方法
      return btoa(password);
    },

    // 解密方法
    decryptPassword(encrypted) {
      return atob(encrypted);
    },

    // 加载保存的凭据
    loadCredentials() {
      const savedCredentials = localStorage.getItem('adminCredentials');
      if (savedCredentials) {
        const credentials = JSON.parse(savedCredentials);
        this.username = credentials.username;
        this.password = this.decryptPassword(credentials.password);
        this.rememberMe = true;
      }
    },

    forgotPassword() {
      ElMessage.info('请联系系统管理员重置密码')
    },

    loginWithGoogle() {
      ElMessage.info('谷歌登录功能开发中')
    }
  },
  mounted() {
    // 组件挂载时加载保存的凭据
    this.loadCredentials();

    // 检查是否已登录
    if (localStorage.getItem('adminLoggedIn') === 'true') {
      // 如果已登录，跳转到管理员页面
      this.$router.push('/admin1');
    }
  }
}
</script>

<style scoped>
/* 添加一些全局样式 */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #151717;
  font-size: 28px;
  margin-bottom: 8px;
}

.login-header p {
  color: #666;
  font-size: 16px;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 12px;
  color: #666;
}

/* 原始样式 */
.form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: #ffffff;
  padding: 30px;
  width: 450px;
  border-radius: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

::placeholder {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.form button {
  align-self: flex-end;
}

.flex-column>label {
  color: #151717;
  font-weight: 600;
}

.inputForm {
  border: 1.5px solid #ecedec;
  border-radius: 10px;
  height: 50px;
  display: flex;
  align-items: center;
  padding-left: 10px;
  transition: 0.2s ease-in-out;
}

.input {
  margin-left: 10px;
  border-radius: 10px;
  border: none;
  width: 100%;
  height: 100%;
}

.input:focus {
  outline: none;
}

.inputForm:focus-within {
  border: 1.5px solid #2d79f3;
}

.flex-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
  justify-content: space-between;
}

.flex-row>div>label {
  font-size: 14px;
  color: black;
  font-weight: 400;
}

.span {
  font-size: 14px;
  margin-left: 5px;
  color: #2d79f3;
  font-weight: 500;
  cursor: pointer;
}

.button-submit {
  margin: 20px 0 10px 0;
  background-color: #151717;
  border: none;
  color: white;
  font-size: 15px;
  font-weight: 500;
  border-radius: 10px;
  height: 50px;
  width: 100%;
  cursor: pointer;
  transition: all 0.2s ease;
}

.button-submit:hover {
  background-color: #2d79f3;
}

.p {
  text-align: center;
  color: black;
  font-size: 14px;
  margin: 5px 0;
}

.p.line {
  display: flex;
  align-items: center;
  margin: 25px 0 15px;
}

.p.line:before,
.p.line:after {
  content: "";
  flex: 1;
  height: 1px;
  background-color: #e0e0e0;
  margin: 0 10px;
}

.btn {
  margin-top: 10px;
  width: 100%;
  height: 50px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 500;
  gap: 10px;
  border: 1px solid #ededef;
  background-color: white;
  cursor: pointer;
  transition: 0.2s ease-in-out;
}

.btn:hover {
  border: 1px solid #2d79f3;
}

.google {
  color: #555;
}

.wechat {
  color: #00c800;
}

@media (max-width: 500px) {
  .form {
    width: 100%;
    padding: 20px;
  }
}
</style>