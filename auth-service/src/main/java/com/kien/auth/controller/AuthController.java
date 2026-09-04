package com.kien.auth.controller;

import com.kien.auth.service.AuthService;
import com.kien.auth.dto.reponse.MeResponse;
import com.kien.auth.dto.reponse.RefreshTokenResponse;
import com.kien.auth.dto.request.*;
import com.kien.auth.entity.NguoiDung;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.kien.auth.common.ApiResponse;
import com.kien.auth.dto.reponse.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "Đăng nhập và nhận token")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Đăng nhập thành công")
                .data(authService.login(request))
                .build();
    }

    @Operation(summary = "Lấy thông tin người dùng hiện tại")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ApiResponse<MeResponse> me(
            Authentication authentication) {

        return ApiResponse.<MeResponse>builder()
                .success(true)
                .message("Lấy thông tin thành công")
                .data(authService.me(authentication.getName()))
                .build();
    }

    @Operation(summary = "Đổi mật khẩu")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {

        authService.changePassword(
                authentication.getName(),
                request
        );

        return ApiResponse.<String>builder()
                .success(true)
                .message("Đổi mật khẩu thành công")
                .data("OK")
                .build();
    }
    @Operation(summary = "Làm mới Access Token")
    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        return ApiResponse.<RefreshTokenResponse>builder()
                .success(true)
                .message("Làm mới token thành công")
                .data(authService.refreshToken(request))
                .build();
    }

    @Operation(summary = "Đăng xuất")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestBody RefreshTokenRequest request) {

        authService.logout(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Đăng xuất thành công")
                .data("OK")
                .build();
    }

    @Operation(summary = "Quên mật khẩu")
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Đã gửi OTP")
                .data("OK")
                .build();
    }

    @Operation(summary = "Đặt lại mật khẩu")
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Đổi mật khẩu thành công")
                .data("OK")
                .build();
    }

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder()
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .message("Đăng ký thành công")
                        .data(null)
                        .build());
    }

    @Operation(summary = "Cập nhật thông tin cá nhân")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        authService.updateProfile(
                userDetails.getUsername(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin thành công")
                        .data(null)
                        .build()
        );
    }
}