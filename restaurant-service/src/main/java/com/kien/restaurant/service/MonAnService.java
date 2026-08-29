package com.kien.restaurant.service;

import com.kien.restaurant.common.KafkaEventPublisher;
import com.kien.restaurant.common.MonAnEvent;
import com.kien.restaurant.dto.MonAnDTO;
import com.kien.restaurant.entity.LoaiMon;
import com.kien.restaurant.entity.MonAn;
import com.kien.restaurant.exception.KhongTimThayException;
import com.kien.restaurant.mapper.MonAnMapper;
import com.kien.restaurant.repository.LoaiMonRepository;
import com.kien.restaurant.repository.MonAnRepository;
import com.kien.restaurant.specification.MonAnSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonAnService {

    private static final String MENU_TOPIC = "restaurant.menu.events";

    private final MonAnRepository monAnRepository;
    private final LoaiMonRepository loaiMonRepository;
    private final FileService fileService;
    private final MonAnMapper monAnMapper;
    private final KafkaEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "monan", allEntries = true)
    public MonAn themMon(MonAnDTO dto, MultipartFile file) {
        MonAn monAn = taoMonAn(dto);

        if (file != null && !file.isEmpty()) {
            monAn.setAnh(fileService.upload(file));
        }

        MonAn saved = monAnRepository.save(monAn);
        publishEvent("CREATED", saved);
        return saved;
    }

    @Cacheable(
            value = "monan",
            key = "#page+'-'+#size+'-'+#keyword+'-'+#maLoai+'-'+#giaTu+'-'+#giaDen+'-'+#trangThai+'-'+#sort+'-'+#direction")
    public Page<MonAn> layTat(
            int page,
            int size,
            String keyword,
            Integer maLoai,
            BigDecimal giaTu,
            BigDecimal giaDen,
            String trangThai,
            String sort,
            String direction) {

        Sort sapXep = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sapXep);

        if (!coDieuKienLoc(keyword, maLoai, giaTu, giaDen, trangThai)) {
            return monAnRepository.findAll(pageable);
        }

        Specification<MonAn> specification = MonAnSpecificationBuilder.build(
                keyword, maLoai, giaTu, giaDen, trangThai);

        return monAnRepository.findAll(specification, pageable);
    }

    @Cacheable(value = "monan", key = "'id-' + #maMon")
    public MonAn layTheoMa(Integer maMon) {
        return timMonAn(maMon);
    }

    @Transactional
    @CacheEvict(value = "monan", allEntries = true)
    public MonAn capNhat(Integer maMon, MonAnDTO dto, MultipartFile file) {
        MonAn monAn = timMonAn(maMon);
        String anhCu = monAn.getAnh();
        String anhMoi = null;
        boolean coAnhMoi = file != null && !file.isEmpty();

        try {
            if (coAnhMoi) {
                anhMoi = fileService.upload(file);
                monAn.setAnh(anhMoi);
            }

            capNhatThongTin(monAn, dto);
            MonAn ketQua = monAnRepository.save(monAn);

            if (coAnhMoi && anhCu != null && !anhCu.isBlank()) {
                fileService.delete(anhCu);
            }

            publishEvent("UPDATED", ketQua);
            return ketQua;
        } catch (Exception e) {
            if (anhMoi != null) {
                try {
                    fileService.delete(anhMoi);
                } catch (Exception cleanupException) {
                    log.warn("Không thể xóa ảnh mới sau khi cập nhật món ăn thất bại: maMon={}, anhMoi={}",
                            maMon, anhMoi, cleanupException);
                }
            }
            throw e;
        }
    }

    @Transactional
    @CacheEvict(value = "monan", allEntries = true)
    public void xoa(Integer maMon) {
        MonAn monAn = timMonAn(maMon);

        if (monAn.getAnh() != null && !monAn.getAnh().isBlank()) {
            fileService.delete(monAn.getAnh());
        }

        monAnRepository.delete(monAn);
        publishEvent("DELETED", monAn);
    }

    private void publishEvent(String action, MonAn monAn) {
        eventPublisher.publish(
                MENU_TOPIC,
                String.valueOf(monAn.getMaMon()),
                new MonAnEvent(action, monAn.getMaMon(), monAn.getTenMon(), LocalDateTime.now())
        ).exceptionally(ex -> {
            log.error("Không thể publish menu event action={} maMon={}", action, monAn.getMaMon(), ex);
            return null;
        });
    }

    private MonAn taoMonAn(MonAnDTO dto) {
        MonAn monAn = monAnMapper.toEntity(dto);
        monAn.setLoaiMon(timLoaiMon(dto.getMaLoai()));
        monAn.setTrangThai("Đang bán");
        return monAn;
    }

    private void capNhatThongTin(MonAn monAn, MonAnDTO dto) {
        monAnMapper.updateEntity(dto, monAn);
        monAn.setLoaiMon(timLoaiMon(dto.getMaLoai()));
    }

    private MonAn timMonAn(Integer maMon) {
        return monAnRepository.findById(maMon)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy món ăn"));
    }

    private LoaiMon timLoaiMon(Integer maLoai) {
        return loaiMonRepository.findById(maLoai)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy loại món"));
    }

    private boolean coDieuKienLoc(
            String keyword,
            Integer maLoai,
            BigDecimal giaTu,
            BigDecimal giaDen,
            String trangThai) {
        return (keyword != null && !keyword.isBlank())
                || maLoai != null
                || giaTu != null
                || giaDen != null
                || (trangThai != null && !trangThai.isBlank());
    }
}
