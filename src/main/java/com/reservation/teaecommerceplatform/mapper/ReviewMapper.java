package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 评论表：商品分页、订单、用户、后台审核列表。 */
@Mapper
public interface ReviewMapper {
    Review selectById(Long id);
    List<Review> selectByProductId(@Param("productId") Long productId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    List<Review> selectByOrderId(Long orderId);
    List<Review> selectByUserId(Long userId);
    List<Review> selectAll(@Param("keyword") String keyword, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countByProductId(Long productId);
    int countAll(@Param("keyword") String keyword, @Param("status") Integer status);
    int insert(Review review);
    int update(Review review);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int delete(Long id);
}

