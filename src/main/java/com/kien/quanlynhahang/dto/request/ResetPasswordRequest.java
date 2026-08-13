package com.kien.quanlynhahang.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Schema(
            description = "Email tài khoản",
            example = "admin@gmail.com"
    )
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Schema(
            description = "Mã OTP gửi về email",
            example = "123456"
    )
    @NotBlank(message = "OTP không được để trống")
    @Size(min = 6, max = 6, message = "OTP phải gồm 6 ký tự")
    private String otp;

    @Schema(
            description = "Mật khẩu mới",
            example = "123456"
    )
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String newPassword;
}