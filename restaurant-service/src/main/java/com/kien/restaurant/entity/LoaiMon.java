package com.kien.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "LoaiMon")
@Data
public class LoaiMon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maLoai;
    private String tenLoai;
}
