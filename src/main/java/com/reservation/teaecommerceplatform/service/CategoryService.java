package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Category;

import java.util.List;

/** 商品分类维护与树形查询。 */
public interface CategoryService {
    /** 查询所有分类 */
    List<Category> getAll();
    /** 根据ID查询分类 */
    Category getById(Long id);
    /** 新增分类 */
    void add(Category category);
    /** 更新分类 */
    void update(Category category);
    /** 删除分类 */
    void delete(Long id);
}

