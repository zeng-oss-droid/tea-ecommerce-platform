package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Cart;

import java.util.List;

/** 购物车：合并同款、数量更新、清空。 */
public interface CartService {
    /** 查询用户购物车列表 */
    List<Cart> getCartList(Long userId);
    /** 添加商品到购物车，同款合并数量 */
    void addToCart(Long userId, Long productId, Integer quantity);
    /** 更新购物车商品数量 */
    void updateQuantity(Long cartId, Integer quantity);
    /** 移除购物车项 */
    void removeFromCart(Long cartId);
    /** 清空用户购物车 */
    void clearCart(Long userId);
}

