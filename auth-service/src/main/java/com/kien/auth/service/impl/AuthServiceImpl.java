package com.kien.auth.service;

import com.kien.auth.dto.reponse.LoginResponse;
import com.kien.auth.dto.reponse.MeResponse;
import com.kien.auth.dto.reponse.RefreshTokenResponse;
import com.kien.auth.dto.request.*;
import com.kien.auth.entity.NguoiDung;
import com.kien.auth.entity.Otp;
import com.kien.auth.entity.RefreshToken;
import com.kien.auth.exception.BadRequestException;
import com.kien.auth.exception.ResourceNotFoundException;
import com.kien.auth.mail.service.MailService;
import com.kien.auth.repository.NguoiDungRepository;
import com.kien.auth.repository.OtpRepository;
import com.kien.auth.repository.RefreshTokenRepository;
import com.kien.auth.security.JwtService;
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

        String accessToken = jwtService.taoToken(
                nguoiDung.getTenDangNhap(),
                nguoiDung.getVaiTro()
        );

        String refreshToken =
                jwtService.taoRefreshToken(nguoiDung.getTenDangNhap());

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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng"
                        ));

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
    public void changePassword(
            String tenDangNhap,
            ChangePasswordRequest request) {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng"
                        ));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                nguoiDung.getMatKhau())) {

            throw new BadRequestException(
                    "Mật khẩu cũ không đúng"
            );
        }

        nguoiDung.setMatKhau(
                passwordEncoder.encode(request.getNewPassword())
        );

        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request) {

        RefreshToken token = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Refresh Token không tồn tại"
                        ));

        if (Boolean.TRUE.equals(token.getRevoked())) {
            throw new BadRequestException(
                    "Refresh Token đã bị thu hồi"
            );
        }

        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    "Refresh Token đã hết hạn"
            );
        }

        NguoiDung nguoiDung = token.getNguoiDung();

        // Thu hồi Refresh Token cũ
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        // Tạo Refresh Token mới
        String newRefreshToken =
                jwtService.taoRefreshToken(
                        nguoiDung.getTenDangNhap()
                );

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(newRefreshToken);
        newToken.setNguoiDung(nguoiDung);
        newToken.setExpiredAt(
                LocalDateTime.now().plusDays(7)
        );
        newToken.setRevoked(false);

        refreshTokenRepository.save(newToken);

        // Tạo Access Token mới
        String accessToken =
                jwtService.taoToken(
                        nguoiDung.getTenDangNhap(),
                        nguoiDung.getVaiTro()
                );

        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken
        );
    }

    @Override
    public void logout(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Refresh Token không tồn tại"
                        ));

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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email không tồn tại"
                        ));

        // Rate limit: chỉ cho gửi OTP mới sau 60 giây
        otpRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .ifPresent(lastOtp -> {

                    if (lastOtp.getCreatedAt() != null
                            && lastOtp.getCreatedAt()
                            .plusSeconds(60)
                            .isAfter(LocalDateTime.now())) {

                        throw new BadRequestException(
                               
                                "Vui lòng đợi 60 giây trước khi yêu cầu OTP mới"
                        );
                    }
                });

        String otp = generateOtp();

        Otp entity = new Otp();
        entity.setEmail(request.getEmail());
        entity.setOtp(otp);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        entity.setUsed(false);
        entity.setAttemptCount(0);

        otpRepository.save(entity);

        mailService.sendOtp(request.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        Otp otp = otpRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy OTP"
                        ));

        // OTP đã sử dụng
        if (Boolean.TRUE.equals(otp.getUsed())) {
            throw new BadRequestException(
                    "OTP đã được sử dụng"
            );
        }

        // OTP hết hạn
        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    "OTP hết hạn"
            );
        }

        // Giới hạn 5 lần nhập sai
        if (otp.getAttemptCount() >= 5) {
            throw new BadRequestException(
                    "OTP đã bị khóa do nhập sai quá nhiều lần"
            );
        }

        // OTP sai
        if (!otp.getOtp().equals(request.getOtp())) {

            otp.setAttemptCount(
                    otp.getAttemptCount() + 1
            );

            // Nếu sai lần thứ 5 thì khóa luôn
            if (otp.getAttemptCount() >= 5) {
                otp.setUsed(true);
            }

            otpRepository.save(otp);

            throw new BadRequestException(
                    "OTP không đúng"
            );
        }

        // OTP đúng
        NguoiDung nguoiDung = nguoiDungRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng"
                        ));

        nguoiDung.setMatKhau(
                passwordEncoder.encode(request.getNewPassword())
        );

        nguoiDungRepository.save(nguoiDung);

        // Đánh dấu OTP đã sử dụng
        otp.setUsed(true);
        otpRepository.save(otp);
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (nguoiDungRepository.existsByTenDangNhap(
                request.getTenDangNhap())) {

            throw new BadRequestException(
                    "Tên đăng nhập đã tồn tại"
            );
        }

        if (nguoiDungRepository.existsByEmail(
                request.getEmail())) {

            throw new BadRequestException(
                    "Email đã tồn tại"
            );
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setTenDangNhap(request.getTenDangNhap());
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setMatKhau(
                passwordEncoder.encode(request.getMatKhau())
        );

        nguoiDung.setVaiTro("CUSTOMER");

        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public void updateProfile(
            String tenDangNhap,
            UpdateProfileRequest request) {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng"
                        ));

        if (!nguoiDung.getEmail().equals(request.getEmail())
                && nguoiDungRepository.existsByEmail(
                request.getEmail())) {

            throw new BadRequestException(
                    "Email đã tồn tại"
            );
        }

        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setEmail(request.getEmail());

        nguoiDungRepository.save(nguoiDung);
    }
}

