package com.kien.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.kien.payment.entity.HoaDon;

@Data
public class HoaDonDTO {

    @Schema(description = "Mã khách hàng lập hóa đơn", example = "1")
    private Integer maKH;

}