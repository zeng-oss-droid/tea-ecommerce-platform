package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Logistics;
import com.reservation.teaecommerceplatform.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/** 订单物流信息查询与维护（可与发货流程结合使用）。 */
@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {
    
    @Autowired
    private LogisticsService logisticsService;

    @GetMapping("/order/{orderId}")
    public Result<Logistics> getByOrderId(@PathVariable Long orderId) {
        try {
            Logistics logistics = logisticsService.getByOrderId(orderId);
            return Result.success(logistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Logistics logistics) {
        try {
            logisticsService.add(logistics);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Logistics logistics) {
        try {
            logisticsService.update(logistics);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

