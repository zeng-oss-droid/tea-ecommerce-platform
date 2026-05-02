package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Logistics;
import org.apache.ibatis.annotations.Mapper;

/** 物流表，一行通常对应一个订单的物流记录。 */
@Mapper
public interface LogisticsMapper {
    Logistics selectByOrderId(Long orderId);
    int insert(Logistics logistics);
    int update(Logistics logistics);
}

