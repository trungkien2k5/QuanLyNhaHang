package com.kien.restaurant.repository;

import com.kien.restaurant.entity.Ban;
import com.kien.restaurant.entity.enums.TrangThaiBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  BanRepository extends JpaRepository <Ban,Integer>{
    List<Ban> findByStatus(TrangThaiBan status);
    List<Ban> findByKhuVuc_MaKhuVuc(Integer maKhuVuc);
    List<Ban> findBySucChuaGreaterThanEqual(Integer sucChua);
    List<Ban> findByStatusAndSucChuaGreaterThanEqual(TrangThaiBan status, Integer sucChua);}
