# 校园失物招领系统 — 快速启动指南

## 一、环境准备
- **JDK**：Java 17 或以上
- **Node.js**：Node 18+ 与 npm
- **MySQL**：MySQL 8.0+
- **数据库名**：`campus_lost_found`

---

## 二、数据库初始化
1. 使用 Navicat、DataGrip 或 MySQL 命令行连接本地数据库：
   ```bash
   mysql -u root -p
   ```
2. 执行位于 `campus-lost-found-backend/src/main/resources/db/V1__init.sql` 的建表与预置数据脚本。
   - 脚本中已预置超级管理员账号：
     - **用户名**：`superadmin`
     - **密码**：`Admin@2026`
     - **角色**：`SUPER_ADMIN`
   - 预置 8 个标准物品分类（证件证照、数码电子、书籍文具、服装鞋包、钥匙卡包、体育器材、生活用品、其它物品）。

---

## 三、后端服务启动
1. 进入后端目录：
   ```bash
   cd campus-lost-found-backend
   ```
2. 检查 `src/main/resources/application.yml` 中的 MySQL 数据库账号密码是否与本地一致。
3. 启动命令：
   ```bash
   mvn spring-boot:run
   ```
   或直接在 IntelliJ IDEA 中打开并运行 `CampusLostFoundApplication.java`。
4. 后端服务默认端口为 **8080**。

---

## 四、前端服务启动
1. 进入前端目录：
   ```bash
   cd campus-lost-found-frontend
   ```
2. 安装依赖（已内置于 node_modules，若需要可重新执行）：
   ```bash
   npm install
   ```
3. 启动开发调试服务器：
   ```bash
   npm run dev
   ```
4. 访问前端页面：浏览器打开 **http://localhost:3000**。
   - 开发服务器已配置 `/api` 代理至后端 `http://localhost:8080`。

---

## 五、核心业务流转测试说明
1. **学生注册与登录**：
   - 访问 `/register` 注册学生账号；
   - 访问 `/login` 登录平台。
2. **发布失物/招领**：
   - 点击顶部“发布信息”，选择“寻物启事”或“招领启事”，上传图片并提交。
3. **管理员初审**：
   - 登录 `superadmin`（密码 `Admin@2026`）；
   - 进入右上角“管理后台” -> “审核中心”进行物品通过/驳回。
4. **发起认领与双重流转**：
   - 学生 B 在前台查看已审核通过的招领物品，点击“申请认领”；
   - 发布者进入“我的认领” -> “我收到的认领申请”进行第一道核实（确认匹配）；
   - 管理员在“管理后台” -> “审核中心” -> “认领终审”点击终审通过；
   - 物品状态自动流转为“已认领（CLAIMED）”，其他申请自动关闭，各方收到站内通知。

---

## 六、VPS 生产服务器上线部署指南（全流程实战）

本系统采用经典且高性价比的生产级部署架构：**Nginx（反向代理 + 前端静态托管） + Systemd（后端 Jar 进程守护） + MySQL 8.0**。即使是 1核2G 或 2核4G 的低配云服务器（Ubuntu / Debian / CentOS / 腾讯云 / 阿里云 / 华为云 / 搬瓦工等），也能稳定丝滑运行。

```
                         客户端浏览器 / 移动端
                                 │
                     (HTTP:80 / HTTPS:443)
                                 ▼
                     ┌───────────────────────┐
                     │   Nginx 反向代理网关   │
                     └──┬─────────────────┬──┘
         静态请求 (/)   │                 │ 动态请求 (/api)
                        ▼                 ▼
          ┌─────────────────────┐   ┌──────────────────────┐
          │  Vue 3 静态资源目录  │   │ Spring Boot 服务(8080)│
          │ (/var/www/lostfound)│   │ (Systemd 守护运行)    │
          └─────────────────────┘   └──────────┬───────────┘
                                               │
                                               ▼
                                    ┌──────────────────────┐
                                    │    MySQL 8.0 数据库   │
                                    │  (campus_lost_found) │
                                    └──────────────────────┘
```

---

