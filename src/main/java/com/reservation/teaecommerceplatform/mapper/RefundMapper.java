package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 退款单表：用户维度、全站后台、按卖家（商家售后列表）。 */
@Mapper
public interface RefundMapper {
    Refund selectById(Long id);
    Refund selectByRefundNo(String refundNo);
    List<Refund> selectByUserId(Long userId);
    List<Refund> selectByOrderId(Long orderId);
    List<Refund> selectAll(@Param("keyword") String keyword, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countAll(@Param("keyword") String keyword, @Param("status") Integer status);
    List<Refund> selectAllBySellerId(@Param("sellerId") Long sellerId, @Param("keyword") String keyword, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countAllBySellerId(@Param("sellerId") Long sellerId, @Param("keyword") String keyword, @Param("status") Integer status);
    int insert(Refund refund);
    int update(Refund refund);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

