package com.kien.reservation.service;

import lombok.RequiredArgsConstructor;

import com.kien.reservation.dto.DatBanDTO;
import com.kien.reservation.entity.DatBan;
import com.kien.reservation.entity.KhachHang;
import com.kien.reservation.repository.DatBanRepository;
import com.kien.reservation.repository.KhachHangRepository;
import com.kien.reservation.specification.DatBanSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.kien.reservation.client.RestaurantClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DatBanService {
    private final DatBanRepository dbr;
    private final KhachHangRepository khrp;
private final RestaurantClient restaurantClient;

    public DatBan them(DatBanDTO dto) {
        DatBan db = new DatBan();
        KhachHang kh = khrp.findById(dto.getMaKH())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        db.setKhachHang(kh);
        db.setNgayDat(dto.getNgayDat());
        db.setGioBatDau(dto.getGioBatDau());
        db.setGioKetThuc(dto.getGioKetThuc());
        db.setSoNguoi(dto.getSoNguoi());
        db.setTrangThai("Chờ xác nhận");

        return dbr.save(db);
    }

    public Page<DatBan> laytat(
            int page,
            int size,
            LocalDate ngay,
            Integer maKhuVuc,
            Integer maBan,
            String trangThai,
            String sort,
            String direction) {

        Sort sapXep = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
Pageable pageable = PageRequest.of(page, size, sapXep);
        List<Integer> maBansTheoKhuVuc = null;

if (maKhuVuc != null) {
    maBansTheoKhuVuc = restaurantClient.layMaBanTheoKhuVuc(maKhuVuc);
}

Specification<DatBan> specification =
        DatBanSpecificationBuilder.build(
                ngay,
                maBansTheoKhuVuc,
                maBan,
                trangThai);

        return dbr.findAll(specification, pageable);
    }

    private boolean coDieuKienLoc(
            LocalDate ngay,
            Integer maKhuVuc,
            Integer maBan,
            String trangThai) {

        return ngay != null
                || maKhuVuc != null
                || maBan != null
                || (trangThai != null && !trangThai.isBlank());
    }

    public DatBan layChiTiet(Integer id) {
        return dbr.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));
    }

    public DatBan huyDatBan(Integer id) {
        DatBan datBan = dbr.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));

        if ("Đã hủy".equals(datBan.getTrangThai())) {
            throw new RuntimeException("Đặt bàn đã được hủy");
        }

        if ("Hoàn thành".equals(datBan.getTrangThai())) {
            throw new RuntimeException("Không thể hủy đặt bàn đã hoàn thành");
        }

        datBan.setTrangThai("Đã hủy");

        return dbr.save(datBan);
    }
}
