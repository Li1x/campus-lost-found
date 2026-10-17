# 校园失物招领系统 — API 接口规范

> **版本**：v1.0  
> **日期**：2026-10-16  
> **基础路径**：`/api`  
> **认证方式**：Bearer JWT Token（`Authorization: Bearer <token>`）  
> **数据格式**：JSON（`Content-Type: application/json`）  
> **文件上传**：`multipart/form-data`

---

## 1. 统一响应格式

### 1.1 成功响应

```json
{
    "code": 200,
    "message": "操作成功",
    "data": { ... }
}
```

### 1.2 分页响应

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "records": [ ... ],
        "total": 100,
        "current": 1,
        "size": 10,
        "pages": 10
    }
}
```

### 1.3 错误响应

```json
{
    "code": 400,
    "message": "参数校验失败",
    "errors": [
        { "field": "title", "message": "物品名称不能为空" }
    ]
}
```

### 1.4 错误码

| HTTP 状态码 | 业务码 | 说明 |
|---|---|---|
| 200 | 200 | 成功 |
| 400 | 400 | 参数校验失败 |
| 401 | 401 | 未登录 / Token 过期 |
| 403 | 403 | 无权限 |
| 404 | 404 | 资源不存在 |
| 409 | 409 | 业务冲突（如重复认领） |
| 500 | 500 | 服务器内部错误 |

---

## 2. 认证模块 `/api/auth`

### 2.1 学生注册

```
POST /api/auth/register
```

**权限**：公开

**请求体**：

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|---|---|---|---|---|
| `studentId` | String | ✅ | 非空，长度 5-32 | 学号 |
| `username` | String | ✅ | 非空，长度 2-50 | 用户名 |
| `password` | String | ✅ | 6-32 位，包含字母和数字 | 密码 |
| `confirmPassword` | String | ✅ | 与 password 一致 | 确认密码 |

**成功响应**（200）：

```json
{
    "code": 200,
    "message": "注册成功",
    "data": null
}
```

**错误场景**：

| 场景 | 状态码 | 消息 |
|---|---|---|
| 学号已存在 | 409 | 该学号已注册 |
| 参数校验失败 | 400 | 具体字段错误 |

---

### 2.2 登录

```
POST /api/auth/login
```

**权限**：公开

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `studentId` | String | ✅ | 学号 |
| `password` | String | ✅ | 密码 |

**成功响应**（200）：

```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1...",
        "refreshToken": "eyJhbGciOiJIUzI1...",
        "expiresIn": 86400,
        "userInfo": {
            "id": 1,
            "studentId": "20230001",
            "username": "张三",
            "nickname": "小张",
            "avatar": "/uploads/avatars/xxx.jpg",
            "role": "STUDENT"
        }
    }
}
```

**错误场景**：

| 场景 | 状态码 | 消息 |
|---|---|---|
| 学号不存在 | 401 | 学号或密码错误 |
| 密码错误 | 401 | 学号或密码错误 |
| 账号被锁定 | 403 | 账号已锁定，请30分钟后重试 |
| 账号被禁用 | 403 | 账号已被禁用 |

---

### 2.3 刷新 Token

```
POST /api/auth/refresh
```

**权限**：需要有效的 Refresh Token

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `refreshToken` | String | ✅ | 刷新令牌 |

**成功响应**（200）：

```json
{
    "code": 200,
    "message": "刷新成功",
    "data": {
        "accessToken": "新的accessToken...",
        "refreshToken": "新的refreshToken...",
        "expiresIn": 86400
    }
}
```

---

### 2.4 登出

```
POST /api/auth/logout
```

**权限**：认证用户

**成功响应**（200）：

```json
{
    "code": 200,
    "message": "登出成功",
    "data": null
}
```

---

## 3. 用户模块 `/api/users`

### 3.1 获取当前用户信息

```
GET /api/users/me
```

**权限**：认证用户

**响应 data**：

```json
{
    "id": 1,
    "studentId": "20230001",
    "username": "张三",
    "nickname": "小张",
    "avatar": "/uploads/avatars/xxx.jpg",
    "phone": "138****1234",
    "email": "zhangsan@edu.cn",
    "contactInfo": "QQ: 123456",
    "role": "STUDENT",
    "createdAt": "2026-10-01T10:00:00"
}
```

---

### 3.2 更新个人资料

```
PUT /api/users/me
```

**权限**：认证用户

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `nickname` | String | ❌ | 昵称 |
| `phone` | String | ❌ | 手机号 |
| `email` | String | ❌ | 邮箱 |
| `contactInfo` | String | ❌ | QQ/微信 |

---

### 3.3 修改密码

```
PUT /api/users/me/password
```

**权限**：认证用户

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `oldPassword` | String | ✅ | 旧密码 |
| `newPassword` | String | ✅ | 新密码 |
| `confirmPassword` | String | ✅ | 确认新密码 |

---

### 3.4 上传头像

```
POST /api/users/me/avatar
```

**权限**：认证用户  
**Content-Type**：`multipart/form-data`

**请求参数**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | File | ✅ | 图片文件，≤2MB，jpg/png/webp |

**响应 data**：

```json
{
    "avatarUrl": "/uploads/avatars/xxx.jpg"
}
```

---

### 3.5 用户列表（超级管理员）

```
GET /api/users
```

**权限**：SUPER_ADMIN

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `keyword` | String | ❌ | 搜索关键词（学号/用户名/昵称） |
| `role` | String | ❌ | 角色筛选 |
| `status` | Integer | ❌ | 状态筛选 |
| `current` | Integer | ❌ | 页码，默认 1 |
| `size` | Integer | ❌ | 每页大小，默认 10 |

---

### 3.6 禁用/启用用户（超级管理员）

```
PUT /api/users/{id}/status
```

**权限**：SUPER_ADMIN

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | Integer | ✅ | 0-禁用 1-启用 |

---

## 4. 分类模块 `/api/categories`

### 4.1 获取分类列表

```
GET /api/categories
```

**权限**：公开

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | Integer | ❌ | 状态筛选，公开接口默认仅返回启用分类 |

**响应 data**：

```json
[
    { "id": 1, "name": "证件", "icon": "id-card", "sortOrder": 1 },
    { "id": 2, "name": "电子设备", "icon": "phone", "sortOrder": 2 }
]
```

---

### 4.2 创建分类（管理员）

```
POST /api/categories
```

**权限**：ADMIN / SUPER_ADMIN

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | String | ✅ | 分类名称 |
| `icon` | String | ❌ | 图标 |
| `sortOrder` | Integer | ❌ | 排序 |

---

### 4.3 更新分类（管理员）

```
PUT /api/categories/{id}
```

**权限**：ADMIN / SUPER_ADMIN

---

### 4.4 删除分类（管理员）

```
DELETE /api/categories/{id}
```

**权限**：ADMIN / SUPER_ADMIN

> 注意：如果该分类下有关联的物品，不允许删除。

---

## 5. 失物模块 `/api/lost-items`

### 5.1 发布失物

```
POST /api/lost-items
```

**权限**：STUDENT

**请求体**：

| 字段 | 类型 | 必填 | 校验 | 说明 |
|---|---|---|---|---|
| `title` | String | ✅ | 长度 2-100 | 物品名称 |
| `categoryId` | Integer | ✅ | 有效分类 ID | 分类 |
| `description` | String | ❌ | 最大 2000 字 | 描述 |
| `lostLocation` | String | ✅ | 长度 2-200 | 丢失地点 |
| `lostTime` | String | ✅ | 日期格式 YYYY-MM-DD | 丢失日期 |
| `contactInfo` | String | ❌ | 最大 100 字 | 联系方式 |
| `imageIds` | Long[] | ❌ | 最多 3 个 | 已上传的图片 ID |

**成功响应 data**：

```json
{
    "id": 1,
    "status": "PENDING"
}
```

---

### 5.2 搜索失物列表

```
GET /api/lost-items
```

**权限**：公开（仅 APPROVED 状态）

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `keyword` | String | ❌ | 标题/描述模糊搜索 |
| `categoryId` | Integer | ❌ | 分类 |
| `location` | String | ❌ | 地点模糊搜索 |
| `startDate` | String | ❌ | 起始日期 |
| `endDate` | String | ❌ | 截止日期 |
| `current` | Integer | ❌ | 页码 |
| `size` | Integer | ❌ | 每页大小 |

**响应 data**（分页格式）：

```json
{
    "records": [
        {
            "id": 1,
            "title": "校园卡",
            "categoryName": "证件",
            "categoryIcon": "id-card",
            "lostLocation": "图书馆三楼",
            "lostTime": "2026-10-15",
            "status": "APPROVED",
            "imageUrl": "/uploads/items/thumb_xxx.jpg",
            "createdAt": "2026-10-15T14:30:00",
            "username": "张三"
        }
    ],
    "total": 50,
    "current": 1,
    "size": 10,
    "pages": 5
}
```

---

### 5.3 获取失物详情

```
GET /api/lost-items/{id}
```

**权限**：公开（APPROVED 状态）/ 发布者本人（所有状态）/ ADMIN

**响应 data**：

```json
{
    "id": 1,
    "userId": 10,
    "username": "张三",
    "userAvatar": "/uploads/avatars/xxx.jpg",
    "title": "校园卡",
    "categoryId": 1,
    "categoryName": "证件",
    "description": "在图书馆三楼丢失了校园卡...",
    "lostLocation": "图书馆三楼",
    "lostTime": "2026-10-15",
    "contactInfo": "QQ: 123456",
    "status": "APPROVED",
    "reviewComment": null,
    "images": [
        { "id": 1, "url": "/uploads/items/xxx.jpg", "sortOrder": 0 }
    ],
    "claimCount": 2,
    "createdAt": "2026-10-15T14:30:00",
    "updatedAt": "2026-10-15T15:00:00"
}
```

> **注意**：`contactInfo` 仅对物品发布者本人、已认领的认领人、管理员可见。其他用户该字段返回 null。

---

### 5.4 编辑失物

```
PUT /api/lost-items/{id}
```

**权限**：发布者本人，物品状态为 PENDING 或 REJECTED

**请求体**：与发布接口一致

**业务规则**：
- 编辑后状态重置为 `PENDING`
- 清除之前的审核信息

---

### 5.5 删除失物

```
DELETE /api/lost-items/{id}
```

**权限**：发布者本人 / ADMIN / SUPER_ADMIN

**业务规则**：逻辑删除

---

### 5.6 关闭失物

```
PUT /api/lost-items/{id}/close
```

**权限**：发布者本人，物品状态为 APPROVED

---

### 5.7 审核失物（管理员）

```
PUT /api/lost-items/{id}/review
```

**权限**：ADMIN / SUPER_ADMIN

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `action` | String | ✅ | APPROVE / REJECT |
| `comment` | String | ❌ | 审核意见（拒绝时建议必填） |

**业务规则**：
- 仅 `PENDING` 状态可审核
- 记录审核人、时间、意见
- 审核结果发送站内消息给发布者

---

### 5.8 我发布的失物

```
GET /api/lost-items/my
```

**权限**：STUDENT

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | String | ❌ | 状态筛选 |
| `current` | Integer | ❌ | 页码 |
| `size` | Integer | ❌ | 每页大小 |

---

## 6. 招领模块 `/api/found-items`

与失物模块结构一致，差异点：

| 差异字段 | 失物 | 招领 |
|---|---|---|
| 地点字段 | `lostLocation` | `foundLocation` |
| 时间字段 | `lostTime` | `foundTime` |
| 额外字段 | 无 | `storageLocation`（存放位置） |

接口列表：

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/found-items` | 发布招领 |
| GET | `/api/found-items` | 搜索招领列表 |
| GET | `/api/found-items/{id}` | 获取招领详情 |
| PUT | `/api/found-items/{id}` | 编辑招领 |
| DELETE | `/api/found-items/{id}` | 删除招领 |
| PUT | `/api/found-items/{id}/close` | 关闭招领 |
| PUT | `/api/found-items/{id}/review` | 审核招领 |
| GET | `/api/found-items/my` | 我发布的招领 |

