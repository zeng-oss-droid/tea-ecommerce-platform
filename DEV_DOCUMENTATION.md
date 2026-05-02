# 茶叶电商平台开发文档

> 项目：`tea-ecommerce-platform`  
> 前端：`Vue 3 + Vue Router 4 + Pinia + Axios + Element Plus + Vite`  
> 后端：`Spring Boot 3.5.8 + MyBatis + MySQL + Redis + JWT`

---

## 1. 项目概览

该项目是一个茶叶电商平台，包含：

1. 前台（用户侧）：首页、商品列表/详情、购物车、下单/支付、订单管理、收货地址、个人信息、加入我们、茶文化、商品评价、申请退款等。
2. 管理后台（管理员侧）：数据统计、用户/商家申请审核、订单管理、退款管理、商品管理、分类管理、评论管理等。
3. 商家后台（商家侧）：商品管理、订单管理、退款管理等（由商家角色触发路由/按钮可见性）。

---

## 2. 技术栈与关键依赖

### 2.1 后端（Spring Boot）

- Web：`spring-boot-starter-web`
- 数据访问：`mybatis-spring-boot-starter`
- 数据库：`MySQL`
- 缓存/扩展：`spring-boot-starter-data-redis`（当前用于基础 Redis 配置）
- 鉴权：`JWT（jjwt）`

关键类/模块：

- 统一返回：`src/main/java/.../common/Result.java`
- JWT：`src/main/java/.../util/JwtUtil.java`
- 拦截器：
  - `src/main/java/.../interceptor/JwtInterceptor.java`（设置 `userId` 到请求属性）
  - `src/main/java/.../interceptor/AdminInterceptor.java`（校验管理员权限，返回 403）
- MVC 配置：`src/main/java/.../config/WebConfig.java`

### 2.2 前端（Vue 3）

- UI：`Element Plus`
- 网络请求：`Axios`
- 状态管理：`Pinia`
- 路由：`Vue Router`
- 构建与开发：`Vite`

关键文件：

- 入口：`frontend/src/main.js`
- 路由：`frontend/src/router/index.js`
- 状态：`frontend/src/stores/user.js`
- API 封装与拦截器：`frontend/src/utils/api.js`
- 布局：
  - 前台布局：`frontend/src/layouts/MainLayout.vue`
  - 后台布局：`frontend/src/layouts/AdminLayout.vue`

---

## 3. 环境准备

### 3.1 运行前要求

- JDK：17+
- Maven：3.6+
- MySQL：8.0+
- Redis：6.0+
- Node.js：16+

### 3.2 数据库初始化

后端提供了初始化脚本：

- `src/main/resources/db/init.sql`

使用方式（按 README 指引）：

1. 创建数据库 `tea_ecommerce`
2. 执行 `init.sql` 初始化表与测试数据

> 注意：`init.sql` 中包含 `user`、`category`、`product`、`cart`、`order`、`order_item`、`address`、`logistics`、`review`、`refund` 等表结构与插入数据。

---

## 4. 配置说明

### 4.1 后端配置文件

`src/main/resources/application.properties`

与开发相关的核心项：

- 服务端口：`server.port=8080`
- MySQL 连接：
  - `spring.datasource.url`
  - `spring.datasource.username`
  - `spring.datasource.password`
- JWT：
  - `jwt.secret=tea-ecommerce-platform-secret-key-2024`
  - `jwt.expiration=86400000`（毫秒）
- 文件上传：
  - `file.upload.path=uploads/`
  - `file.upload.url=http://localhost:8080/uploads/`

### 4.2 前端开发代理

`frontend/vite.config.js`

- 配置了代理：`/api` -> `http://localhost:8080`
- 因此前端请求统一以 `/api/...` 开头，无需关心后端端口。

---

## 5. 目录结构（代码落点）

### 5.1 后端

