package com.kien.payment.controller;

import lombok.RequiredArgsConstructor;

import com.kien.payment.common.ApiResponse;
import com.kien.payment.dto.CapNhatSoLuongDTO;
import com.kien.payment.dto.ThemMonDTO;
import com.kien.payment.entity.ChiTietHoaDon;
import com.kien.payment.service.ChiTietHoaDonService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chitiethoadon")
public class ChiTietHoaDonController {
    private final ChiTietHoaDonService chiTietHoaDonService;

    @Operation(summary = "Thêm món vào hóa đơn")
    @PostMapping("/{maHD}/them-mon")
    public ApiResponse<ChiTietHoaDon> themMon(@PathVariable Integer maHD, @RequestBody ThemMonDTO dto) {
        ChiTietHoaDon chiTietHoaDon = chiTietHoaDonService.themMon(maHD, dto);

        return ApiResponse.<ChiTietHoaDon>builder()
                .success(true)
                .message("Thêm món vào hóa đơn thành công")
                .data(chiTietHoaDon)
                .build();
    }

    @Operation(summary = "Xóa món khỏi hóa đơn")
    @DeleteMapping("/{maHD}/{maMon}")
    public ApiResponse<Void> xoaMon(@PathVariable Integer maHD, @PathVariable Integer maMon) {
        chiTietHoaDonService.xoaMon(maHD, maMon);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa món khỏi hóa đơn thành công")
                .build();
    }

    @Operation(summary = "Cập nhật số lượng món trong hóa đơn")
    @PutMapping("/{maHD}/{maMon}")
    public ApiResponse<ChiTietHoaDon> capNhatSoLuong(@PathVariable Integer maHD, @PathVariable Integer maMon, @RequestBody CapNhatSoLuongDTO dto) {
        ChiTietHoaDon chiTietHoaDon = chiTietHoaDonService.capNhatSoLuong(maHD, maMon, dto);

        return ApiResponse.<ChiTietHoaDon>builder()
                .success(true)
                .message("Cập nhật số lượng món thành công")
                .data(chiTietHoaDon)
                .build();
    }

    @Operation(summary = "Lấy chi tiết theo hóa đơn")
    @GetMapping("/hoadon/{maHD}")
    public ApiResponse<List<ChiTietHoaDon>> layTheoHoaDon(@PathVariable Integer maHD) {
        List<ChiTietHoaDon> chiTietHoaDons = chiTietHoaDonService.layTheoHoaDon(maHD);

        return ApiResponse.<List<ChiTietHoaDon>>builder()
                .success(true)
                .message("Lấy chi tiết hóa đơn thành công")
                .data(chiTietHoaDons)
                .build();
    }

}
