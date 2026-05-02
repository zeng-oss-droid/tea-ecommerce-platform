package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.dto.ProductQueryDTO;
import com.reservation.teaecommerceplatform.entity.Product;
import com.reservation.teaecommerceplatform.mapper.ProductMapper;
import com.reservation.teaecommerceplatform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** {@link com.reservation.teaecommerceplatform.service.ProductService} 实现：分页与权限校验。 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getList(ProductQueryDTO queryDTO) {
        List<Product> list = productMapper.selectList(queryDTO);
        int total = productMapper.count(queryDTO);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());
        result.put("totalPages", (total + queryDTO.getPageSize() - 1) / queryDTO.getPageSize());
        
        return result;
    }

    /** 商家自动带 sellerId；管理员可代填 sellerId；新商品默认上架。 */
    @Override
    public void add(Product product, Long operatorId, Integer role) {
        if (role != null && role == 2) {
            product.setSellerId(operatorId);
        } else if (product.getSellerId() == null) {
            product.setSellerId(operatorId);
        }
        product.setStatus(1);
        product.setSales(0);
        productMapper.insert(product);
    }

    /** 商家仅能改自己的货；sellerId 以库中为准防止篡改。 */
    @Override
    public void update(Product product, Long operatorId, Integer role) {
        Product dbProduct = productMapper.selectById(product.getId());
        if (dbProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        if (role != null && role == 2 && !operatorId.equals(dbProduct.getSellerId())) {
            throw new RuntimeException("无权操作他人商品");
        }
        product.setSellerId(dbProduct.getSellerId());
        productMapper.update(product);
    }

    /** 同 update 的卖家校验。 */
    @Override
    public void delete(Long id, Long operatorId, Integer role) {
        Product dbProduct = productMapper.selectById(id);
        if (dbProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        if (role != null && role == 2 && !operatorId.equals(dbProduct.getSellerId())) {
            throw new RuntimeException("无权操作他人商品");
        }
        productMapper.delete(id);
    }
}

