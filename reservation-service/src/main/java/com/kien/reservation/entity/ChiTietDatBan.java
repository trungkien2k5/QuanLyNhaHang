package com.kien.reservation.entity;
import com.kien.reservation.id.ChiTietDatBanId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ChiTietDatBan")
@Data
public class ChiTietDatBan {

    @EmbeddedId
    private ChiTietDatBanId id;



}
