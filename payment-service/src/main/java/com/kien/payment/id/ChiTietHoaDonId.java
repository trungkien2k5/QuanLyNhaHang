package com.kien.payment.id;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChiTietHoaDonId implements Serializable {
    private Integer maHD;
    private Integer maMon;
}
