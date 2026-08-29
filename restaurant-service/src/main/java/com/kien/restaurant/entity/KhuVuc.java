package com.kien.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "KhuVuc")
@Data
public class KhuVuc extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKhuVuc;
    private String tenKhuVuc;

    @JsonIgnore
    @OneToMany(mappedBy = "khuVuc")
    private List<Ban> danhSachBan;
}
