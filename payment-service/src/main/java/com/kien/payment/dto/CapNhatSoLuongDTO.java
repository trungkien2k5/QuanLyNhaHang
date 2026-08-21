package com.kien.payment.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
public class CapNhatSoLuongDTO {
    @Schema(description = "Số lượng món cần cập nhật", example = "2")
    private Integer soLuong;
}
