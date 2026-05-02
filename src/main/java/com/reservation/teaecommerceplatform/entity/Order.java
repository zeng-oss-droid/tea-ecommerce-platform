package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表：一笔订单对应多行 {@link OrderItem}；状态涵盖支付、发货、完成、取消及退款相关。
 */
@Data
public class Order {
    private Long id;
    private String orderNo; // 订单号
    private Long userId;
    private Long addressId;
    private BigDecimal totalAmount; // 订单总金额
    private BigDecimal discountAmount; // 优惠金额
    private BigDecimal payAmount; // 实付金额
    private Integer status; // 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-退款中 6-已退款
    private Integer payType; // 0-未支付 1-支付宝 2-微信 3-银行卡
    private String payTime; // 支付时间
    private String logisticsNo; // 物流单号
    private String remark; // 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

