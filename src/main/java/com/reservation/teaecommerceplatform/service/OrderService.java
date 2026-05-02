package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * 订单全生命周期 + 后台/商家维度的分页查询与状态流转。
 */
public interface OrderService {
    Order createOrder(Long userId, Long addressId, String remark, List<Long> cartIds);
    List<Order> getOrderList(Long userId);
    Order getOrderDetail(Long orderId);
    void payOrder(Long orderId, Integer payType);
    void cancelOrder(Long orderId);
    void confirmReceipt(Long orderId);

    /** 管理端：全站订单分页 */
    Map<String, Object> getAllOrders(Integer pageNum, Integer pageSize, String keyword, Integer status);
    void updateOrderStatus(Long orderId, Integer status);

    /** 商家端：仅含本店商品的订单分页 */
    Map<String, Object> getMerchantOrders(Long sellerId, Integer pageNum, Integer pageSize, String keyword, Integer status);

    /** 商家发货/取消等（会校验订单是否属于该卖家） */
    void updateMerchantOrderStatus(Long sellerId, Long orderId, Integer status);
}
