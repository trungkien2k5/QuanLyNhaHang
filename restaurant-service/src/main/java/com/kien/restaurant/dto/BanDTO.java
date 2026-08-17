package com.kien.restaurant.dto;

import com.kien.restaurant.entity.enums.TrangThaiBan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BanDTO {
    @Schema(description = "Tên bàn", example = "Bàn 01")
    private String tenBan;

    @Schema(description = "Sức chứa của bàn", example = "4")
    private Integer sucChua;

    @Schema(description = "Trạng thái bàn", example = "TRONG")
    private TrangThaiBan trangThai;

    @Schema(description = "Mã khu vực của bàn", example = "1")
    private Integer maKhuVuc;
}
