package com.kien.payment.service;

import com.kien.payment.client.MonAnClient;
import com.kien.payment.dto.CapNhatSoLuongDTO;
import com.kien.payment.dto.ThemMonDTO;
import com.kien.payment.entity.ChiTietHoaDon;
import com.kien.payment.entity.HoaDon;
import com.kien.payment.exception.BadRequestException;
import com.kien.payment.exception.ConflictException;
import com.kien.payment.id.ChiTietHoaDonId;
import com.kien.payment.repository.ChiTietHoaDonRepository;
import com.kien.payment.repository.HoaDonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChiTietHoaDonServiceTest {

    @Mock
    private ChiTietHoaDonRepository ctr;

    @Mock
    private HoaDonRepository hdr;

    @Mock
    private MonAnClient monAnClient;

    @InjectMocks
    private ChiTietHoaDonService service;

    private HoaDon hoaDon;

    @BeforeEach
    void setUp() {

        hoaDon = new HoaDon();

        hoaDon.setMaHD(1);
        hoaDon.setMaKH(100);
        hoaDon.setTongTien(BigDecimal.ZERO);
        hoaDon.setTrangThai("Chưa thanh toán");
    }

    // =========================================================
    // THÊM MÓN MỚI
    // =========================================================

    @Test
    void themMon_monanMoi_success() {

        ThemMonDTO dto = new ThemMonDTO();
        dto.setMaMon(10);
        dto.setSoLuong(2);

        when(hdr.findById(1))
                .thenReturn(Optional.of(hoaDon));

        when(monAnClient.layDonGia(10))
                .thenReturn(BigDecimal.valueOf(50000));

        when(ctr.findById(any(ChiTietHoaDonId.class)))
                .thenReturn(Optional.empty());

        when(ctr.save(any(ChiTietHoaDon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChiTietHoaDon savedItem = new ChiTietHoaDon();
        savedItem.setHoaDon(hoaDon);
        savedItem.setMaMon(10);
        savedItem.setSoLuong(2);
        savedItem.setDonGia(BigDecimal.valueOf(50000));
        savedItem.setThanhTien(BigDecimal.valueOf(100000));

        when(ctr.findByHoaDon(hoaDon))
                .thenReturn(List.of(savedItem));

        ChiTietHoaDon result =
                service.themMon(1, dto);

        assertNotNull(result);

        assertEquals(10, result.getMaMon());

        assertEquals(
                2,
                result.getSoLuong()
        );

        assertEquals(
                BigDecimal.valueOf(50000),
                result.getDonGia()
        );

        assertEquals(
                BigDecimal.valueOf(100000),
                result.getThanhTien()
        );

        assertEquals(
                BigDecimal.valueOf(100000),
                hoaDon.getTongTien()
        );

        verify(monAnClient)
                .layDonGia(10);

        verify(ctr)
                .save(any(ChiTietHoaDon.class));

        verify(hdr)
                .save(hoaDon);
    }


    // =========================================================
    // THÊM LẠI CÙNG MÓN
    // =========================================================

    @Test
    void themMon_monDaTonTai_congDonSoLuong() {

        ThemMonDTO dto = new ThemMonDTO();

        dto.setMaMon(10);
        dto.setSoLuong(3);

        ChiTietHoaDon existing = new ChiTietHoaDon();

        existing.setId(
                new ChiTietHoaDonId(1, 10)
        );

        existing.setHoaDon(hoaDon);
        existing.setMaMon(10);
        existing.setSoLuong(2);
        existing.setDonGia(
                BigDecimal.valueOf(50000)
        );
        existing.setThanhTien(
                BigDecimal.valueOf(100000)
        );

        when(hdr.findById(1))
                .thenReturn(Optional.of(hoaDon));

        when(monAnClient.layDonGia(10))
                .thenReturn(BigDecimal.valueOf(50000));

        when(ctr.findById(any(ChiTietHoaDonId.class)))
                .thenReturn(Optional.of(existing));

        when(ctr.save(any(ChiTietHoaDon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(ctr.findByHoaDon(hoaDon))
                .thenReturn(List.of(existing));

        ChiTietHoaDon result =
                service.themMon(1, dto);

        assertEquals(
                5,
                result.getSoLuong()
        );

        assertEquals(
                BigDecimal.valueOf(250000),
                result.getThanhTien()
        );

        assertEquals(
                BigDecimal.valueOf(250000),
                hoaDon.getTongTien()
        );

        verify(ctr)
                .save(existing);

        verify(hdr)
                .save(hoaDon);
    }


    // =========================================================
    // UPDATE QUANTITY
    // =========================================================

    @Test
    void capNhatSoLuong_success() {

        CapNhatSoLuongDTO dto =
                new CapNhatSoLuongDTO();

        dto.setSoLuong(5);

        ChiTietHoaDon existing =
                new ChiTietHoaDon();

        existing.setId(
                new ChiTietHoaDonId(1, 10)
        );

        existing.setHoaDon(hoaDon);
        existing.setMaMon(10);
        existing.setSoLuong(2);
        existing.setDonGia(
                BigDecimal.valueOf(50000)
        );
        existing.setThanhTien(
                BigDecimal.valueOf(100000)
        );


        when(ctr.findById(any(ChiTietHoaDonId.class)))
                .thenReturn(Optional.of(existing));

        when(ctr.save(any(ChiTietHoaDon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(ctr.findByHoaDon(hoaDon))
                .thenReturn(List.of(existing));

        ChiTietHoaDon result =
                service.capNhatSoLuong(
                        1,
                        10,
                        dto
                );

        assertEquals(
                5,
                result.getSoLuong()
        );

        assertEquals(
                BigDecimal.valueOf(250000),
                result.getThanhTien()
        );

        assertEquals(
                BigDecimal.valueOf(250000),
                hoaDon.getTongTien()
        );

        verify(ctr)
                .save(existing);

        verify(hdr)
                .save(hoaDon);
    }

    // =========================================================
    // UPDATE QUANTITY = 0
    // =========================================================

    @Test
    void capNhatSoLuong_zero_throwException() {

        CapNhatSoLuongDTO dto =
                new CapNhatSoLuongDTO();

        dto.setSoLuong(0);

        assertThrows(
                BadRequestException.class,
                () -> service.capNhatSoLuong(
                        1,
                        10,
                        dto
                )
        );

        verifyNoInteractions(
                ctr,
                hdr,
                monAnClient
        );
    }


    // =========================================================
    // XÓA MÓN
    // =========================================================

    @Test
    void xoaMon_success() {

        ChiTietHoaDon existing = new ChiTietHoaDon();

        ChiTietHoaDonId id = new ChiTietHoaDonId(1, 10);

        existing.setId(id);
        existing.setHoaDon(hoaDon);
        existing.setMaMon(10);
        existing.setSoLuong(2);
        existing.setDonGia(new BigDecimal("50000"));
        existing.setThanhTien(new BigDecimal("100000"));

        when(ctr.findById(id))
                .thenReturn(Optional.of(existing));

        when(ctr.findByHoaDon(hoaDon))
                .thenReturn(List.of());

        service.xoaMon(1, 10);

        assertEquals(
                BigDecimal.ZERO,
                hoaDon.getTongTien()
        );

        verify(ctr).delete(existing);
        verify(hdr).save(hoaDon);
    }
    // =========================================================
    // THÊM MÓN KHI ĐÃ THANH TOÁN
    // =========================================================

    @Test
    void themMon_hoaDonDaThanhToan_throwException() {

        hoaDon.setTrangThai("Đã thanh toán");

        ThemMonDTO dto =
                new ThemMonDTO();

        dto.setMaMon(10);
        dto.setSoLuong(1);

        when(hdr.findById(1))
                .thenReturn(Optional.of(hoaDon));

        assertThrows(
                ConflictException.class,
                () -> service.themMon(1, dto)
        );

        verifyNoInteractions(monAnClient);

        verify(ctr, never())
                .save(any());
    }


    // =========================================================
    // UPDATE KHI ĐÃ THANH TOÁN
    // =========================================================

    @Test
    void capNhatSoLuong_hoaDonDaThanhToan_throwException() {

        hoaDon.setTrangThai("Đã thanh toán");

        ChiTietHoaDon existing = new ChiTietHoaDon();
        existing.setId(new ChiTietHoaDonId(1, 10));
        existing.setHoaDon(hoaDon);
        existing.setMaMon(10);
        existing.setSoLuong(2);
        existing.setDonGia(new BigDecimal("50000"));
        existing.setThanhTien(new BigDecimal("100000"));

        when(ctr.findById(new ChiTietHoaDonId(1, 10)))
                .thenReturn(Optional.of(existing));

        CapNhatSoLuongDTO dto = new CapNhatSoLuongDTO();
        dto.setSoLuong(5);

        assertThrows(
                ConflictException.class,
                () -> service.capNhatSoLuong(1, 10, dto)
        );

        verify(ctr, never()).save(any());
    }

    // =========================================================
    // XÓA KHI ĐÃ THANH TOÁN
    // =========================================================

    @Test
    void xoaMon_hoaDonDaThanhToan_throwException() {

        hoaDon.setTrangThai("Đã thanh toán");

        ChiTietHoaDon existing = new ChiTietHoaDon();
        existing.setId(new ChiTietHoaDonId(1, 10));
        existing.setHoaDon(hoaDon);
        existing.setMaMon(10);
        existing.setSoLuong(2);
        existing.setDonGia(new BigDecimal("50000"));
        existing.setThanhTien(new BigDecimal("100000"));

        when(ctr.findById(new ChiTietHoaDonId(1, 10)))
                .thenReturn(Optional.of(existing));

        assertThrows(
                ConflictException.class,
                () -> service.xoaMon(1, 10)
        );

        verify(ctr, never()).delete(any());
    }

    // =========================================================
    // TÍNH TỔNG TIỀN
    // =========================================================

    @Test
    void themMon_tinhTongTien_success() {

        ThemMonDTO dto =
                new ThemMonDTO();

        dto.setMaMon(10);
        dto.setSoLuong(2);

        ChiTietHoaDon ct1 =
                new ChiTietHoaDon();

        ct1.setHoaDon(hoaDon);
        ct1.setMaMon(10);
        ct1.setSoLuong(2);
        ct1.setDonGia(
                BigDecimal.valueOf(50000)
        );
        ct1.setThanhTien(
                BigDecimal.valueOf(100000)
        );

        ChiTietHoaDon ct2 =
                new ChiTietHoaDon();

        ct2.setHoaDon(hoaDon);
        ct2.setMaMon(20);
        ct2.setSoLuong(3);
        ct2.setDonGia(
                BigDecimal.valueOf(30000)
        );
        ct2.setThanhTien(
                BigDecimal.valueOf(90000)
        );

        when(hdr.findById(1))
                .thenReturn(Optional.of(hoaDon));

        when(monAnClient.layDonGia(10))
                .thenReturn(BigDecimal.valueOf(50000));

        when(ctr.findById(any(ChiTietHoaDonId.class)))
                .thenReturn(Optional.empty());

        when(ctr.save(any(ChiTietHoaDon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(ctr.findByHoaDon(hoaDon))
                .thenReturn(List.of(ct1, ct2));

        service.themMon(1, dto);

        assertEquals(
                BigDecimal.valueOf(190000),
                hoaDon.getTongTien()
        );

        verify(hdr)
                .save(hoaDon);
    }
    @Test
    void themMon_restaurantUnavailable_throwException() {

        ThemMonDTO dto =
                new ThemMonDTO();

        dto.setMaMon(10);
        dto.setSoLuong(2);

        when(hdr.findById(1))
                .thenReturn(Optional.of(hoaDon));

        when(monAnClient.layDonGia(10))
                .thenThrow(
                        new RuntimeException(
                                "Restaurant service unavailable"
                        )
                );

        assertThrows(
                RuntimeException.class,
                () -> service.themMon(1, dto)
        );

        verify(monAnClient)
                .layDonGia(10);

        verify(
                ctr,
                never()
        ).save(any());

        verify(
                hdr,
                never()
        ).save(any());
    }
}