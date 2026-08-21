package com.kien.payment.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "GiaoDich")
public class GiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maGD;
    private BigDecimal soTien;
    private String loaiThanhToan;
    private LocalDateTime ngayThanhToan;

    @ManyToOne
    @JoinColumn(name = "MaHD")
    private HoaDon hoaDon;
}
