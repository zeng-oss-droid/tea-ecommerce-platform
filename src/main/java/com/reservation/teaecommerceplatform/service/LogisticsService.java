package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Logistics;

/** 订单维度物流记录。 */
public interface LogisticsService {
    Logistics getByOrderId(Long orderId);
    void add(Logistics logistics);
    void update(Logistics logistics);
}

