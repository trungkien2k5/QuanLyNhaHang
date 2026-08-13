package com.kien.quanlynhahang.repository;
import com.kien.quanlynhahang.entity.LoaiMon;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LoaiMonRepository  extends JpaRepository<LoaiMon,Integer> {
    boolean existsByTenLoai(String tenLoai);
}
