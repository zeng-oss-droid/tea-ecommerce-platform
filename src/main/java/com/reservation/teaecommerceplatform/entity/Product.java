package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 商品 SPU；{@code sellerId} 归属商家；{@code images} 多为 JSON 字符串。 */
@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private String detail; // 商品详情
    private BigDecimal price;
    private BigDecimal originalPrice; // 原价
    private Integer stock; // 库存
    private String images; // 商品图片，JSON格式存储
    private Long categoryId;
    private String origin; // 产地
    private String process; // 工艺
    private String grade; // 等级
    private Integer sales; // 销量
    private Integer status; // 0-下架 1-上架
    private Long sellerId; // 商家ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

