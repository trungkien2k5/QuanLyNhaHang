package com.kien.reservation.id;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class ChiTietDatBanId implements Serializable {
    private Integer maDatBan;
    private Integer maBan;
}
