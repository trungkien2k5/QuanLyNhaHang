package com.kien.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(description = "Tên đăng nhập", example = "admin")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @Schema(description = "Mật khẩu", example = "123456")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;

}
