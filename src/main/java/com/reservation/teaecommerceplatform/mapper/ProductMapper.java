package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Product;
import com.reservation.teaecommerceplatform.dto.ProductQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 商品表 product：分页条件查询、库存销量增量更新。 */
@Mapper
public interface ProductMapper {
    Product selectById(Long id);
    List<Product> selectList(ProductQueryDTO queryDTO);
    int count(ProductQueryDTO queryDTO);
    int insert(Product product);
    int update(Product product);
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    int updateSales(@Param("id") Long id, @Param("quantity") Integer quantity);
    int delete(Long id);
}

