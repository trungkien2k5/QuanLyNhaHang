package com.kien.quanlynhahang.service;

import com.kien.quanlynhahang.dto.ChiTietHoaDonDTO;
import com.kien.quanlynhahang.dto.HoaDonDTO;
import com.kien.quanlynhahang.entity.ChiTietHoaDon;
import com.kien.quanlynhahang.entity.HoaDon;
import com.kien.quanlynhahang.entity.KhachHang;
import com.kien.quanlynhahang.entity.MonAn;
import com.kien.quanlynhahang.event.OrderCreatedEvent;
import com.kien.quanlynhahang.exception.KhongTimThayException;
import com.kien.quanlynhahang.exception.NghiepVuException;
import com.kien.quanlynhahang.id.ChiTietHoaDonId;
import com.kien.quanlynhahang.kafka.KafkaProducer;
import com.kien.quanlynhahang.mail.service.MailService;
import com.kien.quanlynhahang.mapper.HoaDonMapper;
import com.kien.quanlynhahang.repository.ChiTietHoaDonRepository;
import com.kien.quanlynhahang.repository.HoaDonRepository;
import com.kien.quanlynhahang.repository.KhachHangRepository;
import com.kien.quanlynhahang.repository.MonAnRepository;
import com.kien.quanlynhahang.specification.HoaDonSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final HoaDonMapper hoaDonMapper;
    private final MonAnRepository monAnRepository;
    private final KafkaProducer kafkaProducer;

    public Page<HoaDon> layTatCa(
            int page,
            int size,
            LocalDate tuNgay,
            LocalDate denNgay,
            Integer maKH,
            String trangThai,
            String sort,
            String direction) {

        Sort sapXep = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sapXep);

        if (!coDieuKienLoc(tuNgay, denNgay, maKH, trangThai)) {
            return hoaDonRepository.findAll(pageable);
        }

        Specification<HoaDon> specification =
                HoaDonSpecificationBuilder.build(
                        tuNgay,
                        denNgay,
                        maKH,
                        trangThai);

        return hoaDonRepository.findAll(
                specification,
                pageable);
    }

    public HoaDon layTheoId(Integer maHD) {
        return timHoaDon(maHD);
    }

    @Transactional
    public HoaDon them(HoaDonDTO dto) {

        KhachHang kh = timKhachHang(dto.getMaKH());

        if (dto.getChiTietHoaDons() == null
                || dto.getChiTietHoaDons().isEmpty()) {
            throw new NghiepVuException("Hóa đơn phải có ít nhất 1 món");
        }

        HoaDon hoaDon = hoaDonMapper.toEntity(dto);
        hoaDon.setKhachHang(kh);
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDon.setTongTien(BigDecimal.ZERO);
        hoaDon.setTrangThai("Chưa thanh toán");

        // Lưu hóa đơn trước để lấy MaHD
        hoaDon = hoaDonRepository.save(hoaDon);

        BigDecimal tongTien = BigDecimal.ZERO;

        for (ChiTietHoaDonDTO item : dto.getChiTietHoaDons()) {

            MonAn monAn = monAnRepository.findById(item.getMaMon())
                    .orElseThrow(() ->
                            new KhongTimThayException("Không tìm thấy món"));

            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setId(new ChiTietHoaDonId());

            ct.setHoaDon(hoaDon);
            ct.setMonAn(monAn);
            ct.setSoLuong(item.getSoLuong());
            ct.setDonGia(monAn.getDonGia());

            BigDecimal thanhTien = monAn.getDonGia()
                    .multiply(BigDecimal.valueOf(item.getSoLuong()));

            ct.setThanhTien(thanhTien);

            chiTietHoaDonRepository.save(ct);

            tongTien = tongTien.add(thanhTien);
        }

        // Cập nhật tổng tiền
        hoaDon.setTongTien(tongTien);
        hoaDon = hoaDonRepository.save(hoaDon);


        // GỬI EVENT kafa
        OrderCreatedEvent event = new OrderCreatedEvent(
                hoaDon.getMaHD(),
                hoaDon.getKhachHang().getMaKH(),
                hoaDon.getTongTien(),
                hoaDon.getKhachHang().getEmail(),
                hoaDon.getKhachHang().getHoTen()
        );


        kafkaProducer.sendOrderCreated(event);


        // GỬI MAIL


        return hoaDon;
    }

    public List<HoaDon> timTheoNgay(
            LocalDate from,
            LocalDate to) {

        return hoaDonRepository.findByNgayLapBetween(
                from.atStartOfDay(),
                to.plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1)
        );
    }

    public List<HoaDon> timTheoTrangThai(String trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    public List<HoaDon> timTheoKhachHang(Integer maKH) {
        return hoaDonRepository.findByKhachHang_MaKH(maKH);
    }

    @Transactional
    public HoaDon huyHoaDon(Integer maHD) {

        HoaDon hoaDon = timHoaDon(maHD);

        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            throw new NghiepVuException(
                    "Không thể hủy hóa đơn đã thanh toán");
        }

        hoaDon.setTrangThai("Đã hủy");

        return hoaDonRepository.save(hoaDon);
    }

    @Transactional
    public void capNhatTongTien(HoaDon hoaDon) {

        List<ChiTietHoaDon> ds =
                chiTietHoaDonRepository.findByHoaDon(hoaDon);

        BigDecimal tongTien = BigDecimal.ZERO;

        for (ChiTietHoaDon ct : ds) {
            tongTien = tongTien.add(ct.getThanhTien());
        }

        hoaDon.setTongTien(tongTien);

        hoaDonRepository.save(hoaDon);
    }

    @Transactional
    public HoaDon thanhToan(Integer maHD) {

        HoaDon hoaDon = timHoaDon(maHD);

        kiemTraDaThanhToan(hoaDon);

        hoaDon.setTrangThai("Đã thanh toán");

        return hoaDonRepository.save(hoaDon);
    }

    private HoaDon timHoaDon(Integer maHD) {

        return hoaDonRepository.findById(maHD)
                .orElseThrow(() ->
                        new KhongTimThayException(
                                "Không tìm thấy hóa đơn"));
    }

    private KhachHang timKhachHang(Integer maKH) {

        return khachHangRepository.findById(maKH)
                .orElseThrow(() ->
                        new KhongTimThayException(
                                "Không tìm thấy khách hàng"));
    }

    private void kiemTraDaThanhToan(HoaDon hoaDon) {

        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            throw new NghiepVuException(
                    "Hóa đơn đã thanh toán");
        }
    }

    private boolean coDieuKienLoc(
            LocalDate tuNgay,
            LocalDate denNgay,
            Integer maKH,
            String trangThai) {

        return tuNgay != null
                || denNgay != null
                || maKH != null
                || (trangThai != null
                && !trangThai.isBlank());
    }
}