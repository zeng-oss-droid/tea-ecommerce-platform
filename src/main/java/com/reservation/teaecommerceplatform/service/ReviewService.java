package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Review;

import java.util.List;
import java.util.Map;

/** 商品评价：用户提交、前台展示、管理端审核与回复。 */
public interface ReviewService {
    /** 提交商品评价 */
    Review createReview(Long userId, Long orderId, Long orderItemId, Long productId, Integer rating, String content, String images);
    /** 分页查询商品评价 */
    List<Review> getReviewsByProductId(Long productId, Integer pageNum, Integer pageSize);
    /** 查询订单下的评价 */
    List<Review> getReviewsByOrderId(Long orderId);
    /** 查询我的评价 */
    List<Review> getMyReviews(Long userId);
    /** 管理端分页查询全站评价 */
    Map<String, Object> getAllReviews(Integer pageNum, Integer pageSize, String keyword, Integer status);
    /** 修改评价审核状态 */
    void updateReviewStatus(Long reviewId, Integer status);
    /** 管理员回复评价 */
    void replyReview(Long reviewId, String reply);
    /** 删除评价 */
    void deleteReview(Long reviewId);
}

