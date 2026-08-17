package com.kien.reservation.controller;

import lombok.RequiredArgsConstructor;

import com.kien.reservation.common.ApiResponse;
import com.kien.reservation.dto.DatBanDTO;
import com.kien.reservation.entity.DatBan;
import com.kien.reservation.service.DatBanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/datban")
public class DatBanController {
    private final DatBanService datBanService;

    @Operation(summary = "Tạo đặt bàn")
    @PostMapping
    public ApiResponse<DatBan> them(@RequestBody DatBanDTO dto){
        DatBan datBan = datBanService.them(dto);

        return ApiResponse.<DatBan>builder()
                .success(true)
                .message("Tạo đặt bàn thành công")
                .data(datBan)
                .build();
    }

    @Operation(summary = "Lấy danh sách đặt bàn")
    @GetMapping
    public ApiResponse<Page<DatBan>> laytat(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            @RequestParam(required = false) Integer maKhuVuc,
            @RequestParam(required = false) Integer maBan,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "maDatBan") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<DatBan> datBans = datBanService.laytat(
                page,
                size,
                ngay,
                maKhuVuc,
                maBan,
                trangThai,
                sort,
                direction);

        return ApiResponse.<Page<DatBan>>builder()
                .success(true)
                .message("Lấy danh sách đặt bàn thành công")
                .data(datBans)
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<DatBan> layChiTiet(@PathVariable Integer id) {
        return ApiResponse.<DatBan>builder()
                .success(true)
                .message("Lấy chi tiết đặt bàn thành công")
                .data(datBanService.layChiTiet(id))
                .build();
    }
    @PutMapping("/{id}/cancel")
    public ApiResponse<DatBan> huyDatBan(@PathVariable Integer id) {
        return ApiResponse.<DatBan>builder()
                .success(true)
                .message("Hủy đặt bàn thành công")
                .data(datBanService.huyDatBan(id))
                .build();
    }
}
