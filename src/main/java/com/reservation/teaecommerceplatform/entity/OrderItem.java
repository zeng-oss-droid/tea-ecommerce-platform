package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 订单明细行：快照商品名/图/单价，并记录 {@code sellerId} 便于商家维度统计与售后。 */
@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal; // 小计
    private Long sellerId; // 商家ID
    private LocalDateTime createTime;
}

