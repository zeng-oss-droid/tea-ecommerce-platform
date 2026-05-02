package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Review;

import java.util.List;
import java.util.Map;

/** 商品评价：用户提交、前台展示、管理端审核与回复。 */
public interface ReviewService {
    Review createReview(Long userId, Long orderId, Long orderItemId, Long productId, Integer rating, String content, String images);
    List<Review> getReviewsByProductId(Long productId, Integer pageNum, Integer pageSize);
    List<Review> getReviewsByOrderId(Long orderId);
    List<Review> getMyReviews(Long userId);
    Map<String, Object> getAllReviews(Integer pageNum, Integer pageSize, String keyword, Integer status);
    void updateReviewStatus(Long reviewId, Integer status);
    void replyReview(Long reviewId, String reply);
    void deleteReview(Long reviewId);
}

