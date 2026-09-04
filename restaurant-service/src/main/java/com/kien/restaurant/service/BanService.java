package com.kien.restaurant.service;

import com.kien.restaurant.entity.enums.TrangThaiBan;
import com.kien.restaurant.mapper.BanMapper;
import lombok.RequiredArgsConstructor;
import com.kien.restaurant.exception.ResourceNotFoundException;
import com.kien.restaurant.dto.BanDTO;
import com.kien.restaurant.entity.Ban;
import com.kien.restaurant.entity.KhuVuc;
import com.kien.restaurant.repository.BanRepository;
import com.kien.restaurant.repository.KhuVucRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BanService {
    private final BanRepository banRepository;
    private final KhuVucRepository khuVucRepository;
    private final BanMapper banMapper;
    public Ban themban(@RequestBody BanDTO dto) {
        KhuVuc khuVuc = khuVucRepository.findById(dto.getMaKhuVuc()).orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));

        Ban ban = new Ban();
        ban.setTenBan(dto.getTenBan());
        ban.setSucChua(dto.getSucChua());
        ban.setStatus(dto.getTrangThai());
        ban.setKhuVuc(khuVuc);
        return banRepository.save(ban);
    }
    public List<BanDTO> layTheoKhuVuc(Integer maKhuVuc) {
        return banRepository.findByKhuVuc_MaKhuVuc(maKhuVuc)
                .stream()
                .map(banMapper::toDTO)
                .toList();
    }
    public List<Ban> laybantheotrangthai(TrangThaiBan status) {
        return banRepository.findByStatus(status);
    }

    public List<Ban> timbanducho (Integer songuoi){
        return banRepository.findBySucChuaGreaterThanEqual(songuoi);
    }

    public List<Ban> timbantrongducho (Integer songuoi){
        return banRepository.findByStatusAndSucChuaGreaterThanEqual(TrangThaiBan.TRONG, songuoi);    }
    public BanDTO capNhatTrangThai(Integer id, TrangThaiBan status) {

        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn"));

        ban.setStatus(status);

        banRepository.save(ban);

        return banMapper.toDTO(ban);
    }
}
