package com.kien.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class KhachHangDTO {
    @Schema(description = "Họ tên khách hàng", example = "Nguyễn Văn A")
    private String hoTen;

    @Schema(description = "Số điện thoại khách hàng", example = "0901234567")
    private String sdt;

    @Schema(description = "Email khách hàng", example = "abc@gmail.com")
    @Email(message = "Email không hợp lệ")
    private String email;
}
