package com.kien.quanlynhahang.auth.service;

import com.kien.quanlynhahang.dto.reponse.LoginResponse;
import com.kien.quanlynhahang.dto.reponse.MeResponse;
import com.kien.quanlynhahang.dto.reponse.RefreshTokenResponse;
import com.kien.quanlynhahang.dto.request.*;
import com.kien.quanlynhahang.entity.NguoiDung;
import com.kien.quanlynhahang.entity.Otp;
import com.kien.quanlynhahang.entity.RefreshToken;
import com.kien.quanlynhahang.exception.BusinessException;
import com.kien.quanlynhahang.mail.service.MailService;
import com.kien.quanlynhahang.repository.NguoiDungRepository;
import com.kien.quanlynhahang.repository.OtpRepository;
import com.kien.quanlynhahang.repository.RefreshTokenRepository;
import com.kien.quanlynhahang.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NguoiDungRepository nguoiDungRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTenDangNhap(),
                        request.getMatKhau()
                )
        );

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(request.getTenDangNhap())
                .orElseThrow();

        String accessToken = jwtService.taoToken(nguoiDung.getTenDangNhap());

        String refreshToken = jwtService.taoRefreshToken(nguoiDung.getTenDangNhap());

        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setNguoiDung(nguoiDung);
        token.setExpiredAt(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);

        refreshTokenRepository.save(token);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public MeResponse me(String tenDangNhap) {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy người dùng"));

        return new MeResponse(
                nguoiDung.getMaND(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getHoTen(),
                nguoiDung.getEmail(),
                nguoiDung.getVaiTro()
        );
    }

    @Override
    @Transactional
    public void changePassword(String tenDangNhap,
                               ChangePasswordRequest request) {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                nguoiDung.getMatKhau())) {

            throw new BusinessException(400, "Mật khẩu cũ không đúng");
        }

        nguoiDung.setMatKhau(
                passwordEncoder.encode(request.getNewPassword()));

        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken token = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(404, "Refresh Token không tồn tại"));

        if (Boolean.TRUE.equals(token.getRevoked())) {
            throw new BusinessException(400, "Refresh Token đã bị thu hồi");
        }

        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "Refresh Token đã hết hạn");
        }

        String accessToken =
                jwtService.taoToken(token.getNguoiDung().getTenDangNhap());

        return new RefreshTokenResponse(accessToken);
    }

    @Override
    public void logout(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(404, "Refresh Token không tồn tại"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    private String generateOtp() {

        return String.valueOf(
                ThreadLocalRandom.current()
                        .nextInt(100000, 999999)
        );
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        nguoiDungRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(404, "Email không tồn tại"));

        String otp = generateOtp();

        Otp entity = new Otp();
        entity.setEmail(request.getEmail());
        entity.setOtp(otp);
        entity.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(entity);

        mailService.sendOtp(request.getEmail(), otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        Otp otp = otpRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy OTP"));

        if (otp.getUsed()) {
            throw new BusinessException(400, "OTP đã được sử dụng");
        }

        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "OTP hết hạn");
        }

        if (!otp.getOtp().equals(request.getOtp())) {
            throw new BusinessException(400, "OTP không đúng");
        }

        NguoiDung nguoiDung = nguoiDungRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy người dùng"));

        nguoiDung.setMatKhau(
                passwordEncoder.encode(request.getNewPassword())
        );

        nguoiDungRepository.save(nguoiDung);

        otp.setUsed(true);
        otpRepository.save(otp);
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (nguoiDungRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new BusinessException(400, "Tên đăng nhập đã tồn tại");
        }

        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "Email đã tồn tại");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setTenDangNhap(request.getTenDangNhap());
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setMatKhau(
                passwordEncoder.encode(request.getMatKhau())
        );

        // Nếu client không truyền vai trò thì mặc định USER
        if (request.getVaiTro() == null || request.getVaiTro().isBlank()) {
            nguoiDung.setVaiTro("USER");
        } else {
            nguoiDung.setVaiTro(request.getVaiTro().toUpperCase());
        }

        nguoiDungRepository.save(nguoiDung);
    }
    @Override
    @Transactional
    public void updateProfile(String tenDangNhap,
                              UpdateProfileRequest request) {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy người dùng"));

        if (!nguoiDung.getEmail().equals(request.getEmail())
                && nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "Email đã tồn tại");
        }
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setEmail(request.getEmail());

        nguoiDungRepository.save(nguoiDung);
    }
}