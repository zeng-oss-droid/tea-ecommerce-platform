package com.reservation.teaecommerceplatform.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/** 登录接口入参，配合 {@code jakarta.validation} 在 Controller 上触发校验。 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}

