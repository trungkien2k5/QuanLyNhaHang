package com.kien.auth.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "NguoiDung")
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maND;
    private String hoTen;
    @Column(unique = true)
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;

    @Column(unique = true)
    private String email;
    @OneToMany(mappedBy = "nguoiDung")
    private List<RefreshToken> refreshTokens;
}
