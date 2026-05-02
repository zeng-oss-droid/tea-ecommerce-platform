package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 订单明细表：批量插入、按订单查询、按卖家计数（商家订单权限）。 */
@Mapper
public interface OrderItemMapper {
    List<OrderItem> selectByOrderId(Long orderId);
    int countByOrderIdAndSellerId(@Param("orderId") Long orderId, @Param("sellerId") Long sellerId);
    int insert(OrderItem orderItem);
    int insertBatch(List<OrderItem> orderItems);
}

