package com.reservation.teaecommerceplatform.config;

import com.reservation.teaecommerceplatform.interceptor.AdminInterceptor;
import com.reservation.teaecommerceplatform.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 扩展：注册拦截器链、本地静态目录映射（上传图片通过 {@code /uploads/**} 访问）。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 全量 /api 解析 Authorization，合法则写入 userId（未登录也放行，由 Controller 自行判断）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/product/list",
                        "/api/product/detail/**",
                        "/api/category/list"
                );

        // 仅管理员(role=1)可访问管理端、上传、分类写操作
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**", "/api/upload/**", "/api/category/add", "/api/category/update", "/api/category/delete/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，让上传的图片可以访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}

