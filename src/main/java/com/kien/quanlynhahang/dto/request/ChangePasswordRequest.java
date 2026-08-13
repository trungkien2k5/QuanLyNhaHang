package com.kien.quanlynhahang.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Schema(
            description = "Mật khẩu hiện tại",
            example = "123456"
    )
    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String oldPassword;

    @Schema(
            description = "Mật khẩu mới",
            example = "12345678"
    )
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;
}