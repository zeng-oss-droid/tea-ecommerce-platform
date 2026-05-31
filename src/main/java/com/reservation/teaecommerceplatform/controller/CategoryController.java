package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Category;
import com.reservation.teaecommerceplatform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 商品分类：读接口公开；增删改走管理端拦截，仅管理员可调。 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    /** 获取所有商品分类列表（公开接口） */
    @GetMapping("/list")
    public Result<List<Category>> getList() {
        try {
            List<Category> list = categoryService.getAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 根据分类 ID 查询分类详情 */
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        try {
            Category category = categoryService.getById(id);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 新增商品分类（需管理员权限） */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Category category) {
        try {
            categoryService.add(category);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 更新商品分类信息（需管理员权限） */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Category category) {
        try {
            categoryService.update(category);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 根据分类 ID 删除商品分类（需管理员权限） */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

