package com.kien.payment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Data
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHD;

    private LocalDateTime ngayLap;
    private BigDecimal tongTien;
    private String trangThai;

    @Column(name = "MaKH")
    private Integer maKH;
}