- `src/main/java/com/reservation/teaecommerceplatform/`
  - `controller/`：HTTP 接口层（用户、商品、订单、退款、管理等）
  - `service/`：业务逻辑层
  - `mapper/`：MyBatis Mapper 接口
  - `entity/`：实体类（User/Product/Order/Refund/Review 等）
  - `interceptor/`：JWT 与管理员权限拦截器
  - `config/`：WebConfig、CorsConfig、RedisConfig 等
  - `util/`：JWT、MD5 等工具类
  - `dto/`：登录注册等请求 DTO
  - `common/`：统一响应体 `Result`

资源层：

- `src/main/resources/mapper/*.xml`：MyBatis SQL 映射
- `src/main/resources/db/*.sql`：数据库脚本

### 5.2 前端

- `frontend/src/`
  - `router/`：路由配置与路由守卫
  - `stores/`：Pinia 状态（当前只有 `user.js`）
  - `utils/`：API 封装（`api.js`）
  - `layouts/`：`MainLayout` 与 `AdminLayout`
  - `views/`：页面组件（前台 + admin 子目录）

---

## 6. 认证与权限模型（JWT + 角色）

### 6.1 前端 token 存储与注入

- 登录后，token 写入：
  - `frontend/src/stores/user.js`：`localStorage.setItem('token', ...)`
- Axios 请求拦截器自动在请求头附加：
  - `Authorization: Bearer <token>`

位置：`frontend/src/utils/api.js`

### 6.2 后端 JWT 处理（登录态）

- `JwtInterceptor`（`/api/**`）：
  - 读取 `Authorization` header
  - 校验 token 并从 token claims 中解析 `userId`
  - 设置到 request attribute：`request.setAttribute("userId", userId)`
  - 注意：当前策略为“JWT 拦截器对所有请求放行”，是否登录由 Controller 决定。

`WebConfig` 里排除了部分公开接口（无需 JWT）：

- `/api/user/register`
- `/api/user/login`
- `/api/product/list`
- `/api/product/detail/**`
- `/api/category/list`

### 6.3 后端管理员权限拦截（403）

`AdminInterceptor` 仅允许管理员（角色 `role == 1`）访问部分路径：

- `WebConfig` 注册拦截器路径：
  - `/api/admin/**`
  - `/api/upload/**`
  - `/api/category/add`
  - `/api/category/update`
  - `/api/category/delete/**`

未通过时返回：

- HTTP 403，并输出 JSON：
  - `{"code":403,"message":"无权限访问"}`

### 6.4 角色定义与 UI 可见性

用户表 `user.role`：

- `0`：普通用户
- `1`：管理员
- `2`：商家

前端路由权限：

- `frontend/src/router/index.js` 使用 route meta：
  - `requiresAuth`：未登录重定向 `/login`
  - `requiresAdmin`：`userInfo.role !== 1` 则无权限
  - `requiresMerchant`：允许 `role` 为 `1` 或 `2`

前台顶部下拉菜单/后台菜单：

- `MainLayout.vue` 根据 `userStore.userInfo?.role` 显示“管理后台/商家后台”
- `AdminLayout.vue` 根据当前路由是否 `/merchant` 来决定菜单项（商家仅显示订单/退款/商品）

---

## 7. 统一返回结构与前端错误处理

### 7.1 后端统一响应

`src/main/java/.../common/Result.java`

- 字段：
  - `code`：200 成功；500/自定义失败
  - `message`：提示信息
  - `data`：业务数据

常见工厂方法：

- `Result.success(data)` -> `code=200`
- `Result.error(message)` -> `code=500`

### 7.2 前端响应拦截器逻辑

在 `frontend/src/utils/api.js`：

- 拦截器读取 `response.data` 作为 `res`
- `res.code === 200`：直接返回 `res`
- `res.code === 401`：
  - 触发 `userStore.logout()`
  - 跳转 `/login`
  - 弹出消息
- 其他 code：把错误 reject 给组件处理，避免重复 toast

---

## 8. API 总览（按 Controller 分组）

> 所有接口统一前缀均为：`/api`

### 8.1 用户与登录

