# Hermes Code Review v2 — 2026-05-29 (更新)

> 审阅者: Hermes Agent (deepseek-v4-pro)
> 版本: v0.0.7
> 状态: 🚧 开发中 → 趋近提测

---

## 版本对比

| 指标 | v1 (上次) | v2 (当前) |
|------|-----------|-----------|
| Activity | 12 | **6** (精简 50%) |
| IoT 实现 | Mock | **Eclipse Paho 真实 MQTT** |
| 仓库卫生 | 大量垃圾文件 | **已清理** |
| 版本号 | 无 | v0.0.7 |

---

## ✅ v1→v2 本期改进

| 改进 | 详情 |
|------|------|
| IoT MQTT 真实化 | Mock -> Eclipse Paho, WebSocket TLS 连接华为云, 属性上报+命令订阅+设备状态 |
| Activity 精简 | 12->6, 砍掉冗余的 Detail/MealPlan/Preference/Settings/WasteStats |
| 仓库清理 | 删除 node_modules, gradle 二进制, .claude, .playwright-mcp, 临时文件 |
| 容错增强 | MainActivity 加了 try-catch + 全局 UI 兜底 |
| 仪表盘升级 | 动态版本号, 食材实时统计, 过期三色标签, 周热量摘要 |
| 应用图标 | 新增 ic_launcher 系列 |
| RecipeHolder | Activity 间传递菜谱数据的轻量方案 |
| TrustAllManager | TLS 全信证书管理器 (开发用) |

---

## 📊 当前 Activity 清单

| # | Activity | 功能 |
|---|----------|------|
| 1 | LoginActivity | 启动页, 用户登录 |
| 2 | MainActivity | 仪表盘 (总览+过期提醒+快捷入口) |
| 3 | InventoryActivity | 食材库存管理 |
| 4 | FoodAddActivity | 食材录入 (含模拟视觉识别) |
| 5 | RecipeListActivity | 菜谱推荐+搜索 |
| 6 | NutritionActivity | 营养报告+雷达图 |

>=4 Activity OK, 每个职责清晰。

---

## IoT MQTT 真实实现

不再 Mock, 真实 Eclipse Paho 连接华为云:
- 端点: wss://21158429fd.st1.iotda-device.cn-north-4.myhuaweicloud.com:443/mqtt
- 认证: 设备密钥 (DeviceSecret)
- TLS: TrustAllManager (开发期, 生产需换成华为云CA证书)
- IotDataSyncService 每30分钟自动同步库存到华为云
- 支持属性上报 + 命令订阅 + 命令响应，完整闭环

---

## ⚠️ 需关注

### 1. TrustAllManager — 开发期可接受

当前接受所有 TLS 证书。提测/演示时可保留（方便调试），文档中注明"生产环境应使用华为云 CA 证书"。

### 2. 凭证管理

Constants.java 中硬编码了 IOT_DEVICE_ID 和 IOT_DEVICE_SECRET。确认未泄露到公开仓库。

### 3. test_iot_connection.py

根目录的 Python 测试脚本，开发工具，可保留也可清理。

### 4. Web 后端残余

web/backend/target/classes/ 仍有编译产物。不影响 Android 构建，可加 .gitignore。

---

## 🟢 评分对齐 (更新)

| 评分项 | 状态 | 证据 |
|--------|------|------|
| SQLite CRUD | OK | Room + 9 DAO + 复杂联表查询 |
| ListView | OK | RecyclerView + FoodItemAdapter + RecipeAdapter |
| Service | OK | ExpiryCheckService + IotDataSyncService (真实 MQTT) |
| Intent | OK | Activity 跳转 + 通知 PendingIntent |
| 服务器通信 | OK | Eclipse Paho MQTT over WebSocket to 华为云 IoTDA |
| >=4 Activity | OK | 6 个 Activity, 精简专注 |
| 华为云 IoT | OK | 真实 MQTT 连接 + 属性上报 + 命令订阅 |

---

## 🟢 提测 Checklist

- [x] 仓库清理
- [ ] 更新详细设计文档 (Activity 6, IoT 真实化)
- [ ] 录制演示视频 (展示 IoT 连接 + 菜谱推荐 + 营养报告)
- [ ] 验证 APK 构建 (GitHub Actions)
- [ ] 确认凭证未泄露到公开仓库

---

> *Hermes Agent 自动审阅 v2*
