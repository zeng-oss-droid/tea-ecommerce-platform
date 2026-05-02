package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Cart;

import java.util.List;

/** 购物车：合并同款、数量更新、清空。 */
public interface CartService {
    List<Cart> getCartList(Long userId);
    void addToCart(Long userId, Long productId, Integer quantity);
    void updateQuantity(Long cartId, Integer quantity);
    void removeFromCart(Long cartId);
    void clearCart(Long userId);
}

