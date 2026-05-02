package com.reservation.teaecommerceplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 商品分类，支持 {@code parentId} 树形结构。 */
@Data
public class Category {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Long parentId; // 父分类ID，0表示顶级分类
    private Integer sortOrder; // 排序
    private Integer status; // 0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