### 6.1 第一步：本地打包构建

在将项目上传至 VPS 之前，建议在本地完成前端静态构建与后端 Jar 包打包：

#### 1. 前端打包
进入前端根目录：
```bash
cd campus-lost-found-frontend
npm run build
```
构建成功后，将在 `campus-lost-found-frontend` 目录下生成 `dist` 文件夹。将该文件夹打包压缩为 `dist.zip` 或保留备用。

#### 2. 后端打包
进入后端根目录：
```bash
cd ../campus-lost-found-backend
mvn clean package -DskipTests
```
构建完成后，在 `target/` 目录下生成运行包 `lost-found-1.0.0.jar`。

---

### 6.2 第二步：VPS 服务器基础环境准备

根据您的云主机操作系统发行版，选择对应的包管理器命令（**CentOS / RHEL / Rocky / 阿里云 Linux / 腾讯云 OS 使用 `yum`**；Ubuntu / Debian 使用 `apt`）：

#### 选项 A：使用 `yum` / `dnf`（CentOS / RHEL / Rocky Linux / Alibaba Cloud Linux / TencentOS）
```bash
# 1. 更新系统软件包
sudo yum update -y

# 2. 安装基础工具与解压依赖
sudo yum install -y curl wget git unzip tar epel-release

# 3. 安装 Java 17 (OpenJDK 17)
sudo yum install -y java-17-openjdk java-17-openjdk-devel
java -version  # 验证是否显示 openjdk version "17.x.x"

# 4. 安装并启动 MySQL 8.0
sudo yum install -y mysql-server
sudo systemctl enable --now mysqld
sudo systemctl status mysqld  # 看到 active (running) 即代表 MySQL 启动成功

# 5. 安装 Nginx
sudo yum install -y nginx
sudo systemctl enable --now nginx
```

#### 选项 B：使用 `apt`（Ubuntu / Debian）
```bash
# 1. 更新系统软件包缓存
sudo apt update && sudo apt upgrade -y

# 2. 安装基础工具与 Web 服务器
sudo apt install -y curl wget git unzip ufw nginx

# 3. 安装 JDK 17
sudo apt install -y openjdk-17-jdk openjdk-17-jre
java -version

# 4. 安装并启动 MySQL 8.0
sudo apt install -y mysql-server
sudo systemctl enable --now mysql
```

---

### 6.3 第三步：VPS 数据库初始化

1. **设置 MySQL 密码并允许连接**：
   - **对于 `yum` 系统（CentOS/RHEL）**：初次安装后直接执行 `sudo mysql` 即可免密进入交互终端：
   - **对于 `apt` 系统（Ubuntu）**：执行 `sudo mysql` 进入：
   
   在终端执行以下 SQL（将 `YourStrongPassword123!` 替换为您的实际数据库强密码）：
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
   FLUSH PRIVILEGES;
   CREATE DATABASE IF NOT EXISTS campus_lost_found DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   EXIT;
   ```
   > [!TIP]
   > 若设置密码时提示 `ERROR 1819 (HY000): Your password does not satisfy the current policy requirements`（密码策略过严），可在 SQL 终端临时调低策略：
   > `SET GLOBAL validate_password.policy=0;` 和 `SET GLOBAL validate_password.length=6;`

2. **导入初始化建表脚本**：
   将项目中的 `campus-lost-found-backend/src/main/resources/db/V1__init.sql` 上传至服务器的 `/tmp/V1__init.sql`，执行导入：
   ```bash
   mysql -u root -p'root' campus_lost_found < /tmp/V1__init.sql
   ```
   *导入完成后，系统即具备 8 张业务表以及默认超级管理员账号 `superadmin` / `Admin@2026`。*

---

### 6.4 第四步：后端服务部署与 Systemd 守护进程

1. **创建生产部署工作目录**：
   ```bash
   sudo mkdir -p /var/www/campus-lost-found/backend
   sudo mkdir -p /var/www/campus-lost-found/backend/uploads
   ```

2. **上传文件**：
   将打包生成的 `lost-found-1.0.0.jar` 上传至 `/var/www/campus-lost-found/backend/app.jar`。

3. **创建生产配置文件 `application-prod.yml`**：
   在 `/var/www/campus-lost-found/backend/` 目录下新建 `application-prod.yml`：
   ```bash
   sudo nano /var/www/campus-lost-found/backend/application-prod.yml
   # 若未安装 nano，可使用 vim：sudo vi /var/www/campus-lost-found/backend/application-prod.yml
   ```
   写入以下内容：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://127.0.0.1:3306/campus_lost_found?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
       username: root
       password: YourStrongPassword123! # 替换为您刚设置的实际数据库密码
   
   upload:
     base-path: /var/www/campus-lost-found/backend/uploads
   ```

