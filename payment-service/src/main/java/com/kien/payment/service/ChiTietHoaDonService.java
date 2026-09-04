package com.kien.payment.service;

import lombok.RequiredArgsConstructor;
import com.kien.payment.client.MonAnClient;
import com.kien.payment.dto.CapNhatSoLuongDTO;
import com.kien.payment.dto.ThemMonDTO;
import com.kien.payment.entity.ChiTietHoaDon;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.exception.ResourceNotFoundException;
import com.kien.payment.exception.BadRequestException;
import com.kien.payment.exception.ConflictException;
import com.kien.payment.id.ChiTietHoaDonId;
import com.kien.payment.repository.ChiTietHoaDonRepository;
import com.kien.payment.repository.HoaDonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChiTietHoaDonService {

    private final ChiTietHoaDonRepository ctr;
    private final HoaDonRepository hdr;
    private final MonAnClient monAnClient;

    @Transactional
    public ChiTietHoaDon themMon(Integer maHD, ThemMonDTO dto) {

        HoaDon hd = timHoaDon(maHD);
        kiemTraHoaDon(hd);

        BigDecimal donGia = monAnClient.layDonGia(dto.getMaMon());
        ChiTietHoaDonId id = new ChiTietHoaDonId(maHD, dto.getMaMon());

        Optional<ChiTietHoaDon> optional = ctr.findById(id);

        if (optional.isPresent()) {
            ChiTietHoaDon ct = optional.get();

            ct.setSoLuong(ct.getSoLuong() + dto.getSoLuong());
            ct.setThanhTien(
                    tinhThanhTien(ct.getDonGia(), ct.getSoLuong())
            );

            ChiTietHoaDon ketQua = ctr.save(ct);
            capNhatTongTien(hd);

            return ketQua;
        }

        ChiTietHoaDon ct = new ChiTietHoaDon();

        ct.setId(id);
        ct.setHoaDon(hd);
        ct.setMaMon(dto.getMaMon());
        ct.setSoLuong(dto.getSoLuong());
        ct.setDonGia(donGia);
        ct.setThanhTien(
                tinhThanhTien(donGia, dto.getSoLuong())
        );

        ChiTietHoaDon ketQua = ctr.save(ct);
        capNhatTongTien(hd);

        return ketQua;
    }

    @Transactional
    public void xoaMon(Integer maHD, Integer maMon) {
        ChiTietHoaDon ct = timChiTiet(maHD, maMon);

        kiemTraHoaDon(ct.getHoaDon());

        HoaDon hoaDon = ct.getHoaDon();

        ctr.delete(ct);
        capNhatTongTien(hoaDon);
    }

    @Transactional
    public ChiTietHoaDon capNhatSoLuong(
            Integer maHD,
            Integer maMon,
            CapNhatSoLuongDTO dto) {

        if (dto.getSoLuong() <= 0) {
            throw new BadRequestException("Số lượng phải lớn hơn 0");
        }

        ChiTietHoaDon ct = timChiTiet(maHD, maMon);

        kiemTraHoaDon(ct.getHoaDon());

        ct.setSoLuong(dto.getSoLuong());
        ct.setThanhTien(
                tinhThanhTien(ct.getDonGia(), dto.getSoLuong())
        );

        ChiTietHoaDon ketQua = ctr.save(ct);
        capNhatTongTien(ct.getHoaDon());

        return ketQua;
    }

    public List<ChiTietHoaDon> layTheoHoaDon(Integer maHD) {
        HoaDon hoaDon = timHoaDon(maHD);
        return ctr.findByHoaDon(hoaDon);
    }

    private HoaDon timHoaDon(Integer maHD) {
        return hdr.findById(maHD)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy hóa đơn"));
    }

    private ChiTietHoaDon timChiTiet(Integer maHD, Integer maMon) {
        ChiTietHoaDonId id = new ChiTietHoaDonId(maHD, maMon);

        return ctr.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy món trong hóa đơn"));
    }

    private void kiemTraHoaDon(HoaDon hoaDon) {
        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            throw new ConflictException(
                    "Hóa đơn đã thanh toán");
        }
    }

    private BigDecimal tinhThanhTien(
            BigDecimal donGia,
            Integer soLuong) {

        return donGia.multiply(
                BigDecimal.valueOf(soLuong));
    }

    private void capNhatTongTien(HoaDon hoaDon) {

        List<ChiTietHoaDon> ds =
                ctr.findByHoaDon(hoaDon);

        BigDecimal tongTien = BigDecimal.ZERO;

        for (ChiTietHoaDon ct : ds) {
            tongTien = tongTien.add(ct.getThanhTien());
        }

        hoaDon.setTongTien(tongTien);
        hdr.save(hoaDon);
    }
}
