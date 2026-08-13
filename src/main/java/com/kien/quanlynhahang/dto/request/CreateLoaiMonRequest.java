package com.kien.quanlynhahang.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLoaiMonRequest {

    @NotBlank(message = "Tên loại món không được để trống")
    private String tenLoai;
}