package com.kien.quanlynhahang.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Schema(
            description = "Họ và tên",
            example = "Nguyễn Văn A"
    )
    @NotBlank(message = "Họ và tên không được để trống")
    private String hoTen;

    @Schema(
            description = "Email",
            example = "nguyenvana@gmail.com"
    )
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
}