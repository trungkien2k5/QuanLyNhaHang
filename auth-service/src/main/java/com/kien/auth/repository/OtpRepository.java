package com.kien.auth.repository;

import com.kien.auth.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository
        extends JpaRepository<Otp,Integer> {

    Optional<Otp> findTopByEmailOrderByIdDesc(String email);

}
