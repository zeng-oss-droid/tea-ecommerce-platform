package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Order;
import com.reservation.teaecommerceplatform.entity.OrderItem;
import com.reservation.teaecommerceplatform.mapper.OrderItemMapper;
import com.reservation.teaecommerceplatform.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * C 端订单：创建（可传购物车选中项）、列表、详情、模拟支付、取消、确认收货。
 * 管理端/商家端订单在 {@link com.reservation.teaecommerceplatform.controller.AdminController} 与
 * {@link com.reservation.teaecommerceplatform.controller.MerchantController}。
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemMapper orderItemMapper;

    /** 创建订单，请求体含 addressId、remark、cartIds（选中的购物车项） */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long addressId = Long.valueOf(params.get("addressId").toString());
            String remark = params.get("remark") != null ? params.get("remark").toString() : "";
            
            // 获取选中的购物车ID列表
            List<Long> cartIds = null;
            if (params.get("cartIds") != null) {
                @SuppressWarnings("unchecked")
                List<Object> cartIdsObj = (List<Object>) params.get("cartIds");
                cartIds = cartIdsObj.stream()
                        .map(id -> Long.valueOf(id.toString()))
                        .collect(Collectors.toList());
            }
            
            Order order = orderService.createOrder(userId, addressId, remark, cartIds);
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 获取当前登录用户的订单列表 */
    @GetMapping("/list")
    public Result<List<Order>> getOrderList(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Order> list = orderService.getOrderList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 获取订单详情，含订单主信息和订单项列表 */
    @GetMapping("/detail/{orderId}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderDetail(orderId);
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            data.put("orderItems", orderItems);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 模拟支付订单，请求体：{@code { "orderId": 1, "payType": 1 }} */
    @PostMapping("/pay")
    public Result<Void> payOrder(@RequestBody Map<String, Object> params) {
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            Integer payType = Integer.valueOf(params.get("payType").toString());
            orderService.payOrder(orderId, payType);
            return Result.success("支付成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 取消未支付或待发货的订单 */
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return Result.success("取消成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 买家确认收货，订单完成 */
    @PostMapping("/confirm/{orderId}")
    public Result<Void> confirmReceipt(@PathVariable Long orderId) {
        try {
            orderService.confirmReceipt(orderId);
            return Result.success("确认收货成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

