package com.kien.payment.controller;

import lombok.RequiredArgsConstructor;

import com.kien.payment.dto.GiaoDichDTO;
import com.kien.payment.entity.GiaoDich;
import com.kien.payment.service.GiaoDichService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/giaodich")
public class GiaoDichController {
    private final GiaoDichService giaoDichService;

    @Operation(summary = "Thanh toán hóa đơn")
    @PostMapping
    public GiaoDich thanhToan(@RequestBody GiaoDichDTO dto) {
        return giaoDichService.thanhToan(dto);
    }
}
