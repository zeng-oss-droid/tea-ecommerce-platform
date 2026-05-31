package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.Order;
import com.reservation.teaecommerceplatform.entity.Refund;
import com.reservation.teaecommerceplatform.mapper.OrderItemMapper;
import com.reservation.teaecommerceplatform.mapper.OrderMapper;
import com.reservation.teaecommerceplatform.mapper.RefundMapper;
import com.reservation.teaecommerceplatform.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** 退款状态机：与订单、订单项、商家/管理员处理人联动。 */
@Service
public class RefundServiceImpl implements RefundService {
    
    @Autowired
    private RefundMapper refundMapper;
    
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;
    
    /** 买家提交退款申请 */
    @Override
    @Transactional
    public Refund createRefund(Long userId, Long orderId, Integer type, String reason, String description, String images) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        // 检查订单状态，只有已支付、已发货、已完成可以申请退款
        if (order.getStatus() == 0 || order.getStatus() == 4 || order.getStatus() == 5 || order.getStatus() == 6) {
            throw new RuntimeException("当前订单状态不允许申请退款");
        }
        
        // 检查是否已有退款申请
        List<Refund> existingRefunds = refundMapper.selectByOrderId(orderId);
        if (existingRefunds.stream().anyMatch(r -> r.getStatus() == 0 || r.getStatus() == 1 || r.getStatus() == 3)) {
            throw new RuntimeException("该订单已有处理中的退款申请");
        }
        
        Refund refund = new Refund();
        refund.setOrderId(orderId);
        refund.setUserId(userId);
        refund.setOrderNo(order.getOrderNo());
        refund.setRefundNo("REF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        refund.setRefundAmount(order.getPayAmount());
        refund.setType(type);
        refund.setReason(reason);
        refund.setDescription(description);
        refund.setImages(images);
        refund.setStatus(0); // 待处理
        
        refundMapper.insert(refund);
        
        // 更新订单状态为退款中
        orderMapper.updateStatus(orderId, 5);
        
        return refund;
    }
    
    /** 查询我的退款列表 */
    @Override
    public List<Refund> getMyRefunds(Long userId) {
        return refundMapper.selectByUserId(userId);
    }
    
    /** 查询退款详情 */
    @Override
    public Refund getRefundDetail(Long refundId) {
        return refundMapper.selectById(refundId);
    }
    
    /** 管理端分页查询全站退款 */
    @Override
    public Map<String, Object> getAllRefunds(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Refund> list = refundMapper.selectAll(keyword, status, offset, pageSize);
        int total = refundMapper.countAll(keyword, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        return result;
    }
    
    /** 管理员同意退款 */
    @Override
    @Transactional
    public void approveRefund(Long refundId, Long handlerId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        
        if (refund.getStatus() != 0) {
            throw new RuntimeException("退款申请状态不正确");
        }
        
        refund.setStatus(1); // 已同意
        refund.setHandlerId(handlerId);
        refund.setHandleTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        refundMapper.update(refund);
        
        // 如果是仅退款，直接进入退款中状态，等待完成退款
        if (refund.getType() == 1) {
            refund.setStatus(3); // 退款中
            refundMapper.update(refund);
        }
        // 如果是退货退款，状态保持为已同意，等待买家填写快递单号
    }
    
    /** 管理员驳回退款 */
    @Override
    @Transactional
    public void rejectRefund(Long refundId, Long handlerId, String rejectReason) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        
        if (refund.getStatus() != 0) {
            throw new RuntimeException("退款申请状态不正确");
        }
        
        refund.setStatus(2); // 已拒绝
        refund.setHandlerId(handlerId);
        refund.setRejectReason(rejectReason);
        refund.setHandleTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        refundMapper.update(refund);
        
        // 恢复订单状态
        Order order = orderMapper.selectById(refund.getOrderId());
        if (order != null && order.getStatus() == 5) {
            // 根据原订单状态恢复
            if (order.getPayTime() != null && !order.getPayTime().isEmpty()) {
                if (order.getLogisticsNo() != null && !order.getLogisticsNo().isEmpty()) {
                    orderMapper.updateStatus(order.getId(), 2); // 已发货
                } else {
                    orderMapper.updateStatus(order.getId(), 1); // 已支付
                }
            }
        }
    }
    
