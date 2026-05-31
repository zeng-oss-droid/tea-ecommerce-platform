package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.dto.ProductQueryDTO;
import com.reservation.teaecommerceplatform.entity.Product;

import java.util.List;
import java.util.Map;

/** 商品检索与维护；{@code operatorId/role} 用于区分管理员与商家数据权限。 */
public interface ProductService {
    /** 根据ID查询商品 */
    Product getById(Long id);
    /** 分页查询商品列表 */
    Map<String, Object> getList(ProductQueryDTO queryDTO);
    /** 新增商品 */
    void add(Product product, Long operatorId, Integer role);
    /** 更新商品 */
    void update(Product product, Long operatorId, Integer role);
    /** 删除商品 */
    void delete(Long id, Long operatorId, Integer role);
}

