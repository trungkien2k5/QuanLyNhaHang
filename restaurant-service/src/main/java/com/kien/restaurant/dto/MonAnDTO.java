package com.kien.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
public class MonAnDTO {

    @Schema(description = "Tên món ăn", example = "Cơm chiên")
    @NotBlank(message = "Tên món không được để trống")
    private String tenMon;

    @Schema(description = "Đơn giá bán", example = "50000")
    @NotNull(message = "Đơn giá không được để trống")
    @Positive(message = "Đơn giá phải lớn hơn 0")
    private BigDecimal donGia;

    @Schema(description = "Mã loại món ăn", example = "1")
    @NotNull(message = "Mã loại không được để trống")
    private Integer maLoai;

    @Schema(description = "Đường dẫn hoặc tên file ảnh món ăn", example = "com-chien.jpg")
    private String anh;
}
