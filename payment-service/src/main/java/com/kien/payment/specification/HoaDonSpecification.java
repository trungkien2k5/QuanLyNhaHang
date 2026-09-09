package com.kien.payment.specification;

import com.kien.payment.entity.HoaDon;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class HoaDonSpecification {

    public static Specification<HoaDon> coTuNgay(LocalDateTime tuNgay) {
        return (root, query, cb) ->
                tuNgay == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("ngayLap"), tuNgay);
    }

    public static Specification<HoaDon> coDenNgay(LocalDateTime denNgay) {
        return (root, query, cb) ->
                denNgay == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("ngayLap"), denNgay);
    }

    public static Specification<HoaDon> coMaKH(Integer maKH) {
        return (root, query, cb) ->
                maKH == null
                        ? null
                        : cb.equal(root.get("maKH"), maKH);
    }

    public static Specification<HoaDon> coTrangThai(String trangThai) {
        return (root, query, cb) ->
                trangThai == null || trangThai.isBlank()
                        ? null
                        : cb.equal(root.get("trangThai"), trangThai);
    }
}