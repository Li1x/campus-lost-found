# 任务清单 (task.md)

## 阶段一：项目基础脚手架与规范搭建
- [x] 1.1 编写技术规范文档（requirements.md, database-design.md, api-spec.md, test-plan.md）
- [x] 1.2 创建后端 Spring Boot 项目与 Maven 配置 pom.xml
- [x] 1.3 编写数据库初始化脚本 V1__init.sql
- [x] 1.4 配置 application.yml、数据库连接池、JWT 秘钥
- [x] 1.5 编写后端统一返回体 Result、ResultCode 与全局异常处理
- [x] 1.6 编写业务枚举（UserRole, ItemStatus, ClaimStatus, ItemType）
- [x] 1.7 编写数据实体 Entity 与 MyBatis-Plus Mapper 接口
- [x] 1.8 编写基础安全组件（JwtUtil, UserContext, RequireRole, JwtInterceptor）
- [x] 1.9 初始化前端 Vite + Vue 3 + TypeScript 项目并安装基础依赖

## 阶段二：认证授权与用户管理模块
- [x] 2.1 安装前端必要辅助插件（unplugin 等）并完善前端路由与 Pinia
- [x] 2.2 封装前端 Axios 请求工具类（统一拦截、Token 注入、错误提示）
- [x] 2.3 编写后端用户 DTO（注册、登录、修改资料、修改密码）与 VO
- [x] 2.4 实现后端 UserService（密码 BCrypt、失败锁定机制、Token 生成）
- [x] 2.5 实现后端 AuthController 与 UserController
- [x] 2.6 实现前端 Auth API、User API 与 useUserStore
- [x] 2.7 实现前端路由配置与权限守卫 router/index.ts
- [x] 2.8 实现前端登录页 Login.vue 与注册页 Register.vue
- [x] 2.9 实现前端个人中心页 Profile.vue

## 阶段三：分类、文件上传与失物/招领发布检索
- [x] 3.1 实现后端 CategoryService 与 CategoryController
- [x] 3.2 实现后端 FileService（上传校验、文件名混淆、存储）与 FileController
- [x] 3.3 实现后端 LostItemService / FoundItemService（CRUD、分页条件查询、状态流转）
- [x] 3.4 实现后端 LostItemController / FoundItemController
- [x] 3.5 实现前端基础布局组件（Header、Sidebar、Layout）
- [x] 3.6 实现前端分类与物品 API 模块
- [x] 3.7 实现前端首页与失物/招领列表页（支持分类筛选、状态过滤、搜索）
- [x] 3.8 实现前端发布失物/招领页面（表单校验、多图上传）
- [x] 3.9 实现前端物品详情页（敏感联系方式权限查看）

## 阶段四：认领申请与双重流转机制
- [x] 4.1 实现后端 ClaimService（防刷防重、发布者初审、管理员终审、连带状态同步）
- [x] 4.2 实现后端 ClaimController
- [x] 4.3 实现后端 MessageService（站内通知发布与已读标记）与 MessageController
- [x] 4.4 实现前端认领申请对话框与认领列表组件
- [x] 4.5 实现前端“我的认领”与“认领审批”页面
- [x] 4.6 实现前端站内消息中心组件与提示通知

## 阶段五：管理后台与运营统计
- [x] 5.1 实现后端 AdminService（物品审核、认领终审、用户状态管控、数据统计）
- [x] 5.2 实现后端 AdminController
- [x] 5.3 实现后端定时任务 ScheduledTask（超过 30 天未认领物品标记 EXPIRED）
- [x] 5.4 实现后端操作日志与安全审计
- [x] 5.5 实现前端管理后台布局与角色鉴权控制
- [x] 5.6 实现前端审核管理页（物品审核、认领终审）
- [x] 5.7 实现前端用户管理页与系统统计面板仪表盘

## 阶段六：集成测试、构建验证与交付
- [x] 6.1 后端关键业务逻辑单元测试与接口测试验证（5/5 用例全部通过）
- [x] 6.2 前端代码类型检查与打包构建验证 (npm run build，退出码 0，生产包就绪)
- [x] 6.3 后端 Maven 打包构建验证 (mvn clean package)
- [x] 6.4 输出系统交付总结与启动部署说明（README.md）
