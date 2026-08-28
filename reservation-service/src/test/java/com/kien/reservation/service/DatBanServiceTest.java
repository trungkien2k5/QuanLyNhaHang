package com.kien.reservation.service;

import com.kien.reservation.client.RestaurantClient;
import com.kien.reservation.dto.DatBanDTO;
import com.kien.reservation.entity.DatBan;
import com.kien.reservation.entity.KhachHang;
import com.kien.reservation.repository.DatBanRepository;
import com.kien.reservation.repository.KhachHangRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatBanServiceTest {

    @Mock
    private DatBanRepository dbr;

    @Mock
    private KhachHangRepository khrp;

    @Mock
    private RestaurantClient restaurantClient;

    @InjectMocks
    private DatBanService datBanService;

    @Test
    void them_success() {
        DatBanDTO dto = new DatBanDTO();
        dto.setMaKH(1);
        dto.setNgayDat(LocalDate.of(2026, 8, 30));
        dto.setSoNguoi(4);

        KhachHang khachHang = new KhachHang();

        when(khrp.findById(1))
                .thenReturn(Optional.of(khachHang));

        when(dbr.save(any(DatBan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DatBan result = datBanService.them(dto);

        assertEquals(khachHang, result.getKhachHang());
        assertEquals(dto.getNgayDat(), result.getNgayDat());
        assertEquals(dto.getSoNguoi(), result.getSoNguoi());
        assertEquals("Chờ xác nhận", result.getTrangThai());

        verify(khrp).findById(1);
        verify(dbr).save(any(DatBan.class));
    }

    @Test
    void them_customerNotFound() {
        DatBanDTO dto = new DatBanDTO();
        dto.setMaKH(999);

        when(khrp.findById(999))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> datBanService.them(dto)
        );

        assertEquals(
                "Không tìm thấy khách hàng",
                exception.getMessage()
        );

        verify(dbr, never()).save(any());
    }

    @Test
    void layChiTiet_success() {
        DatBan datBan = new DatBan();

        when(dbr.findById(1))
                .thenReturn(Optional.of(datBan));

        DatBan result = datBanService.layChiTiet(1);

        assertEquals(datBan, result);

        verify(dbr).findById(1);
    }

    @Test
    void huyDatBan_success() {
        DatBan datBan = new DatBan();
        datBan.setTrangThai("Chờ xác nhận");

        when(dbr.findById(1))
                .thenReturn(Optional.of(datBan));

        when(dbr.save(datBan))
                .thenReturn(datBan);

        DatBan result = datBanService.huyDatBan(1);

        assertEquals("Đã hủy", result.getTrangThai());

        verify(dbr).save(datBan);
    }

    @Test
    void huyDatBan_whenCompleted_shouldThrowException() {
        DatBan datBan = new DatBan();
        datBan.setTrangThai("Hoàn thành");

        when(dbr.findById(1))
                .thenReturn(Optional.of(datBan));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> datBanService.huyDatBan(1)
        );

        assertEquals(
                "Không thể hủy đặt bàn đã hoàn thành",
                exception.getMessage()
        );

        verify(dbr, never()).save(any());
    }
}
