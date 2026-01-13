## 总体方案
- 使用微信开放平台「网站应用」的扫码登录（QR Connect），支持 PC 浏览器扫码授权；移动端在微信内打开时走公众号 OAuth2（snsapi_userinfo）作为回退。
- 前端在登录页渲染官方二维码组件 WxLogin（微信提供的 JS），用户用微信 App 扫码；
- 后端接收回调 `code/state`，与微信服务器交换 `access_token/openid/unionid`，获取用户信息，完成账户绑定/登录，颁发我们系统的会话（JWT/Session）。
- 通过 `state` 防 CSRF；回调后将登录结果（token）安全传回前端登录页并更新 UI。

## 前端实现（Vue 3 + Vite）
1. 登录页增加二维码容器：`<div id="wechat-qr"></div>`，按钮文案“微信扫码登录”。
2. 动态加载官方脚本：`https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js`。
3. 获取后端生成的 `state` 与 `redirect_uri`（后端返回），创建二维码：
   ```js
   new WxLogin({
     id: 'wechat-qr',
     appid: '<APPID>',
     scope: 'snsapi_login',
     redirect_uri: encodeURIComponent('<BACKEND_CALLBACK_URL>'),
     state: '<STATE>',
     self_redirect: true,
     style: 'black'
   })
   ```
4. 登录结果回传给原页面的方式（任选其一，推荐#1）：
   - #1 轮询登录状态：前端持有 `state`，每 1-2s 轮询 `GET /auth/wechat/status?state=...` 直到成功，收到 `token` 后设置本地会话；
   - #2 回调页使用 `window.opener.postMessage({ token }, origin)` 通知并关闭；前端在登录页监听 `message`。
5. 成功后调用现有用户信息接口刷新用户态，跳转到首页/目标页。

## 后端实现（Spring Boot）
1. 配置与Secret管理：`WECHAT_APP_ID`、`WECHAT_APP_SECRET`、`WECHAT_REDIRECT_URI`（公网 https），存于配置中心/环境变量；仅用于服务器端。
2. 生成登录会话 `state`：
   - `POST /auth/wechat/prepare` 返回 `{ appid, state, redirectUri }`；将 `state` 存储（Redis/DB），设置 5 分钟有效期，绑定 CSRF/登录意图。
3. 回调处理：`GET /auth/wechat/callback?code&state`
   - 校验 `state`（匹配且未过期）；
   - 交换 access_token：`GET https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code`
   - 获取用户信息（可选）：`GET https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID`
   - 账户绑定策略：优先使用 `unionid` 做跨应用唯一标识，若无则用 `openid`；找到或创建本地用户；
   - 颁发我们系统的登录态（JWT 或设置 HttpOnly Cookie）；
   - 标记 `state` 为已登录，并存储 `token`，供前端状态轮询读取；
   - 返回一个简单页面/JSON：若使用轮询，返回成功 JSON；若使用 postMessage 方案，返回 HTML 执行 `window.opener.postMessage(...); window.close();`。
4. 状态查询：`GET /auth/wechat/status?state=...` 若成功返回 `{ token }`，否则返回 `{ status: pending }`。
5. 统一安全：
   - 必须使用 HTTPS；
   - `redirect_uri` 需与微信开放平台配置的回调域名一致且白名单；
   - `state` 做一次性消费与过期清理；
   - 限制同一 IP 的尝试频率，防止滥用；

## 兼容移动端（在微信内）
- 若检测到 UA 为微信且在微信内打开登录页，走公众号 OAuth2：`https://open.weixin.qq.com/connect/oauth2/authorize?...&scope=snsapi_userinfo`，其回调与网站扫码流程共享后端绑定逻辑。

## 数据模型与绑定
- 新增/复用用户第三方绑定表：`id, user_id, provider=wechat, openid, unionid, nickname, avatar, created_at`；登录时优先按 `unionid` 查找并绑定；

## 路由与交互
- 后端回调路由建议：`/auth/wechat/callback`；
- 轮询路由：`/auth/wechat/status`；
- 登录成功后设置 HttpOnly Cookie（或返回 JWT），前端跳转。

## 测试与联调
- 使用公网可访问域名（ngrok/loca.lt）作为 `redirect_uri`，在微信开放平台后台配置该域名为回调域；
- 测试场景：
  - 正常扫码授权；
  - 用户取消授权；
  - `state` 过期；
  - 已绑定用户再次登录；
  - 移动端公众号授权；

## 验收标准
- 登录页二维码正常渲染；扫码授权后 5 秒内完成登录跳转；
- 用户能在系统中保持登录态，并能退出登录；
- 绑定去重：同一 `unionid` 不创建重复用户；
- 所有接口均返回明确错误码与提示；

## 实施步骤
1. 后端新增 `prepare/callback/status` 三个接口；完成微信交互与本地会话颁发。
2. 前端登录页接入 WxLogin，调用 `prepare` 获取 `state` 与 `redirectUri`，渲染二维码并轮询状态。
3. 联调并配置微信开放平台回调域名；上线后仅开放生产域名。

## 后续增强
- 绑定已有账号（手机号+微信），支持解绑与重新绑定；
- 登录风险控制（设备指纹、地理位置异常提示）；
- 接入小程序登录与 UnionID 打通。