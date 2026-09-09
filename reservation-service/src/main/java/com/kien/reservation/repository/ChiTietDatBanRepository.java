package com.kien.reservation.repository;

import com.kien.reservation.entity.ChiTietDatBan;
import com.kien.reservation.id.ChiTietDatBanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietDatBanRepository
        extends JpaRepository<ChiTietDatBan, ChiTietDatBanId> {
}