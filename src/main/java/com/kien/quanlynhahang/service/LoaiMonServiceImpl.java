package com.kien.quanlynhahang.service;

import com.kien.quanlynhahang.dto.request.CreateLoaiMonRequest;
import com.kien.quanlynhahang.dto.request.UpdateLoaiMonRequest;
import com.kien.quanlynhahang.entity.LoaiMon;
import com.kien.quanlynhahang.repository.LoaiMonRepository;
import com.kien.quanlynhahang.service.LoaiMonService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoaiMonServiceImpl implements LoaiMonService {

    private final LoaiMonRepository loaiMonRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"loaimon", "monan"}, allEntries = true)
    public LoaiMon them(CreateLoaiMonRequest request) {
        if (loaiMonRepository.existsByTenLoai(request.getTenLoai())) {
            throw new RuntimeException("Tên loại món đã tồn tại");
        }

        LoaiMon loaiMon = new LoaiMon();
        loaiMon.setTenLoai(request.getTenLoai());

        return loaiMonRepository.save(loaiMon);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loaimon", "monan"}, allEntries = true)
    public LoaiMon sua(Integer id, UpdateLoaiMonRequest request) {
        LoaiMon loaiMon = loaiMonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại món"));

        loaiMon.setTenLoai(request.getTenLoai());

        return loaiMonRepository.save(loaiMon);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loaimon", "monan"}, allEntries = true)
    public void xoa(Integer id) {
        LoaiMon loaiMon = loaiMonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại món"));

        loaiMonRepository.delete(loaiMon);
    }

    @Override
    @Cacheable(value = "loaimon", key = "#id")
    public LoaiMon timTheoId(Integer id) {
        return loaiMonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại món"));
    }

    @Override
    @Cacheable(value = "loaimon", key = "'all'")
    public List<LoaiMon> layTatCa() {
        return loaiMonRepository.findAll();
    }
}
