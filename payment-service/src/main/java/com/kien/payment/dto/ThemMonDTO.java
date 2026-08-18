package com.kien.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ThemMonDTO {
    @Schema(description = "Mã món ăn", example = "1")
    private Integer maMon;

    @Schema(description = "Số lượng món", example = "2")
    private Integer soLuong;
}
