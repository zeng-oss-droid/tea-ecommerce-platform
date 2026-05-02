package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.dto.ProductQueryDTO;
import com.reservation.teaecommerceplatform.entity.Product;

import java.util.List;
import java.util.Map;

/** 商品检索与维护；{@code operatorId/role} 用于区分管理员与商家数据权限。 */
public interface ProductService {
    Product getById(Long id);
    Map<String, Object> getList(ProductQueryDTO queryDTO);
    void add(Product product, Long operatorId, Integer role);
    void update(Product product, Long operatorId, Integer role);
    void delete(Long id, Long operatorId, Integer role);
}

