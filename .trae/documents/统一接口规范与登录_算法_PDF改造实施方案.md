## 目标与范围
- 统一前后端接口前缀与调用方式，消除 404 与跨域问题。
- 将微信扫码登录完整融入登录页，后端会话颁发与账号绑定规范化。
- 英国院校匹配算法配置化与专业维度输出一致，前后端分组数据统一。
- PDF 导出中文稳定显示（字体合规与 DOM 回退）。
- 工程化与安全：日志、异常、秘钥、CI、监控与指标。

## 现状研判（代码位置）
- 开发代理：`vite.config.js:17–23`（/api → http://localhost:8080，不重写）。
- 登录页：`src/components/Login.vue`（axios baseURL=/api；微信按钮已替换并调用 /api）。
- 微信后端：`src/main/java/com/study/service/auth/wechat/WeChatAuthController.java`（前缀 /api/auth/wechat）；`WeChatAuthService.java`（state 目前用内存）。
- PDF 导出：`src/utils/pdfExporter.js`（中文字体加载与 html 回退已加入）。
- 匹配算法：`src/main/java/com/study/service/matching/algorithm/UKBirminghamMatchingAlgorithm.java`（伯明翰专属）；`UKGenericMatchingAlgorithm.java`（通用英国算法）；聚合于 `MatchingAlgorithmManager.java`。
- 前端匹配展示：`src/views/ai-school-selection/matching/components/MatchingResults.vue`（仅显示“符合要求”的学校；推荐专业单行省略）。

## 统一接口前缀与调用
### 规范
- 所有后端 REST 路由统一挂载到 `/api/...`。
- 前端统一通过 `/api` 前缀调用后端；静态资源走站点根（/fonts、/images），外部脚本走其域名。
### 执行
1. 后端审计 Controller：将仍为 `/auth/...`、`/...` 的控制器统一前缀 `/api/...`。（示例：WeChat 已完成）
2. 前端审计所有 fetch/axios 调用，确保均为 `/api/...` 或使用 axiosInstance（baseURL=/api）。
3. 如需后端不带 `/api`，可在 `vite.config.js` 启用重写；推荐保持“不重写 + 两端都带 /api”。
4. 验收：`GET/POST http://localhost:8080/api/...` 在后端直接可达；`http://localhost:3000/api/...` 经代理 200。

## 微信扫码登录改造
### 后端
- 路由：`/api/auth/wechat/prepare|callback|status`（WeChatAuthController）。
- 状态存储：改用 Redis（state: value=Pending/Success/token，TTL=5m），替代 ConcurrentHashMap。
- 会话颁发：成功后生成 JWT 或设置 HttpOnly Cookie；`status` 返回标准用户信息（而非临时 token）。
- 账号绑定：表 `user_social_binding`（provider=wechat、unionid、openid、nickname、avatar、user_id），登录按 unionid 识别；不存在则创建并绑定。
- 安全：验证 `state` 一次性消费；限制同 IP/QPS；HTTPS-only；回调域名在开放平台白名单。
### 前端
- 登录页常驻“微信登录”按钮（已完成），二维码弹窗刷新与轮询；成功后使用后端用户信息更新会话。
- Pinia/SessionManager：将 login 成功后的用户信息写入统一 store；路由守卫与后端 `GET /api/user/current` 校验一致。
- 移动端微信内：UA 识别，走公众号 OAuth2 授权为回退。（与网站扫码复用后端绑定）。
### 验收
- 扫码后 5s 内完成登录跳转；同一 unionid 不生成重复用户；状态接口与路由守卫均正常。

## 英国匹配算法与数据配置化
### 目标
- 后端输出“大学＋专业”统一格式；前端与后端 G1–G4 分组一致。
- 伯明翰专属阈值（75/80/85/Reject vs 73/78/83/87）仅在该校生效；其他院校使用通用调整（G5/罗素）。
### 执行
1. 阈值配置文件（JSON/YAML）：
   - `config/uk/admission_rules.json`：按学校/学院/专业类别定义门槛；含默认通用规则。
2. 别名映射：`config/aliases/universities.json`（全角括号、历史名、英文名） → 规范化匹配。
3. 后端加载配置并缓存；UKGeneric/UKBirmingham 使用配置而非写死。
4. 分组数据一致：整理 G1–G4 列表（现已补齐），提供单一来源；前端读取同一源（或后端提供 API）。
5. 验收：多校多专业输出稳定，理由字段包含阈值比较与专业匹配说明；前端只显示“符合要求”。

## PDF 中文与导出稳定性
### 策略
- 首选 TTF（NotoSansSC-Regular.ttf/SourceHanSansCN-Regular.ttf）放 `public/fonts`，路径 `/fonts/...`。
- jsPDF 加载失败走 `pdf.html` DOM 回退，确保中文不乱码。
- 子集化字体以减小体积（fonttools）；仅保留常用字与 ASCII。
### 验收
- 存在 TTF 时表格文本可复制；无 TTF 时仍能正确显示中文（回退）。

## 工程化与安全
### 日志与异常
- 统一返回结构：`{ code, message, data }`；分类错误码；异常统一处理。
- 结构化日志：traceId、userId、state、耗时；关键路径埋点（扫码、状态轮询、匹配）。
### 配置与秘钥
- `WECHAT_APP_ID/SECRET/REDIRECT_URI` 使用环境变量；生产与开发分层。
### CI/CD 与测试
- 单元测试：认证流程（prepare/status/callback）、算法评分与阈值；前端组件快照测试。
- Pipelines：lint、测试、构建；部署阶段注入环境变量。
### 监控与指标
- Prometheus 指标：接口耗时、错误率、登录成功率、匹配命中率；Grafana 看板；告警策略。

## 路线图与里程碑
### 里程碑 1（统一与修复，1–2 天）
- 审计并统一所有后端控制器前缀 `/api/...`；前端 fetch/axios 路径统一；代理配置确认。
- 登录页二维码流程联调，确保 prepare/status 命中；修复 404。 

### 里程碑 2（认证与会话，2–3 天）
- Redis state 与 TTL；一次性消费；限流；`status` 返回真实用户信息。
- 颁发 JWT/HttpOnly Cookie；路由守卫与 `/api/user/current` 对齐。

### 里程碑 3（算法配置化，3–5 天）
- 阈值与分组配置文件；别名映射；加载与缓存；理由字段补充。
- 前端与后端 G1–G4 单一来源；仅显示“符合要求”。

### 里程碑 4（PDF 与工程化，2–3 天）
- TTF 字体上线与子集化；DOM 回退验证；统一异常与日志；初始监控面板。

## 验收标准
- `/api` 路径一致：开发与生产均 200 命中；无“偶尔加/不加”差异。
- 微信扫码：登录成功率 ≥ 98%，状态轮询不超过 10 次；会话一致。
- 匹配算法：输出“大学＋专业”，理由透明；G1–G4 一致。
- PDF：存在 TTF 时可复制文本；无 TTF 时中文仍正常显示。
- 工程化：错误码与日志统一；基本监控与告警生效。

## 需要你提供/确认
- 微信开放平台：`APP_ID/SECRET` 与回调域名；Redis 可用性（或我改用内存本地先跑）。
- 生产域名与部署方式（HTTPS）；是否启用 JWT 或 Cookie 会话策略。

> 方案通过后，我将按里程碑依次提交改动，并在每一步提供联调说明与验收清单。