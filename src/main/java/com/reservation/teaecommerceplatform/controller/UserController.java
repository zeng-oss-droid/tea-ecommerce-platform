package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.dto.UserLoginDTO;
import com.reservation.teaecommerceplatform.dto.UserRegisterDTO;
import com.reservation.teaecommerceplatform.entity.User;
import com.reservation.teaecommerceplatform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台用户接口：{@code /register}、{@code /login} 匿名；{@code /info}、密码修改、商家入驻需带 JWT。
 * 登录态从 {@link HttpServletRequest#getAttribute(String)} 读取 {@code userId}（由 {@link com.reservation.teaecommerceplatform.interceptor.JwtInterceptor} 注入）。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            return Result.success("注册成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "未登录或token已过期");
            }
            User user = userService.getCurrentUser(userId);
            // 不返回密码
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/info")
    public Result<User> updateUser(@RequestBody User user, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User updatedUser = userService.updateUser(userId, user);
            updatedUser.setPassword(null);
            return Result.success("更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交商家入驻：请求体携带店铺名、联系人、电话、主营类目、简介等；
     * 审核在管理端 {@link AdminController} 的 {@code /api/admin/merchant-applications} 系列接口处理。
     */
    @PostMapping("/merchant/apply")
    public Result<Void> applyMerchant(@RequestBody User user, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.applyMerchant(userId, user);
            return Result.success("申请成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

