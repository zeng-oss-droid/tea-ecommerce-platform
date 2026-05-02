package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 购物车表：按用户、按用户+商品查询。 */
@Mapper
public interface CartMapper {
    Cart selectById(Long id);
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    List<Cart> selectByUserId(Long userId);
    int insert(Cart cart);
    int update(Cart cart);
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    int delete(Long id);
    int deleteByUserId(Long userId);
}

