package com.kien.auth.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT token đăng nhập", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    private String refreshToken;}