---

## 7. 认领模块 `/api/claims`

### 7.1 发起认领

```
POST /api/claims
```

**权限**：STUDENT

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `itemType` | String | ✅ | 物品类型：LOST / FOUND |
| `itemId` | Long | ✅ | 物品 ID |
| `description` | String | ✅ | 认领说明 |
| `contactInfo` | String | ❌ | 联系方式 |

**业务规则**：
- 物品状态必须为 `APPROVED`
- 不能认领自己发布的物品
- 同一用户对同一物品不能重复申请（已有 PENDING 或 OWNER_APPROVED 状态时）
- 创建后发送站内消息通知物品发布者

---

### 7.2 某物品的认领列表

```
GET /api/claims/item/{itemType}/{itemId}
```

**权限**：物品发布者 / ADMIN / SUPER_ADMIN

**响应 data**（列表）：

```json
[
    {
        "id": 1,
        "claimantId": 5,
        "claimantName": "李四",
        "claimantAvatar": "/uploads/avatars/xxx.jpg",
        "description": "这是我的校园卡，卡号是...",
        "contactInfo": "138****5678",
        "status": "PENDING",
        "createdAt": "2026-10-15T16:00:00"
    }
]
```

---

### 7.3 我的认领记录

```
GET /api/claims/my
```

**权限**：STUDENT

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | String | ❌ | 状态筛选 |
| `current` | Integer | ❌ | 页码 |
| `size` | Integer | ❌ | 每页大小 |