    /** 管理员完成退款 */
    @Override
    @Transactional
    public void completeRefund(Long refundId, Long handlerId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        
        if (refund.getStatus() != 3) {
            throw new RuntimeException("退款申请状态不正确");
        }
        
        refund.setStatus(4); // 已退款
        refund.setHandlerId(handlerId);
        refund.setHandleTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        refundMapper.update(refund);
        
        // 更新订单状态为已退款
        orderMapper.updateStatus(refund.getOrderId(), 6);
    }
    
    /** 买家填写退货快递单号 */
    @Override
    @Transactional
    public void updateLogisticsNo(Long refundId, String logisticsNo) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        
        if (refund.getType() != 2) {
            throw new RuntimeException("仅退货退款可以填写快递单号");
        }
        
        if (refund.getStatus() != 1) {
            throw new RuntimeException("退款申请状态不正确，需要管理员同意后才能填写快递单号");
        }
        
        refund.setLogisticsNo(logisticsNo);
        refund.setStatus(3); // 更新为退款中（等待卖家确认收货）
        refundMapper.update(refund);
    }
    
    /** 管理员确认收到退货 */
    @Override
    @Transactional
    public void confirmReceive(Long refundId, Long handlerId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        
        if (refund.getStatus() != 3) {
            throw new RuntimeException("退款申请状态不正确");
        }
        
        if (refund.getType() == 2 && (refund.getLogisticsNo() == null || refund.getLogisticsNo().isEmpty())) {
            throw new RuntimeException("退货退款需要买家填写快递单号");
        }
        
        // 确认收货，完成退款
        refund.setStatus(4); // 已退款
        refund.setHandlerId(handlerId);
        refund.setHandleTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        refundMapper.update(refund);
        
        // 更新订单状态为已退款
        orderMapper.updateStatus(refund.getOrderId(), 6);
    }
    
    /** 买家取消退款申请 */
    @Override
    @Transactional
    public void cancelRefund(Long refundId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        
        if (refund.getStatus() != 0) {
            throw new RuntimeException("只能取消待处理的退款申请");
        }
        
        refund.setStatus(5); // 已取消
        refundMapper.update(refund);
        
        // 恢复订单状态
        Order order = orderMapper.selectById(refund.getOrderId());
        if (order != null && order.getStatus() == 5) {
            if (order.getPayTime() != null && !order.getPayTime().isEmpty()) {
                if (order.getLogisticsNo() != null && !order.getLogisticsNo().isEmpty()) {
                    orderMapper.updateStatus(order.getId(), 2); // 已发货
                } else {
                    orderMapper.updateStatus(order.getId(), 1); // 已支付
                }
            }
        }
    }

    /** 商家分页查询本店退款 */
    @Override
    public Map<String, Object> getMerchantRefunds(Long sellerId, Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Refund> list = refundMapper.selectAllBySellerId(sellerId, keyword, status, offset, pageSize);
        int total = refundMapper.countAllBySellerId(sellerId, keyword, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        return result;
    }

    /** 商家同意退款 */
    @Override
    public void approveMerchantRefund(Long refundId, Long sellerId, Long handlerId) {
        checkRefundBelongsToSeller(refundId, sellerId);
        approveRefund(refundId, handlerId);
    }

    /** 商家驳回退款 */
    @Override
    public void rejectMerchantRefund(Long refundId, Long sellerId, Long handlerId, String rejectReason) {
        checkRefundBelongsToSeller(refundId, sellerId);
        rejectRefund(refundId, handlerId, rejectReason);
    }

    /** 商家完成退款 */
    @Override
    public void completeMerchantRefund(Long refundId, Long sellerId, Long handlerId) {
        checkRefundBelongsToSeller(refundId, sellerId);
        completeRefund(refundId, handlerId);
    }

    /** 商家确认收到退货 */
    @Override
    public void confirmMerchantReceive(Long refundId, Long sellerId, Long handlerId) {
        checkRefundBelongsToSeller(refundId, sellerId);
        confirmReceive(refundId, handlerId);
    }

    /** 校验退款是否属于该商家 */
    private void checkRefundBelongsToSeller(Long refundId, Long sellerId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new RuntimeException("退款申请不存在");
        }
        int count = orderItemMapper.countByOrderIdAndSellerId(refund.getOrderId(), sellerId);
        if (count <= 0) {
            throw new RuntimeException("无权操作该退款");
        }
    }
}

