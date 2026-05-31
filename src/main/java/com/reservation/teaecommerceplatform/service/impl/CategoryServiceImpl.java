package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.Category;
import com.reservation.teaecommerceplatform.mapper.CategoryMapper;
import com.reservation.teaecommerceplatform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** 分类增删改查，直接委托 {@link com.reservation.teaecommerceplatform.mapper.CategoryMapper}。 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;

    /** 查询所有分类 */
    @Override
    public List<Category> getAll() {
        return categoryMapper.selectAll();
    }

    /** 根据ID查询分类 */
    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    /** 新增分类 */
    @Override
    public void add(Category category) {
        category.setStatus(1);
        categoryMapper.insert(category);
    }

    /** 更新分类 */
    @Override
    public void update(Category category) {
        categoryMapper.update(category);
    }

    /** 删除分类 */
    @Override
    public void delete(Long id) {
        categoryMapper.delete(id);
    }
}