---

### 7.4 认领详情

```
GET /api/claims/{id}
```

**权限**：认领人 / 物品发布者 / ADMIN

---

### 7.5 发布者确认认领

```
PUT /api/claims/{id}/owner-review
```

**权限**：物品发布者

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `action` | String | ✅ | APPROVE / REJECT |
| `comment` | String | ❌ | 确认意见 |

**业务规则**：
- 仅 `PENDING` 状态可操作
- 确认后状态变为 `OWNER_APPROVED` 或 `OWNER_REJECTED`
- 发送站内消息通知认领人
- `OWNER_APPROVED` 后等待管理员最终审核

---

### 7.6 管理员审核认领

```
PUT /api/claims/{id}/admin-review
```

**权限**：ADMIN / SUPER_ADMIN

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `action` | String | ✅ | APPROVE / REJECT |
| `comment` | String | ❌ | 审核意见 |

**业务规则**：
- 仅 `OWNER_APPROVED` 状态可审核通过
- 管理员可直接拒绝任何 PENDING / OWNER_APPROVED 状态的认领
- 审核通过后：
  - 认领状态变为 `APPROVED`
  - 物品状态变为 `CLAIMED`
  - 同物品其他待处理认领自动标记为 `REJECTED`
  - 发送站内消息通知认领人和发布者

