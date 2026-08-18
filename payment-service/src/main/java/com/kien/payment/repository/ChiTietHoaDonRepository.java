package com.kien.payment.repository;

import com.kien.payment.entity.ChiTietHoaDon;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.id.ChiTietHoaDonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietHoaDonRepository
        extends JpaRepository<ChiTietHoaDon, ChiTietHoaDonId> {

    List<ChiTietHoaDon> findByHoaDon(HoaDon hoaDon);
}
