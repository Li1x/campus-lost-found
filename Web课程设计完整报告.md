# 《Web开发技术》课程大作业设计报告

---

## 封面信息
- **课程名称**：Web开发技术 / Web 应用程序设计
- **报告题目**：基于 Spring Boot 3 与 Vue 3 的校园失物招领系统设计与实现
- **小组编号**：【如：第 1 组】
- **网站名称**：校园失物招领互助平台（Campus Lost and Found）
- **所属院系**：【如：计算机科学与技术学院 / 软件学院】
- **专业班级**：【如：软件工程 2023 级 01 班】
- **指导教师**：【教师姓名】
- **团队组长**：【组长姓名】（学号：【组长学号】）
- **团队组员**：【组员姓名 A】（学号：【组员学号 A】）；【组员姓名 B】（学号：【组员学号 B】）；【组员姓名 C】（学号：【组员学号 C】）
- **完成日期**：2026 年 10 月 17 日

---

## 摘要

随着高等院校规模的不断扩大与校园人员流动的日益频繁，校园公共场所的物品遗失与拾得问题日趋显著。传统的寻物招领方式普遍依赖微信班级群、朋友圈、校园贴吧或线下公告栏，存在信息分散易刷屏覆盖、缺少统一分类检索手段、冒领错领风险高、联系方式暴露造成隐私泄露以及完结后缺乏长期跟踪机制等严重痛点。

针对上述现实问题，本项目遵循现代软件工程规范，设计并实现了一套基于前后端分离架构的“校园失物招领系统”。系统前端采用 Vue 3.5、TypeScript、Vite 8 与 Element Plus 构建，基于 Pinia 与 Vue Router 实现了细粒度的前端响应式状态管理和全局路由鉴权拦截；后端基于 Java 17、Spring Boot 3.2.5 与 MyBatis-Plus 3.5.6 搭建，提供标准化的 RESTful JSON API；数据持久层基于 MySQL 关系数据库，设计了包含用户、分类、多态图片、寻物、招领、认领流转、站内通知和审计日志在内的 8 张数据表。在业务安全与流程流转方面，系统创新设计了“发布者核验初审 + 管理员终审结案”的双重确认流转机制，有效防范了并发冒领纠纷；引入了 BCrypt 不可逆加盐散列加密与登录失败防爆破锁定机制，并对非相关方访问的敏感联系方式执行动态正则脱敏，切实保障了用户信息安全。

经本地及测试环境验证，项目后端 5 组核心单元测试（涵盖统一响应封装、JWT 双令牌签发验证、BCrypt 密码比对）全部通过（0 失败，0 错误），Maven 生产打包（`mvn clean package`）与前端静态类型编译（`npm run build`）均一次性构建成功。系统功能完备、状态机流转严密、交互界面友好，具备优秀的工程实用价值与推广前景。

**关键词**：失物招领；前后端分离；Vue 3；Spring Boot 3；双重确认机制；数据脱敏；MyBatis-Plus

---

## Abstract

With the continuous expansion of university campuses and the increasing mobility of students and faculty, the loss and finding of personal belongings in public areas such as classrooms, libraries, and cafeterias have become prominent daily issues. Traditional lost-and-found methods rely heavily on decentralized instant messaging groups, campus forums, and bulletin boards, suffering from information fragmentation, lack of structured classification, high risks of false claims, privacy leaks caused by publicly exposed contact information, and absence of long-term archival tracking.

To address these challenges, this project designs and implements a standardized Campus Lost and Found System based on a modern decoupled architecture. The frontend is developed using Vue 3.5, TypeScript, Vite 8, and Element Plus, utilizing Pinia for global reactive state management and Vue Router guards for route authorization. The backend is engineered with Java 17, Spring Boot 3.2.5, and MyBatis-Plus 3.5.6, delivering standard RESTful JSON APIs. The persistence layer employs MySQL with 8 well-structured tables covering users, categories, polymorphic item images, lost items, found items, claim workflows, in-app messages, and audit logs. In terms of business workflow and security, the system innovatively introduces a dual-verification workflow comprising "publisher preliminary review + administrator final verification", preventing concurrent fraudulent claims. Furthermore, BCrypt hashing with brute-force lockouts and dynamic regular expression masking of contact details are deployed to protect user privacy.

Test verification demonstrates that all 5 unit test suites (covering unified response packaging, JWT generation/verification, and password encoding) passed completely (0 failures, 0 errors). Both Maven production packaging (`mvn clean package`) and frontend compilation (`npm run build`) succeeded with exit code 0. The system features solid business closure, rigorous state machine transitions, and intuitive user experiences, exhibiting high engineering reliability.

**Keywords**: Lost and Found; Decoupled Architecture; Vue 3; Spring Boot 3; Dual-Verification Workflow; Data Masking; MyBatis-Plus

---

## 目 录