---

### 7.7 取消认领

```
PUT /api/claims/{id}/cancel
```

**权限**：认领人本人

**业务规则**：仅 `PENDING` 或 `OWNER_APPROVED` 状态可取消

---

### 7.8 标记完成

```
PUT /api/claims/{id}/complete
```

**权限**：物品发布者

**业务规则**：
- 仅 `APPROVED` 状态的认领可标记为完成
- 物品状态同步变为 `COMPLETED`

---

## 8. 消息模块 `/api/messages`

### 8.1 消息列表

```
GET /api/messages
```

**权限**：认证用户

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `isRead` | Integer | ❌ | 0-未读 1-已读 |
| `type` | String | ❌ | 消息类型 |
| `current` | Integer | ❌ | 页码 |
| `size` | Integer | ❌ | 每页大小 |

---

### 8.2 标记已读

```
PUT /api/messages/{id}/read
```

**权限**：消息接收者

---

### 8.3 全部标记已读

```
PUT /api/messages/read-all
```

**权限**：认证用户

---

### 8.4 未读消息数

```
GET /api/messages/unread-count
```

**权限**：认证用户

**响应 data**：

```json
{
    "count": 5
}
```

---

## 9. 文件上传模块 `/api/files`

### 9.1 上传图片

```
POST /api/files/upload
```

