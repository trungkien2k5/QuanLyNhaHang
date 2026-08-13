    package com.kien.quanlynhahang.repository;

    import com.kien.quanlynhahang.entity.NguoiDung;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.Optional;

    @Repository
    public interface NguoiDungRepository extends JpaRepository<NguoiDung,Integer> {
        Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
        Optional<NguoiDung> findByEmail(String email);
        boolean existsByTenDangNhap(String tenDangNhap);
        boolean existsByEmail(String email);
    }