-- 创建数据库
CREATE DATABASE IF NOT EXISTS tea_ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tea_ecommerce;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(500) COMMENT '分类描述',
    `image` VARCHAR(255) COMMENT '分类图片',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` VARCHAR(500) COMMENT '商品描述',
    `detail` TEXT COMMENT '商品详情',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10, 2) COMMENT '原价',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `images` TEXT COMMENT '商品图片，JSON格式存储',
    `category_id` BIGINT COMMENT '分类ID',
    `origin` VARCHAR(100) COMMENT '产地',
    `process` VARCHAR(100) COMMENT '工艺',
    `grade` VARCHAR(50) COMMENT '等级',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-上架',
    `seller_id` BIGINT DEFAULT 1 COMMENT '商家ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) COMMENT '省份',
    `city` VARCHAR(50) COMMENT '城市',
    `district` VARCHAR(50) COMMENT '区县',
    `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `postal_code` VARCHAR(10) COMMENT '邮编',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认：0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT DEFAULT 1 COMMENT '商品数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `address_id` BIGINT NOT NULL COMMENT '收货地址ID',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(10, 2) DEFAULT 0 COMMENT '优惠金额',
    `pay_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-退款中 6-已退款',
    `pay_type` TINYINT DEFAULT 0 COMMENT '支付方式：0-未支付 1-支付宝 2-微信 3-银行卡',
    `pay_time` VARCHAR(50) COMMENT '支付时间',
    `logistics_no` VARCHAR(50) COMMENT '物流单号',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(255) COMMENT '商品图片',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `subtotal` DECIMAL(10, 2) NOT NULL COMMENT '小计',
    `seller_id` BIGINT DEFAULT 1 COMMENT '商家ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 物流信息表
CREATE TABLE IF NOT EXISTS `logistics` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `logistics_no` VARCHAR(50) COMMENT '物流单号',
    `company` VARCHAR(100) COMMENT '物流公司',
    `status` VARCHAR(50) COMMENT '物流状态',
    `detail` TEXT COMMENT '物流详情，JSON格式',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 插入测试数据
-- 插入分类数据
INSERT INTO `category` (`name`, `description`, `parent_id`, `sort_order`) VALUES
('绿茶', '绿茶是中国的主要茶类之一，具有清汤绿叶的品质特点', 0, 1),
('红茶', '红茶属全发酵茶，具有红茶、红汤、红叶和香甜味醇的特征', 0, 2),
('乌龙茶', '乌龙茶亦称青茶，属于半发酵茶，既有绿茶的清香，又有红茶的醇厚', 0, 3),
('白茶', '白茶属微发酵茶，具有外形芽毫完整、满身披毫、毫香清鲜的特点', 0, 4),
('黑茶', '黑茶属后发酵茶，具有越陈越香的特点', 0, 5),
('黄茶', '黄茶属轻发酵茶类，具有黄叶黄汤的特点', 0, 6);

-- 插入商品数据
INSERT INTO `product` (`name`, `description`, `detail`, `price`, `original_price`, `stock`, `images`, `category_id`, `origin`, `process`, `grade`, `sales`, `status`) VALUES
('西湖龙井', '正宗西湖龙井，清香甘甜，回甘持久', '西湖龙井茶，中国十大名茶之一，属绿茶，产于浙江省杭州市西湖龙井村周围群山。具有1200多年历史。清乾隆游览杭州西湖时，盛赞西湖龙井茶，把狮峰山下胡公庙前的十八棵茶树封为"御茶"。', 298.00, 398.00, 100, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400","https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400"]', 1, '浙江杭州', '炒青', '特级', 156, 1),
('碧螺春', '洞庭碧螺春，香气浓郁，滋味鲜醇', '碧螺春是中国传统名茶，中国十大名茶之一，属于绿茶类，已有1000多年历史。碧螺春产于江苏省苏州市吴县太湖的东洞庭山及西洞庭山（今苏州吴中区）一带，所以又称"洞庭碧螺春"。', 268.00, 358.00, 80, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 1, '江苏苏州', '炒青', '特级', 98, 1),
('黄山毛峰', '黄山毛峰，形似雀舌，香如白兰', '黄山毛峰是中国十大名茶之一，属于绿茶。产于安徽省黄山（徽州）一带，所以又称徽茶。由清代光绪年间谢裕大茶庄所创制。', 228.00, 298.00, 120, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 1, '安徽黄山', '烘青', '一级', 134, 1),
('信阳毛尖', '信阳毛尖，细圆光直，香高味浓', '信阳毛尖又称豫毛峰，属绿茶类，是中国十大名茶之一，也是河南省著名特产之一。主要产地在信阳市浉河区、平桥区和罗山县。', 198.00, 268.00, 90, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 1, '河南信阳', '炒青', '一级', 87, 1),
('六安瓜片', '六安瓜片，形似瓜子，色泽宝绿', '六安瓜片，中华传统历史名茶，中国十大名茶之一，简称瓜片、片茶，产自安徽省六安市大别山一带，唐称"庐州六安茶"，为名茶；明始称"六安瓜片"，为上品、极品茶；清为朝廷贡茶。', 258.00, 328.00, 70, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 1, '安徽六安', '烘青', '特级', 112, 1),
('正山小种', '武夷山正山小种红茶，松烟香，桂圆味', '正山小种，又称拉普山小种，属红茶类，与人工小种合称为小种红茶。首创于福建省崇安县（1989崇安撤县设市，更名为武夷山市）桐木地区。是世界上最早的红茶，亦称红茶鼻祖。', 268.00, 368.00, 80, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 2, '福建武夷山', '全发酵', '特级', 234, 1),
('祁门红茶', '祁门红茶，世界三大高香茶之一', '祁门红茶简称祁红，茶叶原料选用当地的中叶、中生种茶树"槠叶种"（又名祁门种）制作，是中国历史名茶，著名红茶精品。由安徽茶农创制于光绪年间，但史籍记载最早可追溯至唐朝陆羽的茶经。', 328.00, 428.00, 60, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 2, '安徽祁门', '全发酵', '特级', 189, 1),
('滇红', '云南滇红，金毫显露，汤色红艳', '滇红茶是云南红茶的统称，分为滇红工夫茶和滇红碎茶两种。滇红工夫茶芽叶肥壮，金毫显露，汤色红艳，香气高醇，滋味浓厚。', 188.00, 258.00, 150, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 2, '云南', '全发酵', '一级', 156, 1),
('金骏眉', '金骏眉，顶级红茶，金芽显露', '金骏眉是正山小种的分支，原产于福建省武夷山市桐木村。金骏眉之所以名贵，是因为全程都由制茶师傅手工制作，每500g金骏眉需要数万颗的茶叶鲜芽尖。', 588.00, 788.00, 40, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 2, '福建武夷山', '全发酵', '特级', 67, 1),
('铁观音', '安溪铁观音，香气浓郁，七泡有余香', '铁观音茶，中国传统名茶，属于青茶类，是中国十大名茶之一。原产于福建泉州市安溪县西坪镇，发现于1723—1735年。"铁观音"既是茶名，也是茶树品种名。', 188.00, 288.00, 150, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 3, '福建安溪', '半发酵', '一级', 89, 1),
('大红袍', '武夷山大红袍，岩茶之王', '大红袍，产于福建武夷山，属乌龙茶，品质优异。中国特种名茶。其外形条索紧结，色泽绿褐鲜润，冲泡后汤色橙黄明亮，叶片红绿相间。品质最突出之处是香气馥郁有兰花香，香高而持久，"岩韵"明显。', 488.00, 688.00, 50, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 3, '福建武夷山', '半发酵', '特级', 145, 1),
('冻顶乌龙', '台湾冻顶乌龙，清香甘醇', '冻顶乌龙茶，产地为台湾鹿谷乡凤凰村永隆村彰雅村（冻顶巷），茶区海拔约600-1000公尺，依以上三点为核心产区，被誉为"茶中圣品"。', 368.00, 468.00, 65, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 3, '台湾', '半发酵', '特级', 78, 1),
('白毫银针', '福鼎白毫银针，毫香清鲜，汤色淡黄', '白毫银针，创制于1796年，中国六大茶类之一的白茶，原产地在福建，主要产区为福鼎、政和、松溪、建阳等地，属有中国十大名茶的称号，素有茶中"美女"、"茶王"之美称。', 388.00, 488.00, 60, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 4, '福建福鼎', '微发酵', '特级', 67, 1),
('白牡丹', '白牡丹，绿叶夹银白色毫心，形似花朵', '白牡丹是采自大白茶树或水仙种的短小芽叶新梢的一芽一二叶制成的，是白茶中的上乘佳品。因其绿叶夹银白色毫心，形似花朵，冲泡后绿叶托着嫩芽，宛如蓓蕾初放，故得美名白牡丹。', 268.00, 358.00, 85, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 4, '福建福鼎', '微发酵', '一级', 92, 1),
('寿眉', '寿眉，白茶中的大众茶', '寿眉，乃以福鼎大白、福鼎白茶树制成的白茶。寿眉叶张稍肥嫩，芽叶连枝，无老梗，叶整卷如眉，香气清纯。其中用茶芽叶制成的毛茶称为"小白"，以区别于福鼎大白茶、政和大白茶茶树芽叶制成的"大白"毛茶。', 128.00, 198.00, 200, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 4, '福建福鼎', '微发酵', '二级', 234, 1),
('普洱茶', '云南普洱茶，陈香醇厚，越陈越香', '普洱茶主要产于云南省的西双版纳、临沧、普洱等地区。普洱茶讲究冲泡技巧和品饮艺术，其饮用方法丰富，既可清饮，也可混饮。普洱茶茶汤橙黄浓厚，香气高锐持久，香型独特，滋味浓醇，经久耐泡。', 328.00, 428.00, 120, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 5, '云南', '后发酵', '一级', 145, 1),
('安化黑茶', '湖南安化黑茶，金花茂盛', '安化黑茶，湖南省益阳市安化县特产，中国国家地理标志产品。安化黑茶是六大基本茶类之一，属于后发酵茶，主要产品以茯砖、黑砖、花砖、青砖、湘尖等产品为主。', 198.00, 268.00, 100, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 5, '湖南安化', '后发酵', '一级', 112, 1),
('六堡茶', '广西六堡茶，红浓陈醇', '六堡茶属黑茶类，选用苍梧县群体种、广西大中叶种及其分离、选育的品种、品系茶树的鲜叶为原料，按特定的工艺进行加工，具有独特品质特征的黑茶。', 228.00, 298.00, 90, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 5, '广西梧州', '后发酵', '一级', 98, 1),
('君山银针', '湖南君山银针，黄茶珍品', '君山银针是中国名茶之一。产于湖南岳阳洞庭湖中的君山，形细如针，故名君山银针。属于黄茶。其成品茶芽头茁壮，长短大小均匀，茶芽内面呈金黄色，外层白毫显露完整，而且包裹坚实，茶芽外形很象一根根银针，雅称"金镶玉"。', 458.00, 588.00, 45, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 6, '湖南岳阳', '轻发酵', '特级', 56, 1),
('蒙顶黄芽', '四川蒙顶黄芽，黄叶黄汤', '蒙顶黄芽，是芽形黄茶之一，产于四川省雅安市蒙顶山。蒙顶茶栽培始于西汉，距今已有二千年的历史，古时为贡品供历代皇帝享用，新中国成立后曾被评为全国十大名茶之一。', 388.00, 488.00, 55, '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"]', 6, '四川雅安', '轻发酵', '特级', 43, 1);

-- 插入用户数据（密码：admin123）
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `gender`, `role`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', '13800138000', '管理员', 1, 1, 1),
('zhangsan', '0192023a7bbd73250516f069df18b500', 'zhangsan@example.com', '13800138001', '张三', 1, 0, 1),
('lisi', '0192023a7bbd73250516f069df18b500', 'lisi@example.com', '13800138002', '李四', 2, 0, 1),
('wangwu', '0192023a7bbd73250516f069df18b500', 'wangwu@example.com', '13800138003', '王五', 1, 0, 1),
('zhaoliu', '0192023a7bbd73250516f069df18b500', 'zhaoliu@example.com', '13800138004', '赵六', 2, 0, 1),
('test', '0192023a7bbd73250516f069df18b500', 'test@example.com', '13800138005', '测试用户', 0, 0, 1);

-- 插入收货地址数据
INSERT INTO `address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `postal_code`, `is_default`) VALUES
(2, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001室', '100025', 1),
(2, '张三', '13800138001', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号', '200120', 0),
(3, '李四', '13800138002', '广东省', '广州市', '天河区', '天河路123号', '510000', 1),
(4, '王五', '13800138003', '浙江省', '杭州市', '西湖区', '文三路259号', '310012', 1),
(5, '赵六', '13800138004', '江苏省', '南京市', '鼓楼区', '中山路321号', '210008', 1);

-- 插入购物车数据
INSERT INTO `cart` (`user_id`, `product_id`, `quantity`) VALUES
(2, 1, 2),
(2, 6, 1),
(3, 2, 3),
(3, 7, 1),
(4, 3, 2),
(4, 10, 1);

-- 插入订单数据
INSERT INTO `order` (`order_no`, `user_id`, `address_id`, `total_amount`, `discount_amount`, `pay_amount`, `status`, `pay_type`, `pay_time`, `remark`) VALUES
('TEA202412010001', 2, 1, 566.00, 0.00, 566.00, 1, 1, '2024-12-01 10:30:00', '请尽快发货'),
('TEA202412010002', 3, 3, 594.00, 0.00, 594.00, 2, 2, '2024-12-01 14:20:00', ''),
('TEA202412010003', 4, 4, 456.00, 0.00, 456.00, 3, 1, '2024-12-01 09:15:00', ''),
('TEA202412010004', 2, 1, 268.00, 0.00, 268.00, 0, 0, NULL, '');

-- 插入订单项数据
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, '西湖龙井', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 298.00, 1, 298.00),
(1, 6, '正山小种', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 268.00, 1, 268.00),
(2, 2, '碧螺春', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 268.00, 2, 536.00),
(2, 7, '祁门红茶', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 328.00, 1, 328.00),
(3, 3, '黄山毛峰', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 228.00, 2, 456.00),
(4, 6, '正山小种', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 268.00, 1, 268.00);

-- 插入物流信息数据
INSERT INTO `logistics` (`order_id`, `logistics_no`, `company`, `status`, `detail`) VALUES
(2, 'SF1234567890', '顺丰速运', '运输中', '[{"time":"2024-12-01 15:00:00","status":"已发货","location":"广州分拨中心"},{"time":"2024-12-01 18:30:00","status":"运输中","location":"广州-上海途中"},{"time":"2024-12-02 10:20:00","status":"派送中","location":"上海浦东新区"}]'),
(3, 'YT9876543210', '圆通速递', '已签收', '[{"time":"2024-12-01 10:00:00","status":"已发货","location":"杭州分拨中心"},{"time":"2024-12-01 16:00:00","status":"运输中","location":"杭州-南京途中"},{"time":"2024-12-02 09:00:00","status":"派送中","location":"南京鼓楼区"},{"time":"2024-12-02 14:30:00","status":"已签收","location":"南京市鼓楼区"}]');

-- 商家扩展字段
ALTER TABLE `user`
    MODIFY COLUMN `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员 2-商家',
    ADD COLUMN IF NOT EXISTS `merchant_name` VARCHAR(100) NULL COMMENT '店铺名称' AFTER `role`,
    ADD COLUMN IF NOT EXISTS `contact_name` VARCHAR(50) NULL COMMENT '联系人姓名' AFTER `merchant_name`,
    ADD COLUMN IF NOT EXISTS `contact_phone` VARCHAR(20) NULL COMMENT '联系电话' AFTER `contact_name`,
    ADD COLUMN IF NOT EXISTS `business_scope` VARCHAR(255) NULL COMMENT '主营类目' AFTER `contact_phone`,
    ADD COLUMN IF NOT EXISTS `merchant_intro` VARCHAR(500) NULL COMMENT '店铺简介' AFTER `business_scope`,
    ADD COLUMN IF NOT EXISTS `merchant_apply_status` TINYINT DEFAULT 0 COMMENT '商家申请状态：0-未申请 1-待审核 2-已通过 3-已驳回' AFTER `merchant_intro`,
    ADD COLUMN IF NOT EXISTS `merchant_apply_reject_reason` VARCHAR(255) NULL COMMENT '商家申请驳回原因' AFTER `merchant_apply_status`,
    ADD COLUMN IF NOT EXISTS `merchant_apply_time` DATETIME NULL COMMENT '商家申请时间' AFTER `merchant_apply_reject_reason`,
    ADD COLUMN IF NOT EXISTS `merchant_review_time` DATETIME NULL COMMENT '商家审核时间' AFTER `merchant_apply_time`;

ALTER TABLE `product`
    ADD COLUMN IF NOT EXISTS `seller_id` BIGINT DEFAULT 1 COMMENT '商家ID' AFTER `status`;

ALTER TABLE `order_item`
    ADD COLUMN IF NOT EXISTS `seller_id` BIGINT DEFAULT 1 COMMENT '商家ID' AFTER `subtotal`;

-- 评论表
CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_item_id` BIGINT NOT NULL COMMENT '订单项ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) COMMENT '商品名称',
    `product_image` VARCHAR(255) COMMENT '商品图片',
    `rating` TINYINT NOT NULL COMMENT '评分：1-5星',
    `content` TEXT COMMENT '评论内容',
    `images` TEXT COMMENT '评论图片，JSON格式',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
    `reply` TEXT COMMENT '商家回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表';

-- 退款申请表
CREATE TABLE IF NOT EXISTS `refund` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `refund_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '退款单号',
    `refund_amount` DECIMAL(10, 2) NOT NULL COMMENT '退款金额',
    `type` TINYINT NOT NULL COMMENT '退款类型：1-仅退款 2-退货退款',
    `reason` VARCHAR(200) COMMENT '退款原因',
    `description` TEXT COMMENT '退款说明',
    `images` TEXT COMMENT '凭证图片，JSON格式',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-已同意 2-已拒绝 3-退款中 4-已退款 5-已取消',
    `reject_reason` VARCHAR(500) COMMENT '拒绝原因',
    `logistics_no` VARCHAR(50) COMMENT '退货物流单号',
    `handler_id` BIGINT COMMENT '处理人ID',
    `handle_time` VARCHAR(50) COMMENT '处理时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_refund_no` (`refund_no`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

