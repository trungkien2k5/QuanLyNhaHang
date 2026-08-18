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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
    @RequestMapping("/hoadon")
    public class HoaDonController {
        private final HoaDonService hoaDonService;
        @Operation(summary = "Lấy danh sách hóa đơn")
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
    @GetMapping("/{maHD}")
    public ApiResponse<HoaDon> layTheoId(@PathVariable Integer maHD) {
        HoaDon hoaDon = hoaDonService.layTheoId(maHD);

        return ApiResponse.<HoaDon>builder()
                .success(true)
                .message("Lấy hóa đơn thành công")
                .data(hoaDon)
                .build();
    }

        @Operation(summary = "Tạo hóa đơn")
        @PostMapping
        public ApiResponse<HoaDon> them(@RequestBody HoaDonDTO dto) {
            HoaDon hoaDon = hoaDonService.them(dto);

            return ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message("Tạo hóa đơn thành công")
                    .data(hoaDon)
                    .build();
        }

        @Operation(summary = "Thanh toán hóa đơn")
        @PutMapping("/{maHD}/thanhtoan")
         public ApiResponse<HoaDon> thanhToan(@PathVariable Integer maHD) {
            HoaDon hoaDon = hoaDonService.thanhToan(maHD);

            return ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message("Thanh toán hóa đơn thành công")
                    .data(hoaDon)
                    .build();
    }




        @GetMapping("/date")
        public ResponseEntity<?> timTheoNgay(
                @RequestParam LocalDate from,
                @RequestParam LocalDate to
        ) {
            return ResponseEntity.ok(hoaDonService.timTheoNgay(from, to));
        }

        @GetMapping("/status/{status}")
        public ResponseEntity<?> timTheoTrangThai(@PathVariable String status) {
            return ResponseEntity.ok(hoaDonService.timTheoTrangThai(status));
        }

        @GetMapping("/khachhang/{id}")
        public ResponseEntity<?> timTheoKhachHang(@PathVariable Integer id) {
            return ResponseEntity.ok(hoaDonService.timTheoKhachHang(id));
        }

        @PutMapping("/{id}/cancel")
        public ResponseEntity<?> huyHoaDon(@PathVariable Integer id) {
            return ResponseEntity.ok(hoaDonService.huyHoaDon(id));
        }
    }




