package com.kien.auth.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {
    @Schema(description = "Lấy thông tin người dùng đăng nhập")
    private Integer maND;
    private String tenDangNhap;
    private String hoTen;
    private String email;
    private String vaiTro;
}
