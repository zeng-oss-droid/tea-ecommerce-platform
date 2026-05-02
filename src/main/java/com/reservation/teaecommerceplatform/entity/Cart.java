package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 购物车行：用户 + 商品 + 数量；下单时可按选中行生成订单。 */
@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity; // 商品数量
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

