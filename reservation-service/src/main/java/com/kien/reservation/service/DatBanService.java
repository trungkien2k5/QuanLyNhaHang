package com.kien.reservation.service;

import com.kien.reservation.client.RestaurantClient;
import com.kien.reservation.dto.DatBanDTO;
import com.kien.reservation.entity.ChiTietDatBan;
import com.kien.reservation.entity.DatBan;
import com.kien.reservation.entity.KhachHang;
import com.kien.reservation.exception.BadRequestException;
import com.kien.reservation.exception.ResourceNotFoundException;
import com.kien.reservation.id.ChiTietDatBanId;
import com.kien.reservation.repository.ChiTietDatBanRepository;
import com.kien.reservation.repository.DatBanRepository;
import com.kien.reservation.repository.KhachHangRepository;
import com.kien.reservation.specification.DatBanSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DatBanService {

    private final DatBanRepository datBanRepository;
    private final KhachHangRepository khachHangRepository;
    private final ChiTietDatBanRepository chiTietDatBanRepository;
    private final RestaurantClient restaurantClient;

    // =========================
    // THÊM ĐẶT BÀN
    // =========================
    public DatBan them(DatBanDTO dto) {

        // Kiểm tra giờ
        if (dto.getGioBatDau() == null
                || dto.getGioKetThuc() == null
                || !dto.getGioBatDau().isBefore(dto.getGioKetThuc())) {

            throw new BadRequestException(
                    "Giờ bắt đầu phải trước giờ kết thúc"
            );
        }

        // Kiểm tra bàn
        if (dto.getMaBan() == null) {
            throw new BadRequestException(
                    "Vui lòng chọn bàn"
            );
        }

        // Kiểm tra khách hàng
        if (dto.getMaKH() == null) {
            throw new BadRequestException(
                    "Vui lòng chọn khách hàng"
            );
        }

        // Kiểm tra trùng lịch đặt bàn
        boolean conflict = datBanRepository.existsConflict(
                dto.getMaBan(),
                dto.getNgayDat(),
                dto.getGioBatDau(),
                dto.getGioKetThuc()
        );

        if (conflict) {
            throw new BadRequestException(
                    "Bàn đã được đặt trong khoảng thời gian này"
            );
        }

        // Tìm khách hàng
        KhachHang kh = khachHangRepository.findById(dto.getMaKH())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy khách hàng"
                        ));

        // Tạo đặt bàn
        DatBan db = new DatBan();

        db.setKhachHang(kh);
        db.setNgayDat(dto.getNgayDat());
        db.setGioBatDau(dto.getGioBatDau());
        db.setGioKetThuc(dto.getGioKetThuc());
        db.setSoNguoi(dto.getSoNguoi());
        db.setTrangThai("Chờ xác nhận");

        DatBan saved = datBanRepository.save(db);

        // Tạo chi tiết đặt bàn
        ChiTietDatBan chiTiet = new ChiTietDatBan();

        ChiTietDatBanId id = new ChiTietDatBanId();
        id.setMaDatBan(saved.getMaDatBan());
        id.setMaBan(dto.getMaBan());

        chiTiet.setId(id);

        chiTietDatBanRepository.save(chiTiet);

        return saved;
    }

    // =========================
    // LẤY DANH SÁCH + FILTER
    // =========================
    public Page<DatBan> laytat(
            int page,
            int size,
            LocalDate ngay,
            Integer maKhuVuc,
            Integer maBan,
            String trangThai,
            String sort,
            String direction) {

        // =========================
        // XỬ LÝ SORT
        // =========================

        // Giá trị mặc định
        if (sort == null || sort.isBlank()) {
            sort = "maDatBan";
        }

        if (direction == null || direction.isBlank()) {
            direction = "asc";
        }

        // Chỉ cho phép sort theo các field được whitelist
        Set<String> truongDuocSapXep = Set.of(
                "maDatBan",
                "ngayDat",
                "gioBatDau",
                "gioKetThuc",
                "soNguoi",
                "trangThai"
        );

        // Kiểm tra field sort
        if (!truongDuocSapXep.contains(sort)) {
            throw new BadRequestException(
                    "Không được sắp xếp theo trường: " + sort
            );
        }

        // Chỉ cho phép asc hoặc desc
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Chiều sắp xếp phải là asc hoặc desc"
            );
        }

        // Tạo Sort sau khi đã whitelist
        Sort sapXep = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sapXep
        );

        // =========================
        // FILTER THEO KHU VỰC
        // =========================

        // Nếu lọc theo khu vực
        // thì lấy danh sách mã bàn từ Restaurant Service
        List<Integer> maBansTheoKhuVuc = null;

        if (maKhuVuc != null) {
            maBansTheoKhuVuc =
                    restaurantClient.layMaBanTheoKhuVuc(maKhuVuc);
        }

        // =========================
        // BUILD SPECIFICATION
        // =========================

        Specification<DatBan> specification =
                DatBanSpecificationBuilder.build(
                        ngay,
                        maBansTheoKhuVuc,
                        maBan,
                        trangThai
                );

        return datBanRepository.findAll(
                specification,
                pageable
        );
    }

    // =========================
    // LẤY CHI TIẾT
    // =========================
    public DatBan layChiTiet(Integer id) {

        return datBanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy đặt bàn"
                        ));
    }

    // =========================
    // HỦY ĐẶT BÀN
    // =========================
    public DatBan huyDatBan(Integer id) {

        DatBan datBan = datBanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy đặt bàn"
                        ));

        if ("Đã hủy".equals(datBan.getTrangThai())) {
            throw new BadRequestException(
                    "Đặt bàn đã được hủy"
            );
        }

        if ("Hoàn thành".equals(datBan.getTrangThai())) {
            throw new BadRequestException(
                    "Không thể hủy đặt bàn đã hoàn thành"
            );
        }

        datBan.setTrangThai("Đã hủy");

        return datBanRepository.save(datBan);
    }
}