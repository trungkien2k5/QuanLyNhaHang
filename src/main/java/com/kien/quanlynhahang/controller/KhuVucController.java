package com.kien.quanlynhahang.controller;

import com.kien.quanlynhahang.dto.request.CreateKhuVucRequest;
import com.kien.quanlynhahang.dto.request.UpdateKhuVucRequest;
import com.kien.quanlynhahang.service.KhuVucService;
import lombok.RequiredArgsConstructor;

import com.kien.quanlynhahang.common.ApiResponse;
import com.kien.quanlynhahang.entity.KhuVuc;
import com.kien.quanlynhahang.repository.KhuVucRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping ("/khuvuc")
public class KhuVucController {
private final KhuVucRepository khuVucRepository;
private final KhuVucService khuVucService;

@Operation(summary = "Lấy danh sách khu vực")
@GetMapping
    public ApiResponse<List<KhuVuc>> laytatca(){
    List<KhuVuc> khuVucs = khuVucRepository.findAll();

    return ApiResponse.<List<KhuVuc>>builder()
            .success(true)
            .message("Lấy danh sách khu vực thành công")
            .data(khuVucs)
            .build();
}
    @PostMapping
    public ApiResponse<KhuVuc> them(@RequestBody CreateKhuVucRequest request) {
        return ApiResponse.<KhuVuc>builder()
                .success(true)
                .message("Thêm khu vực thành công")
                .data(khuVucService.them(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<KhuVuc> capNhat(@PathVariable Integer id,
                                       @RequestBody UpdateKhuVucRequest request) {
        return ApiResponse.<KhuVuc>builder()
                .success(true)
                .message("Cập nhật khu vực thành công")
                .data(khuVucService.capNhat(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> xoa(@PathVariable Integer id) {

        khuVucService.xoa(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa khu vực thành công")
                .build();
    }
}
