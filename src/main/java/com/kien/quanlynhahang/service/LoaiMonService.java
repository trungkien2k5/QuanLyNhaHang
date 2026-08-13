package com.kien.quanlynhahang.service;

import com.kien.quanlynhahang.dto.request.CreateLoaiMonRequest;
import com.kien.quanlynhahang.dto.request.UpdateLoaiMonRequest;
import com.kien.quanlynhahang.entity.LoaiMon;

import java.util.List;

public interface LoaiMonService {

    LoaiMon them(CreateLoaiMonRequest request);

    LoaiMon sua(Integer id, UpdateLoaiMonRequest request);

    void xoa(Integer id);

    LoaiMon timTheoId(Integer id);

    List<LoaiMon> layTatCa();
}