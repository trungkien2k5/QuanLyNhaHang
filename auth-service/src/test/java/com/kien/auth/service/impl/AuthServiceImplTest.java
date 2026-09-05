package com.kien.auth.service;

import com.kien.auth.dto.request.ChangePasswordRequest;
import com.kien.auth.dto.request.ForgotPasswordRequest;
import com.kien.auth.dto.request.LoginRequest;
import com.kien.auth.dto.request.RefreshTokenRequest;
import com.kien.auth.dto.request.RegisterRequest;
import com.kien.auth.dto.request.ResetPasswordRequest;
import com.kien.auth.dto.reponse.LoginResponse;
import com.kien.auth.dto.reponse.RefreshTokenResponse;
import com.kien.auth.entity.NguoiDung;
import com.kien.auth.entity.Otp;
import com.kien.auth.entity.RefreshToken;
import com.kien.auth.exception.BadRequestException;
import com.kien.auth.mail.service.MailService;
import com.kien.auth.repository.NguoiDungRepository;
import com.kien.auth.repository.OtpRepository;
import com.kien.auth.repository.RefreshTokenRepository;
import com.kien.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.kien.auth.exception.ResourceNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private MailService mailService;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setTenDangNhap("kien");
        registerRequest.setMatKhau("123456");
        registerRequest.setHoTen("Nguyen Trung Kien");
        registerRequest.setEmail("kien@gmail.com");
    }

    // ==================== REGISTER ====================

    @Test
    void register_success() {
        when(nguoiDungRepository.existsByTenDangNhap("kien"))
                .thenReturn(false);

        when(nguoiDungRepository.existsByEmail("kien@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        authService.register(registerRequest);

        verify(nguoiDungRepository)
                .existsByTenDangNhap("kien");

        verify(nguoiDungRepository)
                .existsByEmail("kien@gmail.com");

        verify(passwordEncoder)
                .encode("123456");

        verify(nguoiDungRepository)
                .save(any(NguoiDung.class));
    }

    @Test
    void register_duplicateUsername() {
        when(nguoiDungRepository.existsByTenDangNhap("kien"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals(
                "Tên đăng nhập đã tồn tại",
                exception.getMessage()
        );

        verify(nguoiDungRepository, never())
                .save(any(NguoiDung.class));
    }

    // ==================== LOGIN ====================

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setTenDangNhap("kien");
        request.setMatKhau("123456");

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setMaND(1);
        nguoiDung.setTenDangNhap("kien");
        nguoiDung.setVaiTro("CUSTOMER");

        when(nguoiDungRepository.findByTenDangNhap("kien"))
                .thenReturn(java.util.Optional.of(nguoiDung));

        when(jwtService.taoToken("kien", "CUSTOMER"))
                .thenReturn("access-token");

        when(jwtService.taoRefreshToken("kien"))
                .thenReturn("refresh-token");

        authService.login(request);

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(nguoiDungRepository)
                .findByTenDangNhap("kien");

        verify(jwtService)
                .taoToken("kien", "CUSTOMER");

        verify(jwtService)
                .taoRefreshToken("kien");

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setTenDangNhap("kien");
        request.setMatKhau("123456");

        when(nguoiDungRepository.findByTenDangNhap("kien"))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .taoToken(anyString(), anyString());

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    // ==================== CHANGE PASSWORD ====================

    @Test
    void changePassword_success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("newPassword123");

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap("kien");
        nguoiDung.setMatKhau("encoded-old-password");

        when(nguoiDungRepository.findByTenDangNhap("kien"))
                .thenReturn(java.util.Optional.of(nguoiDung));

        when(passwordEncoder.matches(
                "123456",
                "encoded-old-password"
        )).thenReturn(true);

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("encoded-new-password");

        authService.changePassword("kien", request);

        assertEquals(
                "encoded-new-password",
                nguoiDung.getMatKhau()
        );

        verify(passwordEncoder)
                .matches("123456", "encoded-old-password");

        verify(passwordEncoder)
                .encode("newPassword123");

        verify(nguoiDungRepository)
                .save(nguoiDung);
    }

    @Test
    void changePassword_oldPasswordIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong-password");
        request.setNewPassword("newPassword123");

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap("kien");
        nguoiDung.setMatKhau("encoded-old-password");

        when(nguoiDungRepository.findByTenDangNhap("kien"))
                .thenReturn(java.util.Optional.of(nguoiDung));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-old-password"
        )).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.changePassword("kien", request)
        );

        assertEquals(
                "Mật khẩu cũ không đúng",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(nguoiDungRepository, never())
                .save(any());
    }

    // ==================== REFRESH TOKEN ====================

    @Test
    void refreshToken_success() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap("kien");
        nguoiDung.setVaiTro("CUSTOMER");

        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token");
        token.setNguoiDung(nguoiDung);
        token.setRevoked(false);
        token.setExpiredAt(
                java.time.LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(java.util.Optional.of(token));

        when(jwtService.taoToken("kien", "CUSTOMER"))
                .thenReturn("new-access-token");

        RefreshTokenResponse response =
                authService.refreshToken(request);

        assertNotNull(response);

        assertEquals(
                "new-access-token",
                response.getAccessToken()
        );

        verify(jwtService)
                .taoToken("kien", "CUSTOMER");
    }

    // ==================== LOGOUT ====================

    @Test
    void logout_success() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token");
        token.setRevoked(false);

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(java.util.Optional.of(token));

        authService.logout(request);

        assertTrue(token.getRevoked());

        verify(refreshTokenRepository)
                .save(token);
    }

    // ==================== FORGOT / RESET PASSWORD ====================

    @Test
    void forgotPassword_success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("kien@gmail.com");

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail("kien@gmail.com");

        when(nguoiDungRepository.findByEmail("kien@gmail.com"))
                .thenReturn(java.util.Optional.of(nguoiDung));

        authService.forgotPassword(request);

        verify(otpRepository)
                .save(any(Otp.class));

        verify(mailService)
                .sendOtp(eq("kien@gmail.com"), anyString());
    }

    @Test
    void resetPassword_success() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("kien@gmail.com");
        request.setOtp("123456");
        request.setNewPassword("newPassword123");

        Otp otp = new Otp();
        otp.setEmail("kien@gmail.com");
        otp.setOtp("123456");
        otp.setUsed(false);
        otp.setExpiredAt(
                java.time.LocalDateTime.now().plusMinutes(5)
        );

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail("kien@gmail.com");
        nguoiDung.setMatKhau("old-password");

        when(otpRepository.findTopByEmailOrderByIdDesc("kien@gmail.com"))
                .thenReturn(java.util.Optional.of(otp));

        when(nguoiDungRepository.findByEmail("kien@gmail.com"))
                .thenReturn(java.util.Optional.of(nguoiDung));

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("encoded-new-password");

        authService.resetPassword(request);

        assertEquals(
                "encoded-new-password",
                nguoiDung.getMatKhau()
        );

        assertTrue(otp.getUsed());

        verify(passwordEncoder)
                .encode("newPassword123");

        verify(nguoiDungRepository)
                .save(nguoiDung);

        verify(otpRepository)
                .save(otp);
    }
}

