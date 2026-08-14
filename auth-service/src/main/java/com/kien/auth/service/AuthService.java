package com.kien.auth.service;

import com.kien.auth.dto.reponse.LoginResponse;
import com.kien.auth.dto.reponse.MeResponse;
import com.kien.auth.dto.reponse.RefreshTokenResponse;
import com.kien.auth.dto.request.*;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    MeResponse me(String tenDangNhap);

    void changePassword(String tenDangNhap, ChangePasswordRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void register(RegisterRequest request);

    void updateProfile(String tenDangNhap, UpdateProfileRequest request);

}
