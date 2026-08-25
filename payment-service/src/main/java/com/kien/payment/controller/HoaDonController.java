package com.kien.payment.controller;

import lombok.RequiredArgsConstructor;

import com.kien.payment.common.ApiResponse;
import com.kien.payment.dto.HoaDonDTO;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.service.HoaDonService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/hoadon")
public class HoaDonController {

    private final HoaDonService hoaDonService;

    @Operation(summary = "Lấy danh sách hóa đơn")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping
    public ApiResponse<Page<HoaDon>> laytat(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer maKH,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "maHD") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<HoaDon> hoaDons = hoaDonService.layTatCa(
                page,
                size,
                tuNgay,
                denNgay,
                maKH,
                trangThai,
                sort,
                direction);

        return ApiResponse.<Page<HoaDon>>builder()
                .success(true)
                .message("Lấy danh sách hóa đơn thành công")
                .data(hoaDons)
                .build();
    }

    @Operation(summary = "Lấy hóa đơn theo mã")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping("/{maHD}")
    public ApiResponse<HoaDon> layTheoId(
            @PathVariable Integer maHD) {

        HoaDon hoaDon = hoaDonService.layTheoId(maHD);

        return ApiResponse.<HoaDon>builder()
                .success(true)
                .message("Lấy hóa đơn thành công")
                .data(hoaDon)
                .build();
    }

    @Operation(summary = "Tạo hóa đơn")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @PostMapping
    public ApiResponse<HoaDon> them(
            @RequestBody HoaDonDTO dto) {

        HoaDon hoaDon = hoaDonService.them(dto);

        return ApiResponse.<HoaDon>builder()
                .success(true)
                .message("Tạo hóa đơn thành công")
                .data(hoaDon)
                .build();
    }

    @Operation(summary = "Thanh toán hóa đơn")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @PutMapping("/{maHD}/thanhtoan")
    public ApiResponse<HoaDon> thanhToan(
            @PathVariable Integer maHD) {

        HoaDon hoaDon = hoaDonService.thanhToan(maHD);

        return ApiResponse.<HoaDon>builder()
                .success(true)
                .message("Thanh toán hóa đơn thành công")
                .data(hoaDon)
                .build();
    }

    @Operation(summary = "Tìm hóa đơn theo ngày")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/date")
    public ResponseEntity<?> timTheoNgay(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        return ResponseEntity.ok(
                hoaDonService.timTheoNgay(from, to)
        );
    }

    @Operation(summary = "Tìm hóa đơn theo trạng thái")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> timTheoTrangThai(
            @PathVariable String status) {

        return ResponseEntity.ok(
                hoaDonService.timTheoTrangThai(status)
        );
    }

    @Operation(summary = "Tìm hóa đơn theo khách hàng")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/khachhang/{id}")
    public ResponseEntity<?> timTheoKhachHang(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                hoaDonService.timTheoKhachHang(id)
        );
    }

    @Operation(summary = "Hủy hóa đơn")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> huyHoaDon(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                hoaDonService.huyHoaDon(id)
        );
    }
}