package com.kien.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateKhuVucRequest {

    @NotBlank(message = "Tên khu vực không được để trống")
    private String tenKhuVuc;
}
