# 课程大作业提交文档 — Web 课程大作业

## 小组信息
- **项目名称**：基于 Spring Boot 3 与 Vue 3 的校园失物招领系统
- **课程名称**：Web 应用程序设计 / Web 开发技术
- **小组名称**：爱心寻物互助组
- **组长姓名**：【请填写组长姓名】（学号：【请填写组长学号】）
- **组员名单**：【请填写组员姓名】（学号：【请填写组员学号】）
- **提交日期**：2026年10月17日

---

## 目 录
- [一、网站规划](#一网站规划)
  - [1.1 项目背景与目标](#11-项目背景与目标)
  - [1.2 目标用户与使用场景](#12-目标用户与使用场景)
  - [1.3 功能清单](#13-功能清单)
  - [1.4 技术栈选择](#14-技术栈选择)
- [二、系统设计与实现](#二系统设计与实现)
  - [2.1 技术架构](#21-技术架构)
  - [2.2 数据库设计](#22-数据库设计)
  - [2.3 核心功能实现](#23-核心功能实现)
  - [2.4 部署说明](#24-部署说明)
- [三、AI 工具对话记录](#三ai-工具对话记录)
  - [3.1 使用的 AI 工具](#31-使用的-ai-工具)
  - [3.2 典型对话摘录](#32-典型对话摘录)
  - [3.3 AI 使用反思](#33-ai-使用反思)
- [四、团队成员分工与贡献度](#四团队成员分工与贡献度)
  - [4.1 成员分工](#41-成员分工)
  - [4.2 贡献度分配](#42-贡献度分配)
- [五、网站演示准备](#五网站演示准备)
  - [5.1 部署与演示信息](#51-部署与演示信息)
  - [5.2 演示视频脚本规划](#52-演示视频脚本规划)
  - [5.3 现场答辩演示分工](#53-现场答辩演示分工)
- [六、总结与反思](#六总结与反思)
- [附录：提交清单核对](#附录提交清单核对)

---

## 一、网站规划

### 1.1 项目背景与目标
在大学校园日常生活中，学生在教室、图书馆、食堂、操场等公共场所遗失校园卡、钥匙、耳机、证件及书本文具等物品的频率极高。传统的寻物招领方式主要依赖微信大群、朋友圈、校园贴吧或线下公告栏，存在**信息分散易被刷屏覆盖、缺少统一分类检索、冒领错领风险高、联系方式暴露造成隐私泄露、缺乏长期跟踪与自动归档**等严重痛点。

本项目旨在设计并实现一个**规范、安全、高效的前后端分离校园失物招领平台**。通过建立标准化的物品发布审核机制、独创的“**发布者初审 + 管理员终审**”双重确认认领流转闭环、站内消息实时触达以及敏感隐私脱敏保护，实现校园物品流转的全生命周期追溯与爱心互助，大幅提升失物寻回率。

### 1.2 目标用户与使用场景

| 用户角色 | 核心需求 | 典型使用场景 |
| :--- | :--- | :--- |
| **失主学生** | 快速发布丢失物品、检索招领库、发起认领 | 刚在自习室丢失校园卡，进入平台按“证件”分类与地点检索，发现有同学发布了拾得招领，填写物品特征暗号提交认领申请，收到站内通知后前往领回。 |
| **拾金不昧者** | 登记捡拾物品、核对失主特征、归还物品 | 在食堂捡到一副蓝牙耳机，拍照上传系统并登记现存放位置；收到系统通知有同学申请认领，在线查看对方提交的凭证（如锁屏界面壁纸、耳机刮痕特征）后点击“确认匹配”。 |
| **系统管理员** | 平台内容初审、争议终审裁定、用户与数据管控 | 定期进入审核中心审批新发布的寻物/招领启事；对发布者已核验的认领记录进行终审归档（结案并变更物品状态为 CLAIMED）；查看运营数据大屏，监控全校失物招领动态。 |

### 1.3 功能清单

| 序号 | 功能模块 | 功能详细描述 | 优先级 / 完成状态 |
| :---: | :--- | :--- | :---: |
| 1 | **用户认证与安全** | 支持学号/用户名登录、账号注册、密码 BCrypt 强哈希加密、连续输错 5 次防爆破锁定 15 分钟、双 Token（Access 24h + Refresh 7d） | 核心 / 已完成 |
| 2 | **个人中心与安全脱敏** | 个人资料修改、密码修改、本人发布记录管理、非本人查看敏感联系方式（手机号、微信号）自动掩码脱敏 | 核心 / 已完成 |
| 3 | **失物/招领发布与检索** | 8 大预置分类、标题/地点模糊搜索、时间范围过滤、多图安全上传（格式白名单、UUID混淆、日期目录归档） | 核心 / 已完成 |
| 4 | **双重确认认领流转** | 防刷防重复申请校验、发布者核验初审、管理员终审裁定并连带关闭同物品其他申请 | 核心 / 已完成 |
| 5 | **站内消息通知中心** | 物品送审通过/驳回通知、收到认领核对通知、终审通过领物通知、单条/全部标为已读 | 核心 / 已完成 |
| 6 | **管理后台与审核大屏** | 物品发布初审、认领流转终审、用户账号启用/禁用、重置密码为 123456、运营数据看板 | 核心 / 已完成 |
| 7 | **超期自动过期任务** | Spring Boot 定时任务每天凌晨 2 点自动扫描超过 30 天未认领的物品标记为 `EXPIRED` | 进阶 / 已完成 |

### 1.4 技术栈选择

| 技术领域 | 选型方案 | 选型理由与优势 |
| :--- | :--- | :--- |
| **前端框架** | Vue 3 + TypeScript + Vite | 采用 Composition API 逻辑更清晰；TypeScript 提供严格类型安全契约；Vite 毫秒级热更新，构建极速。 |
| **UI 组件库** | Element Plus + Icons | 国内生态成熟、组件丰富规范，开箱即用支持表格、表单验证、走马灯、弹窗与时间线。 |
| **前端状态与路由** | Pinia + Vue Router 4 | Pinia 极简无模版代码支持 TypeScript；Vue Router 结合路由全局前置守卫实现无缝登录拦截与管理员角色鉴权。 |
| **后端框架** | Java 17 + Spring Boot 3.2.5 | 业界企业级主流框架，微服务架构基础，生态稳健，内置 Tomcat，配置简化。 |
| **持久层框架** | MyBatis-Plus 3.5.6 | 兼容 MyBatis 灵活性的同时，极大简化单表 CRUD、内置分页插件、逻辑删除支持与字段自动填充（创建/更新时间）。 |
| **数据库** | MySQL 8.x / 5.7 | 关系型数据库事实标准，支持事务 ACID 特性，utf8mb4 字符集原生支持 Emoji。 |
| **安全机制** | Auth0 JWT + Spring Security Crypto | 无状态 JWT 鉴权方便扩展；BCryptPasswordEncoder 提供自适应工作因子的不可逆散列加密。 |
| **AI 辅助工具** | Google Antigravity / Gemini 3.8 / Claude | 辅助进行架构规格梳理、多态表结构设计、TypeScript 与 Java 类型契约同步、异常排查与测试用例生成。 |

---

## 二、系统设计与实现

### 2.1 技术架构

系统采用清晰的标准**前后端分离架构**：
- **表现层（Web 前端）**：运行于浏览器端，基于 Vue 3 + Vite 构建，通过 Axios 统一封装拦截器，实现 Token 自动附带（`Bearer <JWT>`）、401 自动过期登出与全局统一错误处理。
- **网关/反向代理层**：生产环境通过 Nginx 托管前端编译静态包，反向代理 `/api/` 路由并处理文件上传大小与静态资源缓存。
- **业务逻辑层（后端）**：Spring Boot 采用高内聚低耦合分层架构：
  - `controller`：参数绑定、JSR-303 格式校验、权限注解 `@RequireRole` 检查；
  - `service`：核心业务逻辑、防重校验、状态机流转、事务回滚控制；
  - `mapper`：MyBatis-Plus 数据访问层；
  - `security`：JWT 拦截器 `JwtInterceptor` 与 ThreadLocal 用户上下文 `UserContext`。
- **数据持久层**：MySQL 关系数据库，支持事务一致性保障。

```
项目核心目录结构：
WebDevelopmentProject/
├── campus-lost-found-backend/           # 后端 Spring Boot 工程
│   ├── src/main/java/com/campus/lostfound/
│   │   ├── common/                      # 统一返回体 Result、状态码、全局异常处理
│   │   ├── config/                      # WebMvc、MyBatis-Plus 分页、BCrypt 加密配置
│   │   ├── controller/                  # Auth, User, Lost, Found, Claim, Admin, File
│   │   ├── dto/ & vo/                   # 前后端交互请求 DTO 与展示 VO
│   │   ├── entity/                      # 数据实体 Entity（User, LostItem, Claim...）
│   │   ├── mapper/                      # MyBatis-Plus Mapper 接口
│   │   ├── security/                    # JwtUtil, UserContext, RequireRole 鉴权注解
│   │   ├── service/ & impl/             # 业务服务层接口与实现类
│   │   └── task/                        # 自动过期定时任务 ScheduledTasks
│   └── src/main/resources/
│       ├── application.yml              # 数据源、JWT 密钥、文件上传配置
│       └── db/V1__init.sql              # 完整建库建表与预置数据脚本
├── campus-lost-found-frontend/          # 前端 Vue 3 + Vite 工程
│   ├── src/
│   │   ├── api/                         # 模块化网络请求（auth, user, items, claims）
│   │   ├── layout/                      # MainLayout 头部导航、AdminLayout 后台布局
│   │   ├── router/                      # Vue Router 路由表与角色鉴权守卫
│   │   ├── stores/                      # Pinia 用户登录态 userStore
│   │   ├── types/                       # TypeScript 接口契约定义
│   │   ├── utils/                       # Axios request.ts 统一网络工具
│   │   └── views/                       # 首页、列表、发布、详情、认领、后台管理视图
│   └── vite.config.ts                   # 路径别名、API 代理配置
└── docs/                                # 需求规格、数据库设计、API 文档、测试计划
```

### 2.2 数据库设计

系统数据库名为 `campus_lost_found`，采用 utf8mb4 字符集。核心数据表设计如下：

#### 1. 用户表 (`sys_user`)
- `id` (BIGINT, PK, 自增)
- `student_id` (VARCHAR(32), UNIQUE, 学号)
- `username` (VARCHAR(50), UNIQUE, 登录用户名)
- `password` (VARCHAR(128), BCrypt 密文)
- `nickname` (VARCHAR(50), 昵称)
- `phone` (VARCHAR(20), 手机号)
- `role` (VARCHAR(20), STUDENT / ADMIN / SUPER_ADMIN)
- `status` (TINYINT, 1-正常 0-禁用)
- `login_fail_count` (INT, 连续失败计数)
- `lock_time` (DATETIME, 锁定截止时间)
- `deleted` (TINYINT, 逻辑删除标记)

#### 2. 物品分类表 (`sys_category`)
- `id` (INT, PK, 自增)
- `name` (VARCHAR(50), 分类名称，如：证件、电子设备、书籍文具、钥匙卡片等)
- `sort_order` (INT, 排序权重)
- `status` (TINYINT, 1-启用 0-禁用)

#### 3. 寻物启事表 (`lf_lost_item`) & 招领信息表 (`lf_found_item`)
- `id` (BIGINT, PK, 自增)
- `user_id` (BIGINT, 发布人 ID)
- `category_id` (INT, 分类 ID)
- `title` (VARCHAR(100), 物品名称)
- `description` (TEXT, 详细描述特征)
- `lost_location` / `found_location` (VARCHAR(100), 丢失/拾得地点)
- `lost_time` / `found_time` (DATE, 日期)
- `storage_location` (VARCHAR(100), 现存放位置，招领专属)
- `contact_info` (VARCHAR(100), 发布人联系方式，对外脱敏)
- `status` (VARCHAR(20), PENDING / APPROVED / REJECTED / CLAIMED / CLOSED / EXPIRED)

#### 4. 多态物品图片表 (`lf_item_image`)
- 通过 `item_type` ('LOST' / 'FOUND') 与 `item_id` 实现多态关联，支持单物品多图轮播展示。

#### 5. 认领申请流转表 (`lf_claim`)
- `id` (BIGINT, PK, 自增)
- `item_type` ('LOST' / 'FOUND')
- `item_id` (BIGINT, 对应物品 ID)
- `claimant_id` (BIGINT, 认领人 ID)
- `description` (TEXT, 认领人提供的特征凭证与细节说明)
- `contact_info` (VARCHAR(100), 认领人联系方式)
- `status` (VARCHAR(20), PENDING / OWNER_APPROVED / OWNER_REJECTED / APPROVED / REJECTED / CANCELLED)
- `owner_comment` & `owner_reviewed_at` (发布者核验意见与时间)
- `review_comment` & `reviewed_at` (管理员终审意见与时间)

---

### 2.3 核心功能实现

#### 核心亮点一：双重确认认领流转闭环（防冒领核心设计）
1. **实现思路**：
   传统的失物招领大多采用线下单线联系，极易出现“冒名顶替”或“纠纷无处取证”。本系统设计两道核验防线：
   - **第一道（发布者核验）**：认领人填写特征细节（如手机壳内夹的照片、水杯底部的磕碰）。发布者在“我收到的认领”中比对，只有发布者认可后，申请状态才由 `PENDING` 推进为 `OWNER_APPROVED`。
   - **第二道（管理员终审）**：管理员终审确认通过，物品状态正式变更为 `CLAIMED`（已认领），触发全链路广播——自动向认领人与发布者发送完结通知，并将同物品的其他待处理申请自动驳回，防范并发冒领冲突。

2. **后端核心事务代码片段**（`AdminServiceImpl.java`）：
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
        // 1. 同步更新对应物品为 CLAIMED 状态
        if (ItemType.LOST.getCode().equalsIgnoreCase(claim.getItemType())) {
            lostItemMapper.update(null, new LambdaUpdateWrapper<LostItem>()
                .eq(LostItem::getId, claim.getItemId()).set(LostItem::getStatus, ItemStatus.CLAIMED.getCode()));
        } else {
            foundItemMapper.update(null, new LambdaUpdateWrapper<FoundItem>()
                .eq(FoundItem::getId, claim.getItemId()).set(FoundItem::getStatus, ItemStatus.CLAIMED.getCode()));
        }
        // 2. 自动关闭其他并行的申请，避免重复认领
        claimMapper.update(null, new LambdaUpdateWrapper<Claim>()
            .eq(Claim::getItemType, claim.getItemType())
            .eq(Claim::getItemId, claim.getItemId())
            .ne(Claim::getId, claim.getId())
            .in(Claim::getStatus, ClaimStatus.PENDING.getCode(), ClaimStatus.OWNER_APPROVED.getCode())
            .set(Claim::getStatus, ClaimStatus.REJECTED.getCode())
            .set(Claim::getReviewComment, "该物品已被成功认领结案"));
    }
}
```

#### 核心亮点二：安全鉴权与敏感信息脱敏防护
1. **实现思路**：
   失主或拾得人的真实手机号、QQ/微信在公开界面直接暴露会带来骚扰风险。系统在 `LostItemServiceImpl` 与 `UserController` 中，服务层统一判断访问者身份：若既非发布者本人、亦非管理员，手机号统一执行正则表达式脱敏 `138****1234`；详情页中联系方式在未发起认领前进行权限掩盖。
2. **账号防爆破机制**：
   连续 5 次输入错误密码自动锁定 15 分钟，防止暴力破解字典攻击。

---

### 2.4 部署说明

系统支持本地极速部署与云端容器化部署：
1. **本地运行**：
   - 导入 `campus-lost-found-backend/src/main/resources/db/V1__init.sql`；
   - 后端使用 IDEA 或 `mvn spring-boot:run` 启动（端口 8080）；
   - 前端在 `campus-lost-found-frontend` 执行 `npm run dev` 启动（端口 3000）。
2. **云服务器 Docker 生产部署**：
   - 前端执行 `npm run build` 生成 `dist` 静态包；
   - 后端通过 `mvn clean package` 打包为可执行 Jar；
   - 配合 Nginx 实现反向代理与 HTTPS 加密访问。

---

## 三、AI 工具对话记录

### 3.1 使用的 AI 工具
- **辅助编程 Agent**：Google Antigravity
- **底层驱动大模型**：Gemini 3.8 / Claude 3.5 Sonnet
- **应用范围**：技术规格文档草拟、多态数据表建模、前端 TypeScript 类型契约生成、IDEA 编译报错诊断与修复。

### 3.2 典型对话摘录

#### 对话 1：关于认领业务流转规则的设计与推演
- **我们的提问**：
  > “校园失物招领如果只让发布者自己确认归还，容易产生私下纠纷；如果全交由管理员审核，管理员又无法核验每个物品特征。如何设计更合理的流转状态机？”
- **AI 的回答要点**：
  > “建议采用‘**双重确认流转机制**’：
  > 1. 认领人在线提交特征暗号与证明说明；
  > 2. 第一阶段由发布者核验（`PENDING` -> `OWNER_APPROVED`），检验细节特征是否匹配；
  > 3. 第二阶段由管理员终审归档（`APPROVED`），自动将对应物品标记为 `CLAIMED` 并关闭其他冲突申请。既兼顾了物品特征的高效核实，又具备校方公信力。”
- **采纳与修改说明**：
  - **完全采纳**并在数据库 `lf_claim` 表中设计了 `owner_comment`、`owner_reviewed_at`、`review_comment` 字段，完成了业务闭环。

#### 对话 2：前端网络层 Token 注入与无感过期跳转
- **我们的提问**：
  > “Vue 3 + Axios 项目中，如何统一给所有需要鉴权的 API 附加 JWT，并在后端返回 401 或 Token 过期时优雅跳转登录页，同时保存当前的页面路径用于重定向？”
- **AI 的回答要点**：
  > “在 Axios 的 `interceptors.request` 中从 localStorage 读取 `access_token` 并注入 `config.headers.Authorization = 'Bearer ' + token`；在 `interceptors.response` 中捕获 HTTP 401 状态，清除本地凭证，通过 `encodeURIComponent(location.pathname + location.search)` 拼接 redirect 参数跳转至 `/login`。”
- **采纳与修改说明**：
  - **部分修改采纳**：在 `src/utils/request.ts` 中实现了该逻辑，同时补充了 Element Plus 的 `ElMessage.error` 统一错误提示与 429 频繁锁定状态码友好提示。

#### 对话 3：解决 IDEA 编译报错 `TypeTag :: UNKNOWN` 异常
- **我们的提问**：
  > “使用 IDEA 打开项目编译时，报 `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`，使用的编译器是 javac 25.0.1，该如何解决？”
- **AI 的回答要点**：
  > “JDK 25 属于未正式发布的超前预览版，其内部 Javac 编译器重构了 `TypeTag` 类，导致旧版 Lombok 注解处理器初始化崩溃。解决方案：在 `pom.xml` 中将 Lombok 显式升级至适配新特性的 `1.18.34`，并锁定 `maven-compiler-plugin 3.13.0` 为 Java 17；同时将 IDEA 的 Project SDK 切换至本地已有的稳定 LTS 版本 JDK 17（`jdk17.0.17`）。”
- **采纳与修改说明**：
  - **采纳并验证通过**：更新 `pom.xml` 后重新执行 Maven 编译测试，77 个 Java 文件一次性编译打包成功。

### 3.3 AI 使用反思
在本次 Web 课程大作业开发过程中，AI 工具展现出了强大的生产力倍增效应，尤其在**样板代码生成、标准化数据表 DDL 编写、前后端接口契约对齐**等环节大幅节省了机械性劳动。

但团队在实践中深刻认识到，**AI 无法替代人工架构判断与工程审查**：
1. **逻辑严密性必须人工把关**：AI 初始建议的密码比较仅考虑了 BCrypt 哈希，我们在实际排查中发现开发者可能手动修改明文数据，因此人工在 `UserServiceImpl` 中加入了明文平滑自适应升级机制，提升了系统容错性。
2. **编译环境与底层细节需人工定位**：AI 无法感知本地开发者的 IDE 具体设置，必须由人工通过命令行排查本地真实 JDK 安装路径，才能精确定位 JDK 25 与 Lombok 的兼容冲突。
3. **安全与业务规则不可轻信默认生成**：对敏感字段脱敏、防自认领、防重复提交等业务红线，必须由开发人员编写专门的单元测试用例进行白盒覆盖与验证。

---

## 四、团队成员分工与贡献度

### 4.1 成员分工

| 成员姓名 | 学号 | 团队角色 | 主要任务与贡献说明 |
| :--- | :--- | :--- | :--- |
| **【组长姓名】** | 【组长学号】 | 项目组长 / 全栈架构 | 负责系统架构设计、数据库建模、后端核心安全鉴权、双重流转机制与系统集成测试。 |
| **【组员姓名A】** | 【组员学号A】 | 前端开发工程师 | 负责 Vue 3 前端界面搭建、Element Plus 组件库适配、失物招领发布表单与详情页轮播交互开发。 |
| **【组员姓名B】** | 【组员学号B】 | 后端开发 / 测试工程师 | 负责分类与文件上传接口、操作日志记录、定时过期任务编写，编写单元测试用例与接口测试。 |
| **【组员姓名C】** | 【组员学号C】 | 资料整理 / 前端联调 | 负责管理后台数据看板大屏展示、撰写大作业报告文档、整理演示脚本与答辩 PPT 制作。 |

*(注：若团队人数为 1~3 人，可根据实际人数按比例调整上述分工)*

### 4.2 贡献度分配

| 成员姓名 | 学号 | 贡献度比例 | 主要依据与自述说明 |
| :--- | :--- | :---: | :--- |
| **【组长姓名】** | 【组长学号】 | **40%** | 牵头完成前后端工程架构，主导实现后端认证与核心认领状态机流转。 |
| **【组员姓名A】** | 【组员学号A】 | **25%** | 独立完成前台门户列表页、发布页及用户交互组件设计。 |
| **【组员姓名B】** | 【组员学号B】 | **20%** | 完成后端业务 Service 与数据表 SQL 编写，完成 5 组关键单元测试验证。 |
| **【组员姓名C】** | 【组员学号C】 | **15%** | 完成管理后台统计看板前端展示，整理交付文档与系统测试计划。 |
| **合计** | — | **100%** | 全员知情同意，分工明确，各负其责。 |

---

## 五、网站演示准备

### 5.1 部署与演示信息

| 项目 | 配置说明 |
| :--- | :--- |
| **本地运行地址** | 前端：`http://localhost:3000` ； 后端接口：`http://localhost:8080/api` |
| **测试账号（超级管理员）** | 用户名：`admin` ； 初始密码：`123456789` （拥有管理后台全量权限） |
| **测试账号（学生用户）** | 可通过前台页面快速注册；测试可用：用户名 `lll` / 密码 `123456789` |
| **数据库** | MySQL 8.x / 5.7 本地实例，数据库名 `campus_lost_found` |
| **运行依赖环境** | Node.js 18+、Java 17 (LTS)、Maven 3.8+ |

### 5.2 演示视频脚本规划
- **视频时长**：约 5 ~ 8 分钟
- **视频展示大纲**：
  1. **前台门户与搜索（1.5分钟）**：展示首页 Banner、分类筛选标签、寻物启事与招领信息卡片流、关键字模糊匹配。
  2. **发布与多图上传（1.5分钟）**：演示学生发布一条“拾得校园卡”招领，上传卡套照片，表单校验生效，提交送审。
  3. **管理员初审与流转（1.5分钟）**：登录 `admin` 账号进入“管理后台” -> “审核中心”，通过刚发布的招领启事；前台即时同步展示。
  4. **认领双重核验与结案（2分钟）**：另一学生登录账号对该校园卡发起认领，填写私密学号细节；拾得人收到站内通知核对并确认匹配；管理员终审结案，状态变更为 `CLAIMED`。
  5. **管理后台大屏与安全（1.5分钟）**：展示分类饼状统计、用户禁用操作及密码重置演示。

### 5.3 现场答辩演示分工
- **【组长】**：负责 PPT 演讲开场、讲解项目业务背景、总体前后端分离架构与双重流转技术难点。
- **【组员A】**：负责电脑实操投屏演示，按照演示路径操作前台寻物、发布与认领。
- **【组员B/C】**：负责切入管理后台演示审核流程、数据统计大屏，并回答关于数据库与安全脱敏的技术提问。

---

## 六、总结与反思

本次“校园失物招领系统”的大作业开发历程，是一次对现代全栈 Web 开发工程实践的全面历练。团队从零开始，经历了**需求梳理 -> 数据库规范设计 -> API 接口契约制定 -> 前后端工程脚手架搭建 -> 业务流转实现 -> 编译异常排查 -> 单元测试验证**的全过程。

1. **技术栈融合能力的提升**：
   通过实践深入掌握了 **Vue 3 Composition API** 带来的逻辑复用优势，理解了 **Pinia** 响应式状态管理与全局路由守卫在权限系统中的精妙配合；在后端方面，结合 **Spring Boot 3 + MyBatis-Plus** 实现了简洁优雅的 CRUD 与分页插件，利用 **JWT** 实现了无状态的 RESTful 鉴权。
2. **数据一致性与状态机流转的工程领悟**：
   失物与招领不同于简单的博客系统，其核心在于**状态驱动的业务流转**。通过引入“发布者核验初审 + 管理员终审”的双重机制，辅以数据库事务 `@Transactional` 控制，确保了并发认领时不会发生“一物多领”的数据不一致问题。
3. **拥抱 AI 与保持批判性思维**：
   在开发中善用 AI 能够显著加快脚手架搭建与代码生成，但面对复杂环境兼容（如 JDK 25 与 Lombok 的兼容问题）、密码加密与安全脱敏等细节时，唯有扎实的计算机基础与严谨的测试才能确保系统的健壮与安全。
4. **未来改进与展望**：
   未来可进一步引入 **WebSocket / SSE 实时推送**，让认领通知实现浏览器无刷新桌面弹窗；图片存储可从本地磁盘升级为 **阿里云 OSS / 腾讯云 COS** 对象存储配合 CDN 加速；并可引入基于大模型的**智能图文相似度匹配算法**，在失物和招领发布时自动推测潜在匹配项，实现更智能的校园互助体验。

---

## 附录：提交清单核对

| 序号 | 交付项 | 说明与要求 | 完成确认 |
| :---: | :--- | :--- | :---: |
| 1 | **大作业文档（本文件）** | 网站规划、系统设计、核心源码、AI 使用反思、分工表完整 | ✅ 已就绪 |
| 2 | **完整项目源代码** | 包含后端 `campus-lost-found-backend` 与前端 `campus-lost-found-frontend` | ✅ 已就绪 |
| 3 | **数据库初始化脚本** | `V1__init.sql`（包含 8 张完整表结构、预置管理员与分类数据） | ✅ 已就绪 |
| 4 | **部署与启动运行说明** | 根目录下 `README.md`，包含环境要求、启动命令与管理员账号 | ✅ 已就绪 |
| 5 | **构建与测试验证报告** | `mvn clean package` 与 `npm run build` 均以退出码 0 构建通过 | ✅ 已就绪 |
