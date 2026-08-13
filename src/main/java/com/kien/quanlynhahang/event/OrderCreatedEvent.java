package com.kien.quanlynhahang.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Integer maHD,
        Integer maKH,
        BigDecimal tongTien,
        String email,
        String hoTen
) {
}