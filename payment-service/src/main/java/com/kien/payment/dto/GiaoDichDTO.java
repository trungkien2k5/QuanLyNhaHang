package com.kien.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GiaoDichDTO {
    @Schema(description = "Mã hóa đơn cần thanh toán", example = "1")
    private Integer maHD;

    @Schema(description = "Loại thanh toán", example = "TIEN_MAT")
    private String loaiThanhToan;
}
