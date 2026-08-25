package com.kien.auth.service;

import com.kien.auth.entity.NguoiDung;
import com.kien.auth.exception.BusinessException;
import com.kien.auth.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private static final Set<String> ROLES = Set.of("ADMIN", "MANAGER", "STAFF", "CUSTOMER");

    private final NguoiDungRepository nguoiDungRepository;

    @Transactional
    public void changeRole(Integer maND, String vaiTro) {
        String role = vaiTro == null ? "" : vaiTro.trim().toUpperCase();
        if (!ROLES.contains(role)) {
            throw new BusinessException(400, "Vai trò không hợp lệ");
        }

        NguoiDung nguoiDung = nguoiDungRepository.findById(maND)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy người dùng"));

        nguoiDung.setVaiTro(role);
        nguoiDungRepository.save(nguoiDung);
    }
}