- `POST /api/user/register`：注册（公开）
- `POST /api/user/login`：登录（公开）
- `GET /api/user/info`：获取当前用户信息（需要登录）
- `PUT /api/user/info`：更新个人信息（需要登录）
- `PUT /api/user/password`：修改密码（需要登录）
- `POST /api/user/merchant/apply`：申请商家入驻（需要登录）

实现位置：
- `src/main/java/.../controller/UserController.java`

### 8.2 商品

- `GET /api/product/list`：商品列表（公开）
- `GET /api/product/detail/{id}`：商品详情（公开）
- `POST /api/product/add`：新增商品（需要管理员或商家权限，服务层会校验）
- `PUT /api/product/update`：更新商品
- `DELETE /api/product/delete/{id}`：删除商品

实现位置：
- `ProductController.java`

### 8.3 分类

- `GET /api/category/list`：分类列表（公开）
- `GET /api/category/{id}`：分类详情
- `POST /api/category/add`：新增分类（管理员权限拦截）
- `PUT /api/category/update`：更新分类（管理员权限拦截）
- `DELETE /api/category/delete/{id}`：删除分类（管理员权限拦截）

实现位置：
- `CategoryController.java`

### 8.4 购物车

- `GET /api/cart/list`：购物车列表
- `POST /api/cart/add`：加入购物车
- `PUT /api/cart/update`：更新数量
- `DELETE /api/cart/remove/{cartId}`：删除单项
- `DELETE /api/cart/clear`：清空购物车

实现位置：
- `CartController.java`

### 8.5 订单

- `POST /api/order/create`：创建订单
- `GET /api/order/list`：订单列表
- `GET /api/order/detail/{orderId}`：订单详情（含订单项）
- `POST /api/order/pay`：支付订单
- `POST /api/order/cancel/{orderId}`：取消订单
- `POST /api/order/confirm/{orderId}`：确认收货

实现位置：
- `OrderController.java`

### 8.6 收货地址

- `GET /api/address/list`：地址列表
- `GET /api/address/{id}`：地址详情
- `POST /api/address/add`：新增地址
- `PUT /api/address/update`：更新地址
- `POST /api/address/setDefault`：设置默认地址
- `DELETE /api/address/delete/{id}`：删除地址

实现位置：
- `AddressController.java`

### 8.7 物流（订单维度）

- `GET /api/logistics/order/{orderId}`：查询订单物流信息
- `POST /api/logistics/add`：新增物流信息
- `PUT /api/logistics/update`：更新物流信息

实现位置：
- `LogisticsController.java`

### 8.8 评价

- `POST /api/review/create`：创建评价
- `GET /api/review/product/{productId}`：按商品查询
- `GET /api/review/order/{orderId}`：按订单查询
- `GET /api/review/my`：我的评价

实现位置：
- `ReviewController.java`

### 8.9 退款

前台/用户侧：

- `POST /api/refund/create`：创建退款申请
- `GET /api/refund/my`：查询我的退款
- `GET /api/refund/detail/{refundId}`：退款详情
- `POST /api/refund/cancel/{refundId}`：取消退款申请
- `POST /api/refund/update-logistics/{refundId}`：退货物流号更新（用于退货退款流程）

实现位置：
- `RefundController.java`

管理侧：

- 管理员（`role == 1`）：由 `AdminController` 提供
  - `GET /api/admin/refunds`
  - `POST /api/admin/refunds/{id}/approve`
  - `POST /api/admin/refunds/{id}/reject`
  - `POST /api/admin/refunds/{id}/complete`
  - `POST /api/admin/refunds/{id}/confirm-receive`

- 商家（`role == 2`）：由 `MerchantController` 提供
  - `GET /api/merchant/refunds`
  - `POST /api/merchant/refunds/{id}/approve|reject|complete|confirm-receive`

说明：

- `MerchantController` 内部会校验商家角色（若 `role == 1` 会抛出“管理员请使用管理后台”）。

### 8.10 后台管理（管理员）

