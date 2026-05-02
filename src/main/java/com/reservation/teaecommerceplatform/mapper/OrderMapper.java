package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 订单主表：用户订单、后台全量、按卖家、统计用聚合接口。 */
@Mapper
public interface OrderMapper {
    Order selectById(Long id);
    Order selectByOrderNo(String orderNo);
    List<Order> selectByUserId(Long userId);
    int insert(Order order);
    int update(Order order);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    List<Order> selectAll(@Param("keyword") String keyword, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countAll(@Param("keyword") String keyword, @Param("status") Integer status);
    List<Order> selectAllBySellerId(@Param("sellerId") Long sellerId, @Param("keyword") String keyword, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countAllBySellerId(@Param("sellerId") Long sellerId, @Param("keyword") String keyword, @Param("status") Integer status);
    int countTotal();
    java.math.BigDecimal sumTotalSales();
    int countByStatus(@Param("status") Integer status);
}

