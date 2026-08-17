package com.kien.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class DatBanDTO {
    @Schema(description = "Mã khách hàng đặt bàn", example = "1")
    private Integer maKH;

    @Schema(description = "Ngày đặt bàn", example = "2026-08-01")
    private LocalDate ngayDat;

    @Schema(description = "Giờ bắt đầu đặt bàn", example = "18:00")
    private LocalTime gioBatDau;

    @Schema(description = "Giờ kết thúc đặt bàn", example = "20:00")
    private LocalTime gioKetThuc;

    @Schema(description = "Số người dùng bàn", example = "4")
    private Integer soNguoi;

}
