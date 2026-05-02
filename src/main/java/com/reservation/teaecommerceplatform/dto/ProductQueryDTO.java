package com.reservation.teaecommerceplatform.dto;

import lombok.Data;

/** 商品列表/搜索查询条件，含分页与排序；商家端可设 {@code sellerId} 只看自己的货。 */
@Data
public class ProductQueryDTO {
    private String keyword; // 搜索关键词
    private Long categoryId; // 分类ID
    private Integer minPrice; // 最低价格
    private Integer maxPrice; // 最高价格
    private String sortBy; // 排序字段：price, sales, createTime
    private String sortOrder; // 排序方式：asc, desc
    private Long sellerId; // 商家ID
    private Boolean includeDisabled = false; // 是否包含下架商品
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

