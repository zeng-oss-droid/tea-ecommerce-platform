package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Review;
import com.reservation.teaecommerceplatform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/** 用户评价、按商品/订单查询、我的评论；后台审核在 {@code /api/admin/reviews}。 */
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    /** 买家提交商品评价，请求体含 orderId、orderItemId、productId、rating、content、images */
    @PostMapping("/create")
    public Result<Review> createReview(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            Long orderId = Long.valueOf(params.get("orderId").toString());
            Long orderItemId = Long.valueOf(params.get("orderItemId").toString());
            Long productId = Long.valueOf(params.get("productId").toString());
            Integer rating = Integer.valueOf(params.get("rating").toString());
            String content = params.get("content") != null ? params.get("content").toString() : "";
            String images = params.get("images") != null ? params.get("images").toString() : null;
            
            Review review = reviewService.createReview(userId, orderId, orderItemId, productId, rating, content, images);
            return Result.success("评论成功", review);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 分页查询指定商品的评价列表 */
    @GetMapping("/product/{productId}")
    public Result<Map<String, Object>> getReviewsByProduct(@PathVariable Long productId, 
                                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<Review> reviews = reviewService.getReviewsByProductId(productId, pageNum, pageSize);
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("list", reviews);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 查询指定订单下的所有评价 */
    @GetMapping("/order/{orderId}")
    public Result<List<Review>> getReviewsByOrder(@PathVariable Long orderId) {
        try {
            List<Review> reviews = reviewService.getReviewsByOrderId(orderId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /** 获取当前登录用户发表的所有评价 （未使用）*/
    @GetMapping("/my")
    public Result<List<Review>> getMyReviews(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            List<Review> reviews = reviewService.getMyReviews(userId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

