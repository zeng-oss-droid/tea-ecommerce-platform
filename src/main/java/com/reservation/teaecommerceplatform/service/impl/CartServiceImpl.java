package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.Cart;
import com.reservation.teaecommerceplatform.entity.Product;
import com.reservation.teaecommerceplatform.mapper.CartMapper;
import com.reservation.teaecommerceplatform.mapper.ProductMapper;
import com.reservation.teaecommerceplatform.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** 购物车与库存校验、同款合并数量等逻辑。 */
@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;

    /** 查询用户购物车列表 */
    @Override
    public List<Cart> getCartList(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

    /** 添加商品到购物车，同款合并数量 */
    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在且有库存
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new RuntimeException("商品不存在或已下架");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        // 检查购物车中是否已有该商品
        Cart existingCart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            // 更新数量
            int newQuantity = existingCart.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("库存不足");
            }
            cartMapper.updateQuantity(existingCart.getId(), newQuantity);
        } else {
            // 新增购物车项
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }
    }

    /** 更新购物车商品数量 */
    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new RuntimeException("购物车项不存在");
        }
        
        Product product = productMapper.selectById(cart.getProductId());
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        cartMapper.updateQuantity(cartId, quantity);
    }

    /** 移除购物车项 */
    @Override
    public void removeFromCart(Long cartId) {
        cartMapper.delete(cartId);
    }

    /** 清空用户购物车 */
    @Override
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
}

