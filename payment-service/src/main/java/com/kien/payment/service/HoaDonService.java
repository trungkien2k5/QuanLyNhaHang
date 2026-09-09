package com.kien.payment.service;

import com.kien.payment.dto.HoaDonDTO;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.specification.HoaDonSpecification;
import org.springframework.data.jpa.domain.Specification;
import java.util.Set;
import com.kien.payment.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kien.payment.exception.ResourceNotFoundException;
import com.kien.payment.exception.ConflictException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    public Page<HoaDon> layTatCa(
            int page,
            int size,
            LocalDate tuNgay,
            LocalDate denNgay,
            Integer maKH,
            String trangThai,
            String sort,
            String direction) {

        Set<String> allowedSortFields = Set.of(
                "maHD",
                "maKH",
                "ngayLap",
                "tongTien",
                "trangThai"
        );

        if (!allowedSortFields.contains(sort)) {
            throw new ConflictException(
                    "Field sort không hợp lệ: " + sort
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new ConflictException(
                    "Direction phải là asc hoặc desc"
            );
        }

        Sort sapXep = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sapXep);

        Specification<HoaDon> specification =
                Specification
                        .where(HoaDonSpecification.coTuNgay(
                                tuNgay != null
                                        ? tuNgay.atStartOfDay()
                                        : null))
                        .and(HoaDonSpecification.coDenNgay(
                                denNgay != null
                                        ? denNgay.plusDays(1)
                                        .atStartOfDay()
                                        .minusNanos(1)
                                        : null))
                        .and(HoaDonSpecification.coMaKH(maKH))
                        .and(HoaDonSpecification.coTrangThai(trangThai));

        return hoaDonRepository.findAll(specification, pageable);
    }

    public HoaDon layTheoId(Integer maHD) {
        return timHoaDon(maHD);
    }

    @Transactional
    public HoaDon them(HoaDonDTO dto) {
        HoaDon hoaDon = new HoaDon();

        hoaDon.setMaKH(dto.getMaKH());
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDon.setTongTien(BigDecimal.ZERO);
        hoaDon.setTrangThai("Chưa thanh toán");

        return hoaDonRepository.save(hoaDon);
    }

    public List<HoaDon> timTheoNgay(LocalDate from, LocalDate to) {
        return hoaDonRepository.findByNgayLapBetween(
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay().minusNanos(1)
        );
    }

    public List<HoaDon> timTheoTrangThai(String trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    public List<HoaDon> timTheoKhachHang(Integer maKH) {
        return hoaDonRepository.findByMaKH(maKH);
    }

    @Transactional
    public HoaDon huyHoaDon(Integer maHD) {
        HoaDon hoaDon = timHoaDon(maHD);

        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            throw new ConflictException(
                    "Không thể hủy hóa đơn đã thanh toán");
        }

        hoaDon.setTrangThai("Đã hủy");

        return hoaDonRepository.save(hoaDon);
    }

    @Transactional
    public HoaDon thanhToan(Integer maHD) {
        HoaDon hoaDon = timHoaDon(maHD);

        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            throw new ConflictException("Hóa đơn đã thanh toán");
        }

        hoaDon.setTrangThai("Đã thanh toán");

        return hoaDonRepository.save(hoaDon);
    }

    private HoaDon timHoaDon(Integer maHD) {
        return hoaDonRepository.findById(maHD)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy hóa đơn"));
    }
}
