package com.kien.restaurant.controller;

import com.kien.restaurant.common.ApiResponse;
import com.kien.restaurant.dto.request.CreateLoaiMonRequest;
import com.kien.restaurant.dto.request.UpdateLoaiMonRequest;
import com.kien.restaurant.entity.LoaiMon;
import com.kien.restaurant.service.LoaiMonService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loaimon")
@RequiredArgsConstructor
public class LoaiMonController {

    private final LoaiMonService loaiMonService;

    @Operation(summary = "Thêm loại món")
    @PostMapping
    public ApiResponse<LoaiMon> them(@Valid @RequestBody CreateLoaiMonRequest request) {

        LoaiMon loaiMon = loaiMonService.them(request);

        return ApiResponse.<LoaiMon>builder()
                .success(true)
                .message("Thêm loại món thành công")
                .data(loaiMon)
                .build();
    }

    @Operation(summary = "Cập nhật loại món")
    @PutMapping("/{id}")
    public ApiResponse<LoaiMon> sua(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateLoaiMonRequest request) {

        LoaiMon loaiMon = loaiMonService.sua(id, request);

        return ApiResponse.<LoaiMon>builder()
                .success(true)
                .message("Cập nhật loại món thành công")
                .data(loaiMon)
                .build();
    }

    @Operation(summary = "Xóa loại món")
    @DeleteMapping("/{id}")
    public ApiResponse<String> xoa(@PathVariable Integer id) {

        loaiMonService.xoa(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Xóa loại món thành công")
                .data(null)
                .build();
    }

    @Operation(summary = "Lấy danh sách loại món")
    @GetMapping
    public ApiResponse<List<LoaiMon>> layTatCa() {

        List<LoaiMon> loaiMons = loaiMonService.layTatCa();

        return ApiResponse.<List<LoaiMon>>builder()
                .success(true)
                .message("Lấy danh sách loại món thành công")
                .data(loaiMons)
                .build();
    }
}
