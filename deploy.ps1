# Windows 部署脚本
# 为域名 3231232313213.xyz 部署项目到Windows服务器

param(
    [switch]$SkipBuild = $false,
    [switch]$SkipBackend = $false
)

$DOMAIN = "3231232313213.xyz"
$PROJECT_DIR = "C:\xingyingmei\xingyingmei"
$NGINX_PATH = "C:\nginx"
$JAVA_HOME = $env:JAVA_HOME

Write-Host "开始部署项目到Windows服务器..." -ForegroundColor Green
Write-Host "域名: $DOMAIN" -ForegroundColor Cyan

# 检查是否以管理员身份运行
if (-NOT ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Host "建议以管理员身份运行此脚本以获得完整权限!" -ForegroundColor Yellow
}

# 创建项目目录
Write-Host "创建项目目录..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path $PROJECT_DIR
New-Item -ItemType Directory -Force -Path "$PROJECT_DIR\dist"
New-Item -ItemType Directory -Force -Path "$PROJECT_DIR\uploads"
New-Item -ItemType Directory -Force -Path "$NGINX_PATH\logs"
New-Item -ItemType Directory -Force -Path "$NGINX_PATH\conf\conf.d"

# 检查必要的软件
Write-Host "检查必要的软件..." -ForegroundColor Yellow

# 检查Node.js
try {
    $nodeVersion = node --version
    Write-Host "Node.js版本: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "错误: 未找到Node.js，请先安装Node.js" -ForegroundColor Red
    Write-Host "下载地址: https://nodejs.org/" -ForegroundColor Cyan
    exit 1
}

# 检查Java
if (-not $JAVA_HOME) {
    Write-Host "警告: 未设置JAVA_HOME环境变量" -ForegroundColor Yellow
}

try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "Java版本: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "错误: 未找到Java，请先安装JDK 17或更高版本" -ForegroundColor Red
    exit 1
}

# 检查Maven Wrapper
try {
    if (Test-Path ".\mvnw.cmd") {
        $mavenVersion = .\mvnw.cmd --version | Select-String "Apache Maven"
        Write-Host "Maven版本 (使用项目自带的Maven Wrapper): $mavenVersion" -ForegroundColor Green
        $mavenCommand = ".\mvnw.cmd"
    } else {
        # 如果没有Maven Wrapper，尝试系统Maven
        $mavenVersion = mvn --version | Select-String "Apache Maven"
        Write-Host "Maven版本 (使用系统Maven): $mavenVersion" -ForegroundColor Green
        $mavenCommand = "mvn"
    }
} catch {
    Write-Host "错误: 未找到Maven或Maven Wrapper" -ForegroundColor Red
    Write-Host "项目应该包含mvnw.cmd文件，如果没有，请安装Maven" -ForegroundColor Cyan
    Write-Host "Maven下载地址: https://maven.apache.org/download.cgi" -ForegroundColor Cyan
    exit 1
}

if (-not $SkipBuild) {
    # 构建前端项目
    Write-Host "构建前端项目..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "npm install 失败" -ForegroundColor Red
        exit 1
    }

    npm run build
    if ($LASTEXITCODE -ne 0) {
        Write-Host "前端构建失败" -ForegroundColor Red
        exit 1
    }

    # 复制前端构建文件到部署目录
    Write-Host "复制前端文件..." -ForegroundColor Yellow
    Copy-Item -Path "dist\*" -Destination "$PROJECT_DIR\dist" -Recurse -Force
    Copy-Item -Path "public\images" -Destination "$PROJECT_DIR\" -Recurse -Force
}

if (-not $SkipBackend) {
    # 构建后端项目
    Write-Host "构建后端项目..." -ForegroundColor Yellow
    & $mavenCommand clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "后端构建失败" -ForegroundColor Red
        exit 1
    }
}

# 配置Nginx
Write-Host "配置Nginx..." -ForegroundColor Yellow