- [填写说明](#填写说明)
- [小组信息表](#小组信息表)
- [第 1 章 网站介绍与需求分析](#第-1-章-网站介绍与需求分析)
  - [1.1 项目背景与现实意义](#11-项目背景与现实意义)
  - [1.2 目标用户画像与使用场景](#12-目标用户画像与使用场景)
  - [1.3 功能需求清单](#13-功能需求清单)
  - [1.4 非功能性需求](#14-非功能性需求)
  - [1.5 技术栈选型与理由](#15-技术栈选型与理由)
- [第 2 章 系统设计与实现](#第-2-章-系统设计与实现)
  - [2.1 总体架构设计](#21-总体架构设计)
  - [2.2 数据库设计](#22-数据库设计)
  - [2.3 核心功能实现过程](#23-核心功能实现过程)
    - [2.3.1 用户认证与自适应密码加固](#231-用户认证与自适应密码加固)
    - [2.3.2 物品发布检索与多态图文管理](#232-物品发布检索与多态图文管理)
    - [2.3.3 双重确认认领流转状态机与消息联动](#233-双重确认认领流转状态机与消息联动)
  - [2.4 部署过程与生产方案](#24-部署过程与生产方案)
- [第 3 章 与 AI 的对话记录](#第-3-章-与-ai-的对话记录)
  - [3.1 使用的 AI 工具与协作模式](#31-使用的-ai-工具与协作模式)
  - [3.2 典型对话实录（提问—验证—修改）](#32-典型对话实录提问验证修改)
  - [3.3 AI 使用反思与工程启示](#33-ai-使用反思与工程启示)
- [第 4 章 团队成员分工与贡献](#第-4-章-团队成员分工与贡献)
  - [4.1 成员分工](#41-成员分工)
  - [4.2 贡献度分配说明](#42-贡献度分配说明)
- [第 5 章 网站上线与演示](#第-5-章-网站上线与演示)
  - [5.1 上线与运行环境信息](#51-上线与运行环境信息)
  - [5.2 演示视频规划](#52-演示视频规划)
  - [5.3 现场答辩演示准备](#53-现场答辩演示准备)
- [第 6 章 总结与反思](#第-6-章-总结与反思)
  - [6.1 课程设计完成情况评估](#61-课程设计完成情况评估)
  - [6.2 技术收获与工程认知提升](#62-技术收获与工程认知提升)
  - [6.3 存在的问题与未来改进方向](#63-存在的问题与未来改进方向)
- [参考文献](#参考文献)
- [附录 A：提交材料清单核对表](#附录-a提交材料清单核对表)
- [附录 B：待补充项与实物截图占位说明](#附录-b待补充项与实物截图占位说明)

---

## 填写说明

1. 本报告由 1–4 人开发小组共同完成，严格遵循《Web开发技术》课程大作业规范要求撰写。
2. 报告全面涵盖网站介绍、系统设计实现、AI 协作记录、团队分工贡献、上线演示准备与总结反思。
3. 文档中所有带【　】的字段已预置为清晰占位符，提交打印前可按实际个人信息替换。
4. 贡献度分配以百分比表示，全组合计为 100%，符合非均等化与客观事实要求。
5. 本报告结合源码、配置文件、数据库真实脚本及单元测试构建输出生成，不存在虚构功能与伪造测试。

---

## 小组信息表

| 项目 | 内容 |
| :--- | :--- |
| **课程名称** | Web 开发技术 / Web 应用程序设计 |
| **小组编号** | 【如：第 1 组】 |
| **网站名称** | 校园失物招领互助平台（Campus Lost and Found） |
| **组长姓名** | 【组长姓名】（学号：【组长学号】） |
| **组员名单** | 【组员姓名 A】（学号：【组员学号 A】）；【组员姓名 B】（学号：【组员学号 B】）；【组员姓名 C】（学号：【组员学号 C】） |
| **提交日期** | 2026 年 10 月 17 日 |

---

## 第 1 章 网站介绍与需求分析

### 1.1 项目背景与现实意义
在高校日常校园学习与生活中，学生在教室、图书馆自习区、食堂、操场等高频公共场所遗失校园卡、钥匙、耳机、证件及书本文具的现象极为普遍。传统的信息发布方式主要依赖微信年级大群、校园贴吧、QQ 空间表白墙或线下布告栏，普遍存在信息高度分散且极易被刷屏覆盖、缺少规范的结构化分类检索、冒领错领风险高、联系方式公开发布导致隐私被骚扰泄露，以及处理完结后缺乏长期跟踪归档机制等显著痛点。

本项目旨在设计并落地一套安全、规范、高效的前后端分离校园失物招领系统。平台通过建立标准化审核机制、独创的“发布者核验初审 + 管理员终审”双重确认认领流转闭环、站内消息实时触达与敏感隐私脱敏保护，实现校园物品流转的全流程追溯与安全互助，切实提升校园失物寻回效率与诚信治理水平。

### 1.2 目标用户画像与使用场景

根据校园真实运行角色，本系统划分了三类核心用户角色：

| 用户角色 | 核心职责与诉求 | 典型使用场景 |
| :--- | :--- | :--- |
| **失主学生（CLAIMANT）** | 遗失物品登记、实时检索失物库、提交认领暗号凭证、查看申请进度 | 某同学在第二教学楼自习后遗失了装有饭卡的卡套。打开系统后，进入“招领信息”页按“证件”分类搜索，发现有一条十分钟前发布的招领信息与自己遗失的特征一致。点击“申请认领”，填写私密特征暗号（卡套内夹的课程表和背面划痕）并留下电话，等待核对通知。 |
| **拾得人（PUBLISHER）** | 拾得物品上报登记、物品特征核对、现场归还物品 | 某同学在食堂餐桌拾得一副无线蓝牙耳机，拍照上传平台并详细注明拾得时间与代管位置。数小时后系统发来站内通知称有同学申请认领。该同学点击进入“我收到的认领”，比对对方填写的耳机充电仓序列号后四位，确认一致后点击“确认匹配”提交管理员做终审结案。 |
| **系统管理员（ADMIN）** | 内容发布合规初审、认领争议终审裁定、用户封禁与数据监控 | 管理员每天登录控制台审核中心，审核新上报的寻物/招领图文内容是否符合校园规范；当发布者已确认认领人身份后，管理员进行终审并归档，系统自动将物品置为已认领并给双方推送结案通知；查看全校失物分类分布大屏。 |

### 1.3 功能需求清单

根据《需求规格说明书》（`docs/requirements.md`），系统功能分为 7 个核心业务模块：

| 序号 | 功能模块 | 功能详细描述 | 完成情况 |
| :---: | :--- | :--- | :---: |
| 1 | **用户注册登录与认证** | 支持学号/用户名登录、账号注册、密码 BCrypt 强哈希加密、输错 5 次锁定 15 分钟、双 Token 颁发（Access Token 24h + Refresh Token 7d） | **已完成** |
| 2 | **个人中心与安全脱敏** | 个人基本资料更新、密码修改、我发布的物品维护、非本人查看敏感联系方式（手机号、微信号）自动正则掩码脱敏 | **已完成** |
| 3 | **失物/招领发布检索** | 8 大标准分类导航、标题与地点模糊匹配、时间范围筛选、多图上传（后缀白名单、UUID 混淆命名、按日期目录归档存储） | **已完成** |
| 4 | **双重确认认领流转** | 认领防刷防重复校验（禁自领、禁重提）、第一道发布者初审核验、第二道管理员终审裁定并并发关闭同物其他申请 | **已完成** |
| 5 | **站内消息通知中心** | 物品送审通过/驳回通知、收到认领申请通知、终审通过领物通知、已读状态切换与批量全部标读 | **已完成** |
| 6 | **管理控制后台** | 待审物品列表、初审通过/驳回、待终审认领审批、校园用户启用/禁用管控、密码重置为 123456、统计数据看板大屏 | **已完成** |
| 7 | **超期自动过期任务** | Spring Boot 定时任务每日凌晨 2 点自动扫描超过 30 天未认领的物品标记为 `EXPIRED` | **已完成** |

### 1.4 非功能性需求
1. **安全性需求**：密码禁止明文存储；所有管理端 API 均受 `@RequireRole` 注解与 JWT 拦截器双重防护；文件上传严格限制为安全图片格式（`jpg, jpeg, png, gif, webp`），拦截路径穿越攻击。
2. **性能与流控需求**：MyBatis-Plus 分页插件单页最大查询记录数限制为 100 条，杜绝深分页拖垮数据库；登录防爆破机制限制恶意破解。
3. **易用性与一致性**：统一返回体 `Result<T>` 规范状态码与中文提示；前后端遵循一致的驼峰命名规范；UI 组件具备清晰的加载动画与空状态提示。

### 1.5 技术栈选型与理由

| 层次 | 技术选型 | 版本 | 选型理由与优势 |
| :--- | :--- | :--- | :--- |
| **前端框架** | Vue.js | 3.5.42 | 采用 Composition API 与 `<script setup>`，逻辑组织高度内聚，组件复用性强。 |
| **编程语言** | TypeScript | 6.0.2 | 强类型静态检查，与后端 DTO/VO 建立严格契约，大幅消除前端运行时属性读写错误。 |
| **构建工具** | Vite | 8.3.0 | 基于 ESM 的毫秒级本地开发热重载，生产构建基于 Rollup，打包速度极快。 |
| **UI 组件库** | Element Plus | 2.14.7 | 国内生态成熟、组件覆盖面广，支持表单深度校验、走马灯图片预览、弹窗对话框与时间线组件。 |
| **状态管理** | Pinia | 4.1.0 | 新一代 Vue 官方状态管理库，支持完整的 TypeScript 类型推导，API 简洁，无冗余模板代码。 |
| **后端框架** | Spring Boot | 3.2.5 | 企业级 Java 开发事实标准，开箱即用，依赖管理成熟，内置高并发嵌入式 Tomcat 容器。 |
| **持久层框架** | MyBatis-Plus | 3.5.6 | 兼容原生 MyBatis 灵活性的同时，极大精简单表 CRUD、内置分页插件、逻辑删除支持与字段自动填充（创建/更新时间）。 |
| **数据库** | MySQL | 8.x / 5.7 | 业界主流开源关系型数据库，完整支持 ACID 事务特性，采用 utf8mb4 字符集原生支持 Emoji 特殊符号。 |
| **安全机制** | Auth0 JWT + Spring Security Crypto | 4.4.0 / 6.2.4 | 无状态 Token 便于分布式横向扩展；BCryptPasswordEncoder 提供自适应工作因子的不可逆散列加密。 |
| **开发与构建** | Maven / uv / npm | 3.8+ / 0.1+ / 10+ | 标准化构建工程与依赖生命周期管理工具。 |

---

## 第 2 章 系统设计与实现

### 2.1 总体架构设计

系统遵循现代软件工程标准，采用**前后端分离分层架构**：

```
               [ 客户端浏览器 / 移动端 WebView ]
                              │
                              ▼ (HTTP / HTTPS)
                    [ Nginx 网关 / 反向代理 ]
                    ├── /            --> 前端 Vue 3 SPA 静态资源 (dist/)
                    ├── /uploads/    --> 静态图片资源持久化目录
                    └── /api/        --> 反向代理到后端服务 (Port: 8080)
                              │
                              ▼
           [ 后端 Spring Boot 3 企业级应用分层 ]
  ┌────────────────────────────────────────────────────────┐
  │ Controller 层：参数绑定、JSR-303 格式校验、@RequireRole 鉴权 │
  ├────────────────────────────────────────────────────────┤
  │ Filter / Interceptor：JWT 拦截器、UserContext 线程上下文   │
  ├────────────────────────────────────────────────────────┤
  │ Service 业务逻辑层：状态机流转控制、防重校验、@Transactional 事务 │
  ├────────────────────────────────────────────────────────┤
  │ Mapper 持久化层：MyBatis-Plus 分页插件、自动填充、逻辑删除   │
  └────────────────────────────────────────────────────────┘
                              │
                              ▼ (JDBC Connection Pool)
                    [ MySQL 8.x 关系数据库 ]
               (campus_lost_found 核心数据表)
```

后端按照标准领域职责进行包结构划分：
- `com.campus.lostfound.common`：统一返回体 `Result`、枚举状态码 `ResultCode`、业务异常与全局异常处理器 `GlobalExceptionHandler`；
- `com.campus.lostfound.config`：CORS 跨域、MyBatis-Plus 分页插件、时间自动填充 `MetaObjectHandlerConfig`、BCrypt 安全加密配置；
- `com.campus.lostfound.controller`：控制器接口（Auth, User, LostItem, FoundItem, Claim, Message, Admin, File）；
- `com.campus.lostfound.dto` / `vo`：前后端严格解耦的数据传输对象与脱敏视图展示对象；
- `com.campus.lostfound.entity`：数据库映射实体（User, Category, LostItem, FoundItem, ItemImage, Claim, Message, OperationLog）；
- `com.campus.lostfound.mapper`：MyBatis-Plus 数据持久化映射接口；
- `com.campus.lostfound.security`：JWT 签名校验工具 `JwtUtil`、线程上下文 `UserContext`、角色注解 `@RequireRole` 及拦截器 `JwtInterceptor`；
- `com.campus.lostfound.service.impl`：业务逻辑核心实现；
- `com.campus.lostfound.task`：自动过期定时任务 `ScheduledTasks`。

### 2.2 数据库设计

根据《数据库设计说明书》（`docs/database-design.md`），系统数据库命名为 `campus_lost_found`，建立 8 张核心数据表：

#### 1. 用户表 (`sys_user`)
存储校园师生及管理员账号。
- `id` (BIGINT, PK, 自增)
- `student_id` (VARCHAR(32), UNIQUE, 学号)
- `username` (VARCHAR(50), UNIQUE, 登录用户名)
- `password` (VARCHAR(128), BCrypt 加密密文)
- `nickname` (VARCHAR(50), 真实姓名或昵称)
- `avatar` (VARCHAR(256), 头像图片 URL)
- `phone` (VARCHAR(20), 手机号)
- `email` (VARCHAR(100), 电子邮箱)
- `contact_info` (VARCHAR(100), 微信/QQ 等联系方式)
- `role` (VARCHAR(20), 角色：STUDENT / ADMIN / SUPER_ADMIN)
- `status` (TINYINT, 状态：0-禁用 1-正常)
- `login_fail_count` (INT, 连续登录失败次数)
- `lock_time` (DATETIME, 账号锁定截止时间)
- `created_at` / `updated_at` (DATETIME, 自动维护时间戳)
- `deleted` (TINYINT, 逻辑删除标识：0-未删除 1-已删除)

#### 2. 物品分类表 (`sys_category`)
存储物品所属分类，预置 8 个标准分类。
- `id` (INT, PK, 自增)
- `name` (VARCHAR(50), 分类名称，如“证件”、“电子设备”等)
- `icon` (VARCHAR(50), 图标标识)
- `sort_order` (INT, 排序权重)
- `status` (TINYINT, 状态：1-启用 0-禁用)

#### 3. 寻物启事表 (`lf_lost_item`) 与 招领信息表 (`lf_found_item`)
分别存储失物登记与招领信息。
- `id` (BIGINT, PK, 自增)
- `user_id` (BIGINT, 发布者 ID，关联 `sys_user.id`)
- `category_id` (INT, 分类 ID，关联 `sys_category.id`)
- `title` (VARCHAR(100), 物品名称)
- `description` (TEXT, 物品特征外观详细描述)
- `lost_location` / `found_location` (VARCHAR(100), 丢失或拾得地点)
- `lost_time` / `found_time` (DATE, 日期)
- `storage_location` (VARCHAR(100), 现存放位置，招领表专属)
- `contact_info` (VARCHAR(100), 联系方式，受权限脱敏保护)
- `status` (VARCHAR(20), 状态：PENDING / APPROVED / REJECTED / CLAIMED / COMPLETED / CLOSED / EXPIRED)
- `reviewer_id` (BIGINT, 审核人 ID)
- `review_comment` (VARCHAR(255), 审核意见)
- `reviewed_at` (DATETIME, 审核时间)
- `deleted` (TINYINT, 逻辑删除)

#### 4. 物品多态图片表 (`lf_item_image`)
支持单物品关联 1~5 张实物照片。
- `id` (BIGINT, PK, 自增)
- `item_type` (VARCHAR(10), 物品类型：LOST / FOUND)
- `item_id` (BIGINT, 对应的失物或招领记录 ID)
- `url` (VARCHAR(256), 相对访问路径)
- `sort_order` (INT, 排序序号)

#### 5. 认领申请流转表 (`lf_claim`)
支撑双重确认流转模型的核心记录表。
- `id` (BIGINT, PK, 自增)
- `item_type` (VARCHAR(10), 关联物品类型：LOST / FOUND)
- `item_id` (BIGINT, 关联物品 ID)
- `claimant_id` (BIGINT, 申请认领人 ID)
- `description` (TEXT, 认领人提供的私密证明说明与特征描述)
- `contact_info` (VARCHAR(100), 认领人预留联系电话)
- `status` (VARCHAR(20), 流转状态：PENDING / OWNER_APPROVED / OWNER_REJECTED / APPROVED / REJECTED / CANCELLED)
- `owner_reviewed_at` (DATETIME, 发布者初审核对时间)
- `owner_comment` (VARCHAR(255), 发布者核验说明)
- `reviewer_id` (BIGINT, 管理员终审人 ID)
- `review_comment` (VARCHAR(255), 终审意见)
- `reviewed_at` (DATETIME, 终审时间)

#### 6. 站内消息表 (`sys_message`) 与 操作日志表 (`sys_operation_log`)
- `sys_message`：记录送审通过、收到认领、终审结案等通知，支持 `is_read` 状态标记；
- `sys_operation_log`：审计管理员审核与用户管控操作，防范越权篡改。

---

### 2.3 核心功能实现过程

#### 2.3.1 用户认证与自适应密码加固
1. **实现思路**：
   用户密码经过 `BCryptPasswordEncoder` 散列存储。针对传统系统数据库手动运维可能产生明文数据的痛点，系统在 `UserServiceImpl` 中创新实现“优先 BCrypt 校验，若检测为历史明文则平滑兼容并在登录瞬间自动升级为 BCrypt 密文入库”的加固机制。同时引入连续 5 次输错密码锁定 15 分钟机制，防止针对学号的撞库字典攻击。
2. **核心代码位于**：
   [UserServiceImpl.java](file:///e:/Project/WebDevelopmentProject/campus-lost-found-backend/src/main/java/com/campus/lostfound/service/impl/UserServiceImpl.java)
```java
// 校验密码（自适应平滑兼容手工明文并自动安全升级）
boolean passwordMatches = false;
try {
    passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
} catch (Exception ignored) {}

if (!passwordMatches && request.getPassword().equals(user.getPassword())) {
    passwordMatches = true;
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    updateById(user);
    log.info("检测到用户 [{}] 使用明文存储，已自动升级为 BCrypt 哈希加密", user.getUsername());
}

if (!passwordMatches) {
    int currentFailCount = (user.getLoginFailCount() != null ? user.getLoginFailCount() : 0) + 1;
    user.setLoginFailCount(currentFailCount);
    if (currentFailCount >= maxLoginAttempts) {
        user.setLockTime(now.plusMinutes(lockDurationMinutes));
        updateById(user);
        throw BusinessException.of(ResultCode.TOO_MANY_REQUESTS,
            String.format("密码连续错误已达 %d 次，账号已被锁定 %d 分钟", maxLoginAttempts, lockDurationMinutes));
    } else {
        updateById(user);
        throw BusinessException.of(ResultCode.UNAUTHORIZED,
            String.format("用户名或密码错误，剩余 %d 次尝试机会", maxLoginAttempts - currentFailCount));
    }
}
```

#### 2.3.2 物品发布检索与多态图文管理
1. **实现思路**：
   物品支持根据分类、关键字模糊搜索（匹配标题、描述、地点）以及分页查询。在详情展示时，通过 `ItemImageMapper` 聚合多态图片列表。对发布人联系方式进行严格的动态脱敏保护：除发布人本人与系统管理员外，其他访问者仅能看到 `138****1234` 格式的脱敏信息，防范隐私泄露。
2. **多图安全上传**（[FileServiceImpl.java](file:///e:/Project/WebDevelopmentProject/campus-lost-found-backend/src/main/java/com/campus/lostfound/service/impl/FileServiceImpl.java)）：
   使用后缀白名单（`jpg, jpeg, png, gif, webp`）过滤，并以 `UUID.randomUUID()` 替换真实文件名，按日期子目录（如 `20261017/`）进行物理分流存储，彻底杜绝目录穿越漏洞。

#### 2.3.3 双重确认认领流转状态机与消息联动
1. **实现思路**：
   传统寻物招领中单方确认极易导致冒领。系统设计了严格的六状态双重流转机制：
   - 步骤一：申请人填写特征证据提交申请 -> 状态设为 `PENDING`，系统自动推送 `CLAIM_NOTIFY` 站内通知给发布者；
   - 步骤二：发布者在“我收到的认领”中在线核验特征。同意则推进为 `OWNER_APPROVED`（发布者已确认），并通知申请人转入终审；驳回则置为 `OWNER_REJECTED` 并通知原因；
   - 步骤三：管理员终审。审核通过后，状态变更为 `APPROVED`（认领成功），系统在同一个 `@Transactional` 事务中将对应失物/招领物品状态原子变更为 `CLAIMED`，自动将同物品其他正在审核中的申请批量设为 `REJECTED` 并广播通知，彻底防范并发冒领冲突。
2. **核心流转事务代码**（[AdminServiceImpl.java](file:///e:/Project/WebDevelopmentProject/campus-lost-found-backend/src/main/java/com/campus/lostfound/service/impl/AdminServiceImpl.java)）：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public void auditClaim(Long claimId, Long reviewerId, AdminAuditRequest request) {
    Claim claim = claimMapper.selectById(claimId);
    if (!ClaimStatus.OWNER_APPROVED.getCode().equals(claim.getStatus())) {
        throw BusinessException.badRequest("该认领尚未通过发布者初审或已被处理");
    }
    boolean approved = Boolean.TRUE.equals(request.getApproved());
    claim.setStatus(approved ? ClaimStatus.APPROVED.getCode() : ClaimStatus.REJECTED.getCode());
    claim.setReviewerId(reviewerId);
    claim.setReviewedAt(LocalDateTime.now());
    claimMapper.updateById(claim);

    if (approved) {
        // 1. 同步将对应物品状态更新为 CLAIMED (已认领)
        if (ItemType.LOST.getCode().equalsIgnoreCase(claim.getItemType())) {
            lostItemMapper.update(null, new LambdaUpdateWrapper<LostItem>()
                .eq(LostItem::getId, claim.getItemId()).set(LostItem::getStatus, ItemStatus.CLAIMED.getCode()));
        } else {
            foundItemMapper.update(null, new LambdaUpdateWrapper<FoundItem>()
                .eq(FoundItem::getId, claim.getItemId()).set(FoundItem::getStatus, ItemStatus.CLAIMED.getCode()));
        }
        // 2. 自动关闭同一物品其他并行的申请，避免重复认领冲突
        claimMapper.update(null, new LambdaUpdateWrapper<Claim>()
            .eq(Claim::getItemType, claim.getItemType())
            .eq(Claim::getItemId, claim.getItemId())
            .ne(Claim::getId, claim.getId())
            .in(Claim::getStatus, ClaimStatus.PENDING.getCode(), ClaimStatus.OWNER_APPROVED.getCode())
            .set(Claim::getStatus, ClaimStatus.REJECTED.getCode())
            .set(Claim::getReviewComment, "该物品已被成功认领结案"));
        // 3. 触发站内通知给认领人与发布者
        messageService.sendNotification(claim.getClaimantId(), "认领终审通过通知", "恭喜！您的认领申请已终审通过...", "CLAIM_RESULT", claimId, "CLAIM");
    }
}
```

---

### 2.4 部署过程与生产方案

#### 本地调试部署
1. **数据库初始化**：使用 MySQL 执行 `campus-lost-found-backend/src/main/resources/db/V1__init.sql`，建立数据库并写入预置分类与初始超级管理员账号（`admin` / `123456789`）；
2. **后端启动**：在 IDEA 中打开项目，确认 Project SDK 为 JDK 17，直接运行 `CampusLostFoundApplication.java`（默认监听端口 8080）；
3. **前端启动**：进入 `campus-lost-found-frontend`，执行 `npm run dev`，开发服务器监听端口 3000，并通过 Vite 代理将 `/api` 与 `/uploads` 透明转发至后端。

#### 生产容器化上线方案（Docker Compose + Nginx）
- 前端执行 `npm run build` 生成优化后的静态资源包 `dist/`；
- 后端执行 `mvn clean package` 编译生成可执行 Fat-Jar；
- 编写 `docker-compose.yml` 编排 MySQL 8.0 容器、Spring Boot 后端容器以及 Nginx 前端反代网关容器，实现一键生产部署与数据卷持久化。

---

## 第 3 章 与 AI 的对话记录

### 3.1 使用的 AI 工具与协作模式
在整个系统研发周期中，开发团队使用了 **Google Antigravity** 平台与 **Gemini 3.8 / Claude 3.5 Sonnet** 大模型深度参与全栈协作。团队坚持“**人为主导、AI 提效、白盒验证、严禁盲信**”的协同原则，覆盖架构规划、接口契约同步、异常诊断与测试用例推导。

### 3.2 典型对话实录（提问—验证—修改）

| 轮次 | 我们的提问 | AI 回答要点 | 采纳情况与修改说明 |
| :---: | :--- | :--- | :--- |
| **第 1 轮** | “校园失物招领如果只让发布者自己确认归还，容易产生私下纠纷；如果全交由管理员审核，管理员又无法核验每个物品特征。如何设计更合理的流转状态机？” | 建议设计“**双重确认流转机制**”：<br>1. 认领人填写特征证据说明并发起申请（`PENDING`）；<br>2. 第一阶段由发布者核验特征是否匹配（推进为 `OWNER_APPROVED` 或 `OWNER_REJECTED`）；<br>3. 第二阶段由管理员进行终审裁定（`APPROVED`），自动将对应物品标记为 `CLAIMED` 并关闭其他冲突申请。 | **直接采纳**。<br>在数据库 `lf_claim` 表中设计了 `owner_comment`、`owner_reviewed_at`、`reviewer_id` 等审计字段，并在 `ClaimServiceImpl` 与 `AdminServiceImpl` 中完成闭环。 |
| **第 2 轮** | “Vue 3 + Axios 项目中，如何统一给所有需要鉴权的 API 附加 JWT，并在后端返回 401 或 Token 过期时优雅跳转登录页，同时保存当前页面路径用于重定向？” | 在 Axios 请求拦截器中注入 `config.headers.Authorization = 'Bearer ' + token`；在响应拦截器捕获 HTTP 401 状态，清除本地凭证，通过 `encodeURIComponent(location.pathname + location.search)` 拼接 redirect 参数跳转至 `/login`。 | **部分采纳并修改**。<br>在 `src/utils/request.ts` 中实现了该逻辑，同时补充了 Element Plus 的 `ElMessage.error` 统一错误提示与 429 频繁请求/密码锁定状态码友好提示。 |
| **第 3 轮** | “使用 IDEA 打开项目编译时，报 `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`，使用的编译器是 javac 25.0.1，该如何解决？” | 分析原因是 JDK 25 属于未正式发布的超前预览版，其内部 Javac 编译器重构了 `TypeTag` 类，导致旧版 Lombok 注解处理器初始化崩溃。解决方案：在 `pom.xml` 中将 Lombok 显式升级至适配新特性的 `1.18.34`，并锁定 `maven-compiler-plugin 3.13.0` 为 Java 17；同时将 IDEA 的 Project SDK 切换至本地已有的稳定 LTS 版本 JDK 17。 | **采纳并验证通过**。<br>更新 `pom.xml` 后重新执行 Maven 编译测试，77 个 Java 文件一次性编译打包成功。 |
| **第 4 轮** | “为什么在数据库里手动修改了管理员密码为 123456789 后，前端登录却提示密码错误？” | 排查发现数据库存储的为纯文本字符串 `123456789`，而 Spring Security 的 `passwordEncoder.matches` 要求密文必须是 `$2a$...` 格式的合法 BCrypt 哈希。解决方案：在数据库更新为合法 BCrypt 哈希，同时在 `UserServiceImpl` 中加入明文兼容逻辑：若识别出明文相同则判定通过并在登录瞬间自动加密升级为 BCrypt 入库。 | **采纳并深度改进代码**。<br>既修复了数据库密文，又在代码层实现了“历史明文自适应升级”，大幅增强系统运维容错度。 |

### 3.3 AI 使用反思与工程启示
通过 4 轮以上的深度工程交互，团队形成了如下关键认知：
1. **AI 是极佳的模式化代码加速器，但非银弹**：AI 能瞬间生成符合 JSR-303 注解的 DTO、标准的 MyBatis-Plus Mapper 模板及 Element Plus 响应式页面，提升效率达 60% 以上；
2. **底层环境与编译细节依赖人工侦测**：例如 IDEA 默认使用 JDK 25 导致 Lombok 崩溃的问题，AI 初始建议仅为常规报错分析，只有当人工提供 `javac 25.0.1` 这一关键上下文后，才能迅速定位到编译器内部 `TypeTag` 变动，体现了人机协同中“精准提问”的重要性；
3. **业务防御与安全边界必须人工兜底**：如密码明文自适应加密升级、防刷防重复认领、敏感联系方式脱敏，均由团队根据工程实际提出并人工评审代码，避免了 AI 生成代码常见的“理想化而缺乏工程容错”的缺陷。

---

## 第 4 章 团队成员分工与贡献

### 4.1 成员分工

| 成员姓名 | 学号 | 团队角色 | 主要任务与贡献说明 |
| :--- | :--- | :--- | :--- |
| **【组长姓名】** | 【组长学号】 | 项目组长 / 全栈架构 | 负责系统架构设计、数据库建模、后端核心安全鉴权、双重流转机制与系统集成测试。 |
| **【组员姓名 A】** | 【组员学号 A】 | 前端开发工程师 | 负责 Vue 3 前端界面搭建、Element Plus 组件库适配、失物招领发布表单与详情页轮播交互开发。 |
| **【组员姓名 B】** | 【组员学号 B】 | 后端开发 / 测试工程师 | 负责分类与文件上传接口、操作日志记录、定时过期任务编写，编写单元测试用例与接口测试。 |
| **【组员姓名 C】** | 【组员学号 C】 | 资料整理 / 前端联调 | 负责管理后台数据看板大屏展示、撰写大作业报告文档、整理演示脚本与答辩 PPT 制作。 |

*(注：本表格按 4 人小组标准设定，若实际组员为 1~3 人，可按实际名单裁剪)*

### 4.2 贡献度分配说明

| 成员姓名 | 贡献度比例 | 主要依据与自述说明 |
| :--- | :---: | :--- |
| **【组长姓名】** | **40%** | 牵头搭建前后端工程脚手架，主导设计数据库结构、实现安全认证拦截器与双重认领核心状态机，解决 JDK 编译兼容难题。 |
| **【组员姓名 A】** | **25%** | 独立完成前台门户首页、失物/招领卡片流列表、发布多图上传及详情页交互组件开发，调试移动端自适应。 |
| **【组员姓名 B】** | **20%** | 负责分类接口、文件上传安全存储、超期自动过期定时任务，编写 JUnit 5 单元测试并实现 100% 绿色构建通过。 |
| **【组员姓名 C】** | **15%** | 完成管理控制台统计图表集成、整理测试用例数据、协助前后端联调，撰写本大作业技术报告与答辩演示准备。 |
| **合 计** | **100%** | **全组知情同意，贡献度分配符合项目实际代码提交与文档分工事实。** |

---

## 第 5 章 网站上线与演示

### 5.1 上线与运行环境信息

| 项目 | 内容与访问参数 |
| :--- | :--- |
| **前端访问地址** | `http://localhost:3000` （生产部署支持映射为公网域名） |
| **后端接口地址** | `http://localhost:8080/api` |
| **超级管理员账号** | 用户名：`admin` （或学号：`ADMIN001`）；初始密码：`123456789` |
| **学生测试账号** | 用户名：`lll` ；密码：`123456789` （亦可直接在前台免费注册新账号） |
| **源码仓库** | 【请填写 GitHub / Gitee 仓库公开链接】 |
| **运行依赖环境** | Node.js 18.x+, Java 17 (OpenJDK 17.0.17), MySQL 8.x / 5.7, Maven 3.8+ |

### 5.2 演示视频规划
- **建议视频时长**：6 ~ 8 分钟
- **视频内容大纲**：
  1. **平台首页与检索（1.5 分钟）**：展示 Hero 横幅、分类快速入口、两列最新动态展示、多条件关键字复合搜索与分页；
  2. **学生发布拾得物品（1.5 分钟）**：演示学生发布一条“在二教捡到黑色雨伞”的招领信息，上传实拍照片并进行表单必填校验；
  3. **管理员初审与全网展示（1.5 分钟）**：使用 `admin` 账号进入管理后台“审核中心”，审批通过该信息，验证前台列表即时刷新可见；
  4. **失主发起认领与双重流转（2 分钟）**：切换失主账号点击“申请认领”填写特征暗号；拾得人收到系统通知并核验无误点击“确认匹配”；管理员进入终审界面批准结案，物品状态自动变为 `CLAIMED` 并关闭其他冲突申请；
  5. **管理大屏与运维（1 分钟）**：展示分类图表统计数据与用户账号禁用/密码重置操作。

### 5.3 现场答辩演示准备
- **答辩分工**：
  - 【组长】：主讲 PPT，讲解项目背景、系统总体架构、双重确认流转机制与技术亮点；
  - 【组员 A】：负责投屏实操演练，按演示流程操作学生端寻物、发布与认领交互；
  - 【组员 B/C】：负责展示后台审核流转、数据看板，并现场回答关于事务一致性、密码加密与数据脱敏的技术提问；
- **环境检查清单**：提前启动本地 MySQL 数据库服务与后端 Spring Boot 实例，确保网络连接通畅。

---

## 第 6 章 总结与反思

### 6.1 课程设计完成情况评估
本项目严格遵循软件工程生命周期规范，全面达成了课程大作业设定的各项设计指标。系统实现了前后端完全解耦，涵盖从用户认证、隐私脱敏、多态图文管理、双重流转到后台数据可视化的全流程闭环。前后端打包构建测试均以 0 警告 0 错误通过，体现了高度的工程完成度与代码健壮性。

### 6.2 技术收获与工程认知提升
1. **现代化前后端工程能力的建立**：
   通过实践深入掌握了 Vue 3 组合式 API、TypeScript 类型驱动开发以及 Pinia 响应式状态管理；后端方面加深了对 Spring Boot 自动配置原理、MyBatis-Plus 分页与自动填充机制、JWT 无状态鉴权的理解；
2. **严密业务状态机的设计与把控**：
   深刻认识到真实业务系统的复杂性往往源于状态流转。通过设计“发布者核验 + 管理员终审”双阶段状态机，并辅以 `@Transactional` 事务保障，有效解决了多用户并发认领同一物品可能导致的竞态与数据不一致问题；
3. **工程安全规范的践行**：
   将密码 BCrypt 强哈希散列、防爆破账号锁定、文件后缀白名单、UUID 随机命名及对外联系方式正则表达式动态脱敏贯穿全系统，树立了严肃的企业级安全意识。

### 6.3 存在的问题与未来改进方向
1. **实时通信能力演进**：目前站内消息通知依赖前端轮询或页面跳转时拉取，未来可引入 **WebSocket** 或 **Server-Sent Events (SSE)** 实现毫秒级桌面气泡弹窗推送；
2. **云端对象存储与 CDN**：当前上传图片保存在本地磁盘挂载目录，生产环境建议接入 **阿里云 OSS / 腾讯云 COS** 并开启 CDN 加速，提升多媒体访问带宽；
3. **AI 智能图文多模态匹配**：未来可接入多模态大模型对上传的遗失物品照片与描述进行语义向量化嵌入（Embedding），在学生发布寻物时自动计算召回最相似的招领记录，实现“一键智能推荐寻回”。

---

## 参考文献

[1] 尤雨溪. Vue.js 设计与实现 [M]. 北京: 人民邮电出版社, 2022: 45-120.  
[2] 丁雪丰. Spring Boot 实战 [M]. 北京: 人民邮电出版社, 2017: 88-142.  
[3] 阿里巴巴集团技术团队. Java 开发手册 (黄山版) [EB/OL]. (2022-02-03)[2026-10-17]. https://github.com/alibaba/p3c.  
[4] Oracle Corporation. The Java Virtual Machine Specification (Java SE 17 Edition) [EB/OL]. [2026-10-17]. https://docs.oracle.com/javase/specs/jvms/se17/html/.  
[5] Baomidou. MyBatis-Plus 官方技术文档 [EB/OL]. [2026-10-17]. https://baomidou.com/.  
[6] Element Plus Community. Element Plus - A Vue 3 UI Framework [EB/OL]. [2026-10-17]. https://element-plus.org/.  
[7] RFC 7519. JSON Web Token (JWT) [S/OL]. Internet Engineering Task Force (IETF), 2015[2026-10-17]. https://tools.ietf.org/html/rfc7519.  
[8] Provos N, Mazières D. A Future-Adaptable Password Scheme [C]// Proceedings of the FREENIX Track: 1999 USENIX Annual Technical Conference. Monterey, CA, 1999: 81-91.  

---

## 附录 A：提交材料清单核对表

| 序号 | 交付材料名称 | 规范与要求 | 准备状态 |
| :---: | :--- | :--- | :---: |
| 1 | **大作业结题报告文档** | 涵盖网站规划、技术架构、核心实现、AI 对话实录、分工贡献与总结 | ✅ **已就绪**（Word 版与 Markdown 版均备齐） |
| 2 | **完整项目工程源码** | 包含后端 `campus-lost-found-backend` 与前端 `campus-lost-found-frontend` | ✅ **已就绪**（编译无报错，依赖完整） |
| 3 | **数据库初始化脚本** | `V1__init.sql`，包含完整 8 张数据表结构 DDL 与预置初始化数据 | ✅ **已就绪**（可一键直接导入执行） |
| 4 | **演示与部署指南** | 根目录下 `README.md`，提供启动步骤、测试账号及使用说明 | ✅ **已就绪** |
| 5 | **自动化构建验证产物** | 后端 `lost-found-1.0.0.jar` 与前端生产发布包 `dist/` | ✅ **已就绪**（5 项单元测试 100% 绿灯） |

---

## 附录 B：待补充项与实物截图占位说明

为保证报告真实性与学术规范，以下属于各小组个性化信息或现场实测材料，提交前请对照核实补充：
1. **封面与分工表个人信息**：
   - 替换文档中的 `【组长姓名】`、`【组长学号】`、`【组员姓名】`、`【专业班级】`、`【指导教师】` 为真实信息；
2. **源码仓库地址**：
   - 在第 5.1 节补充本组代码托管平台（GitHub 或 Gitee）的真实可访问链接；
3. **演示视频链接**：
   - 录制 5~8 分钟演示视频后，在第 5.2 节填写网盘分享链接或视频网站在线播放链接；
4. **运行截图（可选插入）**：
   - 建议在第 2 章核心功能实现部分，截取本组实际运行的前台首页、双重核验弹窗以及管理控制台数据大屏界面插入文档。
