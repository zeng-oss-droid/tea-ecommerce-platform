package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 商品分类表。 */
@Mapper
public interface CategoryMapper {
    Category selectById(Long id);
    List<Category> selectAll();
    List<Category> selectByParentId(Long parentId);
    int insert(Category category);
    int update(Category category);
    int delete(Long id);
}

