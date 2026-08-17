package com.kien.restaurant.service;

import com.kien.restaurant.dto.request.CreateKhuVucRequest;
import com.kien.restaurant.dto.request.UpdateKhuVucRequest;
import com.kien.restaurant.entity.KhuVuc;
import com.kien.restaurant.repository.KhuVucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class KhuVucService {

    private final KhuVucRepository repository;

    public List<KhuVuc> layTatCa() {
        return repository.findAll();
    }

    public KhuVuc them(CreateKhuVucRequest request) {

        KhuVuc khuVuc = new KhuVuc();
        khuVuc.setTenKhuVuc(request.getTenKhuVuc());

        return repository.save(khuVuc);
    }

    public KhuVuc capNhat(Integer id, UpdateKhuVucRequest request) {

        KhuVuc khuVuc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));

        khuVuc.setTenKhuVuc(request.getTenKhuVuc());

        return repository.save(khuVuc);
    }

    public void xoa(Integer id) {

        KhuVuc khuVuc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));

        repository.delete(khuVuc);
    }
}
