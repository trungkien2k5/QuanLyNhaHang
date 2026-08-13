package com.kien.quanlynhahang.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String tenDangNhap;

    @NotBlank
    private String matKhau;

    @NotBlank
    private String hoTen;

    @Email
    private String email;

    private String vaiTro;
}