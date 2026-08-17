package com.kien.restaurant.repository;
import com.kien.restaurant.entity.LoaiMon;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LoaiMonRepository  extends JpaRepository<LoaiMon,Integer> {
    boolean existsByTenLoai(String tenLoai);
}
