package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Refund;
import com.reservation.teaecommerceplatform.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/** 买家退款申请、我的退款、填写退货物流等；审核流在管理端与商家端。 */
@RestController
@RequestMapping("/api/refund")
public class RefundController {
    
    @Autowired
    private RefundService refundService;
    
    /** 买家提交退款申请，请求体含 orderId、type、reason、description、images */
    @PostMapping("/create")
    public Result<Refund> createRefund(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            Long orderId = Long.valueOf(params.get("orderId").toString());
            Integer type = Integer.valueOf(params.get("type").toString());
            String reason = params.get("reason") != null ? params.get("reason").toString() : "";
            String description = params.get("description") != null ? params.get("description").toString() : "";
            String images = params.get("images") != null ? params.get("images").toString() : null;
            
            Refund refund = refundService.createRefund(userId, orderId, type, reason, description, images);
            return Result.success("退款申请提交成功", refund);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 获取当前登录用户的退款申请列表 */
    @GetMapping("/my")
    public Result<List<Refund>> getMyRefunds(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            List<Refund> refunds = refundService.getMyRefunds(userId);
            return Result.success(refunds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 根据退款 ID 查询退款详情 */
    @GetMapping("/detail/{refundId}")
    public Result<Refund> getRefundDetail(@PathVariable Long refundId) {
        try {
            Refund refund = refundService.getRefundDetail(refundId);
            return Result.success(refund);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 买家取消待审核的退款申请 */
    @PostMapping("/cancel/{refundId}")
    public Result<Void> cancelRefund(@PathVariable Long refundId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            refundService.cancelRefund(refundId);
            return Result.success("取消成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 买家填写退货快递单号，请求体：{@code { "logisticsNo": "SF1234567890" }} */
    @PostMapping("/update-logistics/{refundId}")
    public Result<Void> updateLogisticsNo(@PathVariable Long refundId, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            String logisticsNo = params.get("logisticsNo");
            if (logisticsNo == null || logisticsNo.trim().isEmpty()) {
                return Result.error("快递单号不能为空");
            }
            refundService.updateLogisticsNo(refundId, logisticsNo);
            return Result.success("快递单号提交成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

