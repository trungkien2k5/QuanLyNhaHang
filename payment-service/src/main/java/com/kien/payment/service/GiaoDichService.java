package com.kien.payment.service;

import lombok.RequiredArgsConstructor;

import com.kien.payment.dto.GiaoDichDTO;
import com.kien.payment.entity.GiaoDich;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.repository.GiaoDichRepository;
import com.kien.payment.repository.HoaDonRepository;
import org.springframework.stereotype.Service;
import com.kien.payment.exception.ResourceNotFoundException;
import com.kien.payment.exception.ConflictException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GiaoDichService {
    private final GiaoDichRepository giaoDichRepository;
    private final HoaDonRepository hoaDonRepository;

    public GiaoDich thanhToan(GiaoDichDTO dto) {

        HoaDon hoaDon = hoaDonRepository.findById(dto.getMaHD()).orElseThrow(()
                -> new ResourceNotFoundException("Không tìm thấy hóa đơn"));
        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            throw new ConflictException("Hóa đơn đã thanh toán");
        }
        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setHoaDon(hoaDon);
        giaoDich.setSoTien(hoaDon.getTongTien());
        giaoDich.setLoaiThanhToan(dto.getLoaiThanhToan());
        giaoDich.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setTrangThai("Đã thanh toán");
        hoaDonRepository.save(hoaDon);

        return giaoDichRepository.save(giaoDich);

    }
}
