package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 售后退款单：与用户订单、管理员/商家处理人、物流退货信息关联。 */
@Data
public class Refund {
    private Long id;
    private Long orderId; // 订单ID
    private Long userId; // 用户ID
    private String orderNo; // 订单号
    private String refundNo; // 退款单号
    private BigDecimal refundAmount; // 退款金额
    private Integer type; // 退款类型：1-仅退款 2-退货退款
    private String reason; // 退款原因
    private String description; // 退款说明
    private String images; // 凭证图片，JSON格式
    private Integer status; // 状态：0-待处理 1-已同意 2-已拒绝 3-退款中 4-已退款 5-已取消
    private String rejectReason; // 拒绝原因
    private String logisticsNo; // 退货物流单号
    private Long handlerId; // 处理人ID
    private String handleTime; // 处理时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

