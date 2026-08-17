package com.kien.reservation.specification;

import com.kien.reservation.entity.DatBan;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class DatBanSpecificationBuilder {

    public static Specification<DatBan> build(
            LocalDate ngay,
            List<Integer> maBansTheoKhuVuc,
            Integer maBan,
            String trangThai) {

        return Specification
                .where(DatBanSpecification.coNgay(ngay))
                .and(DatBanSpecification.coDanhSachBan(maBansTheoKhuVuc))
                .and(DatBanSpecification.coBan(maBan))
                .and(DatBanSpecification.coTrangThai(trangThai));
    }
}
