package com.kien.restaurant.monan.specification;

import com.kien.restaurant.entity.MonAn;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class MonAnSpecificationBuilder {

    public static Specification<MonAn> build(
            String keyword,
            Integer maLoai,
            BigDecimal giaTu,
            BigDecimal giaDen) {

        return build(keyword, maLoai, giaTu, giaDen, null);
    }

    public static Specification<MonAn> build(
            String keyword,
            Integer maLoai,
            BigDecimal giaTu,
            BigDecimal giaDen,
            String trangThai) {

        return Specification.where(
                        MonAnSpecification.tenMonContains(keyword))
                .and(
                        MonAnSpecification.theoLoai(maLoai))
                .and(
                        MonAnSpecification.giaTu(giaTu))
                .and(
                        MonAnSpecification.giaDen(giaDen))
                .and(
                        MonAnSpecification.coTrangThai(trangThai));
    }
}
