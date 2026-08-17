package com.kien.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLoaiMonRequest {

    @NotBlank(message = "Tên loại món không được để trống")
    private String tenLoai;
}
