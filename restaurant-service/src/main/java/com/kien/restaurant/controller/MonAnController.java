package com.kien.restaurant.controller;

import com.kien.restaurant.common.ApiResponse;
import com.kien.restaurant.dto.MonAnDTO;
import com.kien.restaurant.entity.MonAn;
import com.kien.restaurant.service.MonAnService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/monan")
public class MonAnController {

    private final MonAnService monAnService;

    @Operation(summary = "Lấy danh sách món ăn")
    @GetMapping
    public ApiResponse<Page<MonAn>> layTat(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) String tenMon,

            @RequestParam(required = false) Integer maLoai,

            @RequestParam(required = false) BigDecimal giaTu,

            @RequestParam(required = false) BigDecimal giaDen,

            @RequestParam(required = false) String trangThai,

            @RequestParam(defaultValue = "maMon") String sort,

            @RequestParam(defaultValue = "asc") String direction) {

        String tenMonLoc = tenMon != null ? tenMon : keyword;

        Page<MonAn> monAns = monAnService.layTat(
                page,
                size,
                tenMonLoc,
                maLoai,
                giaTu,
                giaDen,
                trangThai,
                sort,
                direction);

        return ApiResponse.<Page<MonAn>>builder()
                .success(true)
                .message("Lấy danh sách món ăn thành công")
                .data(monAns)
                .build();
    }
    @Operation(summary = "Lấy món ăn theo mã")
    @GetMapping("/{id}")
    public ApiResponse<MonAn> layTheoMa(@PathVariable Integer id) {

        MonAn monAn = monAnService.layTheoMa(id);

        return ApiResponse.<MonAn>builder()
                .success(true)
                .message("Lấy món ăn thành công")
                .data(monAn)
                .build();
    }

    @Operation(summary = "Thêm món ăn")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MonAn> themMon(
            @Valid @RequestPart("monAn") MonAnDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        MonAn monAn = monAnService.themMon(dto, file);

        return ApiResponse.<MonAn>builder()
                .success(true)
                .message("Thêm món ăn thành công")
                .data(monAn)
                .build();
    }

    @Operation(summary = "Cập nhật món ăn")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MonAn> capNhat(
            @PathVariable Integer id,
            @Valid @RequestPart("monAn") MonAnDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        MonAn monAn = monAnService.capNhat(id, dto, file);

        return ApiResponse.<MonAn>builder()
                .success(true)
                .message("Cập nhật món ăn thành công")
                .data(monAn)
                .build();
    }

    @Operation(summary = "Xóa món ăn")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> xoa(@PathVariable Integer id){

        monAnService.xoa(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa món ăn thành công")
                .build();
    }
}