- `GET /api/admin/users`、`GET /api/admin/users/{id}`
- `PUT /api/admin/users/{id}/status`：启用/禁用
- `PUT /api/admin/users/{id}/role`：修改角色（服务层限制）
- `GET /api/admin/merchant-applications`：商家申请列表
- `POST /api/admin/merchant-applications/{id}/approve|reject`
- `GET /api/admin/statistics/dashboard|orders|products|users`
- `GET /api/admin/orders`、`PUT /api/admin/orders/{id}/status`
- `GET /api/admin/refunds`、`POST /api/admin/refunds/...` 等
- `GET /api/admin/reviews`、`PUT /api/admin/reviews/{id}/status`、`POST /api/admin/reviews/{id}/reply`、`DELETE /api/admin/reviews/{id}`

实现位置：
- `AdminController.java`

### 8.11 文件上传

- `POST /api/upload/image`：单图上传（字段名：`file`）
- `POST /api/upload/images`：多图上传（字段名：`files`）

返回：

- 成功：`Result.success("上传成功", <url 或 url数组>)`

实现位置：
- `FileUploadController.java`

静态资源映射：

- `WebConfig.addResourceHandlers` 将 `/uploads/**` 映射到本机 `file.upload.path`

---

## 9. 数据库设计（表与关键字段）

下面基于 `src/main/resources/db/init.sql` 的核心表给出概览：

1. `user`
   - 角色字段：`role`（0普通用户/1管理员/2商家）
   - 商家申请字段：`merchant_apply_status`、`merchant_apply_reject_reason` 等
2. `category`
   - 树结构：
     - `parent_id`（0 表示顶级）
   - 状态：`status`（0禁用/1启用）
3. `product`
   - 商品状态：`status`（0下架/1上架）
   - 商品图片：`images`（JSON 数组字符串，如 `["url1","url2"]`）
   - 商家归属：`seller_id`
4. `address`
   - 默认地址：`is_default`
5. `cart`
   - 唯一约束：`(user_id, product_id)`，避免同一商品重复出现
6. `order`
   - 订单状态：`status`
     - 0待支付、1已支付、2已发货、3已完成、4已取消、5退款中、6已退款
   - 支付类型：`pay_type`
     - 0未支付、1支付宝、2微信、3银行卡
7. `order_item`
   - 订单项明细：包含 `product_name/product_image/price/quantity/subtotal/seller_id`
8. `logistics`
   - 物流详情：`detail` 为 JSON 字符串
9. `review`
   - 评价状态：`status`（0待审核/1已通过/2已拒绝）
10. `refund`
   - 退款状态：`status`
     - 0待处理、1已同意、2已拒绝、3退款中、4已退款、5已取消
   - 退款类型：`type`（1仅退款/2退货退款）

---

## 10. 前端关键实现与业务流

### 10.1 入口与布局

- `frontend/src/main.js`：
  - 注册 Pinia、Router、Element Plus
  - 挂载到 `#app`
- `App.vue`：直接渲染 `router-view`
- `MainLayout.vue`：
  - 顶部导航
  - 根据登录状态展示登录/注册 or 用户下拉菜单
  - 显示购物车图标并在路由变化时刷新购物车数量
- `AdminLayout.vue`：
  - 左侧菜单（管理员/商家模式切换）
  - 顶部用户下拉（返回前台/退出登录）

### 10.2 路由守卫

`frontend/src/router/index.js` 在 `beforeEach` 中完成：

1. `requiresAuth`：无 token -> `/login`
2. `requiresAdmin`：`userInfo.role !== 1` -> 弹错误并返回前台（`/`）
3. `requiresMerchant`：允许 role 为 1 或 2

---

## 11. 后端关键实现（业务逻辑落点）

### 11.1 密码加密与登录

- 登录/注册使用 MD5：
  - `Md5Util.encrypt(...)`
- `UserServiceImpl`：
  - 注册：将明文密码 MD5 后写入数据库
  - 登录：对输入密码 MD5 后与数据库密码比对
  - 成功后生成 JWT：`jwtUtil.generateToken(userId, username)`

