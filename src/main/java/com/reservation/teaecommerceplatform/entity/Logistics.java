package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 订单物流轨迹；{@code detail} 可存 JSON 轨迹列表。 */
@Data
public class Logistics {
    private Long id;
    private Long orderId;
    private String logisticsNo; // 物流单号
    private String company; // 物流公司
    private String status; // 物流状态
    private String detail; // 物流详情，JSON格式
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

