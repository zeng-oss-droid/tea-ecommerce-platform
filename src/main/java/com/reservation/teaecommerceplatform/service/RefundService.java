package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Refund;

import java.util.List;
import java.util.Map;

/**
 * 退款售后：买家侧申请与查询；管理员全量；商家处理与自己订单相关的退款。
 */
public interface RefundService {
    /** 买家提交退款申请 */
    Refund createRefund(Long userId, Long orderId, Integer type, String reason, String description, String images);
    /** 查询我的退款列表 */
    List<Refund> getMyRefunds(Long userId);
    /** 查询退款详情 */
    Refund getRefundDetail(Long refundId);
    /** 管理端分页查询全站退款 */
    Map<String, Object> getAllRefunds(Integer pageNum, Integer pageSize, String keyword, Integer status);
    /** 管理员同意退款 */
    void approveRefund(Long refundId, Long handlerId);
    /** 管理员驳回退款 */
    void rejectRefund(Long refundId, Long handlerId, String rejectReason);
    /** 管理员完成退款 */
    void completeRefund(Long refundId, Long handlerId);
    /** 买家取消退款申请 */
    void cancelRefund(Long refundId);
    /** 买家填写退货快递单号 */
    void updateLogisticsNo(Long refundId, String logisticsNo);
    /** 管理员确认收到退货 */
    void confirmReceive(Long refundId, Long handlerId);
    /** 商家分页查询本店退款 */
    Map<String, Object> getMerchantRefunds(Long sellerId, Integer pageNum, Integer pageSize, String keyword, Integer status);
    /** 商家同意退款 */
    void approveMerchantRefund(Long refundId, Long sellerId, Long handlerId);
    /** 商家驳回退款 */
    void rejectMerchantRefund(Long refundId, Long sellerId, Long handlerId, String rejectReason);
    /** 商家完成退款 */
    void completeMerchantRefund(Long refundId, Long sellerId, Long handlerId);
    /** 商家确认收到退货 */
    void confirmMerchantReceive(Long refundId, Long sellerId, Long handlerId);
}