4. **配置 Systemd 服务守护**：
   新建系统服务配置文件：
   ```bash
   sudo nano /etc/systemd/system/lostfound.service
   ```
   输入以下配置（针对 1G~2G 小内存 VPS 优化了 JVM 参数 `-Xms256m -Xmx512m`）：
   ```ini
   [Unit]
   Description=Campus Lost and Found Backend Service
   After=network.target mysqld.service mysql.service
   
   [Service]
   Type=simple
   User=root
   WorkingDirectory=/var/www/campus-lost-found/backend
   ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /var/www/campus-lost-found/backend/app.jar --spring.config.additional-location=/var/www/campus-lost-found/backend/application-prod.yml
   Restart=always
   RestartSec=10
   StandardOutput=syslog
   StandardError=syslog
   SyslogIdentifier=lostfound-backend
   
   [Install]
   WantedBy=multi-user.target
   ```

5. **启动并验证后端服务**：
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable --now lostfound
   sudo systemctl status lostfound
   ```
   看到 `Active: active (running)` 且执行 `curl http://127.0.0.1:8080/api/categories` 返回 `200` 即表示后端运行正常！

---

### 6.5 第五步：前端静态托管与 Nginx 反向代理配置

1. **部署前端资源**：
   创建前端目录并将本地打包出的 `dist/` 内的所有文件上传至该目录：
   ```bash
   sudo mkdir -p /var/www/campus-lost-found/frontend
   # 确保 /var/www/campus-lost-found/frontend 目录下直接包含 index.html 和 assets/
   ```

2. **配置 Nginx 站点**：
   - **对于 `yum` 系统（CentOS/RHEL/云服务器）**：直接在 `/etc/nginx/conf.d/` 新建文件即可，Nginx 会自动加载：
     ```bash
     sudo nano /etc/nginx/conf.d/campus-lostfound.conf
     ```
   - **对于 `apt` 系统（Ubuntu/Debian）**：
     ```bash
     sudo nano /etc/nginx/sites-available/campus-lostfound.conf
     ```

   配置文件统一填入以下内容：
   ```nginx
   server {
       listen 80;
       server_name _; # 若已解析域名，可替换为例如 lostfound.yourdomain.com
   
       # 允许上传较大图片（默认限制为 20M）
       client_max_body_size 20M;
   
       # 1. 前端 Vue 静态单页应用托管
       location / {
           root /var/www/campus-lost-found/frontend;
           index index.html index.htm;
           # 解决 Vue Router History 模式刷新 404 问题
           try_files $uri $uri/ /index.html;
       }
   
       # 2. 后端 API 接口反向代理
       location /api/ {
           proxy_pass http://127.0.0.1:8080/api/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           proxy_connect_timeout 60s;
           proxy_read_timeout 60s;
       }
   
       # 3. 静态图片上传目录直接由 Nginx 高性能提供
       location /uploads/ {
           alias /var/www/campus-lost-found/backend/uploads/;
           expires 30d;
           add_header Cache-Control "public, no-transform";
           access_log off;
       }
   }
   ```

