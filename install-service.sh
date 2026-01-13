#!/bin/bash

# 复制plist文件到LaunchAgents目录
cp com.xingyingmei.app.plist ~/Library/LaunchAgents/

# 加载服务
launchctl load ~/Library/LaunchAgents/com.xingyingmei.app.plist

echo "服务已安装并启动。系统重启后会自动运行。"
echo "要手动启动服务，请运行: ./start-service.sh"
echo "要手动停止服务，请运行: ./stop-service.sh" 