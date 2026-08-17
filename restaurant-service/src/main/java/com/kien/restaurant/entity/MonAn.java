package com.kien.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "MonAn")
public class MonAn  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maMon;
    private String tenMon;
    private BigDecimal donGia;
    private String trangThai;


    @Column(name = "anh")
    private String anh;
    @ManyToOne
    @JoinColumn(name = "MaLoai")
    private LoaiMon loaiMon;
}
