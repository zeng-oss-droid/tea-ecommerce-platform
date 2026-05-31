package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.mapper.*;
import com.reservation.teaecommerceplatform.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** 聚合用户、商品、订单等 Mapper 统计接口供大屏展示。 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;

    /** 仪表盘概览统计 */
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计（活跃用户此处与总用户相同，属演示简化，可按最近登录时间等扩展）
        int totalUsers = userMapper.count(null);
        int activeUsers = userMapper.count(null);
        
        // 商品统计
        com.reservation.teaecommerceplatform.dto.ProductQueryDTO queryDTO = new com.reservation.teaecommerceplatform.dto.ProductQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(1);
        int totalProducts = productMapper.count(queryDTO);
        
        // 订单统计
        int totalOrders = orderMapper.countTotal();
        BigDecimal totalSales = orderMapper.sumTotalSales();
        
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("totalSales", totalSales);
        
        return stats;
    }

    /** 订单维度统计 */
    @Override
    public Map<String, Object> getOrderStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();
        int totalOrders = orderMapper.countTotal();
        BigDecimal totalAmount = orderMapper.sumTotalSales();
        int paidOrders = orderMapper.countByStatus(1) + orderMapper.countByStatus(2) + orderMapper.countByStatus(3);
        int pendingOrders = orderMapper.countByStatus(0);
        
        stats.put("totalOrders", totalOrders);
        stats.put("totalAmount", totalAmount);
        stats.put("paidOrders", paidOrders);
        stats.put("pendingOrders", pendingOrders);
        return stats;
    }

    /** 商品维度统计 */
    @Override
    public Map<String, Object> getProductStats() {
        Map<String, Object> stats = new HashMap<>();
        com.reservation.teaecommerceplatform.dto.ProductQueryDTO queryDTO = new com.reservation.teaecommerceplatform.dto.ProductQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(1);
        int total = productMapper.count(queryDTO);
        stats.put("totalProducts", total);
        stats.put("onSaleProducts", total); // 简化处理
        stats.put("offSaleProducts", 0);
        return stats;
    }

    /** 用户维度统计 */
    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        int total = userMapper.count(null);
        stats.put("totalUsers", total);
        stats.put("activeUsers", total); // 简化处理
        stats.put("inactiveUsers", 0);
        return stats;
    }
}

