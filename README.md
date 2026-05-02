# 茶叶电商平台（Tea E-Commerce Platform）

前后端分离的茶叶主题电商系统：用户浏览下单、商家管理店铺、管理员运营全站。后端提供 REST API，前端使用 Vite 开发、生产构建后可由 Nginx 或 Spring 静态资源托管。

**在线仓库：** [https://github.com/zeng-oss-droid/tea-ecommerce-platform](https://github.com/zeng-oss-droid/tea-ecommerce-platform)

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot **3.5.x**、MyBatis **3.0.x**、MySQL 8、Redis、JWT（jjwt）、Maven |
| 前端 | Vue **3**、Vue Router **4**、Pinia、Element Plus、Axios、**ECharts**、Vite **5** |
| 安全 | 密码 **MD5** 存储（演示用）；接口层 JWT 解析 + 管理端路径单独鉴权 |

---

## 角色说明

| role | 说明 | 主要入口 |
|------|------|----------|
| `0` | 普通用户 | 商城、购物车、订单、个人中心、申请入驻 |
| `1` | 管理员 | `/admin/*` 管理后台（用户、商品、分类、订单、退款、评论、商家申请等） |
| `2` | 商家 | `/merchant/*` 商家工作台（本店商品、订单、退款） |

商家入驻资料与申请状态保存在 **`user` 表**的商户相关字段中（无单独「申请表」）。

---

## 功能概览

### 前台（用户）

- 首页、商品列表/搜索/筛选、商品详情  
- 购物车、收货地址、下单、模拟支付、取消订单、确认收货、订单详情  
- 注册、登录（JWT）、个人资料、修改密码  
- **加入我们**：提交商家入驻申请  
- **茶文化**、个人中心等页面  

### 售后与互动

- **退款**：发起退款、我的退款、填写退货物流、取消申请（具体状态以后端为准）  
- **评价**：订单完成后评价、按商品查看评价、我的评价  

### 管理后台（管理员）

- **数据统计**：仪表盘（ECharts 等全站概览）  
- **订单 / 退款 / 评论** 管理与审核流程  
- **商品 / 分类** 维护（全站商品）  
- **用户管理**：启用禁用、角色调整、删除用户  
- **商家申请**：审核通过/驳回、**删除申请记录**（待审/驳回，清空入驻字段；已通过需先处理角色）  

### 商家工作台

- 本店商品 CRUD、本店订单发货与状态、本店相关退款处理  

### 其它

- 图片本地上传（`uploads/`，配置见 `application.properties`）  
- 物流信息维护与查询（`logistics`）  

---

## 项目结构（摘要）

```
tea-ecommerce-platform/
├── pom.xml                          # 后端 Maven
├── src/main/java/.../
│   ├── TeaEcommercePlatformApplication.java
│   ├── package-info.java            # 包级说明（分层约定）
│   ├── common/                      # 统一响应 Result
│   ├── config/                      # Web、CORS、Redis
│   ├── controller/                  # REST（user、product、order、admin、merchant…）
│   ├── service/ / service/impl/
│   ├── mapper/                      # MyBatis 接口
│   ├── entity/、dto/、interceptor/、util/
├── src/main/resources/
│   ├── application.properties       # 数据源、Redis、JWT、上传路径等
│   ├── mapper/*.xml                 # MyBatis SQL
│   └── db/
│       ├── tea_ecommerce.sql        # 推荐：完整表结构 + 演示数据
│       └── init.sql                 # 较早建库脚本（若与现网不一致请以 tea_ecommerce.sql 为准）
├── frontend/
│   ├── package.json
│   ├── vite.config.js               # 开发代理 /api -> localhost:8080
│   └── src/
│       ├── layouts/                 # MainLayout、AdminLayout（含商家布局复用）
│       ├── views/、views/admin/
│       ├── router/、stores/、utils/
└── README.md
```

---

## 环境要求

- **JDK 17+**  
- **Maven 3.6+**（或使用项目自带 `mvnw`）  
- **MySQL 8.0+**  
- **Redis**（与 `application.properties` 中配置一致）  
- **Node.js 18+**（建议 LTS，用于前端）

---

## 数据库初始化（推荐）

1. 在 MySQL 中创建库（若脚本未包含建库语句可手动建）：`tea_ecommerce`  
2. 导入 **`src/main/resources/db/tea_ecommerce.sql`**（含表结构与演示数据）。  

> 若使用仓库中的 **`init.sql`**，表结构可能与当前代码演进不一致，**优先以 `tea_ecommerce.sql` 为准**。

### 演示账号说明

- 脚本中部分普通用户的密码哈希为 `0192023a7bbd73250516f069df18b500`，对应明文 **`admin123`**（与 `init.sql` 注释一致，便于本地演示）。  
- 管理员 **`admin`** 在 `tea_ecommerce.sql` 中的哈希与其它用户不同；**若无法登录**，可在库中将其 `password` 更新为上述 MD5 值，或自行生成 MD5 后更新。  

**切勿**将含真实密码的 `application.properties` 提交到公开仓库；生产环境请用环境变量或 `application-local.properties`（并加入 `.gitignore`）。

---

## 配置说明

编辑 **`src/main/resources/application.properties`**：

| 配置项 | 含义 |
|--------|------|
| `spring.datasource.*` | MySQL 连接 |
| `spring.data.redis.*` | Redis |
| `jwt.secret` / `jwt.expiration` | JWT 签名与过期时间 |
| `file.upload.path` / `file.upload.url` | 本地上传目录与对外访问 URL 前缀 |
| `spring.web.cors.allowed-origins` | 允许的前端源（默认含 `http://localhost:5173`） |

---

## 启动步骤

### 1. 启动 Redis 与 MySQL

确保 Redis、MySQL 已运行，且库表已按上文导入。

### 2. 启动后端

在项目根目录：

```bash
mvn spring-boot:run
```

默认：**http://localhost:8080**

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认：**http://localhost:5173**（`vite.config.js` 已将 `/api` 代理到 `8080`）

### 4. 生产构建（前端）

```bash
cd frontend
npm run build
```

产物在 `frontend/dist/`，可由任意静态服务器或网关反代。

---

## 主要 API 前缀（节选）

| 前缀 | 说明 |
|------|------|
| `/api/user/*` | 注册、登录、资料、密码、商家入驻申请 |
| `/api/product/*` | 商品列表/详情（部分匿名）、增删改（管理员/商家） |
| `/api/category/*` | 分类；写操作需管理员 |
| `/api/cart/*`、`/api/order/*`、`/api/address/*` | 购物车、订单、地址 |
| `/api/refund/*`、`/api/review/*` | 退款、评价 |
| `/api/admin/*` | 管理端（需管理员 JWT） |
| `/api/merchant/*` | 商家端（需商家 JWT） |
| `/api/upload/*` | 图片上传（与 `WebConfig` 中拦截规则一致） |

完整路径见各 `*Controller.java`。

---

## 数据表一览

| 表名 | 说明 |
|------|------|
| `user` | 用户、角色、商家入驻字段 |
| `category`、`product` | 分类与商品（含 `seller_id`） |
| `cart`、`address` | 购物车、收货地址 |
| `order`、`order_item` | 订单与明细 |
| `refund` | 退款单 |
| `review` | 商品评价 |
| `logistics` | 物流信息 |

---

## 注意事项

1. **Redis** 未启动时，依赖 Redis 的接口可能失败，请先启动 Redis。  
2. **JWT** 存于前端 `localStorage`（见 `stores/user.js`），刷新页面可保持登录（未过期前提下）。  
3. **HTTPS / Git**：若本机 Git 访问 GitHub 报 SSL 错误，可配置 `http.sslBackend schannel`（Windows）或按公司要求导入根证书。  
4. **公开仓库**：务必移除或脱敏数据库密码、JWT `secret` 等敏感配置。  

---

## 后续可扩展方向（非必须）

- 对接真实支付、短信/邮件  
- 统一全局异常与 API 文档（SpringDoc）  
- 优惠券、库存预警、导出报表  
- 更细的操作审计日志  

---

## 许可证

MIT License
