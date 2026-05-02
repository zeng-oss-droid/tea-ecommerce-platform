package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.User;
import com.reservation.teaecommerceplatform.service.OrderService;
import com.reservation.teaecommerceplatform.service.StatisticsService;
import com.reservation.teaecommerceplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 管理端 REST 接口统一前缀 {@code /api/admin}。
 * <p>
 * 典型模块：用户与商家申请、仪表盘与统计、全站订单/退款/评论审核。
 * 鉴权一般在拦截器或过滤器中校验 JWT 且角色为管理员，本类只负责参数与 {@link Result} 封装。
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private com.reservation.teaecommerceplatform.service.RefundService refundService;
    
    @Autowired
    private com.reservation.teaecommerceplatform.service.ReviewService reviewService;

    // ---------- 用户管理（含详情、启停、角色、删除账号）----------
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = userService.getUserList(pageNum, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            userService.updateUserStatus(id, status);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        try {
            Integer role = params.get("role");
            userService.updateUserRole(id, role);
            return Result.success("角色更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ---------- 商家入驻申请（数据在 user 表，路径中的 id 为用户主键）----------
    /** 分页列表；status 可选：1待审 2通过 3驳回 */
    @GetMapping("/merchant-applications")
    public Result<Map<String, Object>> getMerchantApplicationList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = userService.getMerchantApplicationList(pageNum, pageSize, keyword, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 审核通过 → 用户变为商家角色 */
    @PostMapping("/merchant-applications/{id}/approve")
    public Result<Void> approveMerchantApplication(@PathVariable Long id) {
        try {
            userService.approveMerchantApplication(id);
            return Result.success("审核通过", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 请求体 JSON：{@code { "rejectReason": "..." }}，可为空则服务层给默认文案 */
    @PostMapping("/merchant-applications/{id}/reject")
    public Result<Void> rejectMerchantApplication(@PathVariable Long id, @RequestBody Map<String, String> params) {
        try {
            userService.rejectMerchantApplication(id, params.get("rejectReason"));
            return Result.success("已驳回申请", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 删除申请记录：清空入驻字段，仅待审/驳回；已通过需走用户管理 */
    @DeleteMapping("/merchant-applications/{id}")
    public Result<Void> deleteMerchantApplication(@PathVariable Long id) {
        try {
            userService.deleteMerchantApplication(id);
            return Result.success("已删除申请记录", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 删除整个用户（非管理员），与 merchant-applications 删除含义不同 */
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ---------- 数据统计（仪表盘、订单/商品/用户维度）----------
    @GetMapping("/statistics/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = statisticsService.getDashboardStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics/orders")
    public Result<Map<String, Object>> getOrderStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> stats = statisticsService.getOrderStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics/products")
    public Result<Map<String, Object>> getProductStats() {
        try {
            Map<String, Object> stats = statisticsService.getProductStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics/users")
    public Result<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = statisticsService.getUserStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ---------- 订单管理 ----------
    @GetMapping("/orders")
    public Result<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = orderService.getAllOrders(pageNum, pageSize, keyword, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/orders/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            orderService.updateOrderStatus(id, status);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ---------- 退款管理 ----------
    @GetMapping("/refunds")
    public Result<Map<String, Object>> getAllRefunds(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = refundService.getAllRefunds(pageNum, pageSize, keyword, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/refunds/{id}/approve")
    public Result<Void> approveRefund(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long handlerId = (Long) request.getAttribute("userId");
            refundService.approveRefund(id, handlerId);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/refunds/{id}/reject")
    public Result<Void> rejectRefund(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            Long handlerId = (Long) request.getAttribute("userId");
            String rejectReason = params.get("rejectReason");
            refundService.rejectRefund(id, handlerId, rejectReason);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/refunds/{id}/complete")
    public Result<Void> completeRefund(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long handlerId = (Long) request.getAttribute("userId");
            refundService.completeRefund(id, handlerId);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/refunds/{id}/confirm-receive")
    public Result<Void> confirmReceive(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long handlerId = (Long) request.getAttribute("userId");
            refundService.confirmReceive(id, handlerId);
            return Result.success("确认收货成功，退款已完成", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ---------- 评论管理 ----------
    @GetMapping("/reviews")
    public Result<Map<String, Object>> getAllReviews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = reviewService.getAllReviews(pageNum, pageSize, keyword, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/reviews/{id}/status")
    public Result<Void> updateReviewStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            reviewService.updateReviewStatus(id, status);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/reviews/{id}/reply")
    public Result<Void> replyReview(@PathVariable Long id, @RequestBody Map<String, String> params) {
        try {
            String reply = params.get("reply");
            reviewService.replyReview(id, reply);
            return Result.success("回复成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/reviews/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

