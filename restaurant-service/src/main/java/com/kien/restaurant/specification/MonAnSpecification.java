package com.kien.restaurant.specification;

import com.kien.restaurant.entity.MonAn;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class MonAnSpecification {

    public static Specification<MonAn> tenMonContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("tenMon")),
                    "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<MonAn> theoLoai(Integer maLoai) {
        return (root, query, cb) -> {
            if (maLoai == null) {
                return null;
            }

            return cb.equal(root.get("loaiMon").get("maLoai"), maLoai);
        };
    }

    public static Specification<MonAn> giaTu(BigDecimal giaTu) {
        return (root, query, cb) -> {
            if (giaTu == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(root.get("donGia"), giaTu);
        };
    }

    public static Specification<MonAn> giaDen(BigDecimal giaDen) {
        return (root, query, cb) -> {
            if (giaDen == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(root.get("donGia"), giaDen);
        };
    }

    public static Specification<MonAn> coTrangThai(String trangThai) {
        return (root, query, cb) -> {
            if (trangThai == null || trangThai.isBlank()) {
                return null;
            }

            return cb.equal(root.get("trangThai"), trangThai);
        };
    }
}