**权限**：认证用户  
**Content-Type**：`multipart/form-data`

**请求参数**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | File | ✅ | 图片文件 |
| `type` | String | ✅ | 用途：AVATAR / ITEM |

**校验规则**：
- AVATAR：最大 2MB
- ITEM：最大 5MB
- 格式白名单：jpg, jpeg, png, webp
- 文件存储路径不在 Web 可执行目录下

**响应 data**：

```json
{
    "id": 1,
    "url": "/uploads/items/2026/10/16/abc123.jpg",
    "name": "abc123.jpg"
}
```

---

### 9.2 获取图片

```
GET /uploads/{type}/{path}
```

**权限**：公开（通过 Web 服务器静态资源映射）

---

## 10. 统计模块 `/api/admin/statistics`

### 10.1 概览数据

```
GET /api/admin/statistics/overview
```

**权限**：ADMIN / SUPER_ADMIN

**响应 data**：

```json
{
    "totalUsers": 1200,
    "todayNewUsers": 15,
    "totalItems": 580,
    "pendingReviews": 12,
    "completedClaims": 230,
    "claimSuccessRate": 0.65
}
```

---

### 10.2 近期趋势

```
GET /api/admin/statistics/trend
```

**权限**：ADMIN / SUPER_ADMIN

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `days` | Integer | ❌ | 天数，默认 7 |

**响应 data**：

```json
{
    "dates": ["2026-10-10", "2026-10-11", "..."],
    "lostItems": [5, 3, 8, 2, 6, 4, 7],
    "foundItems": [3, 2, 5, 4, 3, 6, 2],
    "claims": [2, 1, 3, 1, 2, 3, 1]
}
```

---

### 10.3 分类分布

```
GET /api/admin/statistics/category-distribution
```

**权限**：ADMIN / SUPER_ADMIN

**响应 data**：

```json
[
    { "categoryName": "证件", "count": 120 },
    { "categoryName": "电子设备", "count": 85 },
    { "categoryName": "书籍文具", "count": 60 }
]
```

---

## 11. 管理员待审核列表

### 11.1 待审核物品

```
GET /api/admin/reviews/items
```

**权限**：ADMIN / SUPER_ADMIN

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `itemType` | String | ❌ | LOST / FOUND |
| `current` | Integer | ❌ | 页码 |
| `size` | Integer | ❌ | 每页大小 |

---

### 11.2 待审核认领

```
GET /api/admin/reviews/claims
```

**权限**：ADMIN / SUPER_ADMIN

> 返回 `status = OWNER_APPROVED` 的认领申请列表

---

## 12. 前后端字段映射

### 12.1 Java ↔ TypeScript 类型映射

| Java | TypeScript | 说明 |
|---|---|---|
| `Long` | `number` | ID 等 |
| `Integer` | `number` | 计数、状态码 |
| `String` | `string` | 文本 |
| `BigDecimal` | `number` | 数值 |
| `LocalDate` | `string` | 日期 YYYY-MM-DD |
| `LocalDateTime` | `string` | 时间 YYYY-MM-DDTHH:mm:ss |
| `Boolean` | `boolean` | 布尔 |
| `List<T>` | `T[]` | 数组 |
| `enum` | `string union` | 枚举 |

### 12.2 字段命名规范

| 层级 | 规范 | 示例 |
|---|---|---|
| 数据库 | snake_case | `lost_location` |
| Java Entity | camelCase | `lostLocation` |
| Java DTO | camelCase | `lostLocation` |
| JSON API | camelCase | `lostLocation` |
| TypeScript | camelCase | `lostLocation` |
| Vue 模板 | kebab-case（组件属性） | `:lost-location` |
