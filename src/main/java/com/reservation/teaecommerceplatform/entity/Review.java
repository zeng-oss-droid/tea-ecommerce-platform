package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 订单完成后评价；含审核状态与商家回复；昵称头像为查询时联表填充，非表字段。 */
@Data
public class Review {
    private Long id;
    private Long orderId; // 订单ID
    private Long orderItemId; // 订单项ID
    private Long userId; // 用户ID
    private Long productId; // 商品ID
    private String productName; // 商品名称
    private String productImage; // 商品图片
    private Integer rating; // 评分：1-5星
    private String content; // 评论内容
    private String images; // 评论图片，JSON格式
    private Integer status; // 状态：0-待审核 1-已通过 2-已拒绝
    private String reply; // 商家回复
    private LocalDateTime replyTime; // 回复时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联用户信息（不存储在数据库）
    private String userNickname; // 用户昵称
    private String userAvatar; // 用户头像
}

