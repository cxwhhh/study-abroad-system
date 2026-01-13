import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import fs from 'node:fs'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    host: '0.0.0.0', // 允许外部访问
    https: false, // 先设为false，因为花生壳可能会处理HTTPS
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 不重写路径
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    },
    allowedHosts: [
      'localhost',
      '127.0.0.1',
      '32327ts0ki48.vicp.fun'
    ]
  }
})