# 茶叶电商平台

一个基于 Spring Boot + Vue.js 的茶叶电商平台，实现完整的在线购物功能。

## 技术栈

### 后端
- Spring Boot 3.5.8
- MyBatis 3.0.5
- MySQL 8.0
- Redis
- JWT 认证
- Maven

### 前端
- Vue 3
- Vue Router 4
- Pinia
- Element Plus
- Axios
- Vite

## 功能模块

### 用户管理模块
- 用户注册
- 用户登录（JWT认证）
- 个人信息管理
- 密码修改

### 商品管理模块
- 商品展示
- 商品分类浏览
- 商品搜索筛选
- 商品详情查看

### 交易管理模块
- 购物车管理
- 订单创建
- 在线支付（模拟）
- 订单状态跟踪

### 订单管理模块
- 订单查询
- 订单详情查看
- 订单取消
- 确认收货

### 物流管理模块
- 收货地址管理
- 物流信息查询
- 配送跟踪

## 项目结构

```
tea-ecommerce-platform/
├── src/                          # 后端源码
│   ├── main/
│   │   ├── java/
│   │   │   └── com/reservation/teaecommerceplatform/
│   │   │       ├── common/       # 通用类
│   │   │       ├── config/       # 配置类
│   │   │       ├── controller/   # 控制器
│   │   │       ├── dto/          # 数据传输对象
│   │   │       ├── entity/       # 实体类
│   │   │       ├── interceptor/  # 拦截器
│   │   │       ├── mapper/       # Mapper接口
│   │   │       ├── service/      # 服务层
│   │   │       └── util/         # 工具类
│   │   └── resources/
│   │       ├── mapper/           # MyBatis XML映射文件
│   │       ├── db/               # 数据库脚本
│   │       └── application.properties
│   └── test/
├── frontend/                      # 前端源码
│   ├── src/
│   │   ├── layouts/              # 布局组件
│   │   ├── views/                # 页面组件
│   │   ├── stores/               # Pinia状态管理
│   │   ├── router/               # 路由配置
│   │   └── utils/                # 工具函数
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+

### 后端启动

1. 创建数据库并执行初始化脚本
```sql
-- 执行 src/main/resources/db/init.sql
```

2. 修改数据库配置
编辑 `src/main/resources/application.properties`，修改数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tea_ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

3. 启动Redis服务

4. 运行项目
```bash
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

## API接口

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取当前用户信息
- `PUT /api/user/info` - 更新用户信息
- `PUT /api/user/password` - 修改密码

### 商品相关
- `GET /api/product/list` - 获取商品列表
- `GET /api/product/detail/{id}` - 获取商品详情
- `POST /api/product/add` - 添加商品（管理员）
- `PUT /api/product/update` - 更新商品（管理员）
- `DELETE /api/product/delete/{id}` - 删除商品（管理员）

### 分类相关
- `GET /api/category/list` - 获取分类列表
- `GET /api/category/{id}` - 获取分类详情

### 购物车相关
- `GET /api/cart/list` - 获取购物车列表
- `POST /api/cart/add` - 添加到购物车
- `PUT /api/cart/update` - 更新购物车商品数量
- `DELETE /api/cart/remove/{cartId}` - 删除购物车商品
- `DELETE /api/cart/clear` - 清空购物车

### 订单相关
- `POST /api/order/create` - 创建订单
- `GET /api/order/list` - 获取订单列表
- `GET /api/order/detail/{orderId}` - 获取订单详情
- `POST /api/order/pay` - 支付订单
- `POST /api/order/cancel/{orderId}` - 取消订单
- `POST /api/order/confirm/{orderId}` - 确认收货

### 地址相关
- `GET /api/address/list` - 获取地址列表
- `POST /api/address/add` - 添加地址
- `PUT /api/address/update` - 更新地址
- `POST /api/address/setDefault` - 设置默认地址
- `DELETE /api/address/delete/{id}` - 删除地址

## 数据库设计

主要数据表：
- `user` - 用户表
- `category` - 商品分类表
- `product` - 商品表
- `cart` - 购物车表
- `order` - 订单表
- `order_item` - 订单项表
- `address` - 收货地址表
- `logistics` - 物流信息表

## 默认账号

管理员账号：
- 用户名：admin
- 密码：admin123

## 注意事项

1. 确保MySQL和Redis服务已启动
2. 首次运行需要执行数据库初始化脚本
3. 前端开发服务器已配置代理，API请求会自动转发到后端
4. JWT token存储在localStorage中，刷新页面不会丢失登录状态

## 开发计划

- [ ] 完善支付功能对接
- [ ] 实现物流信息实时查询
- [ ] 添加商品评价功能
- [ ] 实现优惠券系统
- [ ] 添加数据统计功能
- [ ] 优化前端UI/UX
- [ ] 添加单元测试

## 许可证

MIT License

