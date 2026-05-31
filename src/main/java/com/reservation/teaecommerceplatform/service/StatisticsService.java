package com.reservation.teaecommerceplatform.service;

import java.util.Map;

/** 管理后台仪表盘与报表聚合数据。 */
public interface StatisticsService {
    /** 仪表盘概览统计 */
    Map<String, Object> getDashboardStats();
    /** 订单维度统计 */
    Map<String, Object> getOrderStats(String startDate, String endDate);
    /** 商品维度统计 */
    Map<String, Object> getProductStats();
    /** 用户维度统计 */
    Map<String, Object> getUserStats();
}

