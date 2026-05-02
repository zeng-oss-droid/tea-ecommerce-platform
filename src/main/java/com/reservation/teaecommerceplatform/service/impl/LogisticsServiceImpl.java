package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.Logistics;
import com.reservation.teaecommerceplatform.mapper.LogisticsMapper;
import com.reservation.teaecommerceplatform.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 物流表增改查。 */
@Service
public class LogisticsServiceImpl implements LogisticsService {
    
    @Autowired
    private LogisticsMapper logisticsMapper;

    @Override
    public Logistics getByOrderId(Long orderId) {
        return logisticsMapper.selectByOrderId(orderId);
    }

    @Override
    public void add(Logistics logistics) {
        logisticsMapper.insert(logistics);
    }

    @Override
    public void update(Logistics logistics) {
        logisticsMapper.update(logistics);
    }
}

