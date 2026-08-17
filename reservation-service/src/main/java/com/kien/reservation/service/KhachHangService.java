package com.kien.reservation.service;

import lombok.RequiredArgsConstructor;

import com.kien.reservation.dto.KhachHangDTO;
import com.kien.reservation.entity.KhachHang;
import com.kien.reservation.repository.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class KhachHangService {
    private final KhachHangRepository khrp;

    public KhachHang them (KhachHangDTO dto ){
        KhachHang  kh = new KhachHang();
        kh.setHoTen(dto.getHoTen());
        kh.setSdt(dto.getSdt());
        kh.setEmail(dto.getEmail());
        kh.setDiemTichLuy(0);
        return khrp.save(kh);
    }
    public List<KhachHang> laytat(){
        return khrp.findAll();
    }

}
