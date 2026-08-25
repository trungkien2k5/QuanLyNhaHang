package com.kien.restaurant.controller;

import com.kien.restaurant.dto.request.CreateKhuVucRequest;
import com.kien.restaurant.dto.request.UpdateKhuVucRequest;
import com.kien.restaurant.service.KhuVucService;
import lombok.RequiredArgsConstructor;

import com.kien.restaurant.common.ApiResponse;
import com.kien.restaurant.entity.KhuVuc;
import com.kien.restaurant.repository.KhuVucRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/khuvuc")
public class KhuVucController {

    private final KhuVucRepository khuVucRepository;
    private final KhuVucService khuVucService;

    @Operation(summary = "Lấy danh sách khu vực")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping
    public ApiResponse<List<KhuVuc>> laytatca() {

        List<KhuVuc> khuVucs = khuVucRepository.findAll();

        return ApiResponse.<List<KhuVuc>>builder()
                .success(true)
                .message("Lấy danh sách khu vực thành công")
                .data(khuVucs)
                .build();
    }

    @Operation(summary = "Thêm khu vực")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ApiResponse<KhuVuc> them(
            @RequestBody CreateKhuVucRequest request) {

        return ApiResponse.<KhuVuc>builder()
                .success(true)
                .message("Thêm khu vực thành công")
                .data(khuVucService.them(request))
                .build();
    }

    @Operation(summary = "Cập nhật khu vực")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ApiResponse<KhuVuc> capNhat(
            @PathVariable Integer id,
            @RequestBody UpdateKhuVucRequest request) {

        return ApiResponse.<KhuVuc>builder()
                .success(true)
                .message("Cập nhật khu vực thành công")
                .data(khuVucService.capNhat(id, request))
                .build();
    }

    @Operation(summary = "Xóa khu vực")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> xoa(
            @PathVariable Integer id) {

        khuVucService.xoa(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa khu vực thành công")
                .build();
    }
}