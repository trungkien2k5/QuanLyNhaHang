package com.kien.reservation.repository;

import com.kien.reservation.entity.DatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
@Repository
public interface DatBanRepository extends JpaRepository<DatBan,Integer>,
        JpaSpecificationExecutor<DatBan> {
    @Query("""
    SELECT COUNT(db) > 0
    FROM DatBan db, ChiTietDatBan ct
    WHERE ct.id.maDatBan = db.maDatBan
      AND ct.id.maBan = :maBan
      AND db.ngayDat = :ngayDat
      AND db.trangThai <> 'Đã hủy'
      AND db.gioBatDau < :gioKetThuc
      AND db.gioKetThuc > :gioBatDau
""")
    boolean existsConflict(
            @Param("maBan") Integer maBan,
            @Param("ngayDat") LocalDate ngayDat,
            @Param("gioBatDau") LocalTime gioBatDau,
            @Param("gioKetThuc") LocalTime gioKetThuc);
}
