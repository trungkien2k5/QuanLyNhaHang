package com.kien.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kien.restaurant.entity.enums.TrangThaiBan;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Ban")
@Data
public class Ban extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maBan;
    private String tenBan;
    private Integer sucChua;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false)
    private TrangThaiBan status = TrangThaiBan.TRONG;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "maKhuVuc")
    private KhuVuc khuVuc;
}
