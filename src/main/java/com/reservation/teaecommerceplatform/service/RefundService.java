package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Refund;

import java.util.List;
import java.util.Map;

/**
 * 退款售后：买家侧申请与查询；管理员全量；商家处理与自己订单相关的退款。
 */
public interface RefundService {
    Refund createRefund(Long userId, Long orderId, Integer type, String reason, String description, String images);
    List<Refund> getMyRefunds(Long userId);
    Refund getRefundDetail(Long refundId);
    Map<String, Object> getAllRefunds(Integer pageNum, Integer pageSize, String keyword, Integer status);
    void approveRefund(Long refundId, Long handlerId);
    void rejectRefund(Long refundId, Long handlerId, String rejectReason);
    void completeRefund(Long refundId, Long handlerId);
    void cancelRefund(Long refundId);
    void updateLogisticsNo(Long refundId, String logisticsNo);
    void confirmReceive(Long refundId, Long handlerId);
    Map<String, Object> getMerchantRefunds(Long sellerId, Integer pageNum, Integer pageSize, String keyword, Integer status);
    void approveMerchantRefund(Long refundId, Long sellerId, Long handlerId);
    void rejectMerchantRefund(Long refundId, Long sellerId, Long handlerId, String rejectReason);
    void completeMerchantRefund(Long refundId, Long sellerId, Long handlerId);
    void confirmMerchantReceive(Long refundId, Long sellerId, Long handlerId);
}

