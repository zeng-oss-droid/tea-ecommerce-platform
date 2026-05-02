package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.dto.ProductQueryDTO;
import com.reservation.teaecommerceplatform.entity.Product;
import com.reservation.teaecommerceplatform.entity.User;
import com.reservation.teaecommerceplatform.mapper.UserMapper;
import com.reservation.teaecommerceplatform.service.OrderService;
import com.reservation.teaecommerceplatform.service.ProductService;
import com.reservation.teaecommerceplatform.service.RefundService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商家工作台（role=2）：本店订单、商品、退款处理；管理员不可走此入口（会提示用管理后台）。
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RefundService refundService;

    @GetMapping("/orders")
    public Result<Map<String, Object>> getMerchantOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            Map<String, Object> result = orderService.getMerchantOrders(merchant.getId(), pageNum, pageSize, keyword, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/orders/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            Integer status = params.get("status");
            orderService.updateMerchantOrderStatus(merchant.getId(), id, status);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/products")
    public Result<Map<String, Object>> getMerchantProducts(ProductQueryDTO queryDTO, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            queryDTO.setSellerId(merchant.getId());
            queryDTO.setIncludeDisabled(true);
            return Result.success(productService.getList(queryDTO));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/products")
    public Result<Void> addProduct(@RequestBody Product product, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            productService.add(product, merchant.getId(), merchant.getRole());
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/products")
    public Result<Void> updateProduct(@RequestBody Product product, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            productService.update(product, merchant.getId(), merchant.getRole());
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            productService.delete(id, merchant.getId(), merchant.getRole());
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/refunds")
    public Result<Map<String, Object>> getMerchantRefunds(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            return Result.success(refundService.getMerchantRefunds(merchant.getId(), pageNum, pageSize, keyword, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/refunds/{id}/approve")
    public Result<Void> approveRefund(@PathVariable Long id, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            refundService.approveMerchantRefund(id, merchant.getId(), merchant.getId());
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/refunds/{id}/reject")
    public Result<Void> rejectRefund(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            refundService.rejectMerchantRefund(id, merchant.getId(), merchant.getId(), params.get("rejectReason"));
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/refunds/{id}/complete")
    public Result<Void> completeRefund(@PathVariable Long id, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            refundService.completeMerchantRefund(id, merchant.getId(), merchant.getId());
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/refunds/{id}/confirm-receive")
    public Result<Void> confirmReceive(@PathVariable Long id, HttpServletRequest request) {
        try {
            User merchant = checkMerchantRole(request);
            refundService.confirmMerchantReceive(id, merchant.getId(), merchant.getId());
            return Result.success("确认收货成功，退款已完成", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private User checkMerchantRole(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null || (user.getRole() != 1 && user.getRole() != 2)) {
            throw new RuntimeException("无权限访问");
        }
        if (user.getRole() == 1) {
            throw new RuntimeException("管理员请使用管理后台");
        }
        return user;
    }
}
