package com.reservation.teaecommerceplatform.service;

import java.util.Map;

/** 管理后台仪表盘与报表聚合数据。 */
public interface StatisticsService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getOrderStats(String startDate, String endDate);
    Map<String, Object> getProductStats();
    Map<String, Object> getUserStats();
}

