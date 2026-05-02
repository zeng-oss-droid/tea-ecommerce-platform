package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体，对应数据库表 {@code user}。
 * <p>
 * 商家入驻没有独立子表：店铺名、联系人、申请状态等均为本表的字段，
 * 与 {@code role}（0普通 1管理 2商家）配合表示账号与入驻生命周期。
 * </p>
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer gender; // 0-未知 1-男 2-女
    private Integer status; // 0-禁用 1-启用
    private Integer role; // 0-普通用户 1-管理员 2-商家
    private String merchantName; // 店铺名称
    private String contactName; // 联系人姓名
    private String contactPhone; // 联系电话
    private String businessScope; // 主营类目
    private String merchantIntro; // 店铺简介
    private Integer merchantApplyStatus; // 商家申请状态：0-未申请 1-待审核 2-已通过 3-已驳回
    private String merchantApplyRejectReason; // 驳回原因
    private LocalDateTime merchantApplyTime; // 申请时间
    private LocalDateTime merchantReviewTime; // 审核时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

