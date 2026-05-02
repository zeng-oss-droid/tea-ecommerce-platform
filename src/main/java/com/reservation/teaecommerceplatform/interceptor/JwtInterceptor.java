package com.reservation.teaecommerceplatform.interceptor;

import com.reservation.teaecommerceplatform.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 从请求头 {@code Authorization}（Bearer 前缀 + JWT）解析令牌，将 {@code userId} 放入 request，供后续 Controller 使用。
 * 不强制登录：未带 Token 或非法 Token 仍放行，由业务方法判断 {@code userId} 是否为空。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;

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
                request.setAttribute("userId", userId);
            }
        }
        
        // 对于所有请求都放行，由Controller层判断是否需要登录
        return true;
    }
}