### 11.2 订单创建（核心逻辑）

`OrderServiceImpl.createOrder(...)` 的主要步骤：

1. 校验 `addressId` 是否属于当前用户
2. 读取购物车数据；若传了 `cartIds` 则仅处理选中商品
3. 计算总金额：遍历购物车项，检查商品是否上架、库存是否足够
4. 插入 `order`，状态置为 `0`（待支付）
5. 插入 `order_item`，同时扣减商品库存
6. 清空购物车

### 11.3 退款流程（关键状态变更）

`RefundServiceImpl` 核心逻辑：

- 创建退款申请：
  - 仅允许特定订单状态申请（不允许待支付/取消/已退款等）
  - 退款状态置为 `0`（待处理）
  - 同步更新订单状态为 `5`（退款中）
- 管理员 approve/reject/complete/confirm-receive：
  - 根据退款类型（仅退款/退货退款）推进不同的 `refund.status` 与 `order.status`

---

## 12. 文件上传（图片/多图）

后端上传端点：

- `POST /api/upload/image`（单图，表单字段 `file`）
- `POST /api/upload/images`（多图，表单字段 `files`）

上传校验：

- 限制后缀：`jpg|jpeg|png|gif|webp`
- 单个/多文件均会自动生成随机文件名（UUID + 扩展名）

上传目录与访问：

- 文件落地在 `file.upload.path`（默认 `uploads/`）
- 访问路径为 `file.upload.url`（映射 `/uploads/**`）

前端使用方式：

- 后台商品/分类管理页面把 `uploadUrl` 指向：`/api/upload/image`
- 并在上传时通过请求头携带 `Authorization`（当用户 token 存在）
- 上传成功后从响应 `response.code === 200` 写入表单的 `mainImage/image`

---

## 13. 新增功能的开发指南（推荐流程）

当你要新增一个业务能力（例如：优惠券、商品收藏、通知系统等），建议按以下路径推进：

1. 后端新增
   - `controller/`：增加接口路由（路径统一以 `/api/...`）
   - `service/`：实现业务逻辑与权限/状态校验
   - `mapper/ + resources/mapper/*.xml`：补齐 SQL 与数据映射
   - 返回统一结构：`Result.success / Result.error`
2. 安全与权限
   - 明确接口是否需要登录（属于 `/api/**` 且在 `WebConfig` 中是否排除 JWT）
   - 明确是否需要管理员权限（属于 `/api/admin/**` 或是否属于 `AdminInterceptor` 管控路径）
3. 前端新增
   - `views/`：新增页面或复用已有页面（如在 `views/admin/`）
   - `router/index.js`：添加路由与 meta 权限配置（`requiresAuth/ requiresAdmin/ requiresMerchant`）
   - 需要调用接口时：统一走 `utils/api.js`（从而复用 token 注入与错误处理）

---

## 14. 常见问题排查

### 14.1 `401`：未登录或 token 过期

- 前端 `api.js` 会捕获 `res.code === 401`，触发：
  - `logout()`
  - 跳转 `/login`
- 常见原因：
  - token 丢失/过期
  - 请求未携带 Authorization header

### 14.2 `403`：无权限访问

- 常见原因：访问了受 `AdminInterceptor` 管控的路径，但当前 `role != 1`
- 典型路径：
  - `/api/admin/**`
  - `/api/upload/**`
  - `/api/category/add|update|delete/**`

### 14.3 图片无法显示

- 确认上传返回的 URL 是不是以 `/uploads/` 开头
- 前端在订单详情等处对图片路径做了兼容处理（相对路径会拼 `http://localhost:8080`）

---

## 15. 默认账号（便于测试）

README 中给出的默认管理员账号：

- 用户名：`admin`
- 密码：`admin123`

> 后端登录会对输入密码执行 MD5 后比对数据库，因此实际数据库中存储的是 MD5 值。

