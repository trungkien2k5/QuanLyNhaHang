package com.kien.payment.service;

import com.kien.payment.dto.HoaDonDTO;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.exception.ConflictException;
import com.kien.payment.repository.HoaDonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoaDonServiceTest {

    @Mock
    private HoaDonRepository hoaDonRepository;

    @InjectMocks
    private HoaDonService service;

    private HoaDon hoaDon;


    @BeforeEach
    void setUp() {

        hoaDon = new HoaDon();

        hoaDon.setMaHD(1);
        hoaDon.setMaKH(100);
        hoaDon.setTongTien(
                BigDecimal.valueOf(250000)
        );
        hoaDon.setTrangThai(
                "Chưa thanh toán"
        );
    }


    // =========================================================
    // TẠO HÓA ĐƠN
    // =========================================================

    @Test
    void them_success() {

        HoaDonDTO dto =
                new HoaDonDTO();

        dto.setMaKH(100);

        when(hoaDonRepository.save(any(HoaDon.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        HoaDon result =
                service.them(dto);

        assertNotNull(result);

        assertEquals(
                100,
                result.getMaKH()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.getTongTien()
        );

        assertEquals(
                "Chưa thanh toán",
                result.getTrangThai()
        );

        assertNotNull(
                result.getNgayLap()
        );

        verify(hoaDonRepository)
                .save(any(HoaDon.class));
    }


    // =========================================================
    // THANH TOÁN
    // =========================================================

    @Test
    void thanhToan_success() {

        when(hoaDonRepository.findById(1))
                .thenReturn(Optional.of(hoaDon));

        when(hoaDonRepository.save(hoaDon))
                .thenReturn(hoaDon);

        HoaDon result =
                service.thanhToan(1);

        assertEquals(
                "Đã thanh toán",
                result.getTrangThai()
        );

        verify(hoaDonRepository)
                .save(hoaDon);
    }


    // =========================================================
    // THANH TOÁN LẠI
    // =========================================================

    @Test
    void thanhToan_daThanhToan_throwException() {

        hoaDon.setTrangThai(
                "Đã thanh toán"
        );

        when(hoaDonRepository.findById(1))
                .thenReturn(Optional.of(hoaDon));

        assertThrows(
                ConflictException.class,
                () -> service.thanhToan(1)
        );

        verify(
                hoaDonRepository,
                never()
        ).save(any());
    }


    // =========================================================
    // HỦY HÓA ĐƠN
    // =========================================================

    @Test
    void huyHoaDon_success() {

        when(hoaDonRepository.findById(1))
                .thenReturn(Optional.of(hoaDon));

        when(hoaDonRepository.save(hoaDon))
                .thenReturn(hoaDon);

        HoaDon result =
                service.huyHoaDon(1);

        assertEquals(
                "Đã hủy",
                result.getTrangThai()
        );

        verify(hoaDonRepository)
                .save(hoaDon);
    }


    // =========================================================
    // HỦY HÓA ĐƠN ĐÃ THANH TOÁN
    // =========================================================

    @Test
    void huyHoaDon_daThanhToan_throwException() {

        hoaDon.setTrangThai(
                "Đã thanh toán"
        );

        when(hoaDonRepository.findById(1))
                .thenReturn(Optional.of(hoaDon));

        assertThrows(
                ConflictException.class,
                () -> service.huyHoaDon(1)
        );

        verify(
                hoaDonRepository,
                never()
        ).save(any());
    }
}