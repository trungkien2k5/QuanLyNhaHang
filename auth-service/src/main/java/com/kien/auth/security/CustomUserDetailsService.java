package com.kien.auth.security;

import com.kien.auth.entity.NguoiDung;
import com.kien.auth.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        String role = nguoiDung.getVaiTro() == null
                ? "CUSTOMER"
                : nguoiDung.getVaiTro().trim().toUpperCase();

        if (!java.util.Set.of("ADMIN", "MANAGER", "STAFF", "CUSTOMER").contains(role)) {
            throw new UsernameNotFoundException("Vai trò người dùng không hợp lệ");
        }

        return User.builder()
                .username(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhau())
                .roles(role)
                .build();
    }
}
