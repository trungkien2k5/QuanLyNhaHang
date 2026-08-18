package com.kien.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChiTietHoaDonDTO {
    @Schema(description = "Mã món", example = "2")
    @NotNull
    private Integer maMon;
    @Schema(description = "Số lượng món ", example = "2")
    @NotNull
    @Min(1)
    private Integer soLuong;

}
