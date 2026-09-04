package com.kien.restaurant.service;

import com.kien.restaurant.common.KafkaEventPublisher;
import com.kien.restaurant.dto.MonAnDTO;
import com.kien.restaurant.entity.LoaiMon;
import com.kien.restaurant.entity.MonAn;
import com.kien.restaurant.exception.ResourceNotFoundException;
import com.kien.restaurant.mapper.MonAnMapper;
import com.kien.restaurant.repository.LoaiMonRepository;
import com.kien.restaurant.repository.MonAnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonAnServiceTest {

    @Mock
    private MonAnRepository monAnRepository;

    @Mock
    private LoaiMonRepository loaiMonRepository;

    @Mock
    private FileService fileService;

    @Mock
    private MonAnMapper monAnMapper;

    @Mock
    private KafkaEventPublisher eventPublisher;

    @InjectMocks
    private MonAnService monAnService;

    @Test
    void themMon_success() {
        MonAnDTO dto = new MonAnDTO();
        dto.setMaLoai(1);

        LoaiMon loaiMon = new LoaiMon();
        MonAn monAn = new MonAn();

        when(monAnMapper.toEntity(dto))
                .thenReturn(monAn);

        when(loaiMonRepository.findById(1))
                .thenReturn(Optional.of(loaiMon));

        when(monAnRepository.save(any(MonAn.class)))
                .thenReturn(monAn);

        MonAn result = monAnService.themMon(dto, null);

        assertEquals(monAn, result);
        assertEquals("Đang bán", monAn.getTrangThai());

        verify(monAnRepository).save(monAn);
    }

    @Test
    void layTheoMa_success() {
        MonAn monAn = new MonAn();

        when(monAnRepository.findById(1))
                .thenReturn(Optional.of(monAn));

        MonAn result = monAnService.layTheoMa(1);

        assertEquals(monAn, result);

        verify(monAnRepository).findById(1);
    }

    @Test
    void layTheoMa_notFound() {
        when(monAnRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> monAnService.layTheoMa(1)
        );

        verify(monAnRepository).findById(1);
    }

    @Test
    void capNhat_success() {
        MonAnDTO dto = new MonAnDTO();
        dto.setMaLoai(1);

        MonAn monAn = new MonAn();
        LoaiMon loaiMon = new LoaiMon();

        when(monAnRepository.findById(1))
                .thenReturn(Optional.of(monAn));

        when(loaiMonRepository.findById(1))
                .thenReturn(Optional.of(loaiMon));

        when(monAnRepository.save(monAn))
                .thenReturn(monAn);

        MonAn result = monAnService.capNhat(
                1,
                dto,
                null
        );

        assertEquals(monAn, result);

        verify(monAnMapper)
                .updateEntity(dto, monAn);

        verify(monAnRepository)
                .save(monAn);
    }

    @Test
    void xoa_success() {
        MonAn monAn = new MonAn();

        when(monAnRepository.findById(1))
                .thenReturn(Optional.of(monAn));

        monAnService.xoa(1);

        verify(monAnRepository)
                .findById(1);

        verify(monAnRepository)
                .delete(monAn);
    }
}
