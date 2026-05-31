package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.*;
import com.reservation.teaecommerceplatform.mapper.*;
import com.reservation.teaecommerceplatform.service.OrderService;
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
import java.util.stream.Collectors;

/**
 * 订单核心实现：下单扣库存、写订单项、清购物车；支付/发货等为状态机式更新。
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private AddressMapper addressMapper;

    /** 创建订单，扣库存并清空购物车 */
    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, String remark, List<Long> cartIds) {
        // 检查地址
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        
        // 获取购物车
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        
        // 如果指定了购物车ID列表，只处理选中的商品
        if (cartIds != null && !cartIds.isEmpty()) {
            cartList = cartList.stream()
                    .filter(cart -> cartIds.contains(cart.getId()))
                    .collect(Collectors.toList());
            if (cartList.isEmpty()) {
                throw new RuntimeException("选中的商品为空");
            }
        }
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品不存在或已下架：" + product.getName());
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("库存不足：" + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setStatus(0); // 待支付
        order.setPayType(0);
        order.setRemark(remark);
        orderMapper.insert(order);
        
        // 创建订单项并扣减库存
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            // 解析商品图片（可能是JSON格式或逗号分隔的字符串）
            String productImage = "";
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String images = product.getImages().trim();
                if (images.startsWith("[") && images.endsWith("]")) {
                    // JSON格式：["url1", "url2"] 或 ["url1","url2"]
                    try {
                        // 去掉方括号
                        images = images.substring(1, images.length() - 1).trim();
                        // 使用正则表达式提取第一个URL
                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(https?://[^,\"\\s]+)");
                        java.util.regex.Matcher matcher = pattern.matcher(images);
                        if (matcher.find()) {
                            productImage = matcher.group(1);
                        } else {
                            // 如果没有找到URL，尝试按引号分割
                            String[] parts = images.split("\"");
                            for (String part : parts) {
                                part = part.trim();
                                if (part.startsWith("http://") || part.startsWith("https://")) {
                                    productImage = part;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 解析失败，尝试按逗号分割
                        if (images.contains(",")) {
                            String[] parts = images.split(",");
                            for (String part : parts) {
                                part = part.trim().replace("\"", "").replace("'", "");
                                if (part.startsWith("http://") || part.startsWith("https://")) {
                                    productImage = part;
                                    break;
                                }
                            }
                        }
                    }
                } else if (images.contains(",")) {
                    // 逗号分隔的字符串
                    String[] parts = images.split(",");
                    for (String part : parts) {
                        part = part.trim();
                        if (part.startsWith("http://") || part.startsWith("https://")) {
                            productImage = part;
                            break;
                        }
                    }
                } else {
                    // 单个URL
                    productImage = images;
                }
            }
            orderItem.setProductImage(productImage);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItem.setSellerId(product.getSellerId());
            orderItemMapper.insert(orderItem);
            
            // 扣减库存
            productMapper.updateStock(product.getId(), cart.getQuantity());
        }
        
        // 清空购物车
        cartMapper.deleteByUserId(userId);
        
        return order;
    }

    /** 查询用户订单列表 */
    @Override
    public List<Order> getOrderList(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    /** 查询订单详情 */
    @Override
    public Order getOrderDetail(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    /** 模拟支付订单 */
    @Override
    @Transactional
    public void payOrder(Long orderId, Integer payType) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(1); // 已支付
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        orderMapper.update(order);
    }

    /** 取消待支付订单，恢复库存 */
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只能取消待支付订单");
        }
        
        // 恢复库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            productMapper.updateStock(item.getProductId(), -item.getQuantity());
        }
        
        order.setStatus(4); // 已取消
        orderMapper.update(order);
    }

    /** 确认收货，累加销量 */
    @Override
    @Transactional
    public void confirmReceipt(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }

        // 确认收货后累加商品销量（按订单项购买数量累加）
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            productMapper.updateSales(item.getProductId(), item.getQuantity());
        }

        order.setStatus(3); // 已完成
        orderMapper.update(order);
    }

    /** 生成订单号 */
    private String generateOrderNo() {
        return "TEA" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /** 管理端分页查询全站订单 */
    @Override
    public Map<String, Object> getAllOrders(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> list = orderMapper.selectAll(keyword, status, offset, pageSize);
        int total = orderMapper.countAll(keyword, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return result;
    }

    /** 管理员修改订单状态 */
    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        orderMapper.updateStatus(orderId, status);
    }

    /** 商家分页查询本店订单 */
    @Override
    public Map<String, Object> getMerchantOrders(Long sellerId, Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> list = orderMapper.selectAllBySellerId(sellerId, keyword, status, offset, pageSize);
        int total = orderMapper.countAllBySellerId(sellerId, keyword, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        return result;
    }

    /** 商家修改本店订单状态 */
    @Override
    public void updateMerchantOrderStatus(Long sellerId, Long orderId, Integer status) {
        int count = orderItemMapper.countByOrderIdAndSellerId(orderId, sellerId);
        if (count <= 0) {
            throw new RuntimeException("无权限操作该订单");
        }
        orderMapper.updateStatus(orderId, status);
    }
}

