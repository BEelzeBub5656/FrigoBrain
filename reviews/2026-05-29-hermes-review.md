# Hermes Code Review — 2026-05-29

> 审阅者: Hermes Agent (deepseek-v4-pro)
> 状态: 🚧 开发中

---

## 项目规模

| 层级 | 文件数 | 说明 |
|------|--------|------|
| Android | 48 Java | 12 Activity + 2 Service + 2 BroadcastReceiver |
| Web 后端 | 25 Java | Spring Boot REST API |
| Web 前端 | Vue 3 | ECharts + Pinia |
| Room Entity | 11 |
| Room DAO | 8 |
| XML Layout | 14 |

---

## ✅ 亮点

- [x] Room 标准用法 TypeConverter + DAO + 种子数据
- [x] Service + AlarmManager 每日 8:00 过期通知
- [x] NotificationChannel 兼容 Android O+
- [x] 华为云 IoT MQTT 客户端封装完整
- [x] AndroidManifest 权限声明完整
- [x] 12 Activity 远超 >=4 要求
- [x] BootReceiver + AlarmReceiver
- [x] Web 管理后台可作扩展加分项

---

## ⚠️ 待处理

### 1. Git 仓库卫生

不应提交:
- node_modules/
- gradle/gradle-8.2/
- gradle/gradle-8.10-bin.zip
- ~$网工程应用期末...doc
- doc_output.txt / extract_doc_text.py
- .claude/

### 2. 文档与代码不一致

| 项目 | 文档 | 实际 |
|------|------|------|
| Activity | 4 | 12 |
| 数据库表 | 5 | 11 |
| 技术栈 | Android | + Spring Boot + Vue 3 |

### 3. Bug: MainActivity.java:87-98

getExpiringFoodsWithRecipes() 被调用两次, 第一次结果未使用。

### 4. 评分对齐检查

| 评分项 | 状态 |
|--------|------|
| SQLite CRUD | ✅ |
| ListView | ✅ RecyclerView |
| Service | ✅ 2 个 |
| Intent | ✅ |
| 服务器通信 | ✅ IoT + API |
| >=4 Activity | ✅ 12 |
| 华为云 IoT | ✅ 完整 |

---

## 🟢 提测 Checklist

- [ ] 添加 .gitignore 清理仓库
- [ ] 更新详细设计文档
- [ ] 修复 MainActivity 重复查询
- [ ] 录制演示视频
- [ ] 验证华为云 IoT

> *Hermes Agent 自动审阅*
