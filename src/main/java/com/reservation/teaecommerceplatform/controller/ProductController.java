package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.dto.ProductQueryDTO;
import com.reservation.teaecommerceplatform.entity.Product;
import com.reservation.teaecommerceplatform.entity.User;
import com.reservation.teaecommerceplatform.mapper.UserMapper;
import com.reservation.teaecommerceplatform.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品 API：列表与详情可匿名（见 {@link com.reservation.teaecommerceplatform.config.WebConfig} 排除路径）；
 * 增删改需登录且角色为管理员(1)或商家(2)，商家仅能操作自己的 {@code sellerId}（逻辑在 Service）。
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public Result<?> getList(ProductQueryDTO queryDTO) {
        try {
            return Result.success(productService.getList(queryDTO));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Product> getDetail(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Product product, HttpServletRequest request) {
        try {
            User current = getCurrentUser(request);
            checkManageProductPermission(current);
            productService.add(product, current.getId(), current.getRole());
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Product product, HttpServletRequest request) {
        try {
            User current = getCurrentUser(request);
            checkManageProductPermission(current);
            productService.update(product, current.getId(), current.getRole());
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        try {
            User current = getCurrentUser(request);
            checkManageProductPermission(current);
            productService.delete(id, current.getId(), current.getRole());
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private User getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    private void checkManageProductPermission(User user) {
        if (user.getRole() == null || (user.getRole() != 1 && user.getRole() != 2)) {
            throw new RuntimeException("无权限操作商品");
        }
    }
}

