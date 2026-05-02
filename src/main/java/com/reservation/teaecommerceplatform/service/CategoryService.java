package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Category;

import java.util.List;

/** 商品分类维护与树形查询。 */
public interface CategoryService {
    List<Category> getAll();
    Category getById(Long id);
    void add(Category category);
    void update(Category category);
    void delete(Long id);
}