3. **yum 系统特有避坑：SELinux 权限放行（非常重要）**：
   CentOS / RHEL 默认开启 SELinux，若不放行会导致 Nginx 代理接口报 `502 Bad Gateway` 或访问前端报 `403 Forbidden`。请在终端执行：
   ```bash
   # 允许 Nginx 网络反向代理（解决 502 错误）
   sudo setsebool -P httpd_can_network_connect 1
   
   # 赋予前端与上传目录的安全上下文（解决 403 权限问题）
   sudo chcon -R -t httpd_sys_content_t /var/www/campus-lost-found/
   ```

4. **测试并重启 Nginx**：
   - 若是 Ubuntu：
     ```bash
     sudo rm -f /etc/nginx/sites-enabled/default
     sudo ln -s /etc/nginx/sites-available/campus-lostfound.conf /etc/nginx/sites-enabled/
     ```
   - 测试语法与重启服务：
     ```bash
     sudo nginx -t                # 确保输出 syntax is ok / test is successful
     sudo systemctl restart nginx
     ```

---

### 6.6 第六步：开放防火墙与云安全组端口

根据您的防火墙类型进行放行：

#### 选项 A：使用 `yum` 系统（CentOS/RHEL 默认使用 `firewalld`）
```bash
sudo firewall-cmd --zone=public --add-port=80/tcp --permanent
sudo firewall-cmd --zone=public --add-port=443/tcp --permanent
sudo firewall-cmd --zone=public --add-port=22/tcp --permanent
sudo firewall-cmd --reload
```

#### 选项 B：使用 `apt` 系统（Ubuntu 默认使用 `ufw`）
```bash
sudo ufw allow 22/tcp     # 务必放行 SSH，避免断开连接
sudo ufw allow 80/tcp     # HTTP 服务
sudo ufw allow 443/tcp    # HTTPS 服务
sudo ufw enable
```

#### 云服务器控制台安全组（必须操作）
无论使用哪种系统，都必须登录您的云服务商控制台（阿里云/腾讯云/华为云等），进入服务器实例的“安全组”页面，**添加入方向规则**：
- 端口 `80`（HTTP），授权对象 `0.0.0.0/0`
- 端口 `443`（HTTPS），授权对象 `0.0.0.0/0`
- 端口 `22`（SSH）

此时，在浏览器直接输入您 VPS 的公网 IP：`http://<您的VPS公网IP>`，即可完整访问并使用校园失物招领系统！

---

### 6.7 第七步（推荐）：绑定域名与开启 HTTPS 免费证书

若您拥有自己的域名（如 `lostfound.example.com`）：

1. **域名解析**：在域名 DNS 解析服务商处，添加一条 `A 记录`，主机记录为 `@` 或 `lostfound`，记录值填写 VPS 的公网 IP。
2. **安装 Certbot 并一键签发 Let's Encrypt 证书**：
   ```bash
   sudo apt install -y certbot python3-certbot-nginx
   sudo certbot --nginx -d lostfound.example.com
   ```
   根据命令行提示输入邮箱并同意条款，Certbot 将自动配置 Nginx 的 443 端口与 HTTPS 自动重定向。

---

### 6.8 生产环境常用运维命令与排错手册

| 场景 / 需求 | 对应操作命令 | 说明 |
| :--- | :--- | :--- |
| **查看后端运行日志** | `journalctl -u lostfound -f` | 实时滚动查看 Spring Boot 业务与异常日志 |
| **重启后端服务** | `sudo systemctl restart lostfound` | 更新 `app.jar` 后执行 |
| **重启 Nginx** | `sudo systemctl restart nginx` | 修改反向代理配置或上传新前端后执行 |
| **更新前端版本** | 替换 `/var/www/campus-lost-found/frontend/` 目录内容 | 无需重启服务，清除浏览器缓存即可 |
| **查看系统端口占用** | `sudo netstat -tlpn \| grep -E '80\|8080\|3306'` | 排查端口冲突 |
| **排查前端刷新 404** | 检查 Nginx 对应 `location /` 是否配置 `try_files $uri $uri/ /index.html;` | 必须配置以适配 Vue Router |
| **小内存 VPS 爆内存（OOM）** | 检查 Systemd 中 `-Xms256m -Xmx512m` 是否生效；必要时配置 Linux Swap 交换分区 | `sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile` |

