/*
 Navicat Premium Data Transfer

 Source Server         : 2261200222
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : localhost:3306
 Source Schema         : tea_ecommerce

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 08/02/2026 15:00:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `postal_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮编',
  `is_default` tinyint(0) NULL DEFAULT 0 COMMENT '是否默认：0-否 1-是',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 2, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001室', '100025', 0, '2025-11-27 13:55:47', '2025-11-27 14:07:32');
INSERT INTO `address` VALUES (2, 2, '张三', '13800138001', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号', '200120', 1, '2025-11-27 13:55:47', '2025-11-27 14:07:32');
INSERT INTO `address` VALUES (3, 3, '李四', '13800138002', '广东省', '广州市', '天河区', '天河路123号', '510000', 1, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `address` VALUES (4, 4, '王五', '13800138003', '浙江省', '杭州市', '西湖区', '文三路259号', '310012', 1, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `address` VALUES (5, 5, '赵六', '13800138004', '江苏省', '南京市', '鼓楼区', '中山路321号', '210008', 1, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `address` VALUES (6, 1, '123', '18812312312', '123', '123', '123', '123', '', 1, '2025-11-30 14:23:01', '2025-11-30 14:34:27');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `product_id` bigint(0) NOT NULL COMMENT '商品ID',
  `quantity` int(0) NULL DEFAULT 1 COMMENT '商品数量',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id`, `product_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (3, 3, 2, 3, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `cart` VALUES (4, 3, 7, 1, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `cart` VALUES (5, 4, 3, 2, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `cart` VALUES (6, 4, 10, 1, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `cart` VALUES (8, 6, 15, 1, '2025-11-27 14:03:26', '2025-11-27 14:03:26');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图片',
  `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '绿茶', '绿茶是中国的主要茶类之一，具有清汤绿叶的品质特点', 'http://localhost:8080/uploads/bf82f1f3-8455-4661-8df8-bc7d44f63e9c.png', 0, 1, 1, '2025-11-27 13:55:47', '2025-11-30 16:16:58');
INSERT INTO `category` VALUES (2, '红茶', '红茶属全发酵茶，具有红茶、红汤、红叶和香甜味醇的特征', 'http://localhost:8080/uploads/24570b7f-1582-47c5-a1f7-655507b1de55.png', 0, 2, 1, '2025-11-27 13:55:47', '2025-11-30 16:17:03');
INSERT INTO `category` VALUES (3, '乌龙茶', '乌龙茶亦称青茶，属于半发酵茶，既有绿茶的清香，又有红茶的醇厚', 'http://localhost:8080/uploads/8dc9bf85-f935-4f4c-ba62-aeff1e3811e6.png', 0, 3, 1, '2025-11-27 13:55:47', '2025-11-30 16:17:10');
INSERT INTO `category` VALUES (4, '白茶', '白茶属微发酵茶，具有外形芽毫完整、满身披毫、毫香清鲜的特点', 'http://localhost:8080/uploads/09b3fba0-1e70-4b0f-a51d-f3b1b6532df3.png', 0, 4, 1, '2025-11-27 13:55:47', '2025-11-30 16:17:15');
INSERT INTO `category` VALUES (5, '黑茶', '黑茶属后发酵茶，具有越陈越香的特点', 'http://localhost:8080/uploads/f118b1ee-f06b-4c4e-a096-750d283e7059.png', 0, 5, 1, '2025-11-27 13:55:47', '2025-11-30 16:17:20');
INSERT INTO `category` VALUES (6, '黄茶', '黄茶属轻发酵茶类，具有黄叶黄汤的特点', 'http://localhost:8080/uploads/8f5616e6-ac9d-4938-8308-7c1bef678b93.png', 0, 6, 1, '2025-11-27 13:55:47', '2025-11-30 16:19:42');

-- ----------------------------
-- Table structure for logistics
-- ----------------------------
DROP TABLE IF EXISTS `logistics`;
CREATE TABLE `logistics`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `logistics_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流单号',
  `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流公司',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流状态',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '物流详情，JSON格式',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物流信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of logistics
-- ----------------------------
INSERT INTO `logistics` VALUES (1, 2, 'SF1234567890', '顺丰速运', '运输中', '[{\"time\":\"2024-12-01 15:00:00\",\"status\":\"已发货\",\"location\":\"广州分拨中心\"},{\"time\":\"2024-12-01 18:30:00\",\"status\":\"运输中\",\"location\":\"广州-上海途中\"},{\"time\":\"2024-12-02 10:20:00\",\"status\":\"派送中\",\"location\":\"上海浦东新区\"}]', '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `logistics` VALUES (2, 3, 'YT9876543210', '圆通速递', '已签收', '[{\"time\":\"2024-12-01 10:00:00\",\"status\":\"已发货\",\"location\":\"杭州分拨中心\"},{\"time\":\"2024-12-01 16:00:00\",\"status\":\"运输中\",\"location\":\"杭州-南京途中\"},{\"time\":\"2024-12-02 09:00:00\",\"status\":\"派送中\",\"location\":\"南京鼓楼区\"},{\"time\":\"2024-12-02 14:30:00\",\"status\":\"已签收\",\"location\":\"南京市鼓楼区\"}]', '2025-11-27 13:55:47', '2025-11-27 13:55:47');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `address_id` bigint(0) NOT NULL COMMENT '收货地址ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-退款中 6-已退款',
  `pay_type` tinyint(0) NULL DEFAULT 0 COMMENT '支付方式：0-未支付 1-支付宝 2-微信 3-银行卡',
  `pay_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付时间',
  `logistics_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流单号',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 'TEA202412010001', 2, 1, 566.00, 0.00, 566.00, 2, 1, '2024-12-01 10:30:00', NULL, '请尽快发货', '2025-11-27 13:55:47', '2025-11-30 13:51:56');
INSERT INTO `order` VALUES (2, 'TEA202412010002', 3, 3, 594.00, 0.00, 594.00, 2, 2, '2024-12-01 14:20:00', NULL, '', '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `order` VALUES (3, 'TEA202412010003', 4, 4, 456.00, 0.00, 456.00, 3, 1, '2024-12-01 09:15:00', NULL, '', '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `order` VALUES (4, 'TEA202412010004', 2, 1, 268.00, 0.00, 268.00, 2, 1, '2025-11-27 14:01:06', NULL, '', '2025-11-27 13:55:47', '2025-11-30 13:51:58');
INSERT INTO `order` VALUES (5, 'TEA17642237960297E51E6', 2, 1, 396.00, 0.00, 396.00, 4, 0, NULL, NULL, '', '2025-11-27 14:09:56', '2025-11-27 14:10:04');
INSERT INTO `order` VALUES (6, 'TEA1764223970812D909D7', 2, 2, 456.00, 0.00, 456.00, 2, 1, '2025-11-27 14:12:57', NULL, '', '2025-11-27 14:12:50', '2025-11-27 14:42:10');
INSERT INTO `order` VALUES (7, 'TEA1764484474706282B65', 1, 6, 128.00, 0.00, 128.00, 6, 1, '2025-11-30 14:34:38', NULL, '', '2025-11-30 14:34:34', '2025-11-30 15:03:47');
INSERT INTO `order` VALUES (8, 'TEA1764485005003082C62', 1, 6, 328.00, 0.00, 328.00, 3, 1, '2025-11-30 14:43:26', NULL, '', '2025-11-30 14:43:25', '2025-11-30 14:43:42');
INSERT INTO `order` VALUES (9, 'TEA1764486250856DD7253', 1, 6, 456.00, 0.00, 456.00, 3, 1, '2025-11-30 15:04:14', NULL, '', '2025-11-30 15:04:10', '2025-11-30 15:04:32');
INSERT INTO `order` VALUES (10, 'TEA1764486366791C90B6C', 1, 6, 594.00, 0.00, 594.00, 3, 1, '2025-11-30 15:06:09', NULL, '', '2025-11-30 15:06:06', '2025-11-30 15:06:19');
INSERT INTO `order` VALUES (11, 'TEA1764496310576F1B59B', 1, 6, 328.00, 0.00, 328.00, 0, 0, NULL, NULL, '', '2025-11-30 17:51:50', '2025-11-30 17:51:50');
INSERT INTO `order` VALUES (12, 'TEA1764497922533ED66AC', 1, 6, 2185.00, 0.00, 2185.00, 3, 1, '2025-11-30 18:18:46', NULL, '', '2025-11-30 18:18:42', '2025-11-30 18:19:32');
INSERT INTO `order` VALUES (13, 'TEA17644979470265DDF83', 1, 6, 488.00, 0.00, 488.00, 3, 1, '2025-11-30 18:19:08', NULL, '', '2025-11-30 18:19:07', '2025-11-30 18:19:31');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `product_id` bigint(0) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int(0) NOT NULL COMMENT '数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, '西湖龙井', '[\"http://localhost:8080/uploads/142b74db-42f9-436f-a234-e0497d11a1ef.png\",\"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400\"]', 298.00, 1, 298.00, '2025-11-27 13:55:47');
INSERT INTO `order_item` VALUES (2, 1, 6, '正山小种', '[\"http://localhost:8080/uploads/9a8b24a5-94ab-4920-8ea9-a9d5e76d6d60.png\"]', 268.00, 1, 268.00, '2025-11-27 13:55:47');
INSERT INTO `order_item` VALUES (3, 2, 2, '碧螺春', '[\"http://localhost:8080/uploads/142a7a83-6aca-4d2e-97cb-17b68cb8b86e.png\"]', 268.00, 2, 536.00, '2025-11-27 13:55:47');
INSERT INTO `order_item` VALUES (4, 2, 7, '祁门红茶', '[\"http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png\"]', 328.00, 1, 328.00, '2025-11-27 13:55:47');
INSERT INTO `order_item` VALUES (5, 3, 3, '黄山毛峰', '[\"http://localhost:8080/uploads/d884309b-658f-4ee8-9aaf-23d7098a8b28.png\"]', 228.00, 2, 456.00, '2025-11-27 13:55:47');
INSERT INTO `order_item` VALUES (6, 4, 6, '正山小种', '[\"http://localhost:8080/uploads/9a8b24a5-94ab-4920-8ea9-a9d5e76d6d60.png\"]', 268.00, 1, 268.00, '2025-11-27 13:55:47');
INSERT INTO `order_item` VALUES (7, 5, 15, '寿眉', '[\"http://localhost:8080/uploads/84b7abf2-8650-41b9-8970-296f305ecdb1.png\"]', 128.00, 1, 128.00, '2025-11-27 14:09:56');
INSERT INTO `order_item` VALUES (8, 5, 6, '正山小种', '[\"http://localhost:8080/uploads/9a8b24a5-94ab-4920-8ea9-a9d5e76d6d60.png\"]', 268.00, 1, 268.00, '2025-11-27 14:09:56');
INSERT INTO `order_item` VALUES (9, 6, 7, '祁门红茶', '[\"http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png\"]', 328.00, 1, 328.00, '2025-11-27 14:12:50');
INSERT INTO `order_item` VALUES (10, 6, 15, '寿眉', '[\"http://localhost:8080/uploads/84b7abf2-8650-41b9-8970-296f305ecdb1.png\"]', 128.00, 1, 128.00, '2025-11-27 14:12:50');
INSERT INTO `order_item` VALUES (11, 7, 15, '寿眉', '[\"http://localhost:8080/uploads/84b7abf2-8650-41b9-8970-296f305ecdb1.png\"]', 128.00, 1, 128.00, '2025-11-30 14:34:34');
INSERT INTO `order_item` VALUES (12, 8, 7, '祁门红茶', '[\"http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png\"]', 328.00, 1, 328.00, '2025-11-30 14:43:25');
INSERT INTO `order_item` VALUES (13, 9, 7, '祁门红茶', '[\"http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png\"]', 328.00, 1, 328.00, '2025-11-30 15:04:10');
INSERT INTO `order_item` VALUES (14, 9, 15, '寿眉', '[\"http://localhost:8080/uploads/84b7abf2-8650-41b9-8970-296f305ecdb1.png\"]', 128.00, 1, 128.00, '2025-11-30 15:04:10');
INSERT INTO `order_item` VALUES (15, 10, 1, '西湖龙井', '[\"http://localhost:8080/uploads/142b74db-42f9-436f-a234-e0497d11a1ef.png\",\"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400\"]', 297.00, 2, 594.00, '2025-11-30 15:06:06');
INSERT INTO `order_item` VALUES (16, 11, 7, '祁门红茶', '[\"http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png\"]', 328.00, 1, 328.00, '2025-11-30 17:51:50');
INSERT INTO `order_item` VALUES (17, 12, 11, '大红袍', 'http://localhost:8080/uploads/cab37a99-7aff-4c9a-b708-926dfd347deb.png', 488.00, 2, 976.00, '2025-11-30 18:18:42');
INSERT INTO `order_item` VALUES (18, 12, 8, '滇红', 'http://localhost:8080/uploads/d41d94af-24ea-4b29-ba5f-8784803782e8.png', 188.00, 1, 188.00, '2025-11-30 18:18:42');
INSERT INTO `order_item` VALUES (19, 12, 1, '西湖龙井', 'http://localhost:8080/uploads/142b74db-42f9-436f-a234-e0497d11a1ef.png', 297.00, 1, 297.00, '2025-11-30 18:18:42');
INSERT INTO `order_item` VALUES (20, 12, 6, '正山小种', 'http://localhost:8080/uploads/9a8b24a5-94ab-4920-8ea9-a9d5e76d6d60.png', 268.00, 1, 268.00, '2025-11-30 18:18:42');
INSERT INTO `order_item` VALUES (21, 12, 15, '寿眉', 'http://localhost:8080/uploads/84b7abf2-8650-41b9-8970-296f305ecdb1.png', 128.00, 1, 128.00, '2025-11-30 18:18:42');
INSERT INTO `order_item` VALUES (22, 12, 7, '祁门红茶', 'http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png', 328.00, 1, 328.00, '2025-11-30 18:18:42');
INSERT INTO `order_item` VALUES (23, 13, 11, '大红袍', 'http://localhost:8080/uploads/cab37a99-7aff-4c9a-b708-926dfd347deb.png', 488.00, 1, 488.00, '2025-11-30 18:19:07');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品描述',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品详情',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `stock` int(0) NULL DEFAULT 0 COMMENT '库存',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品图片，JSON格式存储',
  `category_id` bigint(0) NULL DEFAULT NULL COMMENT '分类ID',
  `origin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产地',
  `process` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工艺',
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '等级',
  `sales` int(0) NULL DEFAULT 0 COMMENT '销量',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-下架 1-上架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '西湖龙井', '正宗西湖龙井，清香甘甜，回甘持久', '西湖龙井茶，中国十大名茶之一，属绿茶，产于浙江省杭州市西湖龙井村周围群山。具有1200多年历史。清乾隆游览杭州西湖时，盛赞西湖龙井茶，把狮峰山下胡公庙前的十八棵茶树封为\"御茶\"。', 297.00, 398.00, 97, '[\"http://localhost:8080/uploads/142b74db-42f9-436f-a234-e0497d11a1ef.png\",\"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400\"]', 1, '浙江杭州', '炒青', '特级', 156, 1, '2025-11-27 13:55:47', '2025-11-30 18:18:42');
INSERT INTO `product` VALUES (2, '碧螺春', '洞庭碧螺春，香气浓郁，滋味鲜醇', '碧螺春是中国传统名茶，中国十大名茶之一，属于绿茶类，已有1000多年历史。碧螺春产于江苏省苏州市吴县太湖的东洞庭山及西洞庭山（今苏州吴中区）一带，所以又称\"洞庭碧螺春\"。', 268.00, 358.00, 80, '[\"http://localhost:8080/uploads/142a7a83-6aca-4d2e-97cb-17b68cb8b86e.png\"]', 1, '江苏苏州', '炒青', '特级', 98, 1, '2025-11-27 13:55:47', '2025-11-30 15:54:46');
INSERT INTO `product` VALUES (3, '黄山毛峰', '黄山毛峰，形似雀舌，香如白兰', '黄山毛峰是中国十大名茶之一，属于绿茶。产于安徽省黄山（徽州）一带，所以又称徽茶。由清代光绪年间谢裕大茶庄所创制。', 228.00, 298.00, 120, '[\"http://localhost:8080/uploads/d884309b-658f-4ee8-9aaf-23d7098a8b28.png\"]', 1, '安徽黄山', '烘青', '一级', 134, 1, '2025-11-27 13:55:47', '2025-11-30 15:55:00');
INSERT INTO `product` VALUES (4, '信阳毛尖', '信阳毛尖，细圆光直，香高味浓', '信阳毛尖又称豫毛峰，属绿茶类，是中国十大名茶之一，也是河南省著名特产之一。主要产地在信阳市浉河区、平桥区和罗山县。', 198.00, 268.00, 90, '[\"http://localhost:8080/uploads/c565daea-b17e-4fa9-855c-6ee8dc4b75b0.png\"]', 1, '河南信阳', '炒青', '一级', 87, 1, '2025-11-27 13:55:47', '2025-11-30 16:06:28');
INSERT INTO `product` VALUES (5, '六安瓜片', '六安瓜片，形似瓜子，色泽宝绿', '六安瓜片，中华传统历史名茶，中国十大名茶之一，简称瓜片、片茶，产自安徽省六安市大别山一带，唐称\"庐州六安茶\"，为名茶；明始称\"六安瓜片\"，为上品、极品茶；清为朝廷贡茶。', 258.00, 328.00, 70, '[\"http://localhost:8080/uploads/ff97258b-50b9-4373-aa46-5e19cf23be76.png\"]', 1, '安徽六安', '烘青', '特级', 112, 1, '2025-11-27 13:55:47', '2025-11-30 16:06:33');
INSERT INTO `product` VALUES (6, '正山小种', '武夷山正山小种红茶，松烟香，桂圆味', '正山小种，又称拉普山小种，属红茶类，与人工小种合称为小种红茶。首创于福建省崇安县（1989崇安撤县设市，更名为武夷山市）桐木地区。是世界上最早的红茶，亦称红茶鼻祖。', 268.00, 368.00, 79, '[\"http://localhost:8080/uploads/9a8b24a5-94ab-4920-8ea9-a9d5e76d6d60.png\"]', 2, '福建武夷山', '全发酵', '特级', 234, 1, '2025-11-27 13:55:47', '2025-11-30 18:18:42');
INSERT INTO `product` VALUES (7, '祁门红茶', '祁门红茶，世界三大高香茶之一', '祁门红茶简称祁红，茶叶原料选用当地的中叶、中生种茶树\"槠叶种\"（又名祁门种）制作，是中国历史名茶，著名红茶精品。由安徽茶农创制于光绪年间，但史籍记载最早可追溯至唐朝陆羽的茶经。', 328.00, 428.00, 55, '[\"http://localhost:8080/uploads/160d71a3-ef09-4373-8e4e-d36e3e652fda.png\"]', 2, '安徽祁门', '全发酵', '特级', 189, 1, '2025-11-27 13:55:47', '2025-11-30 18:18:42');
INSERT INTO `product` VALUES (8, '滇红', '云南滇红，金毫显露，汤色红艳', '滇红茶是云南红茶的统称，分为滇红工夫茶和滇红碎茶两种。滇红工夫茶芽叶肥壮，金毫显露，汤色红艳，香气高醇，滋味浓厚。', 188.00, 258.00, 149, '[\"http://localhost:8080/uploads/d41d94af-24ea-4b29-ba5f-8784803782e8.png\"]', 2, '云南', '全发酵', '一级', 156, 1, '2025-11-27 13:55:47', '2025-11-30 18:18:42');
INSERT INTO `product` VALUES (9, '金骏眉', '金骏眉，顶级红茶，金芽显露', '金骏眉是正山小种的分支，原产于福建省武夷山市桐木村。金骏眉之所以名贵，是因为全程都由制茶师傅手工制作，每500g金骏眉需要数万颗的茶叶鲜芽尖。', 588.00, 788.00, 40, '[\"http://localhost:8080/uploads/57a02c4c-bd91-4df8-b825-b30254217067.png\"]', 2, '福建武夷山', '全发酵', '特级', 67, 1, '2025-11-27 13:55:47', '2025-11-30 15:55:33');
INSERT INTO `product` VALUES (10, '铁观音', '安溪铁观音，香气浓郁，七泡有余香', '铁观音茶，中国传统名茶，属于青茶类，是中国十大名茶之一。原产于福建泉州市安溪县西坪镇，发现于1723—1735年。\"铁观音\"既是茶名，也是茶树品种名。', 188.00, 288.00, 150, '[\"http://localhost:8080/uploads/ddcf3f57-064e-4256-ba41-dac9c298212b.png\"]', 3, '福建安溪', '半发酵', '一级', 89, 1, '2025-11-27 13:55:47', '2025-11-30 16:06:42');
INSERT INTO `product` VALUES (11, '大红袍', '武夷山大红袍，岩茶之王', '大红袍，产于福建武夷山，属乌龙茶，品质优异。中国特种名茶。其外形条索紧结，色泽绿褐鲜润，冲泡后汤色橙黄明亮，叶片红绿相间。品质最突出之处是香气馥郁有兰花香，香高而持久，\"岩韵\"明显。', 488.00, 688.00, 47, '[\"http://localhost:8080/uploads/cab37a99-7aff-4c9a-b708-926dfd347deb.png\"]', 3, '福建武夷山', '半发酵', '特级', 145, 1, '2025-11-27 13:55:47', '2025-11-30 18:19:07');
INSERT INTO `product` VALUES (12, '冻顶乌龙', '台湾冻顶乌龙，清香甘醇', '冻顶乌龙茶，产地为台湾鹿谷乡凤凰村永隆村彰雅村（冻顶巷），茶区海拔约600-1000公尺，依以上三点为核心产区，被誉为\"茶中圣品\"。', 368.00, 468.00, 65, '[\"http://localhost:8080/uploads/ad1606e3-0457-40f2-8a34-9c32cc573aec.png\"]', 3, '台湾', '半发酵', '特级', 78, 1, '2025-11-27 13:55:47', '2025-11-30 16:06:47');
INSERT INTO `product` VALUES (13, '白毫银针', '福鼎白毫银针，毫香清鲜，汤色淡黄', '白毫银针，创制于1796年，中国六大茶类之一的白茶，原产地在福建，主要产区为福鼎、政和、松溪、建阳等地，属有中国十大名茶的称号，素有茶中\"美女\"、\"茶王\"之美称。', 388.00, 488.00, 60, '[\"http://localhost:8080/uploads/4e5e760c-9049-480f-a5ce-354bad6583f5.png\"]', 4, '福建福鼎', '微发酵', '特级', 67, 1, '2025-11-27 13:55:47', '2025-11-30 15:55:44');
INSERT INTO `product` VALUES (14, '白牡丹', '白牡丹，绿叶夹银白色毫心，形似花朵', '白牡丹是采自大白茶树或水仙种的短小芽叶新梢的一芽一二叶制成的，是白茶中的上乘佳品。因其绿叶夹银白色毫心，形似花朵，冲泡后绿叶托着嫩芽，宛如蓓蕾初放，故得美名白牡丹。', 268.00, 358.00, 85, '[\"http://localhost:8080/uploads/ec83691f-679b-45fa-82b7-d1c8789676c9.png\"]', 4, '福建福鼎', '微发酵', '一级', 92, 1, '2025-11-27 13:55:47', '2025-11-30 16:06:51');
INSERT INTO `product` VALUES (15, '寿眉', '寿眉，白茶中的大众茶', '寿眉，乃以福鼎大白、福鼎白茶树制成的白茶。寿眉叶张稍肥嫩，芽叶连枝，无老梗，叶整卷如眉，香气清纯。其中用茶芽叶制成的毛茶称为\"小白\"，以区别于福鼎大白茶、政和大白茶茶树芽叶制成的\"大白\"毛茶。', 128.00, 198.00, 196, '[\"http://localhost:8080/uploads/84b7abf2-8650-41b9-8970-296f305ecdb1.png\"]', 4, '福建福鼎', '微发酵', '二级', 234, 1, '2025-11-27 13:55:47', '2025-11-30 18:18:42');
INSERT INTO `product` VALUES (16, '普洱茶', '云南普洱茶，陈香醇厚，越陈越香', '普洱茶主要产于云南省的西双版纳、临沧、普洱等地区。普洱茶讲究冲泡技巧和品饮艺术，其饮用方法丰富，既可清饮，也可混饮。普洱茶茶汤橙黄浓厚，香气高锐持久，香型独特，滋味浓醇，经久耐泡。', 328.00, 428.00, 120, '[\"http://localhost:8080/uploads/4bf402be-3d56-4467-94ed-d2532a3b9d56.png\"]', 5, '云南', '后发酵', '一级', 145, 1, '2025-11-27 13:55:47', '2025-11-30 16:07:00');
INSERT INTO `product` VALUES (17, '安化黑茶', '湖南安化黑茶，金花茂盛', '安化黑茶，湖南省益阳市安化县特产，中国国家地理标志产品。安化黑茶是六大基本茶类之一，属于后发酵茶，主要产品以茯砖、黑砖、花砖、青砖、湘尖等产品为主。', 198.00, 268.00, 100, '[\"http://localhost:8080/uploads/d9db3a3b-7a06-46e5-9b91-e6713af4aba5.png\"]', 5, '湖南安化', '后发酵', '一级', 112, 1, '2025-11-27 13:55:47', '2025-11-30 16:07:10');
INSERT INTO `product` VALUES (18, '六堡茶', '广西六堡茶，红浓陈醇', '六堡茶属黑茶类，选用苍梧县群体种、广西大中叶种及其分离、选育的品种、品系茶树的鲜叶为原料，按特定的工艺进行加工，具有独特品质特征的黑茶。', 228.00, 298.00, 90, '[\"http://localhost:8080/uploads/c1012939-e06f-4367-978e-8a0fcc552aa8.png\"]', 5, '广西梧州', '后发酵', '一级', 98, 1, '2025-11-27 13:55:47', '2025-11-30 16:07:51');
INSERT INTO `product` VALUES (19, '君山银针', '湖南君山银针，黄茶珍品', '君山银针是中国名茶之一。产于湖南岳阳洞庭湖中的君山，形细如针，故名君山银针。属于黄茶。其成品茶芽头茁壮，长短大小均匀，茶芽内面呈金黄色，外层白毫显露完整，而且包裹坚实，茶芽外形很象一根根银针，雅称\"金镶玉\"。', 458.00, 588.00, 45, '[\"http://localhost:8080/uploads/2d11153c-e914-4024-9b51-b015b0111992.png\"]', 6, '湖南岳阳', '轻发酵', '特级', 56, 1, '2025-11-27 13:55:47', '2025-11-30 16:07:59');
INSERT INTO `product` VALUES (20, '蒙顶黄芽', '四川蒙顶黄芽，黄叶黄汤', '蒙顶黄芽，是芽形黄茶之一，产于四川省雅安市蒙顶山。蒙顶茶栽培始于西汉，距今已有二千年的历史，古时为贡品供历代皇帝享用，新中国成立后曾被评为全国十大名茶之一。', 388.00, 488.00, 55, '[\"http://localhost:8080/uploads/cdee598d-b67c-46e8-98f9-34cab912d060.png\"]', 6, '四川雅安', '轻发酵', '特级', 43, 1, '2025-11-27 13:55:47', '2025-11-30 16:08:02');

-- ----------------------------
-- Table structure for refund
-- ----------------------------
DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `refund_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退款单号',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `type` tinyint(0) NOT NULL COMMENT '退款类型：1-仅退款 2-退货退款',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '退款说明',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '凭证图片，JSON格式',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态：0-待处理 1-已同意 2-已拒绝 3-退款中 4-已退款 5-已取消',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `logistics_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退货物流单号',
  `handler_id` bigint(0) NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `refund_no`(`refund_no`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_refund_no`(`refund_no`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund
-- ----------------------------
INSERT INTO `refund` VALUES (1, 7, 1, 'TEA1764484474706282B65', 'REF17644849671002273CE59', 128.00, 2, '不想要了', '', NULL, 4, NULL, '123', 1, '2025-11-30 15:03:47', '2025-11-30 14:42:47', '2025-11-30 15:03:47');

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `order_item_id` bigint(0) NOT NULL COMMENT '订单项ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `product_id` bigint(0) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `rating` tinyint(0) NOT NULL COMMENT '评分：1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论图片，JSON格式',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商家回复',
  `reply_time` datetime(0) NULL DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 8, 12, 1, 7, '祁门红茶', '[\"https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400\"]', 5, '哈哈哈哈哈', NULL, 1, NULL, NULL, '2025-11-30 14:43:46', '2025-11-30 14:43:46');
INSERT INTO `review` VALUES (2, 9, 14, 1, 15, '寿眉', '[\"https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400\"]', 1, '不好喝', NULL, 1, NULL, NULL, '2025-11-30 15:04:42', '2025-11-30 15:04:42');
INSERT INTO `review` VALUES (3, 9, 13, 1, 7, '祁门红茶', '[\"https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400\"]', 1, '不好喝', NULL, 1, NULL, NULL, '2025-11-30 15:04:43', '2025-11-30 15:04:43');
INSERT INTO `review` VALUES (4, 10, 15, 1, 1, '西湖龙井', '[\"https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400\"', 5, '哈哈哈哈哈', NULL, 1, NULL, NULL, '2025-11-30 15:06:23', '2025-11-30 15:06:23');
INSERT INTO `review` VALUES (5, 13, 23, 1, 11, '大红袍', 'http://localhost:8080/uploads/cab37a99-7aff-4c9a-b708-926dfd347deb.png', 5, '好喝', NULL, 1, NULL, NULL, '2025-11-30 18:20:07', '2025-11-30 18:20:07');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` tinyint(0) NULL DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `role` tinyint(0) NULL DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员 2-商家',
  `merchant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺名称',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `business_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主营类目',
  `merchant_intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺简介',
  `merchant_apply_status` tinyint(0) NULL DEFAULT 0 COMMENT '商家申请状态：0-未申请 1-待审核 2-已通过 3-已驳回',
  `merchant_apply_reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商家申请驳回原因',
  `merchant_apply_time` datetime(0) NULL DEFAULT NULL COMMENT '商家申请时间',
  `merchant_review_time` datetime(0) NULL DEFAULT NULL COMMENT '商家审核时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '4297f44b13955235245b2497399d7a93', 'admin@example.com', '13800138000', '嘻嘻', NULL, 1, 1, 1, NULL, NULL, NULL, NULL, NULL, '2025-11-27 13:55:47', '2025-11-30 14:57:35');
INSERT INTO `user` VALUES (2, 'zhangsan', '0192023a7bbd73250516f069df18b500', 'zhangsan@example.com', '13800138001', '张三', NULL, 1, 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `user` VALUES (3, 'lisi', '0192023a7bbd73250516f069df18b500', 'lisi@example.com', '13800138002', '李四', NULL, 2, 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `user` VALUES (4, 'wangwu', '0192023a7bbd73250516f069df18b500', 'wangwu@example.com', '13800138003', '王五', NULL, 1, 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `user` VALUES (5, 'zhaoliu', '0192023a7bbd73250516f069df18b500', 'zhaoliu@example.com', '13800138004', '赵六', NULL, 2, 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-11-27 13:55:47', '2025-11-27 13:55:47');
INSERT INTO `user` VALUES (6, 'test', '0192023a7bbd73250516f069df18b500', 'test@example.com', '13800138005', '测试用户', NULL, 0, 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-11-27 13:55:47', '2025-11-27 13:55:47');

SET FOREIGN_KEY_CHECKS = 1;
