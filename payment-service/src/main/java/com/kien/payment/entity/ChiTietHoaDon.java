package com.kien.payment.entity;

import com.kien.payment.id.ChiTietHoaDonId;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDon {

    @EmbeddedId
    private ChiTietHoaDonId id = new ChiTietHoaDonId();

    @ManyToOne
    @MapsId("maHD")
    @JoinColumn(name = "MaHD")
    private HoaDon hoaDon;

    @Column(name = "MaMon")
    private Integer maMon;

    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}
