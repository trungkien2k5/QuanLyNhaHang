package com.kien.restaurant.dto;

import com.kien.restaurant.entity.enums.TrangThaiBan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CapNhatTrangThaiBanDTO {

    @NotNull
    private TrangThaiBan status;

}
