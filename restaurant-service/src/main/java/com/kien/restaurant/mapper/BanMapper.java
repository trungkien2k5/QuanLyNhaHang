package com.kien.restaurant.mapper;

import com.kien.restaurant.dto.BanDTO;
import com.kien.restaurant.entity.Ban;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BanMapper {

    BanDTO toDTO(Ban ban);

    Ban toEntity(BanDTO dto);
}
