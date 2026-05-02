package com.reservation.teaecommerceplatform.interceptor;

import com.reservation.teaecommerceplatform.entity.User;
import com.reservation.teaecommerceplatform.mapper.UserMapper;
import com.reservation.teaecommerceplatform.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 保护 {@code /api/admin/**}、上传与分类写接口：仅数据库中 {@code role=1} 的管理员可通过，否则 403 JSON。
 * 商家工作台接口在 {@code /api/merchant}，由 {@link com.reservation.teaecommerceplatform.controller.MerchantController} 自行校验角色。
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                User user = userMapper.selectById(userId);
                if (user != null && user.getRole() == 1) {
                    // 是管理员，放行
                    request.setAttribute("userId", userId);
                    return true;
                }
            }
        }
        
        // 不是管理员，返回403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
        return false;
    }
}

