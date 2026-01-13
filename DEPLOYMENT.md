# 新英美留学项目部署指南

这个文档提供了将星迎美留学系统部署到Mac服务器上的完整指南。

## 准备工作

1. 确保你已经注册了域名并将DNS解析指向你的Mac服务器IP地址
2. 确保你的Mac能够从公网访问（设置了端口转发或拥有公网IP）
3. 确保已安装以下软件：
   - JDK 21
   - Node.js
   - Nginx
   - MySQL 8.x

## 部署步骤

### 1. 修改配置文件

在部署前，需要修改以下配置文件：

- `xingyingmei.conf` 和 `xingyingmei-ssl.conf` - 替换 `your-domain.com` 为你的实际域名
- `setup-ssl.sh` - 替换 `your-domain.com` 和 `your-email@example.com` 为你的实际信息
- `src/main/resources/application.properties` - 根据需要修改数据库配置和上传目录

### 2. 构建前端和后端

```bash
# 构建前端
npm run build

# 构建后端
./mvnw clean package -DskipTests
```

### 3. 配置SSL证书（可选但推荐）

如果你想使用HTTPS（推荐），运行：

```bash
./setup-ssl.sh
```

这会自动获取SSL证书并配置Nginx。

### 4. 启动服务

手动启动：

```bash
./start-service.sh
```

设置为开机自启：

```bash
./install-service.sh
```

### 5. 验证部署

访问以下URL验证部署是否成功：

- 前端: http://your-domain.com 或 https://your-domain.com
- API: http://your-domain.com/api 或 https://your-domain.com/api

## 管理服务

- 启动服务: `./start-service.sh`
- 停止服务: `./stop-service.sh`
- 查看日志: 
  - 应用日志: `tail -f logs/app.log`
  - Nginx访问日志: `tail -f logs/nginx_access.log`
  - Nginx错误日志: `tail -f logs/nginx_error.log`

## 故障排除

1. 如果前端无法访问，检查Nginx配置和日志
2. 如果API无法访问，检查后端应用日志
3. 如果SSL证书有问题，重新运行 `./setup-ssl.sh`

## 维护

- SSL证书会自动更新（通过crontab）
- 定期检查日志文件大小，避免磁盘空间问题
- 定期备份数据库

## 安全提示

- 确保防火墙只开放必要的端口（80和443）
- 定期更新服务器和应用依赖
- 考虑设置自动备份 