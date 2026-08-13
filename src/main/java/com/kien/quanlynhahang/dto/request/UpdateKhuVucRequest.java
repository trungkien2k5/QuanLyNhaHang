package com.kien.quanlynhahang.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateKhuVucRequest {

    @NotBlank(message = "Tên khu vực không được để trống")
    private String tenKhuVuc;
}