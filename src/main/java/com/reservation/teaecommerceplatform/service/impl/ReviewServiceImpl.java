package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.Order;
import com.reservation.teaecommerceplatform.entity.OrderItem;
import com.reservation.teaecommerceplatform.entity.Review;
import com.reservation.teaecommerceplatform.mapper.OrderItemMapper;
import com.reservation.teaecommerceplatform.mapper.OrderMapper;
import com.reservation.teaecommerceplatform.mapper.ReviewMapper;
import com.reservation.teaecommerceplatform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 校验订单完成后才可评；支持管理端审核与商家回复。 */
@Service
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    /** 提交商品评价 */
    @Override
    @Transactional
    public Review createReview(Long userId, Long orderId, Long orderItemId, Long productId, Integer rating, String content, String images) {
        // 检查订单是否已完成
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 3) {
            throw new RuntimeException("订单不存在或未完成，无法评论");
        }
        
        // 检查是否已评论
        List<Review> existingReviews = reviewMapper.selectByOrderId(orderId);
        if (existingReviews.stream().anyMatch(r -> r.getOrderItemId().equals(orderItemId))) {
            throw new RuntimeException("该订单项已评论");
        }
        
        // 获取订单项信息
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        OrderItem orderItem = orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElse(null);
        if (orderItem == null) {
            throw new RuntimeException("订单项不存在");
        }
        
        Review review = new Review();
        review.setOrderId(orderId);
        review.setOrderItemId(orderItemId);
        review.setUserId(userId);
        review.setProductId(productId);
        review.setProductName(orderItem.getProductName());
        review.setProductImage(orderItem.getProductImage());
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        review.setStatus(1); // 默认已通过
        
        reviewMapper.insert(review);
        return review;
    }
    
    /** 分页查询商品评价 */
    @Override
    public List<Review> getReviewsByProductId(Long productId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return reviewMapper.selectByProductId(productId, offset, pageSize);
    }
    
    /** 查询订单下的评价 */
    @Override
    public List<Review> getReviewsByOrderId(Long orderId) {
        return reviewMapper.selectByOrderId(orderId);
    }
    
    /** 查询我的评价 */
    @Override
    public List<Review> getMyReviews(Long userId) {
        return reviewMapper.selectByUserId(userId);
    }
    
    /** 管理端分页查询全站评价 */
    @Override
    public Map<String, Object> getAllReviews(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Review> list = reviewMapper.selectAll(keyword, status, offset, pageSize);
        int total = reviewMapper.countAll(keyword, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        return result;
    }
    
    /** 修改评价审核状态 */
    @Override
    public void updateReviewStatus(Long reviewId, Integer status) {
        reviewMapper.updateStatus(reviewId, status);
    }
    
    /** 管理员回复评价 */
    @Override
    public void replyReview(Long reviewId, String reply) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }
        review.setReply(reply);
        review.setReplyTime(java.time.LocalDateTime.now());
        reviewMapper.update(review);
    }
    
    /** 删除评价 */
    @Override
    public void deleteReview(Long reviewId) {
        reviewMapper.delete(reviewId);
    }
}

