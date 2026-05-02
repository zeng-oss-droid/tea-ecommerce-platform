package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Cart;
import com.reservation.teaecommerceplatform.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 当前登录用户的购物车增删改查；依赖 JWT 中的 {@code userId}。 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public Result<List<Cart>> getCartList(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Cart> list = cartService.getCartList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long productId = Long.valueOf(params.get("productId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());
            cartService.addToCart(userId, productId, quantity);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> updateQuantity(@RequestBody Map<String, Object> params) {
        try {
            Long cartId = Long.valueOf(params.get("cartId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());
            cartService.updateQuantity(cartId, quantity);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        try {
            cartService.removeFromCart(cartId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            cartService.clearCart(userId);
            return Result.success("清空成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