# 检查Nginx是否存在
if (-not (Test-Path "$NGINX_PATH\nginx.exe")) {
    Write-Host "错误: 未找到Nginx，请先安装Nginx for Windows" -ForegroundColor Red
    Write-Host "下载地址: http://nginx.org/en/download.html" -ForegroundColor Cyan
    exit 1
}

# 创建项目Nginx配置目录和文件
Write-Host "设置Nginx配置..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path ".\conf\conf.d"
New-Item -ItemType Directory -Force -Path ".\temp\client_body_temp", ".\temp\proxy_temp", ".\temp\fastcgi_temp", ".\temp\uwsgi_temp", ".\temp\scgi_temp"

# 复制必要的配置文件
Copy-Item "C:\nginx\conf\mime.types" ".\conf\mime.types" -Force -ErrorAction SilentlyContinue
Copy-Item "xingyingmei.conf" ".\conf\conf.d\xingyingmei.conf" -Force

# 测试Nginx配置
Write-Host "测试Nginx配置..." -ForegroundColor Yellow
& "$NGINX_PATH\nginx.exe" -t -c "$PROJECT_DIR\conf\nginx.conf"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Nginx配置测试通过" -ForegroundColor Green
    
    # 重新加载Nginx配置
    try {
        & "$NGINX_PATH\nginx.exe" -s reload -c "$PROJECT_DIR\conf\nginx.conf"
        Write-Host "Nginx配置已重新加载" -ForegroundColor Green
    } catch {
        Write-Host "Nginx重新加载失败，尝试启动Nginx..." -ForegroundColor Yellow
        & "$NGINX_PATH\nginx.exe" -c "$PROJECT_DIR\conf\nginx.conf"
    }
} else {
    Write-Host "Nginx配置有误，请检查" -ForegroundColor Red
    exit 1
}

if (-not $SkipBackend) {
    # 启动后端服务
    Write-Host "启动后端服务..." -ForegroundColor Yellow
    
    # 停止现有的Java进程（如果存在）
    $existingProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*study-service*" }
    if ($existingProcess) {
        Write-Host "停止现有的后端服务..." -ForegroundColor Yellow
        $existingProcess | Stop-Process -Force
        Start-Sleep -Seconds 3
    }

    # 启动新的后端服务
    $jarFile = Get-ChildItem -Path "target" -Name "study-service-*.jar" | Select-Object -First 1
    if ($jarFile) {
        Write-Host "启动后端服务: $jarFile" -ForegroundColor Green
        
        # 创建日志目录
        New-Item -ItemType Directory -Force -Path "logs"
        
        # 启动Java应用
        Start-Process -FilePath "java" -ArgumentList "-jar", "target\$jarFile" -RedirectStandardOutput "logs\app.log" -RedirectStandardError "logs\app_error.log" -NoNewWindow
        
        # 保存进程ID
        Start-Sleep -Seconds 2
        $javaProcess = Get-Process -Name "java" | Where-Object { $_.CommandLine -like "*study-service*" } | Select-Object -First 1
        if ($javaProcess) {
            $javaProcess.Id | Out-File -FilePath "app.pid" -Encoding UTF8
            Write-Host "后端服务已启动，进程ID: $($javaProcess.Id)" -ForegroundColor Green
        }
    } else {
        Write-Host "错误: 未找到后端JAR文件" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n部署完成！" -ForegroundColor Green
Write-Host "HTTP访问地址: http://$DOMAIN`:8088" -ForegroundColor Cyan
Write-Host "如需HTTPS，请运行: .\setup-ssl.ps1" -ForegroundColor Cyan
Write-Host "后端服务日志: Get-Content logs\app.log -Wait" -ForegroundColor Cyan

Write-Host "`n使用说明:" -ForegroundColor Yellow
Write-Host "- 跳过构建: .\deploy.ps1 -SkipBuild" -ForegroundColor White
Write-Host "- 跳过后端: .\deploy.ps1 -SkipBackend" -ForegroundColor White
Write-Host "- 查看日志: Get-Content logs\app.log -Wait" -ForegroundColor White
Write-Host "- 停止服务: Stop-Process -Id (Get-Content app.pid)" -ForegroundColor White