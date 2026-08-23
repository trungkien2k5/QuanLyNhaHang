package com.kien.reservation.specification;

import com.kien.reservation.entity.ChiTietDatBan;
import com.kien.reservation.entity.DatBan;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class DatBanSpecification {

    public static Specification<DatBan> coNgay(LocalDate ngay) {
        return (root, query, cb) -> ngay == null
                ? null
                : cb.equal(root.get("ngayDat"), ngay);
    }

    public static Specification<DatBan> coBan(Integer maBan) {
        return (root, query, cb) -> {
            if (maBan == null) {
                return null;
            }

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<ChiTietDatBan> chiTiet = subquery.from(ChiTietDatBan.class);

            subquery.select(
                    chiTiet.get("id").get("maDatBan")
            ).where(
                    cb.equal(
                            chiTiet.get("id").get("maBan"),
                            maBan
                    )
            );

            return root.get("maDatBan").in(subquery);
        };
    }

    public static Specification<DatBan> coDanhSachBan(List<Integer> maBans) {
        return (root, query, cb) -> {
            if (maBans == null || maBans.isEmpty()) {
                return cb.disjunction();
            }

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<ChiTietDatBan> chiTiet = subquery.from(ChiTietDatBan.class);

            subquery.select(
                    chiTiet.get("id").get("maDatBan")
            ).where(
                    chiTiet.get("id").get("maBan").in(maBans)
            );

            return root.get("maDatBan").in(subquery);
        };
    }

    public static Specification<DatBan> coTrangThai(String trangThai) {
        return (root, query, cb) -> {
            if (!hasText(trangThai)) {
                return null;
            }

            return cb.equal(
                    root.get("trangThai"),
                    trangThai
            );
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}