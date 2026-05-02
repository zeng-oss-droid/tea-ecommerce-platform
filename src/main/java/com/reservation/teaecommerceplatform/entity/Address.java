package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 用户收货地址；{@code isDefault} 标记默认地址。 */
@Data
public class Address {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private String postalCode;
    private Integer isDefault; // 0-否 1-是
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

