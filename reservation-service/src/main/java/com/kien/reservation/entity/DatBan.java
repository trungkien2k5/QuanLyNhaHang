package com.kien.reservation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "DatBan")
@Data
public class DatBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDatBan;
    private LocalDate ngayDat;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private Integer soNguoi;
    private String trangThai;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;
}
